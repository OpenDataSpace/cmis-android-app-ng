package org.opendataspace.android.ui;

import android.os.Environment;
import android.widget.ListView;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.operation.OperationFolderLocal;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowEnvironment;

import java.io.File;

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

        OperationFolderLocal op = new OperationFolderLocal();
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
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_UNMOUNTED);
    }
}
