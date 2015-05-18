package org.opendataspace.android.operation;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class OperationAccountSelectTest {

    @Test
    public void execute() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        Account acc = TestUtil.getDefaultAccount();
        app.getDatabase().getAccounts().create(acc);
        app.getPrefs().setLastAccountId(acc);

        OperationAccountSelect op = new OperationAccountSelect(null);
        OperationStatus st = op.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(acc.getId(), app.getPrefs().getLastAccountId());

        TestUtil.waitRunnable();
        Assert.assertEquals(true, app.getDatabase().getRepos().countOf() > 0);
    }
}
