package org.opendataspace.android.test;

import android.annotation.SuppressLint;
import android.os.Bundle;

import org.opendataspace.android.navigation.NavigationInterface;
import org.opendataspace.android.ui.ActivityMain;

@SuppressLint("Registered")
@SuppressWarnings("unused")
public class TestActivity extends ActivityMain {

    @Override
    protected NavigationInterface createNavigation(Bundle savedInstanceState) {
        return new TestNavigation();
    }
}
