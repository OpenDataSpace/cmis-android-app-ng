package org.opendataspace.android.ui;

import android.os.Bundle;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.navigation.Navigation;
import org.opendataspace.android.navigation.NavigationState;
import org.opendataspace.android.operations.OperationAccount;
import org.opendataspace.android.test.RunnerDefault;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.util.ActivityController;

@RunWith(RunnerDefault.class)
public class ActivityMainTest {

    @Test
    public void checkNoAccounts() {
        ActivityMain ac = Robolectric.setupActivity(ActivityMain.class);
        Assert.assertEquals(FragmentAccountList.class, ac.getNavigation().getTopFragment().getClass());
        ac.onBackPressed();
        Assert.assertEquals(true, Shadows.shadowOf(ac).isFinishing());
    }

    @Test
    public void checkNavigation() throws Exception {
        ActivityMain ac = TestUtil.setupActivity();
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
        navigateClick(R.id.action_nav_manage, FragmentAccountList.class, ac);
        navigateMenu(R.id.menu_accounts_add, FragmentAccountDetails.class, ac);
        navigateBack(FragmentAccountList.class, ac);
        navigateBack(FragmentNavigation.class, ac);

        // exit
        ac.onBackPressed();
        Assert.assertEquals(true, Shadows.shadowOf(ac).isFinishing());
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
        Assert.assertEquals(false, Shadows.shadowOf(ac).isFinishing());
        Assert.assertEquals(cls, ac.getNavigation().getTopFragment().getClass());
    }

    @Test
    public void checkSerialization() throws Exception {
        ActivityMain ac = TestUtil.setupActivity();
        Navigation nav = ac.getNavigation();
        nav.openRootFolder(FragmentAccountList.class, null);
        nav.openFile(FragmentAccountDetails.class, new OperationAccount(TestUtil.getDefaultAccount()));

        Bundle bu = TestUtil.dismisActivity(ac);
        NavigationState[] ns = OdsApp.gson.fromJson(bu.getString("ods.backstack"), NavigationState[].class);
        Assert.assertEquals(3, ns.length);
        Assert.assertEquals(FragmentAccountDetails.class, ns[2].getFragmentClass());
        Assert.assertEquals(FragmentAccountList.class, ns[1].getFragmentClass());
        Assert.assertEquals(FragmentNavigation.class, ns[0].getFragmentClass());
        Assert.assertEquals(OperationAccount.class, ns[2].getOperation().getClass());

        ac = ActivityController.of(Robolectric.getShadowsAdapter(), ActivityMain.class).setup(bu).get();
        Assert.assertEquals(FragmentAccountDetails.class, ac.getNavigation().getTopFragment().getClass());
        TestUtil.dismisActivity(ac);
    }
}
