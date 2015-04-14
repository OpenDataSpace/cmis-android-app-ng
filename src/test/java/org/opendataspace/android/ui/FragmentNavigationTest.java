package org.opendataspace.android.ui;

import android.widget.Spinner;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.test.RunnerDefault;
import org.opendataspace.android.test.TestUtil;

@RunWith(RunnerDefault.class)
public class FragmentNavigationTest {

    @Ignore
    @Test
    public void checkDefaults() throws Exception {
        FragmentNavigation fgm = new FragmentNavigation();
        ActivityMain ac = TestUtil.setupFragment(fgm);
        TestUtil.waitRunnable();
        Spinner spin = (Spinner) ac.findViewById(R.id.spin_nav_accounts);
        Assert.assertEquals(1, spin.getCount());
        TestUtil.dismisActivity(ac);
    }
}
