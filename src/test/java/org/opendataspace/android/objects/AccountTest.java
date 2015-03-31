package org.opendataspace.android.objects;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.test.OdsRunner;

import java.io.IOException;

@RunWith(OdsRunner.class)
public class AccountTest {

    @Test
    public void testUri() throws IOException {
        Account acc = new Account();
        String test = "https://demo.dataspace.cc";
        acc.setUri(test);
        acc.setUseJson(false);
        Assert.assertEquals(acc.getUri().getPath(), "/cmis/atom");
        acc.setUseJson(true);
        Assert.assertEquals(acc.getUri().getPath(), "/cmis/browser");
        Assert.assertEquals(acc.getDisplayUri(), test);
    }
}
