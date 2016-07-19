package org.opendataspace.android.ui;

import org.junit.Assert;
import org.junit.Ignore;
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
@Ignore
public class FragmentRepoListTest {

    @Test
    public void checkDefaults() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        CmisSession session = TestUtil.setupSession(app, Repo.Type.PRIVATE);
        app.getViewManager().getRepos().setAccount(session.getAccount());
        app.getViewManager().getRepos().sync(app.getDatabase().getRepos());
        OperationFolderBrowse op =
                new OperationFolderBrowse(session.getAccount(), null, OperationFolderBrowse.Mode.SEL_FILES);
        Assert.assertEquals(null, op.getRepo());
        FragmentRepoList fgm = new FragmentRepoList(op);
        ActivityMain ac = TestUtil.setupFragment(fgm);
        Assert.assertEquals(app.getViewManager().getRepos().getCount(), fgm.getList().getCount());
        Shadows.shadowOf(fgm.getList())
                .clickFirstItemContainingText(session.getRepo().getDisplayName(fgm.getActivity()));
        Assert.assertEquals(session.getRepo(), op.getRepo());
        TestUtil.dismisActivity(ac);
    }
}
