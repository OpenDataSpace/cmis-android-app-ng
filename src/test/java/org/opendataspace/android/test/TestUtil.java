package org.opendataspace.android.test;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.ui.ActivityMain;
import org.opendataspace.android.ui.FragmentBase;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowLooper;
import org.robolectric.util.ActivityController;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

public class TestUtil {

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

    public static Account getDefaultAccount() throws MalformedURLException {
        Properties p = getProperties();
        Account acc = new Account();

        acc.setUri(p.getProperty("test.acc.url"));
        acc.setLogin(p.getProperty("test.acc.login"));
        acc.setPassword(p.getProperty("test.acc.pwd"));

        return acc;
    }

    public static ActivityMain setupActivity() throws Exception {
        TestApp app = (TestApp) RuntimeEnvironment.application;
        Account acc = TestUtil.getDefaultAccount();
        app.getDatabase().getAccounts().create(acc);
        app.getPrefs().setLastAccount(acc);
        app.testStartSync();
        return Robolectric.setupActivity(ActivityMain.class);
    }

    public static ActivityMain setupFragment(FragmentBase fragment) throws Exception {
        TestApp app = (TestApp) RuntimeEnvironment.application;
        Account acc = TestUtil.getDefaultAccount();
        app.getDatabase().getAccounts().create(acc);
        app.getPrefs().setLastAccount(acc);
        app.testStartSync();
        ActivityMain ac = Robolectric.setupActivity(TestActivity.class);
        replaceFragment(ac, fragment);
        return ac;
    }

    public static ActivityMain replaceFragment(ActivityMain ac, FragmentBase fragment) throws Exception {
        FragmentTransaction t = ac.getSupportFragmentManager().beginTransaction();
        t.replace(R.id.main_view_frame, fragment, "main");
        t.commit();
        return ac;
    }

    public static ActivityMain replaceSecondaryFragment(ActivityMain ac, FragmentBase fragment) throws Exception {
        FragmentTransaction t = ac.getSupportFragmentManager().beginTransaction();
        t.replace(R.id.main_view_drawer, fragment, "secondary");
        t.commit();
        return ac;
    }

    public static void waitRunnable() throws InterruptedException {
        int cnt = 0;

        while (!ShadowLooper.getUiThreadScheduler().areAnyRunnable() && cnt < 20) {
            Thread.sleep(10);
            cnt++;
        }

        ShadowLooper.runUiThreadTasks();
    }

    public static Bundle dismisActivity(ActivityMain ac) {
        ActivityController con = ActivityController.of(Robolectric.getShadowsAdapter(), ac);
        Bundle bu = new Bundle();
        con.pause().stop().saveInstanceState(bu).destroy();
        return bu;
    }
}
