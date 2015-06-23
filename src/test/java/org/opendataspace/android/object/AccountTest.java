package org.opendataspace.android.object;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.test.TestRunner;

import java.io.IOException;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class AccountTest {

    @Test
    public void checkUri() throws IOException {
        Account acc = new Account();
        String domain = "demo.dataspace.cc";
        acc.setUri("https://" + domain);
        Assert.assertEquals("https", acc.getUri().getScheme());
        Assert.assertEquals(domain, acc.getUri().getAuthority());
        acc.setUseJson(false);
        Assert.assertEquals("/cmis/atom", acc.getUri().getPath());
        acc.setUseJson(true);
        Assert.assertEquals("/cmis/browser", acc.getUri().getPath());
        Assert.assertEquals(domain, acc.getDisplayUri());

        String path = "/test/path";
        acc.setUri("http://" + domain + path);
        Assert.assertEquals("http", acc.getUri().getScheme());
        Assert.assertEquals(domain, acc.getUri().getAuthority());
        acc.setUseJson(false);
        Assert.assertEquals(path, acc.getUri().getPath());
        acc.setUseJson(true);
        Assert.assertEquals(path, acc.getUri().getPath());
        Assert.assertEquals("http://" + domain + path, acc.getDisplayUri());
    }
}
