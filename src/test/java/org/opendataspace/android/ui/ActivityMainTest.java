package org.opendataspace.android.ui;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.test.OdsRunner;
import org.opendataspace.android.test.OdsTestUtil;
import org.robolectric.Robolectric;

@RunWith(OdsRunner.class)
public class ActivityMainTest {

    @Test
    public void checkNoAccounts() {
        ActivityMain ac = Robolectric.setupActivity(ActivityMain.class);
        Assert.assertEquals(FragmentAccountList.class, ac.getNavigation().getTopFragment().getClass());
    }

    @Test
    public void checkNavigation() throws Exception {
        OdsApp.get().getDatabase().getAccounts().create(OdsTestUtil.getDefaultAccount());
        ActivityMain ac = Robolectric.setupActivity(ActivityMain.class);
        Assert.assertEquals(FragmentNavigation.class, ac.getNavigation().getTopFragment().getClass());
    }
}
