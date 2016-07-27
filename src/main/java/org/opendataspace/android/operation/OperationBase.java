package org.opendataspace.android.operation;

import android.app.Activity;
import android.app.AlertDialog;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.status.StatusContext;

public abstract class OperationBase {

    private boolean cancelled = false;
    private StatusContext status;
    private OperationResult result;

    public OperationResult execute() {
        result = new OperationResult();
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

    public boolean reportError(final Activity ac) {
        if (!result.isOk()) {
            new AlertDialog.Builder(ac).setMessage(result.getMessage(ac)).setCancelable(true)
                    .setPositiveButton(R.string.common_ok, (dialogInterface, i) -> dialogInterface.cancel()).show();

            return true;
        }

        return false;
    }
}
