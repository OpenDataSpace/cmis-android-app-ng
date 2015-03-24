package org.opendataspace.android.operations;

import android.content.Context;
import android.text.TextUtils;

import org.opendataspace.android.app.beta.R;

public class OperationStatus {

    private boolean ok;
    private String message;

    public void setError(String message) {
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
}
