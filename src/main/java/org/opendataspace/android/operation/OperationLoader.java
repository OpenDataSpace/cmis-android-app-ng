package org.opendataspace.android.operation;

import android.content.Context;

import org.opendataspace.android.app.TaskLoader;

public class OperationLoader extends TaskLoader<OperationResult> {

    private final OperationBase op;
    private OperationResult result;

    public OperationLoader(OperationBase op, Context context) {
        super(context);
        this.op = op;
    }

    @Override
    public OperationResult loadInBackground() throws Exception {
        return op.execute();
    }

    @Override
    protected boolean isCmis() {
        return op instanceof OperationBaseCmis;
    }

    @Override
    public void deliverResult(OperationResult result) {
        if (isReset()) {
            return;
        }

        this.result = result;

        if (isStarted()) {
            super.deliverResult(result);
        }
    }

    @Override
    protected void onStartLoading() {
        if (result != null) {
            deliverResult(result);
        }
        if (takeContentChanged() || result == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        op.setCancel(true);
        stopLoad();
        result = null;
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
