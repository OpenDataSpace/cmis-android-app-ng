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
        }

        return status;
    }

    protected abstract void doExecute(OperationStatus status);

    public synchronized void setCancel(boolean val) {
        cancelled = val;
    }

    protected synchronized boolean isCancel() {
        return cancelled;
    }
}
