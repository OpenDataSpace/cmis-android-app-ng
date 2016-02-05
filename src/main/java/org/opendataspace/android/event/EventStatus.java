package org.opendataspace.android.event;

import org.opendataspace.android.status.StatusContext;

public class EventStatus {

    private final StatusContext context;
    private final String message;
    private final boolean shouldDismiss;

    public EventStatus(final StatusContext context, final String message) {
        this.context = context;
        this.message = message;
        shouldDismiss = false;
    }

    public EventStatus(final StatusContext context) {
        this.context = context;
        this.message = null;
        this.shouldDismiss = true;
    }

    public StatusContext getStatusContext() {
        return context;
    }

    public String getMessage() {
        return message;
    }

    public boolean isShouldDismiss() {
        return shouldDismiss;
    }
}
