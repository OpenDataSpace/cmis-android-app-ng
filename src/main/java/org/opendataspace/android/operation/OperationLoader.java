package org.opendataspace.android.operation;

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
    protected boolean isCmis() {
        return op instanceof OperationBaseCmis;
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
        stopLoad();
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
