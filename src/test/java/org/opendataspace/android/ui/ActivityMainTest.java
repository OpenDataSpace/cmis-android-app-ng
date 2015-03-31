package org.opendataspace.android.ui;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.test.OdsRunner;
import org.robolectric.Robolectric;

@RunWith(OdsRunner.class)
public class ActivityMainTest {

    @Test
    public void checkNoAccounts() {
        ActivityMain ac = Robolectric.setupActivity(ActivityMain.class);
        Assert.assertEquals(ac.getNavigation().getTopFragment().getClass(), FragmentAccountList.class);
    }
}
