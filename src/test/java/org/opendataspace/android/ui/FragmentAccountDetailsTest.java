package org.opendataspace.android.ui;

import android.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.operation.OperationAccountUpdate;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowView;

@RunWith(TestRunner.class)
public class FragmentAccountDetailsTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkDefaults() throws Exception {
        Account acc = TestUtil.getDefaultAccount();
        OperationAccountUpdate op = new OperationAccountUpdate(acc);
        FragmentAccountDetails fgm1 = new FragmentAccountDetails(op);
        ActivityMain ac = TestUtil.setupFragment(fgm1);
        TestUtil.waitRunnable();
        View vw = fgm1.getView();
        Assert.assertEquals(true, vw != null);

        EditText etl = (EditText) vw.findViewById(R.id.edit_account_username);
        EditText etp = (EditText) vw.findViewById(R.id.edit_account_password);
        EditText etu = (EditText) vw.findViewById(R.id.edit_account_host);
        EditText etd = (EditText) vw.findViewById(R.id.edit_account_description);
        CheckBox cbp = (CheckBox) vw.findViewById(R.id.check_account_json);

        Assert.assertEquals(acc.getLogin(), etl.getText().toString());
        Assert.assertEquals(acc.getPassword(), etp.getText().toString());
        Assert.assertEquals(acc.getDisplayUri(), etu.getText().toString());
        Assert.assertEquals(acc.getName(), etd.getText().toString());
        Assert.assertEquals(acc.isUseJson(), cbp.isChecked());
        Assert.assertEquals(false, fgm1.isDirty());

        String login = "login";
        String password = "password";
        String uri = "test.server.com/cmis";
        String desc = "description";
        boolean useJs = !acc.isUseJson();

        etl.setText(login);
        etp.setText(password);
        etu.setText(uri);
        etd.setText(desc);
        cbp.setChecked(useJs);

        FragmentAccountDetails fgm2 = new FragmentAccountDetails(op);
        TestUtil.replaceFragment(ac, fgm2);
        Assert.assertEquals(true, fgm1.isDirty());

        Assert.assertEquals(login, acc.getLogin());
        Assert.assertEquals(password, acc.getPassword());
        Assert.assertEquals(uri, acc.getDisplayUri());
        Assert.assertEquals(desc, acc.getName());
        Assert.assertEquals(useJs, acc.isUseJson());

        TestUtil.dismisActivity(ac);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void performActions() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        Account acc = TestUtil.getDefaultAccount();
        OperationAccountUpdate op = new OperationAccountUpdate(acc);
        FragmentAccountDetails fgm1 = new FragmentAccountDetails(op);
        ActivityMain ac = TestUtil.setupFragment(fgm1);
        fgm1.onOptionsItemSelected(new RoboMenuItem(R.id.menu_account_apply));
        TestUtil.waitRunnable();
        long cnt = app.getDatabase().getRepos().countOf();
        Assert.assertEquals(2, app.getDatabase().getAccounts().countOf());
        Assert.assertEquals(true, cnt > 0);

        FragmentAccountDetails fgm2 = new FragmentAccountDetails(op);
        TestUtil.replaceFragment(ac, fgm2);
        View vw = fgm2.getView();
        Assert.assertEquals(true, vw != null);
        EditText etd = (EditText) vw.findViewById(R.id.edit_account_description);
        etd.setText(acc.getName() + "xxx");
        fgm2.onOptionsItemSelected(new RoboMenuItem(R.id.menu_account_apply));
        TestUtil.waitRunnable();
        Assert.assertEquals(2, app.getDatabase().getAccounts().countOf());
        Assert.assertEquals(cnt, app.getDatabase().getRepos().countOf());

        FragmentAccountDetails fgm3 = new FragmentAccountDetails(op);
        TestUtil.replaceFragment(ac, fgm3);
        fgm3.onOptionsItemSelected(new RoboMenuItem(R.id.menu_account_delete));
        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        ShadowView.clickOn(alert.getButton(AlertDialog.BUTTON_POSITIVE));
        TestUtil.waitRunnable();
        Assert.assertEquals(1, app.getDatabase().getAccounts().countOf());
        Assert.assertEquals(0, TestUtil.allOf(app.getDatabase().getRepos().forAccount(acc)).size());

        TestUtil.dismisActivity(ac);
    }
}
