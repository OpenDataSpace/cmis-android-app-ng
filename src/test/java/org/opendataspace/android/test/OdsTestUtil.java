package org.opendataspace.android.test;

import org.opendataspace.android.objects.Account;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class OdsTestUtil {

    public static Account getDefaultAccount() throws Exception {
        Properties p = new Properties();
        p.load(new FileReader(new File("build.properties")));

        Account acc = new Account();
        acc.setUri(p.getProperty("test.acc.url"));
        acc.setLogin(p.getProperty("test.acc.login"));
        acc.setPassword(p.getProperty("test.acc.pwd"));

        return acc;
    }
}
