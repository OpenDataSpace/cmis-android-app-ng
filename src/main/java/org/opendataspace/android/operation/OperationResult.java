package org.opendataspace.android.operation;

import android.content.Context;
import android.text.TextUtils;

import org.opendataspace.android.app.beta.R;

public class OperationResult {

    private boolean ok;
    private String message;

    private void setError(String message) {
        ok = false;
        this.message = message;
    }

    public void setOk() {
        ok = true;
        message = null;
    }

    public boolean isOk() {
        return ok;
    }

    public String getMessage(Context context) {
        return TextUtils.isEmpty(message) ? context.getString(R.string.common_error) : message;
    }

    public void setError(Exception ex) {
        Throwable cause = ex;

        while (cause != null) {
            Throwable parent = cause.getCause();

            if (parent == null) {
                break;
            }

            cause = parent;
        }

        setError(cause != null ? cause.getMessage() : null);
    }
}
