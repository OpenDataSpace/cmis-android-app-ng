package org.opendataspace.android.object;

import com.google.gson.annotations.Expose;

import java.io.File;

class CacheEntryInfo {

    @Expose
    public String path = "";

    @Expose
    public long size = 0;

    @Expose
    public long ts = 0;

    public void update(File f) {
        path = f.getAbsolutePath();
        size = f.length();
        ts = f.lastModified();
    }
}
