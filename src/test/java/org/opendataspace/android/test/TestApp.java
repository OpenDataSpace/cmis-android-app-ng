package org.opendataspace.android.test;

import android.annotation.SuppressLint;

import org.opendataspace.android.app.OdsApp;

@SuppressLint("Registered")
public class TestApp extends OdsApp {

    @Override
    public boolean isRealApp() {
        return false;
    }
}
