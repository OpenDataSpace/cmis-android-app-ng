package org.opendataspace.android.ui;

import android.widget.ListView;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opendataspace.android.test.TestRunner;
import org.opendataspace.android.test.TestUtil;

@SuppressWarnings("unused")
@RunWith(TestRunner.class)
public class FragmentFolderLibraryTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void navigate() throws Exception {
        FragmentFolderLibrary fgm = new FragmentFolderLibrary();
        ActivityMain ac = TestUtil.setupFragment(fgm);
        ListView lv = (ListView) fgm.getView().findViewById(android.R.id.list);
        Assert.assertEquals(true, lv.getCount() != 0);
        TestUtil.dismisActivity(ac);
    }
}
