package org.opendataspace.android.view;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.AccountAdapter;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(TestRunner.class)
public class ViewManagerTest {

    @Test
    public void checkSync() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        Account acc = TestUtil.getDefaultAccount();
        app.getDatabase().getAccounts().create(acc);
        Repo r1 = new Repo(null, acc);
        app.getDatabase().getRepos().create(r1);
        Repo r2 = new Repo(null, acc);
        app.getDatabase().getRepos().create(r2);
        app.getViewManager().setCurrentAccount(acc);
        TestUtil.waitRunnable();
        Assert.assertEquals(1, app.getViewManager().getAccounts().getCount());
        Assert.assertEquals(true, app.getViewManager().getRepos().getCount() > 0);
        Assert.assertEquals(-1, app.getViewManager().getRepos().getObjects().indexOf(r1));
        Assert.assertEquals(-1, app.getViewManager().getRepos().getObjects().indexOf(r2));
    }

    @Test
    public void checkNotify() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        AtomicBoolean invalidated = new AtomicBoolean(false);

        AccountAdapter adp = new AccountAdapter(app.getViewManager().getAccounts(), app.getApplicationContext()) {
            @Override
            public void invalidate() {
                super.invalidate();
                invalidated.set(true);
            }
        };

        Account acc = TestUtil.getDefaultAccount();
        app.getDatabase().getAccounts().create(acc);
        app.getDatabase().getRepos().create(new Repo(null, acc));
        TestUtil.waitRunnable();
        Assert.assertEquals(true, invalidated.get());
    }

    @Test
    public void checkTransaction() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        final Account acc = TestUtil.getDefaultAccount();
        app.getDatabase().getAccounts().create(acc);
        app.getViewManager().getRepos().setAccount(acc);
        AtomicInteger invalidated = new AtomicInteger(0);

        AccountAdapter adp = new AccountAdapter(app.getViewManager().getAccounts(), app.getApplicationContext()) {
            @Override
            public void invalidate() {
                super.invalidate();
                invalidated.incrementAndGet();
            }
        };

        app.getDatabase().transact(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                app.getDatabase().getRepos().create(new Repo(null, acc));
                app.getDatabase().getAccounts().create(TestUtil.getDefaultAccount());
                app.getDatabase().getAccounts().create(TestUtil.getDefaultAccount());

                app.getDatabase().transact(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        app.getDatabase().getAccounts().create(TestUtil.getDefaultAccount());
                        app.getDatabase().getAccounts().create(TestUtil.getDefaultAccount());
                        return null;
                    }
                });

                return null;
            }
        });

        app.getDatabase().getRepos().create(new Repo(null, acc));

        TestUtil.waitRunnable();
        Assert.assertEquals(1, invalidated.get());
        Assert.assertEquals(5, app.getViewManager().getAccounts().getCount());
        Assert.assertEquals(2, app.getViewManager().getRepos().getCount());
    }

    @Test
    public void checkInsertUpdate() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        final Account acc = TestUtil.getDefaultAccount();
        app.getDatabase().getAccounts().create(acc);
        app.getDatabase().getAccounts().createOrUpdate(acc);

        Assert.assertEquals(1, app.getViewManager().getAccounts().getCount());
    }
}
