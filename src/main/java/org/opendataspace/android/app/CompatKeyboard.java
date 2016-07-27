package org.opendataspace.android.app;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class CompatKeyboard {

    private CompatKeyboard() {
    }

    public static void hide(Activity activity) {
        final View vw = activity.getCurrentFocus();

        if (vw != null) {
            final InputMethodManager inputManager =
                    (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(vw.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void request(EditText edit, Activity activity) {
        final InputMethodManager inputManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        edit.requestFocus();
        inputManager.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
    }
}
