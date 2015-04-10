package org.opendataspace.android.ui;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.test.RunnerDefault;
import org.opendataspace.android.test.TestUtil;

@RunWith(RunnerDefault.class)
public class FragmentAccountListTest {

    @Test
    public void checkDefaults() throws Exception {
        FragmentAccountList fgm = new FragmentAccountList();
        ActivityMain ac = TestUtil.setupFragment(fgm);
        TestUtil.waitRunnable();
        Assert.assertEquals(1, fgm.getList().getCount());
        TestUtil.dismisActivity(ac);
    }
}
