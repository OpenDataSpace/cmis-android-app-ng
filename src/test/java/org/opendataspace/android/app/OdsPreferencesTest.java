package org.opendataspace.android.app;

import android.text.TextUtils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.test.TestRunner;
import org.robolectric.RuntimeEnvironment;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class OdsPreferencesTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkDefaults() {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        OdsPreferences pref = app.getPrefs();

        Assert.assertEquals(true, pref != null);
        Assert.assertEquals(false, TextUtils.isEmpty(pref.getInstallId()));
    }
}
