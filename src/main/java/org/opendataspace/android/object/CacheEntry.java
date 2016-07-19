package org.opendataspace.android.object;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.File;

@DatabaseTable(tableName = "cache")
public class CacheEntry extends ObjectBase {

    public static final String FIELD_REPID = "rid";

    @SuppressWarnings("unused")
    @Expose
    @DatabaseField(index = true, columnName = FIELD_REPID, canBeNull = false)
    private final long repoId;

    @Expose
    @DatabaseField(columnName = "data", canBeNull = false, persisterClass = CacheEntrySerializer.class)
    private final CacheEntryInfo info = new CacheEntryInfo();

    @SuppressWarnings("unused")
    public CacheEntry() {
        repoId = ObjectBase.INVALID_ID;
    }

    public CacheEntry(File f, Repo repo) {
        repoId = repo.getId();
        info.update(f);
    }

    public String getPath() {
        return info.path;
    }

    @SuppressWarnings("unused")
    public long getSize() {
        return info.size;
    }

    public long getTimestamp() {
        return info.ts;
    }

    public void update(File f) {
        info.update(f);
    }
}
