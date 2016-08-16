package org.opendataspace.android.event;

public class EventLinkUpdate {

    private final String uuid;

    public EventLinkUpdate(final String uuid) {
        this.uuid = uuid;
    }

    public String getNodeUuid() {
        return uuid;
    }
}
