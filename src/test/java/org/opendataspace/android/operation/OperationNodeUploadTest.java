package org.opendataspace.android.operation;

import android.os.Environment;
import junit.framework.Assert;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.storage.FileInfo;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowEnvironment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class OperationNodeUploadTest {

    @Test
    public void execute() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        CmisSession session = TestUtil.setupSession(app, Repo.Type.PRIVATE);

        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        String name = "test.dat";
        TestUtil.removeIfExists(session, name);

        byte[] local = new byte[]{0, 1, 2, 3};
        File f = new File(Environment.getExternalStorageDirectory(), name);
        Assert.assertEquals(true, f.createNewFile());
        FileOutputStream fs = new FileOutputStream(f);
        fs.write(local);
        fs.close();

        OperationNodeUpload op = new OperationNodeUpload(session, new Node(session.getRoot(), session.getRepo()),
                Collections.singletonList(new FileInfo(f, null)));

        OperationStatus st = op.execute();
        Assert.assertEquals(true, st.isOk());
        CmisObject cmis = session.getObjectByPath(name);
        Assert.assertEquals(true, cmis != null);
        Node node = new Node(cmis, session.getRepo());
        Assert.assertEquals(local.length, node.getSize());

        byte[] content = new byte[local.length];
        ContentStream cs = session.getStream(node);
        //Assert.assertEquals(local.length, cs.getLength());
        InputStream is = cs.getStream();
        Assert.assertEquals(local.length, is.read(content));
        is.close();
        Assert.assertEquals(true, Arrays.equals(local, content));

        session.delete(node);
        Assert.assertEquals(true, f.delete());
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_UNMOUNTED);
    }
}
