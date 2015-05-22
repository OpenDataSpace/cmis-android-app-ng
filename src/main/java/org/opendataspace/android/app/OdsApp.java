package org.opendataspace.android.app;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import de.greenrobot.event.EventBus;
import io.fabric.sdk.android.Fabric;
import org.opendataspace.android.app.beta.BuildConfig;
import org.opendataspace.android.cmis.CmisRenditionCache;
import org.opendataspace.android.data.DataBase;
import org.opendataspace.android.view.ViewManager;

import java.io.File;

public class OdsApp extends Application {

    private static OdsApp instance;
    private static boolean crashl = false;

    public static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(Class.class, new ClassSerializer())
            .registerTypeAdapter(ClassWrapper.class, new ClassWrapperSerializer())
            .registerTypeAdapter(File.class, new ClassFileSerializer()).create();

    public static final EventBus bus =
            EventBus.builder().eventInheritance(false).sendNoSubscriberEvent(false).sendSubscriberExceptionEvent(false).
                    throwSubscriberException(false).build();

    private OdsPreferences prefs;
    private DataBase database;
    private TaskPool pool;
    private ViewManager vm;
    private CmisRenditionCache cmiscache;

    public static OdsApp get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        try {
            if (isRealApp()) {
                CompatPRNG.apply();

                if (!BuildConfig.DEBUG) {
                    Fabric.with(this, new Crashlytics());
                    Crashlytics.setUserIdentifier(prefs.getInstallId());
                    crashl = true;
                }
            }
        } catch (final Exception ex) {
            OdsLog.ex(getClass(), ex);
        }

        prefs = new OdsPreferences(this);
        database = OpenHelperManager.getHelper(this, DataBase.class);
        pool = new TaskPool();
        vm = new ViewManager();
        cmiscache = new CmisRenditionCache(getApplicationContext(), pool.getCmisService());
    }

    @Override
    public void onTerminate() {
        pool.stop();
        OpenHelperManager.releaseHelper();

        vm.dispose();
        cmiscache.dispose();

        instance = null;
        prefs = null;
        database = null;
        pool = null;
        vm = null;

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

    public ViewManager getViewManager() {
        return vm;
    }

    static boolean hasCrahlytics() {
        return crashl;
    }

    public boolean isRealApp() {
        return true;
    }

    public CmisRenditionCache getCmisCache() {
        return cmiscache;
    }
}
