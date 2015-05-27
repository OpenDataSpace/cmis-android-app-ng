package org.opendataspace.android.operation;

import android.text.TextUtils;

import junit.framework.Assert;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.object.ObjectBase;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;

@RunWith(TestRunner.class)
public class OperationFolderCreateTest {

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
        String name = "Test123";
        CmisObject obj = session.getObject(name);

        if (obj != null) {
            session.delete(obj.getId());
        }

        OperationFolderCreate op2 = new OperationFolderCreate(session, null, name);
        st = op2.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(false, TextUtils.isEmpty(op2.getLastUuid()));

        boolean found = false;

        for (Node cur : TestUtil.allOf(app.getDatabase().getNodes().forParent(repo, ObjectBase.INVALID_ID))) {
            if (cur.getType() == Node.Type.FOLDER && name.equals(cur.getName())) {
                found = true;
                break;
            }
        }

        Assert.assertEquals(true, found);
        session.delete(op2.getLastUuid());
    }
}
