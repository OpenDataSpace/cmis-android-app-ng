package org.opendataspace.android.app;

import android.os.Handler;
import android.util.Log;

public abstract class Task implements Runnable {

    private final Handler handler = new Handler();

    public abstract void onExecute() throws Exception;

    protected void onDone() {
        // nothing
    }

    @Override
    public void run() {
        try {
            onExecute();
        } catch (final Exception ex) {
            Log.w(getClass().getSimpleName(), ex);
        }

        handler.post(new Runnable() {

            @Override
            public void run() {
                try {
                    onDone();
                } catch (final Exception ex) {
                    Log.w(getClass().getSimpleName(), ex);
                }
            }
        });
    }
}
