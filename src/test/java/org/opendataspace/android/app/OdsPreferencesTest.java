package org.opendataspace.android.app;

import android.text.TextUtils;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.test.OdsRunner;

@RunWith(OdsRunner.class)
public class OdsPreferencesTest {

    @Test
    public void checkDefaults() {
        OdsPreferences pref = OdsApp.get().getPrefs();

        Assert.assertNotNull(pref);
        Assert.assertFalse(TextUtils.isEmpty(pref.version()));
        Assert.assertFalse(TextUtils.isEmpty(pref.getInstallId()));
    }
}
