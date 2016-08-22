package org.opendataspace.android.event;

import org.opendataspace.android.object.Link;

public class EventLinkUpdate {

    private final String uuid;
    private final Link.Type type;

    public EventLinkUpdate(final String uuid, final Link.Type type) {
        this.uuid = uuid;
        this.type = type;
    }

    public String getNodeUuid() {
        return uuid;
    }

    public Link.Type getType() {
        return type;
    }
}
