package org.opendataspace.android.test;

import android.app.Application;

import org.robolectric.DefaultTestLifecycle;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.shadows.ShadowLog;

import java.lang.reflect.Method;

@SuppressWarnings("WeakerAccess")
public class TestLifecycle extends DefaultTestLifecycle {

    @Override
    public Application createApplication(Method method, AndroidManifest appManifest, Config config) {
        return new TestApp();
    }

    @Override
    public void beforeTest(Method method) {
        super.beforeTest(method);

        if (Boolean.valueOf(TestUtil.getProperties().getProperty("test.logging"))) {
            ShadowLog.stream = System.out;
        }
    }

    @Override
    public void afterTest(Method method) {
        super.afterTest(method);
        System.gc();
    }
}
