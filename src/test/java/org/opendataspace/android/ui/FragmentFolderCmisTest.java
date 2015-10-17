package org.opendataspace.android.ui;

import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.ListView;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.operation.OperationFolderBrowse;
import org.opendataspace.android.test.TestNavigation;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowView;

import java.util.ArrayList;

import static org.robolectric.Shadows.shadowOf;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class FragmentFolderCmisTest {

    @Test
    public void navigate() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        CmisSession session = TestUtil.setupSession(app, Repo.Type.GLOBAL);
        OperationFolderBrowse op =
                new OperationFolderBrowse(app.getDatabase().getAccounts().get(app.getPrefs().getLastAccountId()),
                        session.getRepo(), OperationFolderBrowse.Mode.DEFAULT);
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

    @Test
    public void createDeleteFolder() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        CmisSession session = TestUtil.setupSession(app, Repo.Type.PRIVATE);
        String name = "Test123";
        TestUtil.removeIfExists(session, name);

        OperationFolderBrowse op =
                new OperationFolderBrowse(app.getDatabase().getAccounts().get(app.getPrefs().getLastAccountId()),
                        session.getRepo(), OperationFolderBrowse.Mode.DEFAULT);
        FragmentFolderCmis fgm = new FragmentFolderCmis(op);
        ActivityMain ac = TestUtil.setupFragment(fgm);
        TestUtil.waitRunnable();

        //noinspection ConstantConditions
        ListView lv = (ListView) fgm.getView().findViewById(android.R.id.list);
        int idx = createFolder(app, name, fgm, lv);
        lv.getOnItemLongClickListener().onItemLongClick(lv, lv.getChildAt(idx), idx, lv.getItemIdAtPosition(idx));
        fgm.onOptionsItemSelected(new RoboMenuItem(R.id.menu_folder_delete));
        TestUtil.waitRunnable();
        Assert.assertEquals(true, Shadows.shadowOf(lv).findIndexOfItemContainingText(name) == -1);

        TestUtil.dismisActivity(ac);
    }

    @Test
    public void copy() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        CmisSession session = TestUtil.setupSession(app, Repo.Type.PRIVATE);
        String name1 = "Test123";
        String name2 = "456Test";
        TestUtil.removeIfExists(session, name1);
        TestUtil.removeIfExists(session, name2);

        OperationFolderBrowse op =
                new OperationFolderBrowse(app.getDatabase().getAccounts().get(app.getPrefs().getLastAccountId()),
                        session.getRepo(), OperationFolderBrowse.Mode.DEFAULT);
        FragmentFolderCmis fgm = new FragmentFolderCmis(op);
        ActivityMain ac = TestUtil.setupFragment(fgm);
        TestUtil.waitRunnable();

        //noinspection ConstantConditions
        ListView lv = (ListView) fgm.getView().findViewById(android.R.id.list);
        int idx = createFolder(app, name1, fgm, lv);

        lv.getOnItemLongClickListener().onItemLongClick(lv, lv.getChildAt(idx), idx, lv.getItemIdAtPosition(idx));
        fgm.onOptionsItemSelected(new RoboMenuItem(R.id.menu_folder_copy));
        Assert.assertEquals(1, fgm.getCopyMove().getNodes().size());
        Assert.assertEquals(name1, fgm.getCopyMove().getNodes().get(0).getName());
        Assert.assertEquals(true, fgm.getCopyMove().willCopy());
        Assert.assertEquals(false, fgm.getCopyMove().canPaste(op.getFolder()));

        lv.getOnItemLongClickListener().onItemLongClick(lv, lv.getChildAt(idx), idx, lv.getItemIdAtPosition(idx));
        fgm.onOptionsItemSelected(new RoboMenuItem(R.id.menu_folder_cut));
        Assert.assertEquals(1, fgm.getCopyMove().getNodes().size());
        Assert.assertEquals(name1, fgm.getCopyMove().getNodes().get(0).getName());
        Assert.assertEquals(false, fgm.getCopyMove().willCopy());
        Assert.assertEquals(false, fgm.getCopyMove().canPaste(op.getFolder()));

        createFolder(app, name2, fgm, lv);
        Shadows.shadowOf(lv).clickFirstItemContainingText(name2);
        TestUtil.waitRunnable();
        Assert.assertEquals(true, fgm.getCopyMove().canPaste(op.getFolder()));
        fgm.onOptionsItemSelected(new RoboMenuItem(R.id.menu_folder_paste));
        TestUtil.waitRunnable();
        Assert.assertEquals(true, Shadows.shadowOf(lv).findIndexOfItemContainingText(name1) != -1);

        TestUtil.dismisActivity(ac);
        TestUtil.removeIfExists(session, name1);
        TestUtil.removeIfExists(session, name2);
    }

    private int createFolder(OdsApp app, String name, FragmentFolderCmis fgm, ListView lv) throws InterruptedException {
        Assert.assertEquals(true, Shadows.shadowOf(lv).findIndexOfItemContainingText(name) == -1);
        fgm.onOptionsItemSelected(new RoboMenuItem(R.id.menu_folder_create));
        ShadowAlertDialog dialog = shadowOf(app).getLatestAlertDialog();
        AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
        EditText et = (EditText) dialog.getView().findViewById(R.id.edit_dialog_name);
        et.setText(name);
        ShadowView.clickOn(alert.getButton(AlertDialog.BUTTON_POSITIVE));
        TestUtil.waitRunnable();
        int idx = Shadows.shadowOf(lv).findIndexOfItemContainingText(name);
        Assert.assertEquals(true, idx != -1);
        return idx;
    }

    @Test
    public void modeUpload() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        CmisSession session = TestUtil.setupSession(app, Repo.Type.GLOBAL);
        OperationFolderBrowse op =
                new OperationFolderBrowse(app.getDatabase().getAccounts().get(app.getPrefs().getLastAccountId()),
                        session.getRepo(), OperationFolderBrowse.Mode.SEL_FOLDER);
        op.setContext(new ArrayList<>());
        FragmentFolderCmis fgm = new FragmentFolderCmis(op);
        ActivityMain ac = TestUtil.setupFragment(fgm);
        TestUtil.waitRunnable();

        //noinspection ConstantConditions
        ListView lv = (ListView) fgm.getView().findViewById(android.R.id.list);
        int idx = Shadows.shadowOf(lv).findIndexOfItemContainingText("GDS");
        Assert.assertEquals(true, Shadows.shadowOf(lv).findIndexOfItemContainingText("GDS") != -1);
        Shadows.shadowOf(lv).clickFirstItemContainingText("GDS");
        fgm.onOptionsItemSelected(new RoboMenuItem(R.id.menu_folder_apply));
        TestNavigation tn = (TestNavigation) fgm.getNavigation();
        Assert.assertEquals(1, tn.getBackCnt());
        TestUtil.waitRunnable();

        TestUtil.dismisActivity(ac);
    }
}
