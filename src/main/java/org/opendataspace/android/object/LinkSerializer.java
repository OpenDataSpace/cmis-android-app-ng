package org.opendataspace.android.object;

import org.opendataspace.android.data.DataGsonSerializer;

public class LinkSerializer extends DataGsonSerializer<Link> {

    private static final LinkSerializer instance = new LinkSerializer();

    protected LinkSerializer() {
        super(Link.class);
    }

    @SuppressWarnings("unused")
    public static LinkSerializer getSingleton() {
        return instance;
    }
}
