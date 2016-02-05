package org.opendataspace.android.operation;

import android.graphics.drawable.Drawable;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.storage.Storage;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;

import java.io.File;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class OperationAccountConfigTest {

    @Test
    public void execute() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        Account account = TestUtil.getDefaultAccount();
        app.getDatabase().getAccounts().create(account);
        cleanup(app, account);

        Drawable d1 =
                Storage.getBrandingDrawable(app.getApplicationContext(), account, OperationAccountConfig.BRAND_ICON);
        Drawable d2 =
                Storage.getBrandingDrawable(app.getApplicationContext(), account, OperationAccountConfig.BRAND_LARGE);
        Assert.assertEquals(true, d1 == null);
        Assert.assertEquals(true, d2 == null);

        OperationRepoFetch sync = new OperationRepoFetch(account);
        OperationResult st = sync.execute();
        Assert.assertEquals(true, st.isOk());
        TestUtil.waitRunnable();

        d1 = Storage.getBrandingDrawable(app.getApplicationContext(), account, OperationAccountConfig.BRAND_ICON);
        d2 = Storage.getBrandingDrawable(app.getApplicationContext(), account, OperationAccountConfig.BRAND_LARGE);
        Assert.assertEquals(true, d1 != null);
        Assert.assertEquals(true, d2 != null);

        cleanup(app, account);
    }

    private void cleanup(OdsApp app, Account account) {
        File folder = Storage.getLocalFolder(app.getApplicationContext(), account, null, Storage.CATEGORY_CONFIG);
        File f1 = new File(folder, OperationAccountConfig.BRAND_ICON);
        File f2 = new File(folder, OperationAccountConfig.BRAND_LARGE);

        Assert.assertEquals(true, !f1.exists() || f1.delete());
        Assert.assertEquals(true, !f2.exists() || f2.delete());
    }
}
