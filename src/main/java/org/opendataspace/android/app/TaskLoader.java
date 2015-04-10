package org.opendataspace.android.app;

import android.content.Context;
import android.support.v4.content.Loader;

public abstract class TaskLoader<T> extends Loader<T> {

    private class InternalTask extends Task {

        private T data;

        @Override
        public void onExecute() throws Exception {
            data = loadInBackground();
        }

        @Override
        public void onDone() {
            if (current == this) {
                current = null;

                if (data != null && !isAbandoned()) {
                    commitContentChanged();
                    deliverResult(data);
                }
            } else {
                rollbackContentChanged();
            }
        }
    }

    private Task current;

    protected TaskLoader(Context context) {
        super(context);
    }

    protected abstract T loadInBackground() throws Exception;

    @Override
    protected void onForceLoad() {
        cancelLoad();
        current = new InternalTask();
        OdsApp.get().getPool().execute(current);
    }

    protected void cancelLoad() {
        if (current != null) {
            OdsApp.get().getPool().cancel(current);
            current = null;
        }
    }
}
