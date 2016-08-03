package org.opendataspace.android.event;

import org.opendataspace.android.object.Node;

public class EventProgress {

    private final Node node;
    private final long pos;
    private final long max;

    public EventProgress(final Node node, final long pos, final long max) {
        this.node = node;
        this.pos = pos;
        this.max = max;
    }

    public Node getNode() {
        return node;
    }

    public long getPos() {
        return pos;
    }

    public long getMax() {
        return max;
    }
}
