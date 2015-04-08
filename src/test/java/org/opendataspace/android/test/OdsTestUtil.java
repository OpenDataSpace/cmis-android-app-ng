package org.opendataspace.android.test;

import org.opendataspace.android.objects.Account;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class OdsTestUtil {

    private static Properties props = new Properties();

    static {
        File f = new File("build.properties");

        try {
            if (f.exists()) {
                props.load(new FileReader(new File("build.properties")));
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Properties getProperties() {
        return props;
    }

    public static Account getDefaultAccount() throws Exception {
        Properties p = getProperties();
        Account acc = new Account();

        acc.setUri(p.getProperty("test.acc.url"));
        acc.setLogin(p.getProperty("test.acc.login"));
        acc.setPassword(p.getProperty("test.acc.pwd"));

        return acc;
    }
}
