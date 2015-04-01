package org.opendataspace.android.app;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import io.fabric.sdk.android.Fabric;
import org.opendataspace.android.data.DataBase;

public class OdsApp extends Application {

    private static OdsApp instance;
    public static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(Class.class, new ClassSerializer())
            .registerTypeAdapter(ClassWrapper.class, new ClassWrapperSerializer()).create();

    private OdsPreferences prefs;
    private DataBase database;
    private TaskPool pool;

    public static OdsApp get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        try {
            performHacks();
        } catch (final Exception ex) {
            OdsLog.ex(getClass(), ex);
        }

        prefs = new OdsPreferences(this);
        database = OpenHelperManager.getHelper(this, DataBase.class);
        pool = new TaskPool();
    }

    @Override
    public void onTerminate() {
        pool.stop();
        OpenHelperManager.releaseHelper();

        instance = null;
        prefs = null;
        database = null;
        pool = null;

        super.onTerminate();
    }

    public OdsPreferences getPrefs() {
        return prefs;
    }

    public DataBase getDatabase() {
        return database;
    }

    public TaskPool getPool() {
        return pool;
    }

    protected void performHacks() {
        CompatPRNG.apply();

        if (!prefs.isDebug()) {
            Fabric.with(this, new Crashlytics());
        }
    }
}
