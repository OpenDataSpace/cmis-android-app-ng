package org.opendataspace.android.ui;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.test.OdsRunner;
import org.opendataspace.android.test.OdsTestUtil;

@RunWith(OdsRunner.class)
public class FragmentAccountListTest {

    @Test
    public void checkDefaults() throws Exception {
        FragmentAccountList fgm = new FragmentAccountList();
        ActivityMain ac = OdsTestUtil.setupFragment(fgm);
        OdsTestUtil.waitRunnable();
        Assert.assertEquals(1, fgm.getList().getCount());
        OdsTestUtil.dismisActivity(ac);
    }
}
