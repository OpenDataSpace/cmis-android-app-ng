package org.opendataspace.android.event;

public class EventNodeUpdate {

    private String uuid;

    public EventNodeUpdate(final String uuid) {
        this.uuid = uuid;
    }

    public String getNodeUuid() {
        return uuid;
    }
}
