package org.opendataspace.android.operation;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DataBase;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

@RunWith(TestRunner.class)
public class OperationAccountUpdateTest {

    @Test
    public void connectJson() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        OperationAccountUpdate op = new OperationAccountUpdate(TestUtil.getDefaultAccount());
        op.getAccount().setUseJson(true);
        OperationStatus st = op.execute();
        Assert.assertEquals(true, st.isOk());
        checkDb(app.getDatabase());
    }

    @Test
    public void connectAtom() throws Exception {
        OdsApp app = (OdsApp) RuntimeEnvironment.application;
        OperationAccountUpdate op = new OperationAccountUpdate(TestUtil.getDefaultAccount());
        op.getAccount().setUseJson(false);
        OperationStatus st = op.execute();
        Assert.assertEquals(true, st.isOk());
        checkDb(app.getDatabase());
    }

    private void checkDb(DataBase db) throws Exception {
        Assert.assertEquals(1, db.getAccounts().countOf());
        Assert.assertEquals(true, db.getRepos().countOf() > 0);

        boolean hasPrivate = false;
        boolean hasShared = false;
        boolean hasGlobal = false;
        boolean hasConfig = false;
        List<Repo> ls = TestUtil.allOf(db.getRepos());

        for (Repo cur : ls) {
            switch (cur.getType()) {
            case PRIVATE:
                hasPrivate = true;
                break;
            case SHARED:
                hasShared = true;
                break;
            case GLOBAL:
                hasGlobal = true;
                break;
            case CONFIG:
                hasConfig = true;
                break;
            }
        }

        Assert.assertEquals(true, hasPrivate);
        Assert.assertEquals(true, hasShared);
        Assert.assertEquals(true, hasGlobal);
        Assert.assertEquals(true, hasConfig);
    }
}
