package org.opendataspace.android.operation;

import org.opendataspace.android.app.OdsLog;

public abstract class OperationBase {

    private boolean cancelled = false;

    public OperationStatus execute() {
        OperationStatus status = new OperationStatus();

        try {
            doExecute(status);
        } catch (InterruptedException ignore) {
            // nothing
        } catch (Exception ex) {
            OdsLog.ex(getClass(), ex);
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
