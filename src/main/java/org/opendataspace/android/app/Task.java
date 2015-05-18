package org.opendataspace.android.app;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Task implements Runnable {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private AtomicInteger decrement;

    public abstract void onExecute() throws Exception;

    @SuppressWarnings("RedundantThrows")
    public void onDone() throws Exception {
        // nothing
    }

    @Override
    public void run() {
        try {
            onExecute();

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
        } catch (final Exception ex) {
            OdsLog.ex(getClass(), ex);
        }

        if (decrement != null) {
            decrement.decrementAndGet();
        }
    }

    public void setDecrement(AtomicInteger value) {
        decrement = value;
    }
}
