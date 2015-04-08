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

        Assert.assertNotNull(pref);
        Assert.assertFalse(TextUtils.isEmpty(pref.version()));
        Assert.assertFalse(TextUtils.isEmpty(pref.getInstallId()));
    }
}
