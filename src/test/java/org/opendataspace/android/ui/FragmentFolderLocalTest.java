package org.opendataspace.android.ui;

import android.app.AlertDialog;
import android.os.Environment;
import android.widget.EditText;
import android.widget.ListView;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.operation.OperationLocalBrowse;
import org.opendataspace.android.storage.Storage;
import org.opendataspace.android.test.TestNavigation;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowEnvironment;
import org.robolectric.shadows.ShadowView;

import java.io.File;
import java.util.ArrayList;

import static org.robolectric.Shadows.shadowOf;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class FragmentFolderLocalTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void navigate() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        File f = Environment.getExternalStorageDirectory();
        File dir = new File(f, "test");
        boolean res = dir.mkdir();
        Assert.assertEquals(true, res);
        Assert.assertEquals(true, dir.exists());

        OperationLocalBrowse op = new OperationLocalBrowse(null, OperationLocalBrowse.Mode.DEFAULT);
        FragmentFolderLocal fgm = new FragmentFolderLocal(op);
        ActivityMain ac = TestUtil.setupFragment(fgm);
        TestUtil.waitRunnable();
        ListView lv = (ListView) fgm.getView().findViewById(android.R.id.list);
        Assert.assertEquals(true, lv.getCount() != 0);

        Shadows.shadowOf(lv).clickFirstItemContainingText(ac.getString(R.string.folder_root));
        TestUtil.waitRunnable();
        Assert.assertEquals(true, lv.getCount() != 0);

        Shadows.shadowOf(lv).clickFirstItemContainingText(dir.getName());
        TestUtil.waitRunnable();
        Assert.assertEquals(true, lv.getCount() == 0);

        res = fgm.backPressed();
        TestUtil.waitRunnable();
        Assert.assertEquals(true, res);
        Assert.assertEquals(true, lv.getCount() != 0);
        res = fgm.backPressed();
        TestUtil.waitRunnable();
        Assert.assertEquals(true, res);
        Assert.assertEquals(true, lv.getCount() != 0);
        res = fgm.backPressed();
        Assert.assertEquals(false, res);

        TestUtil.dismisActivity(ac);
        Assert.assertEquals(true, Storage.deleteTree(dir));
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_UNMOUNTED);
    }


    @Test
    public void createDeleteFolder() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        String name = "Test123";

        OperationLocalBrowse op = new OperationLocalBrowse(null, OperationLocalBrowse.Mode.DEFAULT);
        FragmentFolderLocal fgm = new FragmentFolderLocal(op);
        ActivityMain ac = TestUtil.setupFragment(fgm);
        TestUtil.waitRunnable();

        //noinspection ConstantConditions
        ListView lv = (ListView) fgm.getView().findViewById(android.R.id.list);
        Shadows.shadowOf(lv).clickFirstItemContainingText(ac.getString(R.string.folder_root));
        TestUtil.waitRunnable();

        int idx = createFolder(app, name, fgm, lv);
        lv.getOnItemLongClickListener().onItemLongClick(lv, lv.getChildAt(idx), idx, lv.getItemIdAtPosition(idx));
        fgm.onOptionsItemSelected(new RoboMenuItem(R.id.menu_folder_delete));
        TestUtil.waitRunnable();
        Assert.assertEquals(true, Shadows.shadowOf(lv).findIndexOfItemContainingText(name) == -1);

        TestUtil.dismisActivity(ac);
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_UNMOUNTED);
    }

    @Test
    public void copy() throws Exception {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        String name1 = "Test123";
        String name2 = "456Test";

        OperationLocalBrowse op = new OperationLocalBrowse(null, OperationLocalBrowse.Mode.DEFAULT);
        FragmentFolderLocal fgm = new FragmentFolderLocal(op);
        ActivityMain ac = TestUtil.setupFragment(fgm);
        TestUtil.waitRunnable();

        //noinspection ConstantConditions
        ListView lv = (ListView) fgm.getView().findViewById(android.R.id.list);
        Shadows.shadowOf(lv).clickFirstItemContainingText(ac.getString(R.string.folder_root));
        TestUtil.waitRunnable();
        int idx = createFolder(app, name1, fgm, lv);

        lv.getOnItemLongClickListener().onItemLongClick(lv, lv.getChildAt(idx), idx, lv.getItemIdAtPosition(idx));
        fgm.onOptionsItemSelected(new RoboMenuItem(R.id.menu_folder_copy));
        Assert.assertEquals(1, fgm.getCopyMove().getNodes().size());
        Assert.assertEquals(name1, fgm.getCopyMove().getNodes().get(0).getName(app.getApplicationContext()));
        Assert.assertEquals(true, fgm.getCopyMove().willCopy());

        lv.getOnItemLongClickListener().onItemLongClick(lv, lv.getChildAt(idx), idx, lv.getItemIdAtPosition(idx));
        fgm.onOptionsItemSelected(new RoboMenuItem(R.id.menu_folder_cut));
        Assert.assertEquals(1, fgm.getCopyMove().getNodes().size());
        Assert.assertEquals(name1, fgm.getCopyMove().getNodes().get(0).getName(app.getApplicationContext()));
        Assert.assertEquals(false, fgm.getCopyMove().willCopy());

        createFolder(app, name2, fgm, lv);
        Shadows.shadowOf(lv).clickFirstItemContainingText(name2);
        TestUtil.waitRunnable();
        fgm.onOptionsItemSelected(new RoboMenuItem(R.id.menu_folder_paste));
        TestUtil.waitRunnable();
        TestUtil.waitRunnable();
        Assert.assertEquals(true, Shadows.shadowOf(lv).findIndexOfItemContainingText(name1) != -1);

        TestUtil.dismisActivity(ac);
        Assert.assertEquals(true, Storage.deleteTree(new File(Environment.getExternalStorageDirectory(), name2)));
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_UNMOUNTED);
    }

    private int createFolder(OdsApp app, String name, FragmentFolderLocal fgm, ListView lv) throws
            InterruptedException {
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
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        File f = Environment.getExternalStorageDirectory();
        String name = "test";
        File dir = new File(f, name);
        boolean res = dir.mkdir();
        Assert.assertEquals(true, res);
        Assert.assertEquals(true, dir.exists());

        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        OperationLocalBrowse op = new OperationLocalBrowse(null, OperationLocalBrowse.Mode.SEL_FOLDER);
        op.setContext(new ArrayList<>());
        FragmentFolderLocal fgm = new FragmentFolderLocal(op);
        ActivityMain ac = TestUtil.setupFragment(fgm);
        TestUtil.waitRunnable();

        //noinspection ConstantConditions
        ListView lv = (ListView) fgm.getView().findViewById(android.R.id.list);
        Shadows.shadowOf(lv).clickFirstItemContainingText(ac.getString(R.string.folder_root));
        TestUtil.waitRunnable();

        int idx = Shadows.shadowOf(lv).findIndexOfItemContainingText(name);
        Assert.assertEquals(true, Shadows.shadowOf(lv).findIndexOfItemContainingText(name) != -1);
        Shadows.shadowOf(lv).clickFirstItemContainingText(name);
        fgm.onOptionsItemSelected(new RoboMenuItem(R.id.menu_folder_apply));
        TestNavigation tn = (TestNavigation) fgm.getNavigation();
        Assert.assertEquals(1, tn.getBackCnt());
        TestUtil.waitRunnable();

        TestUtil.dismisActivity(ac);
        Assert.assertEquals(true, Storage.deleteTree(dir));
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_UNMOUNTED);
    }
}
