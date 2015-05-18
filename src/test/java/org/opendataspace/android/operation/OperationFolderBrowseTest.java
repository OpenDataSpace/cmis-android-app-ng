package org.opendataspace.android.operation;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class OperationFolderBrowseTest {

    @Test
    public void execute() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        Account acc = TestUtil.getDefaultAccount();
        app.getDatabase().getAccounts().create(acc);
        Repo repo = new Repo(null, acc);
        app.getDatabase().getRepos().create(repo);
        app.getPrefs().setLastAccountId(acc);

        OperationFolderBrowse op = new OperationFolderBrowse(acc, repo);
        OperationStatus st = op.execute();
        Assert.assertEquals(true, st.isOk());
    }
}
