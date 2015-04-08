package org.opendataspace.android.operations;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.test.OdsRunner;
import org.opendataspace.android.test.OdsTestUtil;

@RunWith(OdsRunner.class)
public class OperationAccountTest {

    private OperationAccount op;

    @Before
    public void setUp() throws Exception {
        op = new OperationAccount(OdsTestUtil.getDefaultAccount());
    }

    @Test
    public void connectJson() {
        op.getAccount().setUseJson(true);
        OperationStatus st = op.execute();
        Assert.assertTrue(st.isOk());
    }

    @Test
    public void connectAtom() {
        op.getAccount().setUseJson(false);
        OperationStatus st = op.execute();
        Assert.assertTrue(st.isOk());
    }
}
