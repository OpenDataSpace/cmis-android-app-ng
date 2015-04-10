package org.opendataspace.android.operations;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.test.RunnerSimple;
import org.opendataspace.android.test.TestUtil;

@RunWith(RunnerSimple.class)
public class OperationAccountTest {

    private OperationAccount op;

    @Before
    public void setUp() throws Exception {
        op = new OperationAccount(TestUtil.getDefaultAccount());
    }

    @Test
    public void connectJson() {
        op.getAccount().setUseJson(true);
        OperationStatus st = op.execute();
        Assert.assertEquals(true, st.isOk());
    }

    @Test
    public void connectAtom() {
        op.getAccount().setUseJson(false);
        OperationStatus st = op.execute();
        Assert.assertEquals(true, st.isOk());
    }
}
