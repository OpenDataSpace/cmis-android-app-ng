package org.opendataspace.android.app;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

public class OdsLog {

    public static void ex(Class<?> cls, Throwable ex) {
        Log.w(cls.getSimpleName(), "Caught at " + cls.getCanonicalName(), ex);

        if (OdsApp.hasCrahlytics()) {
            Crashlytics.logException(ex);
        }
    }

    public static void msg(Class<?> cls, String message) {
        Log.i(cls.getSimpleName(), message);
    }

    public static void debug(Class<?> cls, String message) {
        Log.d(cls.getSimpleName(), message);
    }
}
