package org.opendataspace.android.app;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

public class OdsLog {

    public static void ex(Class<?> cls, Throwable ex) {
        Log.w("Caught at " + cls.getSimpleName(), ex);

        if (OdsApp.hasCrahlytics()) {
            Crashlytics.logException(ex);
        }
    }
}
