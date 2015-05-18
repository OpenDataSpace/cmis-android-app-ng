package org.opendataspace.android.operation;

import com.j256.ormlite.dao.CloseableIterator;
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
        OperationStatus st = op1.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(true, app.getDatabase().getRepos().countOf() > 0);

        CloseableIterator<Repo> it = app.getDatabase().getRepos().forAccount(acc);
        CmisSession session = new CmisSession(acc, it.nextThrow());
        it.close();

        OperationFolderFetch op2 = new OperationFolderFetch(session, null);
        st = op2.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(true, app.getDatabase().getNodes().countOf() > 0);
    }
}
