package org.opendataspace.android.operation;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.CmisSession;
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
        CmisSession session = TestUtil.setupSession(app, Repo.Type.PRIVATE);
        OperationFolderBrowse op =
                new OperationFolderBrowse(app.getDatabase().getAccounts().get(app.getPrefs().getLastAccountId()),
                        session.getRepo(), OperationFolderBrowse.Mode.DEFAULT);
        OperationResult st = op.execute();
        TestUtil.waitRunnable();
        Assert.assertEquals(true, st.isOk());
    }
}
