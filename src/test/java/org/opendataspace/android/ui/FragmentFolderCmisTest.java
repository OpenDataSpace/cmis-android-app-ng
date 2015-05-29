package org.opendataspace.android.ui;

import android.widget.ListView;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.operation.OperationFolderBrowse;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class FragmentFolderCmisTest {

    @Test
    public void navigate() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        CmisSession session = TestUtil.setupSession(app, Repo.Type.GLOBAL);
        OperationFolderBrowse op =
                new OperationFolderBrowse(app.getDatabase().getAccounts().get(app.getPrefs().getLastAccountId()),
                        session.getRepo());
        FragmentFolderCmis fgm = new FragmentFolderCmis(op);
        ActivityMain ac = TestUtil.setupFragment(fgm);
        TestUtil.waitRunnable();

        //noinspection ConstantConditions
        ListView lv = (ListView) fgm.getView().findViewById(android.R.id.list);
        Assert.assertEquals(true, lv.getCount() != 0);
        Shadows.shadowOf(lv).clickFirstItemContainingText("GDS");
        TestUtil.waitRunnable();
        Assert.assertEquals(true, Shadows.shadowOf(lv).findIndexOfItemContainingText("GDS") == -1);
        boolean res = fgm.backPressed();
        TestUtil.waitRunnable();
        Assert.assertEquals(true, Shadows.shadowOf(lv).findIndexOfItemContainingText("GDS") != -1);
        Assert.assertEquals(true, res);
        res = fgm.backPressed();
        Assert.assertEquals(false, res);

        TestUtil.dismisActivity(ac);
    }
}
