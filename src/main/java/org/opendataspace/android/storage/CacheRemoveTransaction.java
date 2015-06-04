package org.opendataspace.android.storage;

import org.opendataspace.android.object.CacheEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CacheRemoveTransaction {

    private final List<CacheEntry> data = new ArrayList<>();

    public void add(CacheEntry ce) {
        if (ce != null) {
            data.add(ce);
        }
    }

    public void commit() {
        for (CacheEntry cur : data) {
            File f = new File(cur.getPath());
            //noinspection ResultOfMethodCallIgnored
            f.delete();
        }

        data.clear();
    }
}
