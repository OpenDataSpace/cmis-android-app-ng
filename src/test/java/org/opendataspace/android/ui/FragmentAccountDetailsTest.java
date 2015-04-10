package org.opendataspace.android.ui;

import android.widget.CheckBox;
import android.widget.EditText;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.objects.Account;
import org.opendataspace.android.operations.OperationAccount;
import org.opendataspace.android.test.RunnerDefault;
import org.opendataspace.android.test.TestUtil;

@RunWith(RunnerDefault.class)
public class FragmentAccountDetailsTest {

    @Test
    public void checkDefaults() throws Exception {
        Account acc = TestUtil.getDefaultAccount();
        OperationAccount op = new OperationAccount(acc);
        FragmentAccountDetails fgm = new FragmentAccountDetails(op);
        ActivityMain ac = TestUtil.setupFragment(fgm);

        EditText etl = (EditText) ac.findViewById(R.id.edit_account_username);
        EditText etp = (EditText) ac.findViewById(R.id.edit_account_password);
        EditText etu = (EditText) ac.findViewById(R.id.edit_account_host);
        EditText etd = (EditText) ac.findViewById(R.id.edit_account_description);
        CheckBox cbp = (CheckBox) ac.findViewById(R.id.check_account_json);

        Assert.assertEquals(acc.getLogin(), etl.getText().toString());
        Assert.assertEquals(acc.getPassword(), etp.getText().toString());
        Assert.assertEquals(acc.getDisplayUri(), etu.getText().toString());
        Assert.assertEquals(acc.getName(), etd.getText().toString());
        Assert.assertEquals(acc.isUseJson(), cbp.isChecked());

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

        fgm = new FragmentAccountDetails(op);
        TestUtil.replaceFragment(ac, fgm);

        Assert.assertEquals(login, acc.getLogin());
        Assert.assertEquals(password, acc.getPassword());
        Assert.assertEquals(uri, acc.getDisplayUri());
        Assert.assertEquals(desc, acc.getName());
        Assert.assertEquals(useJs, acc.isUseJson());

        TestUtil.dismisActivity(ac);
    }
}
