package org.opendataspace.android.operation;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.status.StatusContext;

public abstract class OperationBase {

    private boolean cancelled = false;
    private StatusContext status;

    public OperationResult execute() {
        OperationResult result = new OperationResult();
        status = OdsApp.get().getStatusManager().createContext(getClass());

        try {
            doExecute(result);
        } catch (InterruptedException ignore) {
            // nothing
        } catch (Exception ex) {
            OdsLog.ex(getClass(), ex);
            result.setError(ex);
        }

        status.dispose();
        status = null;
        return result;
    }

    protected abstract void doExecute(OperationResult result) throws Exception;

    public synchronized void setCancel(boolean val) {
        cancelled = val;
    }

    synchronized boolean isCancel() {
        return cancelled;
    }

    StatusContext getStatus() {
        return status;
    }
}
