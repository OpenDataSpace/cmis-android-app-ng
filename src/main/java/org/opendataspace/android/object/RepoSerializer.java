package org.opendataspace.android.object;

import org.opendataspace.android.data.DataGsonSerializer;

public class RepoSerializer extends DataGsonSerializer<RepoInfo> {

    private static final RepoSerializer instance = new RepoSerializer();

    private RepoSerializer() {
        super(RepoInfo.class);
    }

    public static RepoSerializer getSingleton() {
        return instance;
    }
}
