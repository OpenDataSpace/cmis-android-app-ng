package org.opendataspace.android.operation;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;

@RunWith(TestRunner.class)
public class OperationAccountUpdateTest {

    @Test
    public void connectJson() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        OperationAccountUpdate op = new OperationAccountUpdate(TestUtil.getDefaultAccount());
        op.getAccount().setUseJson(true);
        OperationStatus st = op.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(1, app.getDatabase().getAccounts().countOf());
        Assert.assertEquals(true, app.getDatabase().getRepos().countOf() > 0);
    }

    @Test
    public void connectAtom() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        OperationAccountUpdate op = new OperationAccountUpdate(TestUtil.getDefaultAccount());
        op.getAccount().setUseJson(false);
        OperationStatus st = op.execute();
        Assert.assertEquals(true, st.isOk());
        Assert.assertEquals(1, app.getDatabase().getAccounts().countOf());
        Assert.assertEquals(true, app.getDatabase().getRepos().countOf() > 0);
    }
}
