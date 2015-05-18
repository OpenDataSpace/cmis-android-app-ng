package org.opendataspace.android.object;

import org.opendataspace.android.data.DataGsonSerializer;

public class NodeSerializer extends DataGsonSerializer<NodeInfo> {

    private static final NodeSerializer instance = new NodeSerializer();

    NodeSerializer() {
        super(NodeInfo.class);
    }

    @SuppressWarnings("unused")
    public static NodeSerializer getSingleton() {
        return instance;
    }
}
