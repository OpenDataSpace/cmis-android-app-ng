package org.opendataspace.android.test;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import org.robolectric.DefaultTestLifecycle;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.ActivityData;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.builder.RobolectricPackageManager;
import org.robolectric.shadows.ShadowLog;

import java.lang.reflect.Method;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class TestLifecycle extends DefaultTestLifecycle {

    @Override
    public Application createApplication(Method method, AndroidManifest appManifest, Config config) {
        Application app = createApp();

        if (appManifest != null) {
            Map ad = appManifest.getActivityDatas();
            RobolectricPackageManager packageManager = (RobolectricPackageManager) app.getPackageManager();

            for (Object o : ad.values()) {
                ActivityData data = (ActivityData) o;
                String name = data.getName();
                String activityName = name.startsWith(".") ? appManifest.getPackageName() + name : name;
                packageManager.addResolveInfoForIntent(new Intent(activityName), new ResolveInfo());
            }
        }

        return app;
    }

    private Application createApp() {
        return new TestApp();
    }

    @Override
    public void beforeTest(Method method) {
        super.beforeTest(method);

        if (Boolean.valueOf(TestUtil.getProperties().getProperty("test.logging"))) {
            ShadowLog.stream = System.out;
        }
    }
}
