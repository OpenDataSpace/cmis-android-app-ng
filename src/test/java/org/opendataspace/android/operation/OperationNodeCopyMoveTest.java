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

import java.util.Collections;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class OperationNodeCopyMoveTest {

    @Test
    public void execute() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        CmisSession session = TestUtil.setupSession(app, Repo.Type.PRIVATE);
        String name1 = "Test123";
        String name2 = "456Test";
        String name3 = "Te789st";
        OperationNodeCopyMove op = new OperationNodeCopyMove(session);
        OperationResult st;
        TestUtil.removeIfExists(session, name1);
        TestUtil.removeIfExists(session, name2);
        TestUtil.removeIfExists(session, name3);

        CmisObject obj1 = session.createFolder(new Node(session.getRoot(null), session.getRepo()), name1, null);
        Node node1 = new Node(obj1, session.getRepo());
        OdsApp.get().getDatabase().getNodes().create(node1);
        op.setContext(Collections.singletonList(node1), true);
        op.setTarget(node1);
        Assert.assertEquals(false, op.canPaste(node1));
        st = op.execute();
        Assert.assertEquals(false, st.isOk());

        CmisObject obj2 = session.createFolder(new Node(session.getRoot(null), session.getRepo()), name2, null);
        Node node2 = new Node(obj2, session.getRepo());
        OdsApp.get().getDatabase().getNodes().create(node2);
        op.setTarget(node2);
        Assert.assertEquals(true, op.canPaste(node2));
        st = op.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(true, TestUtil.hasChild(app, session.getRepo(), null, name1, Node.Type.FOLDER));
        Assert.assertEquals(true, TestUtil.hasChild(app, session.getRepo(), node2, name1, Node.Type.FOLDER));

        CmisObject obj3 = session.createFolder(new Node(session.getRoot(null), session.getRepo()), name3, null);
        Node node3 = new Node(obj3, session.getRepo());
        OdsApp.get().getDatabase().getNodes().create(node3);
        op.setContext(Collections.singletonList(node3), false);
        Assert.assertEquals(true, op.canPaste(node2));
        st = op.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(false, TestUtil.hasChild(app, session.getRepo(), null, name3, Node.Type.FOLDER));
        Assert.assertEquals(true, TestUtil.hasChild(app, session.getRepo(), node2, name3, Node.Type.FOLDER));

        session.delete(node1, null);
        session.delete(node2, null);
    }
}
