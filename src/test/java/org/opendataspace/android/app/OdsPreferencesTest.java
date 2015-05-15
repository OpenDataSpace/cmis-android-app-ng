package org.opendataspace.android.app;

import android.text.TextUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.test.TestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(TestRunner.class)
public class OdsPreferencesTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkDefaults() throws InterruptedException {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        OdsPreferences pref = app.getPrefs();

        Assert.assertEquals(true, pref != null);
        Assert.assertEquals(false, TextUtils.isEmpty(pref.version()));
        Assert.assertEquals(false, TextUtils.isEmpty(pref.getInstallId()));
    }
}
