package org.opendataspace.android.operation;

import android.text.TextUtils;

import junit.framework.Assert;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.object.ObjectBase;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class OperationFolderCreateTest {

    @Test
    public void execute() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        CmisSession session = TestUtil.setupSession(app, Repo.Type.PRIVATE);
        String name = "Test123";
        CmisObject obj = session.getObjectByPath(name);

        if (obj != null) {
            session.delete(obj.getId());
        }

        OperationFolderCreate op =
                new OperationFolderCreate(session, new Node(session.getRoot(), session.getRepo()), name);
        OperationStatus st = op.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(false, TextUtils.isEmpty(op.getLastUuid()));

        boolean found = false;

        for (Node cur : TestUtil
                .allOf(app.getDatabase().getNodes().forParent(session.getRepo(), ObjectBase.INVALID_ID))) {
            if (cur.getType() == Node.Type.FOLDER && name.equals(cur.getName())) {
                found = true;
                break;
            }
        }

        Assert.assertEquals(true, found);
        session.delete(op.getLastUuid());
    }
}
