package org.opendataspace.android.objects;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class AccountTest {

    @Test
    public void testUri() throws IOException {
        Account acc = new Account();
        String test = "https://demo.dataspace.cc";
        acc.setUri(test);
        acc.setUseJson(false);
        Assert.assertEquals(acc.getUri().getPath(), "/service/cmis");
        acc.setUseJson(true);
        Assert.assertEquals(acc.getUri().getPath(), "/cmis/browser");
        Assert.assertEquals(acc.getDisplayUri(), test);
    }
}
