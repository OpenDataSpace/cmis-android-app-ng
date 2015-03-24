package org.opendataspace.android.operations;

import android.util.Log;

public abstract class OperationBase {

    private boolean cancelled;

    public OperationStatus execute() {
        OperationStatus status = new OperationStatus();

        try {
            doExecute(status);
        } catch (Exception ex) {
            Log.w(getClass().getSimpleName(), ex);
            status.setError(ex.getMessage());
        }

        return status;
    }

    protected abstract void doExecute(OperationStatus status) throws Exception;

    public synchronized void setCancel(boolean val) {
        cancelled = val;
    }

    protected synchronized boolean isCancel() {
        return cancelled;
    }
}
