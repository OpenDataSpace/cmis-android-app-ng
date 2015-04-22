package org.opendataspace.android.test;

import android.os.Bundle;

import org.opendataspace.android.navigation.Navigation;
import org.opendataspace.android.navigation.NavigationInterface;
import org.opendataspace.android.ui.ActivityMain;

public class TestActivity extends ActivityMain {

    private TestNavigation nav = new TestNavigation();

    @Override
    protected Navigation createNavigation(Bundle savedInstanceState) {
        return null;
    }

    @Override
    public NavigationInterface getNavigation() {
        return nav;
    }
}
