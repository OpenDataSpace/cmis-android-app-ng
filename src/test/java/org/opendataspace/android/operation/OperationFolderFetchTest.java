package org.opendataspace.android.operation;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class OperationFolderFetchTest {

    @Test
    public void execute() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        Account acc = TestUtil.getDefaultAccount();
        app.getDatabase().getAccounts().create(acc);
        app.getPrefs().setLastAccountId(acc);

        OperationRepoFetch op1 = new OperationRepoFetch(acc);
        op1.setShouldConfig(false);
        OperationStatus st = op1.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(true, app.getDatabase().getRepos().countOf() > 0);

        Repo repo = TestUtil.repo(app, acc, Repo.Type.PRIVATE);
        CmisSession session = new CmisSession(acc, repo);
        OperationFolderFetch op2 = new OperationFolderFetch(session, null);
        st = op2.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(true, app.getDatabase().getNodes().countOf() > 0);
    }
}
