package org.opendataspace.android.operation;

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

@RunWith(TestRunner.class)
public class OperationNodeDeleteTest {

    @Test
    public void deleteFolder() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        CmisSession session = TestUtil.setupSession(app, Repo.Type.PRIVATE);
        String name = "Test123";
        CmisObject obj = session.getObject(name);

        if (obj != null) {
            session.delete(obj.getId());
        }

        obj = session.createFolder(new Node(session.getRoot(), session.getRepo()), name);
        Node node = new Node(obj, session.getRepo());
        OdsApp.get().getDatabase().getNodes().create(node);
        OperationNodeDelete op = new OperationNodeDelete(node, session);
        OperationStatus st = op.execute();
        Assert.assertEquals(true, st.isOk());

        boolean found = false;

        for (Node cur : TestUtil
                .allOf(app.getDatabase().getNodes().forParent(session.getRepo(), ObjectBase.INVALID_ID))) {
            if (cur.getType() == Node.Type.FOLDER && name.equals(cur.getName())) {
                found = true;
                break;
            }
        }

        Assert.assertEquals(false, found);
    }
}
