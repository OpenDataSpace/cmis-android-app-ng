package org.opendataspace.android.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.Account;

import java.util.UUID;

public class OdsPreferences {

    private final Context context;
    private final SharedPreferences prefs;

    public OdsPreferences(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("ods", Context.MODE_PRIVATE);
    }

    public static boolean isDebug() {
        return 0 !=
                (OdsApp.get().getApplicationContext().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE);
    }

    public boolean isTablet() {
        return context.getResources().getBoolean(R.bool.isTablet);
    }

    public String version() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception ex) {
            OdsLog.ex(getClass(), ex);
        }

        return "";
    }

    private String getString(final String key, final String def) {
        return prefs.getString(key, def);
    }

    private void setString(final String key, final String val) {
        prefs.edit().putString(key, val).apply();
    }

    private long getLong(final String key, final long def) {
        return prefs.getLong(key, def);
    }

    private void setLong(final String key, final long val) {
        prefs.edit().putLong(key, val).apply();
    }

    public String getInstallId() {
        String res = getString("install-id", "");

        if (TextUtils.isEmpty(res)) {
            res = UUID.randomUUID().toString();
            setString("install-id", res);
        }

        return res;
    }

    public long getLastAccountId() {
        return getLong("last-acc", -1);
    }

    public void setLastAccountId(Account val) {
        setLong("last-acc", val != null ? val.getId() : -1);
    }
}
