package org.opendataspace.android.operation;

import android.os.Environment;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.test.TestRunner;
import org.robolectric.shadows.ShadowEnvironment;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class OperationFolderLocalTest {

    @Test
    public void execute() {
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        OperationFolderLocal op = new OperationFolderLocal();
        OperationStatus st = op.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(true, op.getData().size() > 0);
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_UNMOUNTED);
    }
}
