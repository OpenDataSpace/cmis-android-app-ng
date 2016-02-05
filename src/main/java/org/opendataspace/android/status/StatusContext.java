package org.opendataspace.android.status;

import org.opendataspace.android.app.CompatDisposable;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.event.EventStatus;

public class StatusContext implements CompatDisposable {

    private final Class<?> cls;
    private final long idx;

    public StatusContext(final Class<?> cls, long idx) {
        this.cls = cls;
        this.idx = idx;
    }

    public Class<?> getContextClass() {
        return cls;
    }

    public long getIndex() {
        return idx;
    }

    public void postMessage(int res, Object... arg) {
        OdsApp.bus.post(new EventStatus(this, String.format(OdsApp.get().getApplicationContext().getString(res), arg)));
    }

    @Override
    public void dispose() {
        OdsApp.bus.post(new EventStatus(this));
    }
}
