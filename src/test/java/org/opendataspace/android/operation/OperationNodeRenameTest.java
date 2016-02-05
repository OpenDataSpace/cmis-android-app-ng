package org.opendataspace.android.operation;

import junit.framework.Assert;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class OperationNodeRenameTest {

    @Test
    public void execute() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        CmisSession session = TestUtil.setupSession(app, Repo.Type.PRIVATE);
        String oldName = "Test123";
        String newName = "456Test";
        TestUtil.removeIfExists(session, oldName);
        TestUtil.removeIfExists(session, newName);

        CmisObject obj = session.createFolder(new Node(session.getRoot(null), session.getRepo()), oldName, null);
        Node node = new Node(obj, session.getRepo());
        OdsApp.get().getDatabase().getNodes().create(node);
        OperationNodeRename op = new OperationNodeRename(session, node, newName);
        OperationResult st = op.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(true, TestUtil.hasChild(app, session.getRepo(), null, newName, Node.Type.FOLDER));
        session.delete(node, null);
    }
}
