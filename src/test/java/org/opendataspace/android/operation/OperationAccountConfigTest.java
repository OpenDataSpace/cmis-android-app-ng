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

@RunWith(TestRunner.class)
public class OperationAccountConfigTest {

    @Test
    public void execute() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        Account acc = TestUtil.getDefaultAccount();
        app.getDatabase().getAccounts().create(acc);

        OperationRepoSync sync = new OperationRepoSync(acc);
        OperationStatus st = sync.execute();
        Assert.assertEquals(true, st.isOk());

        Drawable d1 = Storage.getBrandingDrawable(app.getApplicationContext(), OperationAccountConfig.BRAND_ICON, acc);
        Drawable d2 = Storage.getBrandingDrawable(app.getApplicationContext(), OperationAccountConfig.BRAND_LARGE, acc);
        Assert.assertEquals(true, d1 == null);
        Assert.assertEquals(true, d2 == null);

        OperationAccountConfig op = new OperationAccountConfig(acc);
        st = op.execute();
        d1 = Storage.getBrandingDrawable(app.getApplicationContext(), OperationAccountConfig.BRAND_ICON, acc);
        d2 = Storage.getBrandingDrawable(app.getApplicationContext(), OperationAccountConfig.BRAND_LARGE, acc);

        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(true, d1 != null);
        Assert.assertEquals(true, d2 != null);
    }
}
