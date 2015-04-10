package org.opendataspace.android.app;

import android.os.Handler;

public abstract class Task implements Runnable {

    private final Handler handler = new Handler();

    public abstract void onExecute() throws Exception;

    public void onDone() throws Exception {
        // nothing
    }

    @Override
    public void run() {
        try {
            onExecute();
        } catch (final Exception ex) {
            OdsLog.ex(getClass(), ex);
        }

        handler.post(new Runnable() {

            @Override
            public void run() {
                try {
                    onDone();
                } catch (final Exception ex) {
                    OdsLog.ex(getClass(), ex);
                }
            }
        });
    }
}
