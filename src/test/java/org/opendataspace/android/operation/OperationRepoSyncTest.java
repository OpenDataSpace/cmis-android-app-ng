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
public class OperationRepoSyncTest {

    @Test
    public void execute() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        Account acc = TestUtil.getDefaultAccount();
        app.getDatabase().getAccounts().create(acc);
        Repo r1 = new Repo(null, acc);
        Repo r2 = new Repo(null, acc);
        app.getDatabase().getRepos().create(r1);
        app.getDatabase().getRepos().create(r2);

        OperationRepoSync op = new OperationRepoSync(acc);
        OperationStatus st = op.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(1, app.getDatabase().getAccounts().countOf());
        Assert.assertEquals(true, app.getDatabase().getRepos().countOf() > 0);
        Assert.assertEquals(false, app.getDatabase().getRepos().exists(r1));
        Assert.assertEquals(false, app.getDatabase().getRepos().exists(r2));
    }
}
