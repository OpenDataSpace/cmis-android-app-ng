package org.opendataspace.android.test;

import android.app.Application;

import org.opendataspace.android.app.OdsApp;

public class LifecycleSimple extends LifecycleDefault {
    @Override
    protected Application createApp() {
        return new OdsApp() {
            @Override
            protected void performHacks() {
                // nothing
            }

            @Override
            protected void performSync() {
                // nothing
            }
        };
    }
}
