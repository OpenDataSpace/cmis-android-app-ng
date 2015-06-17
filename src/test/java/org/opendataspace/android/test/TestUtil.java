package org.opendataspace.android.test;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.j256.ormlite.dao.CloseableIterator;
import junit.framework.Assert;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.object.ObjectBase;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.operation.OperationRepoFetch;
import org.opendataspace.android.operation.OperationStatus;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TestUtil {

    private static final Properties props = new Properties();

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

        if (app.getPrefs().getLastAccountId() == ObjectBase.INVALID_ID) {
            Account acc = TestUtil.getDefaultAccount();
            app.getDatabase().getAccounts().create(acc);
            app.getPrefs().setLastAccountId(acc);
        }

        return Robolectric.setupActivity(ActivityMain.class);
    }

    public static ActivityMain setupFragment(FragmentBase fragment) throws Exception {
        TestApp app = (TestApp) RuntimeEnvironment.application;

        if (app.getPrefs().getLastAccountId() == ObjectBase.INVALID_ID) {
            Account acc = TestUtil.getDefaultAccount();
            app.getDatabase().getAccounts().create(acc);
            app.getPrefs().setLastAccountId(acc);
        }

        ActivityMain ac = Robolectric.setupActivity(TestActivity.class);
        replaceFragment(ac, fragment);
        return ac;
    }

    public static void replaceFragment(ActivityMain ac, FragmentBase fragment) {
        FragmentTransaction t = ac.getSupportFragmentManager().beginTransaction();
        t.replace(R.id.main_view_frame, fragment, "main");
        t.commit();
    }

    public static void replaceSecondaryFragment(ActivityMain ac, FragmentBase fragment) {
        FragmentTransaction t = ac.getSupportFragmentManager().beginTransaction();
        t.replace(R.id.main_view_drawer, fragment, "secondary");
        t.commit();
    }

    public static void waitRunnable() throws InterruptedException {
        int cnt = 0;

        do {
            Thread.sleep(100);

            if (++cnt > 200) {
                throw new InterruptedException();
            }
        } while (OdsApp.get().getPool().hasTasks());

        ShadowLooper.runUiThreadTasks();
    }

    public static Bundle dismisActivity(ActivityMain ac) throws InterruptedException {
        ActivityController con = ActivityController.of(Robolectric.getShadowsAdapter(), ac);
        Bundle bu = new Bundle();
        con.pause().stop().saveInstanceState(bu).destroy();
        waitRunnable();
        return bu;
    }

    public static <T extends ObjectBase> List<T> allOf(CloseableIterator<T> it) throws SQLException {
        List<T> ls = new ArrayList<>();

        try {
            while (it.hasNext()) {
                ls.add(it.nextThrow());
            }
        } finally {
            it.closeQuietly();
        }

        return ls;
    }

    public static CmisSession setupSession(OdsApp app, Repo.Type type) throws Exception {
        Account acc = TestUtil.getDefaultAccount();
        app.getDatabase().getAccounts().create(acc);
        app.getPrefs().setLastAccountId(acc);

        OperationRepoFetch op = new OperationRepoFetch(acc);
        op.withoutConfig();
        OperationStatus st = op.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(true, app.getDatabase().getRepos().countOf() > 0);

        for (Repo cur : allOf(app.getDatabase().getRepos().forAccount(acc))) {
            if (cur.getType() == type) {
                return new CmisSession(acc, cur);
            }
        }

        throw new IllegalArgumentException();
    }

    public static void removeIfExists(CmisSession session, String name) {
        CmisObject obj = session.getObjectByPath(name);

        if (obj != null) {
            session.delete(new Node(obj, session.getRepo()));
        }
    }

    public static boolean hasChild(OdsApp app, Repo repo, Node parent, String name, Node.Type type) throws
            java.sql.SQLException {
        for (Node cur : TestUtil.allOf(app.getDatabase().getNodes()
                .forParent(repo, parent != null ? parent.getId() : ObjectBase.INVALID_ID))) {
            if (cur.getType() == type && name.equals(cur.getName())) {
                return true;
            }
        }

        return false;
    }
}
