package org.opendataspace.android.app;

import android.text.TextUtils;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.test.OdsRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(OdsRunner.class)
public class OdsPreferencesTest {

    @Test
    public void checkDefaults() {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        OdsPreferences pref = app.getPrefs();

        Assert.assertEquals(true, pref != null);
        Assert.assertEquals(false, TextUtils.isEmpty(pref.version()));
        Assert.assertEquals(false, TextUtils.isEmpty(pref.getInstallId()));
    }
}
