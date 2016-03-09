package org.opendataspace.android.status;

import android.support.design.widget.Snackbar;

import org.opendataspace.android.app.CompatDisposable;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.event.Event;
import org.opendataspace.android.event.EventStatus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class StatusManager implements CompatDisposable {

    private final List<StatusContext> data = new ArrayList<>();
    private long idx = 0;
    private WeakReference<Snackbar> snack;

    public StatusManager() {
        OdsApp.bus.register(this, Event.PRIORITY_UI);
    }

    @Override
    public void dispose() {
        OdsApp.bus.unregister(this);

        for (StatusContext cur : data) {
            OdsLog.debug(cur.getContextClass(), "Aborted status context");
        }

        data.clear();
        updateSnack();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(EventStatus event) {
        final StatusContext context = event.getStatusContext();

        if (event.isShouldDismiss()) {
            dismissContext(context);
        } else if (data.contains(context)) {
            OdsLog.msg(context.getContextClass(), String.valueOf(context.getIndex()) + ": " + event.getMessage());
            context.setLastMessage(event.getMessage());
        } else {
            OdsLog.debug(context.getContextClass(),
                    String.valueOf(context.getIndex()) + " (unknown): " + event.getMessage());
        }

        updateSnack();
    }

    public synchronized StatusContext createContext(Class<?> cls) {
        StatusContext res = new StatusContext(cls, idx++);
        data.add(res);
        return res;
    }

    private synchronized void dismissContext(StatusContext value) {
        if (!data.remove(value)) {
            OdsLog.debug(getClass(), "Attempt to remove unknown context " + String.valueOf(value.getIndex()));
        }
    }

    public void setSnack(Snackbar value) {
        snack = new WeakReference<>(value);
        updateSnack();
    }

    private void updateSnack() {
        final Snackbar bar = snack.get();

        if (bar == null) {
            return;
        }

        if (data.isEmpty()) {
            bar.dismiss();
            return;
        }

        bar.setText(data.get(data.size() - 1).getLastMessage()).show();
    }
}
