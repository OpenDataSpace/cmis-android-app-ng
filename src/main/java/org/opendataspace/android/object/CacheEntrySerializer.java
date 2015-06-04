package org.opendataspace.android.object;

import org.opendataspace.android.data.DataGsonSerializer;

public class CacheEntrySerializer extends DataGsonSerializer<CacheEntryInfo> {

    private static final CacheEntrySerializer instance = new CacheEntrySerializer();

    CacheEntrySerializer() {
        super(CacheEntryInfo.class);
    }

    @SuppressWarnings("unused")
    public static CacheEntrySerializer getSingleton() {
        return instance;
    }
}
