package org.opendataspace.android.status;

import org.opendataspace.android.app.CompatDisposable;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.event.Event;
import org.opendataspace.android.event.EventStatus;

import java.util.ArrayList;
import java.util.List;

public class StatusManager implements CompatDisposable {

    private final List<StatusContext> data = new ArrayList<>();
    private long idx = 0;

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
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(EventStatus event) {
        final StatusContext context = event.getStatusContext();

        if (event.isShouldDismiss()) {
            dismissContext(context);
        } else if (data.contains(context)) {
            OdsLog.msg(context.getContextClass(), String.valueOf(context.getIndex()) + ": " + event.getMessage());
        } else {
            OdsLog.debug(context.getContextClass(),
                    String.valueOf(context.getIndex()) + " (unknown): " + event.getMessage());
        }
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
}
