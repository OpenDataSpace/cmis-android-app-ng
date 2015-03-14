package org.opendataspace.android.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import org.opendataspace.android.app.beta.R;

public class OdsPreferences {

    private final Context context;

    public OdsPreferences(Context context) {
        this.context = context;
    }

    public boolean isDebug() {
        return 0 != (context.getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE);
    }

    public boolean isTablet() {
        return context.getResources().getBoolean(R.bool.isTablet);
    }

    public String version() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception ex) {
            Log.w(getClass().getSimpleName(), ex);
        }

        return "";
    }
}
