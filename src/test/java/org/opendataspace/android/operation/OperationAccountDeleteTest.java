package org.opendataspace.android.operation;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;

@RunWith(TestRunner.class)
public class OperationAccountDeleteTest {

    @Test
    public void checkExecute() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        Account acc = TestUtil.getDefaultAccount();
        app.getDatabase().getAccounts().create(acc);
        app.getDatabase().getRepos().create(new Repo(null, acc));
        app.getDatabase().getRepos().create(new Repo(null, acc));
        Assert.assertEquals(1, app.getDatabase().getAccounts().countOf());
        Assert.assertEquals(2, app.getDatabase().getRepos().countOf());

        OperationAccountDelete op = new OperationAccountDelete(acc);
        OperationStatus st = op.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(0, app.getDatabase().getAccounts().countOf());
        Assert.assertEquals(0, app.getDatabase().getRepos().countOf());
    }
}
