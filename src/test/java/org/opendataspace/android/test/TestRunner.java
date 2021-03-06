package org.opendataspace.android.test;

import android.os.Build;

import org.junit.runners.model.InitializationError;
import org.opendataspace.android.app.beta.BuildConfig;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.internal.dependency.CachedDependencyResolver;
import org.robolectric.internal.dependency.DependencyResolver;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.FileFsFile;
import org.robolectric.util.ReflectionHelpers;

import java.io.File;
import java.util.Properties;

public class TestRunner extends RobolectricTestRunner {

    private DependencyResolver dr;

    public TestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        String type = getType();
        String flavor = getFlavor();
        String applicationId = getApplicationId();
        FileFsFile res = FileFsFile.from("build/intermediates", "res/merged", flavor, type);
        FileFsFile assets = FileFsFile.from("build/intermediates", "assets", flavor, type);
        FileFsFile manifest;

        if (FileFsFile.from(new String[] {"build/intermediates", "manifests"}).exists()) {
            manifest = FileFsFile.from("build/intermediates", "manifests", "full", flavor, type, "AndroidManifest.xml");
        } else {
            manifest = FileFsFile.from("build/intermediates", "bundles", flavor, type, "AndroidManifest.xml");
        }

        return new AndroidManifest(manifest, res, assets, applicationId);
    }

    private String getType() {
        try {
            return (String) ReflectionHelpers.getStaticField(BuildConfig.class, "BUILD_TYPE");
        } catch (Throwable var3) {
            return null;
        }
    }

    private String getFlavor() {
        try {
            return (String) ReflectionHelpers.getStaticField(BuildConfig.class, "FLAVOR");
        } catch (Throwable var3) {
            return null;
        }
    }

    private String getApplicationId() {
        try {
            return (String) ReflectionHelpers.getStaticField(BuildConfig.class, "APPLICATION_ID");
        } catch (Throwable var3) {
            return null;
        }
    }

    @Override
    protected int pickSdkVersion(Config config, AndroidManifest manifest) {
        return Build.VERSION_CODES.JELLY_BEAN;
    }

    @Override
    protected Class<? extends org.robolectric.TestLifecycle> getTestLifecycleClass() {
        return TestLifecycle.class;
    }

    @Override
    protected Properties getConfigProperties() {
        return TestUtil.getProperties();
    }

    @Override
    protected DependencyResolver getJarResolver() {
        if (dr == null) {
            File cacheDir = new File(new File(System.getProperty("java.io.tmpdir")), "robolectric");
            //noinspection ResultOfMethodCallIgnored
            cacheDir.mkdir();

            if (cacheDir.exists()) {
                dr = new CachedDependencyResolver(new TestResolver(), cacheDir, 60 * 60 * 24 * 1000);
            } else {
                dr = new TestResolver();
            }
        }

        return dr;
    }
}
