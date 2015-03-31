package org.opendataspace.android.test;

import android.os.Build;

import org.junit.Before;
import org.robolectric.shadows.ShadowLog;

public abstract class OdsBaseTest {

    public static final int SDK = Build.VERSION_CODES.LOLLIPOP;

    @Before
    public void setUp() throws Exception {
        ShadowLog.stream = System.out;
    }
}
