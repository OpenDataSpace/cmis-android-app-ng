package org.opendataspace.android.ui;

import android.widget.Spinner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.test.OdsRunner;
import org.opendataspace.android.test.OdsTestUtil;

@RunWith(OdsRunner.class)
public class FragmentNavigationTest {

    @Test
    public void checkDefaults() throws Exception {
        FragmentNavigation fgm = new FragmentNavigation();
        ActivityMain ac = OdsTestUtil.setupFragment(fgm);
        OdsTestUtil.waitRunnable();
        Spinner spin = (Spinner) ac.findViewById(R.id.spin_nav_accounts);
        Assert.assertEquals(1, spin.getCount());
        OdsTestUtil.dismisActivity(ac);
    }
}
