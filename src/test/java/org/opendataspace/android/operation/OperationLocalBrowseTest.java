package org.opendataspace.android.operation;

import android.os.Environment;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.storage.Storage;
import org.opendataspace.android.test.TestRunner;
import org.robolectric.shadows.ShadowEnvironment;

import java.io.File;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class OperationLocalBrowseTest {

    @Test
    public void execute() {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        File f = Environment.getExternalStorageDirectory();
        File dir = new File(f, "test");
        boolean res = dir.mkdir();
        org.junit.Assert.assertEquals(true, res);

        OperationLocalBrowse op = new OperationLocalBrowse(null, OperationLocalBrowse.Mode.DEFAULT);
        OperationStatus st = op.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(true, op.getData().size() > 0);
        Assert.assertEquals(true, op.getTop() == null);

        op.setFolder(f);
        st = op.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(true, op.getData().size() > 0);
        Assert.assertEquals(true, op.getTop() != null);

        Assert.assertEquals(true, Storage.deleteTree(dir));
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_UNMOUNTED);
    }
}
