package org.opendataspace.android.ui;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class FragmentAccountListTest {

    @Test
    public void checkDefaults() throws Exception {
        FragmentAccountList fgm = new FragmentAccountList();
        ActivityMain ac = TestUtil.setupFragment(fgm);
        Assert.assertEquals(1, fgm.getList().getCount());
        TestUtil.dismisActivity(ac);
    }
}
