package org.opendataspace.android.operations;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.objects.Account;
import org.opendataspace.android.test.OdsRunner;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

@RunWith(OdsRunner.class)
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
    public void connectJson() {
        op.getAccount().setUseJson(true);
        OperationStatus st = op.execute();
        Assert.assertTrue(st.isOk());
    }

    @Test
    public void connectAtom() {
        op.getAccount().setUseJson(false);
        OperationStatus st = op.execute();
        Assert.assertTrue(st.isOk());
    }
}
