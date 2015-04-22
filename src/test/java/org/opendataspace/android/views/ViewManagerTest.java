package org.opendataspace.android.views;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.objects.Account;
import org.opendataspace.android.objects.AccountAdapter;
import org.opendataspace.android.objects.Repo;
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
        app.getPrefs().setLastAccount(acc);
        app.getDatabase().getRepos().create(new Repo(null, acc));
        app.getDatabase().getRepos().create(new Repo(null, acc));
        app.getViewManager().sync(app.getDatabase());
        TestUtil.waitRunnable();
        Assert.assertEquals(1, app.getViewManager().getAccounts().getObjects().size());
        Assert.assertEquals(2, app.getViewManager().getRepos().getObjects().size());
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
        AtomicInteger invalidated = new AtomicInteger(0);

        AccountAdapter adp = new AccountAdapter(app.getViewManager().getAccounts(), app.getApplicationContext()) {
            @Override
            public void invalidate() {
                super.invalidate();
                invalidated.incrementAndGet();
            }
        };

        final Account acc = TestUtil.getDefaultAccount();

        app.getDatabase().transact(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                app.getDatabase().getAccounts().create(acc);
                app.getDatabase().getRepos().create(new Repo(null, acc));
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
        Assert.assertEquals(4, app.getViewManager().getAccounts().getObjects().size());
        Assert.assertEquals(2, app.getViewManager().getRepos().getObjects().size());
    }
}
