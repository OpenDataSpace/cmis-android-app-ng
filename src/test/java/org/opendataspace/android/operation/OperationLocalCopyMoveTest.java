package org.opendataspace.android.operation;

import android.os.Environment;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.storage.FileInfo;
import org.opendataspace.android.storage.Storage;
import org.opendataspace.android.test.TestRunner;
import org.robolectric.shadows.ShadowEnvironment;

import java.io.File;
import java.sql.SQLException;
import java.util.Collections;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class OperationLocalCopyMoveTest {

    @Test
    public void execute() throws SQLException {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        File f = Environment.getExternalStorageDirectory();
        File dir1 = new File(f, "test1");
        File dir2 = new File(f, "test2");
        File dir3 = new File(f, "test3");
        Assert.assertEquals(true, dir1.mkdir());
        Assert.assertEquals(true, dir2.mkdir());
        Assert.assertEquals(true, dir3.mkdir());

        OperationLocalCopyMove op = new OperationLocalCopyMove();
        op.setContext(Collections.singletonList(new FileInfo(dir1, null)), true);
        op.setTarget(dir3);
        OperationResult st = op.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(true, dir1.exists());
        Assert.assertEquals(true, new File(dir3, dir1.getName()).exists());

        op.setContext(Collections.singletonList(new FileInfo(dir2, null)), false);
        st = op.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(false, dir2.exists());
        Assert.assertEquals(true, new File(dir3, dir2.getName()).exists());

        Assert.assertEquals(true, Storage.deleteTree(dir1));
        Assert.assertEquals(true, Storage.deleteTree(dir3));
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_UNMOUNTED);
    }
}
