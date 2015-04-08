package org.opendataspace.android.ui;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.test.OdsRunner;
import org.opendataspace.android.test.OdsTestUtil;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.fakes.RoboMenuItem;

@RunWith(OdsRunner.class)
public class ActivityMainTest {

    @Test
    public void checkNoAccounts() {
        ActivityMain ac = Robolectric.setupActivity(ActivityMain.class);
        Assert.assertEquals(FragmentAccountList.class, ac.getNavigation().getTopFragment().getClass());
        ac.onBackPressed();
        Assert.assertTrue(Shadows.shadowOf(ac).isFinishing());
    }

    @Test
    public void checkNavigation() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        app.getDatabase().getAccounts().create(OdsTestUtil.getDefaultAccount());
        ActivityMain ac = Robolectric.setupActivity(ActivityMain.class);
        Assert.assertEquals(FragmentNavigation.class, ac.getNavigation().getTopFragment().getClass());

        // settings
        navigateClick(R.id.action_nav_settings, FragmentSettings.class, ac);
        navigateBack(FragmentNavigation.class, ac);
        navigateMenu(R.id.menu_main_settings, FragmentSettings.class, ac);
        navigateBack(FragmentNavigation.class, ac);

        // about
        navigateMenu(R.id.menu_main_about, FragmentAbout.class, ac);
        navigateBack(FragmentNavigation.class, ac);

        // accounts
        ((FragmentNavigation) ac.getNavigation().getTopFragment()).actionManage();
        Assert.assertEquals(FragmentAccountList.class, ac.getNavigation().getTopFragment().getClass());
        navigateMenu(R.id.menu_accounts_add, FragmentAccountDetails.class, ac);
        navigateBack(FragmentAccountList.class, ac);
        navigateBack(FragmentNavigation.class, ac);

        // exit
        ac.onBackPressed();
        Assert.assertTrue(Shadows.shadowOf(ac).isFinishing());
    }

    private void navigateClick(int resId, Class<?> cls, ActivityMain ac) {
        ac.findViewById(resId).performClick();
        Assert.assertEquals(cls, ac.getNavigation().getTopFragment().getClass());
        ac.getSupportFragmentManager().executePendingTransactions();
    }

    private void navigateMenu(int resId, Class<?> cls, ActivityMain ac) {
        ac.getNavigation().getTopFragment().onOptionsItemSelected(new RoboMenuItem(resId));
        Assert.assertEquals(cls, ac.getNavigation().getTopFragment().getClass());
        ac.getSupportFragmentManager().executePendingTransactions();
    }

    private void navigateBack(Class<?> cls, ActivityMain ac) {
        ac.onBackPressed();
        Assert.assertFalse(Shadows.shadowOf(ac).isFinishing());
        Assert.assertEquals(cls, ac.getNavigation().getTopFragment().getClass());
    }
}
