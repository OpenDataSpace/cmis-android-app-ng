package org.opendataspace.android.operations;

import android.content.Context;

import org.opendataspace.android.app.TaskLoader;

public class OperationLoader extends TaskLoader<OperationStatus> {

    private final OperationBase op;
    private OperationStatus status;

    public OperationLoader(OperationBase op, Context context) {
        super(context);
        this.op = op;
    }

    @Override
    public OperationStatus loadInBackground() throws Exception {
        return op.execute();
    }

    @Override
    public void deliverResult(OperationStatus status) {
        if (isReset()) {
            return;
        }

        this.status = status;

        if (isStarted()) {
            super.deliverResult(status);
        }
    }

    @Override
    protected void onStartLoading() {
        if (status != null) {
            deliverResult(status);
        }
        if (takeContentChanged() || status == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        op.setCancel(true);
        cancelLoad();
        status = null;
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
    }

    @Override
    protected void onForceLoad() {
        op.setCancel(false);
        super.onForceLoad();
    }
}
