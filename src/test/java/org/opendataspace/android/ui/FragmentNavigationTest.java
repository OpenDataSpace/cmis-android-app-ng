package org.opendataspace.android.ui;

import android.widget.ListView;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.operation.OperationAccountDelete;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
@Ignore
public class FragmentNavigationTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkDefaults() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        FragmentNavigation fgm = new FragmentNavigation();
        ActivityMain ac = TestUtil.setupFragment(fgm);
        TestUtil.waitRunnable();
        ListView lv = (ListView) fgm.getView().findViewById(R.id.list_nav_accounts);
        TextView tv = (TextView) fgm.getView().findViewById(R.id.action_nav_account);
        Assert.assertEquals(2, lv.getCount());
        Assert.assertEquals(app.getViewManager().getCurrentAccount().getDisplayName(), tv.getText());
        TestUtil.dismisActivity(ac);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void selectAccount() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        FragmentNavigation fgm1 = new FragmentNavigation();
        FragmentNavigation fgm2 = new FragmentNavigation();
        ActivityMain ac = TestUtil.setupFragment(fgm1);
        TestUtil.replaceSecondaryFragment(ac, fgm2);
        TestUtil.waitRunnable();

        ListView lv1 = (ListView) fgm1.getView().findViewById(R.id.list_nav_accounts);
        ListView lv2 = (ListView) fgm2.getView().findViewById(R.id.list_nav_accounts);
        TextView tv1 = (TextView) fgm1.getView().findViewById(R.id.action_nav_account);
        TextView tv2 = (TextView) fgm2.getView().findViewById(R.id.action_nav_account);
        Assert.assertEquals(2, lv1.getCount());
        Assert.assertEquals(2, lv2.getCount());
        Assert.assertEquals(app.getViewManager().getCurrentAccount().getDisplayName(), tv1.getText());
        Assert.assertEquals(app.getViewManager().getCurrentAccount().getDisplayName(), tv2.getText());

        Account acc = TestUtil.getDefaultAccount();
        String newName = "xxx";
        acc.setName(newName);
        app.getDatabase().getAccounts().create(acc);
        Assert.assertEquals(3, lv1.getCount());
        Assert.assertEquals(3, lv2.getCount());
        Assert.assertEquals(app.getViewManager().getCurrentAccount().getDisplayName(), tv1.getText());
        Assert.assertEquals(app.getViewManager().getCurrentAccount().getDisplayName(), tv2.getText());

        Shadows.shadowOf(lv2).clickFirstItemContainingText(newName);
        TestUtil.waitRunnable();
        Assert.assertEquals(app.getViewManager().getCurrentAccount().getDisplayName(), tv1.getText());
        Assert.assertEquals(app.getViewManager().getCurrentAccount().getDisplayName(), tv2.getText());
        Assert.assertEquals(newName, tv1.getText());
        Assert.assertEquals(newName, tv2.getText());

        TestUtil.waitRunnable();
        TestUtil.dismisActivity(ac);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void deleteAccount() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        FragmentNavigation fgm1 = new FragmentNavigation();
        ActivityMain ac = TestUtil.setupFragment(fgm1);

        Account acc = app.getDatabase().getAccounts().get(app.getPrefs().getLastAccountId());
        OperationAccountDelete op = new OperationAccountDelete(acc);
        op.execute();
        TestUtil.waitRunnable();

        TextView tv = (TextView) fgm1.getView().findViewById(R.id.action_nav_account);
        Assert.assertEquals(ac.getString(R.string.nav_noaccount), tv.getText());

        TestUtil.dismisActivity(ac);
    }
}
