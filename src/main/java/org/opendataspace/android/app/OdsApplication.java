package org.opendataspace.android.app;

import android.app.Application;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OdsApplication extends Application {

    private static OdsApplication instance;
    public static final Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassSerializer()).create();

    private Preferences prefs;

    public static OdsApplication get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        prefs = new Preferences(this);
    }

    @Override
    public void onTerminate() {
        instance = null;
        super.onTerminate();
    }

    public Preferences getPrefs() {
        return prefs;
    }
}
