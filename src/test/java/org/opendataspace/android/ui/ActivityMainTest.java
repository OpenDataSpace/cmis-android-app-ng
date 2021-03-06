package org.opendataspace.android.ui;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.widget.ListView;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.navigation.NavigationInterface;
import org.opendataspace.android.navigation.NavigationState;
import org.opendataspace.android.operation.OperationAccountUpdate;
import org.opendataspace.android.operation.OperationBase;
import org.opendataspace.android.status.StatusContext;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.util.ActivityController;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
@Ignore
public class ActivityMainTest {

    @Test
    public void checkNoAccounts() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        ActivityMain ac = Robolectric.setupActivity(ActivityMain.class);
        TestUtil.waitRunnable();
        Assert.assertEquals(FragmentAccountDetails.class, app.getNavigation().getTopFragment().getClass());
        ac.onBackPressed();
        Assert.assertEquals(true, Shadows.shadowOf(ac).isFinishing());
    }

    @Test
    public void checkNavigation() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        ActivityMain ac = TestUtil.setupActivity();
        Assert.assertEquals(FragmentNavigation.class, app.getNavigation().getTopFragment().getClass());
        TestUtil.waitRunnable();

        // settings
        navigateMenu(R.id.menu_main_settings, FragmentSettings.class, ac, app);
        navigateBack(FragmentNavigation.class, ac, app);

        // about
        navigateMenu(R.id.menu_main_about, FragmentAbout.class, ac, app);
        navigateBack(FragmentNavigation.class, ac, app);

        // accounts
        navigateList(R.id.list_nav_accounts, ac.getString(R.string.nav_manage), FragmentAccountList.class, ac, app);
        navigateMenu(R.id.menu_account_add, FragmentAccountDetails.class, ac, app);
        navigateBack(FragmentAccountList.class, ac, app);
        navigateBack(FragmentNavigation.class, ac, app);
        navigateList(R.id.list_nav_accounts, ac.getString(R.string.nav_addaccount), FragmentAccountDetails.class, ac,
                app);
        navigateBack(FragmentNavigation.class, ac, app);

        // folders
        navigateList(R.id.list_nav_folders, ac.getString(R.string.nav_personal), FragmentFolderCmis.class, ac, app);
        navigateBack(FragmentNavigation.class, ac, app);
        navigateList(R.id.list_nav_folders, ac.getString(R.string.nav_shared), FragmentFolderCmis.class, ac, app);
        navigateBack(FragmentNavigation.class, ac, app);
        navigateList(R.id.list_nav_folders, ac.getString(R.string.nav_global), FragmentFolderCmis.class, ac, app);
        navigateBack(FragmentNavigation.class, ac, app);
        navigateList(R.id.list_nav_folders, ac.getString(R.string.nav_localfolder), FragmentFolderLocal.class, ac, app);
        navigateBack(FragmentNavigation.class, ac, app);

        // exit
        ac.onBackPressed();
        Assert.assertEquals(true, Shadows.shadowOf(ac).isFinishing());
    }

    private void navigateList(int resId, String item, Class<?> cls, ActivityMain ac, OdsApp app) {
        ListView lv = (ListView) ac.findViewById(resId);
        Assert.assertNotEquals(lv, null);
        //noinspection ConstantConditions
        Shadows.shadowOf(lv).clickFirstItemContainingText(item);
        Assert.assertEquals(cls, app.getNavigation().getTopFragment().getClass());
        ac.getSupportFragmentManager().executePendingTransactions();
    }

    private void navigateMenu(int resId, Class<?> cls, ActivityMain ac, OdsApp app) {
        app.getNavigation().getTopFragment().onOptionsItemSelected(new RoboMenuItem(resId));
        Assert.assertEquals(cls, app.getNavigation().getTopFragment().getClass());
        ac.getSupportFragmentManager().executePendingTransactions();
    }

    private void navigateBack(Class<?> cls, ActivityMain ac, OdsApp app) {
        ac.onBackPressed();
        Assert.assertEquals(false, Shadows.shadowOf(ac).isFinishing());
        Assert.assertEquals(cls, app.getNavigation().getTopFragment().getClass());
    }

    @Test
    public void checkSerialization() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        ActivityMain ac = TestUtil.setupActivity();
        TestUtil.waitRunnable();
        NavigationInterface nav = app.getNavigation();
        nav.openRootFolder(FragmentAccountList.class, null);
        nav.openFile(FragmentAccountDetails.class, new OperationAccountUpdate(TestUtil.getDefaultAccount()));

        Bundle bu = TestUtil.dismisActivity(ac);
        NavigationState[] ns = OdsApp.gson.fromJson(bu.getString("ods.backstack"), NavigationState[].class);
        Assert.assertEquals(3, ns.length);
        Assert.assertEquals(FragmentAccountDetails.class, ns[2].getFragmentClass());
        Assert.assertEquals(FragmentAccountList.class, ns[1].getFragmentClass());
        Assert.assertEquals(FragmentNavigation.class, ns[0].getFragmentClass());
        Assert.assertEquals(OperationAccountUpdate.class, ns[2].getOperation().getClass());

        ac = ActivityController.of(Robolectric.getShadowsAdapter(), ac).setup(bu).get();
        TestUtil.waitRunnable();
        Assert.assertEquals(FragmentAccountDetails.class, app.getNavigation().getTopFragment().getClass());
        TestUtil.dismisActivity(ac);
    }

    @Test
    public void checkSnackbar() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        ActivityMain ac = TestUtil.setupActivity();
        TestUtil.waitRunnable();

        Snackbar bar = app.getStatusManager().getSnackbar();
        Assert.assertNotEquals(null, bar);

        StatusContext context = app.getStatusManager().createContext(OperationBase.class);
        context.postMessage(R.string.common_ok);
        TestUtil.waitRunnable();
        Assert.assertEquals(app.getString(R.string.common_ok), context.getLastMessage());
        Assert.assertEquals(true, bar.isShown());

        context.dispose();
        TestUtil.waitRunnable();
        Assert.assertEquals(app.getString(R.string.common_ok), context.getLastMessage());
        Assert.assertEquals(false, bar.isShown());

        TestUtil.dismisActivity(ac);
    }
}
