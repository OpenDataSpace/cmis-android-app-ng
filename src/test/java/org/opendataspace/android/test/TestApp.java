package org.opendataspace.android.test;

import org.opendataspace.android.app.OdsApp;

public class TestApp extends OdsApp {

    @Override
    protected void performHacks() {
        // nothing
    }

    @Override
    protected void performSync() {
        // nothing
    }

    public void testStartSync() {
        super.performSync();
    }
}
