package org.opendataspace.android.object;

import android.text.TextUtils;
import android.util.Log;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class MimeTypeTest {

    @Test
    public void verify() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        List<MimeType> ls = TestUtil.allOf(app.getDatabase().getMime().iterate());
        Assert.assertEquals(false, ls.isEmpty());

        for (MimeType cur : ls) {
            Assert.assertEquals(cur.getExtenstion(), false, cur.getIcon(app.getApplicationContext()) == 0);
            Assert.assertEquals(cur.getExtenstion(), false,
                    TextUtils.isEmpty(cur.getDescription(app.getApplicationContext())));
        }
    }
}
