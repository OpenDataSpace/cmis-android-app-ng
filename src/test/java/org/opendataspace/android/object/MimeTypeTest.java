package org.opendataspace.android.object;

import android.text.TextUtils;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;

import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class MimeTypeTest {

    @Test
    public void verify() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;

        //noinspection Convert2Lambda
        app.getDatabase().transact(new Callable<Object>() {
            public Object call() throws Exception {
                app.getDatabase().getMime().createDefaults();
                return null;
            }
        });

        List<MimeType> ls = TestUtil.allOf(app.getDatabase().getMime().iterate());
        Assert.assertEquals(false, ls.isEmpty());

        for (MimeType cur : ls) {
            Assert.assertEquals(cur.getExtenstion(), false, cur.getIcon(app.getApplicationContext()) == 0);
            Assert.assertEquals(cur.getExtenstion(), false,
                    TextUtils.isEmpty(cur.getDescription(app.getApplicationContext())));
        }
    }
}
