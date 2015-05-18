package org.opendataspace.android.test;

import android.os.Build;

import org.junit.runners.model.InitializationError;
import org.opendataspace.android.app.beta.BuildConfig;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.internal.SdkConfig;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.FileFsFile;
import org.robolectric.util.ReflectionHelpers;

import java.util.Properties;

public class TestRunner extends RobolectricTestRunner {

    public TestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        String type = getType();

        if (type == null) {
            FileFsFile res = FileFsFile.from("target/combined-resources");
            FileFsFile assets = FileFsFile.from("target/generated-sources/combined-assets");
            FileFsFile manifest = FileFsFile.from("target/AndroidManifest.xml");

            return new AndroidManifest(manifest, res, assets);
        }

        String flavor = getFlavor();
        String applicationId = getApplicationId();
        FileFsFile res = FileFsFile.from("build/intermediates", "res", flavor, type);
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
    protected SdkConfig pickSdkVersion(AndroidManifest appManifest, Config config) {
        return new SdkConfig(Build.VERSION_CODES.JELLY_BEAN);
    }

    @Override
    protected int pickReportedSdkVersion(Config config, AndroidManifest appManifest) {
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
}
