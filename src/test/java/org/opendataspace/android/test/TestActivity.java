package org.opendataspace.android.test;

import android.os.Bundle;

import org.opendataspace.android.navigation.NavigationInterface;
import org.opendataspace.android.ui.ActivityMain;

public class TestActivity extends ActivityMain {

    @Override
    protected NavigationInterface createNavigation(Bundle savedInstanceState) {
        return new TestNavigation();
    }
}
