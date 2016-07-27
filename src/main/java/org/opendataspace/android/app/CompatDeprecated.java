package org.opendataspace.android.app;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class CompatDeprecated {

    private CompatDeprecated() {
    }

    public static Spanned fromHtml(final String source) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return Html.fromHtml(source);
        } else {
            return Html.fromHtml(source, 0);
        }
    }
}
