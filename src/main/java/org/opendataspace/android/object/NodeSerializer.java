package org.opendataspace.android.object;

import org.opendataspace.android.data.DataGsonSerializer;

public class NodeSerializer extends DataGsonSerializer<NodeInfo> {

    private static final NodeSerializer instance = new NodeSerializer();

    protected NodeSerializer() {
        super(NodeInfo.class);
    }

    public static NodeSerializer getSingleton() {
        return instance;
    }
}
