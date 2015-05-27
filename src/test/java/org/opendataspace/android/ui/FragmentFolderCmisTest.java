package org.opendataspace.android.ui;

import android.widget.ListView;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.operation.OperationFolderBrowse;
import org.opendataspace.android.operation.OperationRepoFetch;
import org.opendataspace.android.operation.OperationStatus;
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
        Account acc = TestUtil.getDefaultAccount();
        app.getDatabase().getAccounts().create(acc);
        app.getPrefs().setLastAccountId(acc);

        OperationRepoFetch fetch = new OperationRepoFetch(acc);
        fetch.setShouldConfig(false);
        OperationStatus st = fetch.execute();
        Assert.assertEquals(true, st.isOk());
        Repo repo = TestUtil.repo(app, acc, Repo.Type.GLOBAL);
        Assert.assertEquals(true, repo != null);
        OperationFolderBrowse op = new OperationFolderBrowse(acc, repo);
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
