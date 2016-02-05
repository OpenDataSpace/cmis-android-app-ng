package org.opendataspace.android.operation;

import android.os.Environment;

import junit.framework.Assert;
import org.apache.chemistry.opencmis.client.api.CmisObject;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class OperationNodeDownloadTest {

    @Test
    public void execute() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        CmisSession session = TestUtil.setupSession(app, Repo.Type.PRIVATE);

        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED);
        String name = "test.dat";
        TestUtil.removeIfExists(session, name);

        byte[] local = new byte[] {0, 1, 2, 3};
        File f = new File(Environment.getExternalStorageDirectory(), name);
        Assert.assertEquals(true, f.createNewFile());
        FileOutputStream fs = new FileOutputStream(f);
        fs.write(local);
        fs.close();

        CmisObject cmis =
                session.createDocument(new Node(session.getRoot(null), session.getRepo()), name, new FileInfo(f, null),
                        null);
        Assert.assertEquals(true, f.delete());

        Node node = new Node(cmis, session.getRepo());
        OperationNodeDownload op = new OperationNodeDownload(session, Environment.getExternalStorageDirectory(),
                Collections.singletonList(node));

        OperationResult st = op.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(true, f.exists());
        Assert.assertEquals(local.length, f.length());

        byte[] content = new byte[local.length];
        InputStream is = new FileInputStream(f);
        Assert.assertEquals(local.length, is.read(content));
        is.close();
        Assert.assertEquals(true, Arrays.equals(local, content));

        session.delete(node, null);
        Assert.assertEquals(true, f.delete());
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_UNMOUNTED);
    }
}
