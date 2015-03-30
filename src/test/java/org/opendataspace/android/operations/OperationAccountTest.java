package org.opendataspace.android.operations;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.objects.Account;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class OperationAccountTest {

    private OperationAccount op;

    @Before
    public void setUp() throws Exception {
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
        op.execute();
    }

    @Test
    public void testAtomConnection() {
        op.getAccount().setUseJson(false);
        op.execute();
    }
}
