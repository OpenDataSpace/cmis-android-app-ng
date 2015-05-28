package org.opendataspace.android.operation;

import junit.framework.Assert;
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
public class OperationFolderFetchTest {

    @Test
    public void execute() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        CmisSession session = TestUtil.setupSession(app, Repo.Type.PRIVATE);
        OperationFolderFetch op = new OperationFolderFetch(session, new Node(session.getRoot(), session.getRepo()));
        OperationStatus st = op.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(true, app.getDatabase().getNodes().countOf() > 0);
    }
}
