package org.opendataspace.android.views;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.objects.AccountAdapter;
import org.opendataspace.android.test.RunnerSimple;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(RunnerSimple.class)
public class ViewManagerTest {

    @Test
    public void checkSync() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        app.getDatabase().getAccounts().create(TestUtil.getDefaultAccount());
        app.getViewManager().sync(app.getDatabase());
        TestUtil.waitRunnable();
        Assert.assertEquals(1, app.getViewManager().getAccounts().getObjects().size());
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

        app.getDatabase().getAccounts().create(TestUtil.getDefaultAccount());
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

        app.getDatabase().transact(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
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

        TestUtil.waitRunnable();
        Assert.assertEquals(1, invalidated.get());
        Assert.assertEquals(4, app.getViewManager().getAccounts().getObjects().size());
    }
}
