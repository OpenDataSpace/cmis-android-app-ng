package org.opendataspace.android.operations;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.objects.Account;
import org.opendataspace.android.test.OdsAppTest;
import org.opendataspace.android.test.OdsBaseTest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", application = OdsAppTest.class, emulateSdk = OdsBaseTest.SDK)
public class OperationAccountTest extends OdsBaseTest {

    private OperationAccount op;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        Properties p = new Properties();
        p.load(new FileReader(new File("build.properties")));
        Account acc = new Account();
        acc.setUri(p.getProperty("test.acc.url"));
        acc.setLogin(p.getProperty("test.acc.login"));
        acc.setPassword(p.getProperty("test.acc.pwd"));
        op = new OperationAccount(acc);
    }

    @Test
    public void testJsonConnection() {
        op.getAccount().setUseJson(true);
        OperationStatus st = op.execute();
        Assert.assertTrue(st.isOk());
    }

    @Test
    public void testAtomConnection() {
        op.getAccount().setUseJson(false);
        OperationStatus st = op.execute();
        Assert.assertTrue(st.isOk());
    }
}
