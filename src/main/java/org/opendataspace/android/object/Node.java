package org.opendataspace.android.object;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Formatter;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.status.StatusContext;

@DatabaseTable(tableName = "node")
public class Node extends ObjectBase {

    public static final String FIELD_RID = "rid";
    public static final String FIELD_PID = "pid";
    private static final String FIELD_TYPE = "t";

    public enum Type {UNKNOWN, DOCUMENT, FOLDER}

    @Expose
    @DatabaseField(index = true, columnName = FIELD_RID, canBeNull = false)
    private final long repoId;

    @Expose
    @DatabaseField(index = true, columnName = FIELD_PID, canBeNull = false)
    private long parentId;

    @Expose
    @DatabaseField(columnName = FIELD_TYPE, canBeNull = false, dataType = DataType.ENUM_INTEGER,
            unknownEnumName = "UNKNOWN")
    private Type type;

    @Expose
    @DatabaseField(columnName = "data", canBeNull = false, persisterClass = NodeSerializer.class)
    private final NodeInfo info;

    private transient CmisObject cmis;
    private transient MimeType mime;
    private transient int progress = 100;

    public Node() {
        info = new NodeInfo();
        cmis = null;
        type = Type.UNKNOWN;
        repoId = INVALID_ID;
        parentId = INVALID_ID;
    }

    public Node(Node other) {
        setId(other.getId());
        info = new NodeInfo(other.info);
        cmis = other.cmis;
        type = other.type;
        repoId = other.repoId;
        parentId = other.parentId;
    }

    public Node(CmisObject val, Repo repo) {
        info = new NodeInfo();
        parentId = INVALID_ID;
        update(val);

        if (repo != null) {
            repoId = repo.getId();
        } else {
            repoId = INVALID_ID;
        }
    }

    public Node(CmisObject val, Node parent) {
        info = new NodeInfo();
        update(val);

        if (parent != null) {
            info.pid = parent.getUuid();
            repoId = parent.getRepoId();
            parentId = parent.getId();
        } else {
            repoId = INVALID_ID;
            parentId = INVALID_ID;
        }
    }

    public Node(Repo repo) {
        info = new NodeInfo();
        parentId = INVALID_ID;
        type = Type.FOLDER;
        repoId = repo.getId();
        info.uuid = repo.getRootFolderUuid();
        info.path = "/";
        info.permissions = NodeInfo.CAN_CREATE_DOCUMENT | NodeInfo.CAN_CREATE_FOLDER;
    }

    public boolean merge(CmisObject val) {
        return (cmis == null || cmis.getId().equals(val.getId())) && update(val);
    }

    private boolean update(CmisObject val) {
        cmis = val;

        if (cmis != null) {
            if (cmis instanceof Folder) {
                type = Type.FOLDER;
            } else if (cmis instanceof Document) {
                type = Type.DOCUMENT;
            } else {
                type = Type.UNKNOWN;
            }

            return info.update(cmis, type);
        } else {
            type = Type.UNKNOWN;
            return true;
        }
    }

    public String getName() {
        return info.name;
    }

    public String getUuid() {
        return info.uuid;
    }

    public Type getType() {
        return type;
    }

    public long getRepoId() {
        return repoId;
    }

    public String getParentUuid() {
        return info.pid;
    }

    public long getParentId() {
        return parentId;
    }

    public int getIcon(Context context) {
        switch (type) {
        case FOLDER:
            return R.drawable.ic_folder;

        default:
            return mime != null ? mime.getIcon(context) : R.drawable.ic_file;
        }
    }

    public String getNodeDecription(Context context) {
        String res = "";

        if (info.size != 0) {
            res += Formatter.formatShortFileSize(context, info.size);
        }

        if (mime != null) {
            if (!res.isEmpty()) {
                res += " ";
            }

            res += mime.getDescription(context);
        }

        if (info.mdt != null) {
            if (!res.isEmpty()) {
                res += " ";
            }

            res += DateUtils.getRelativeTimeSpanString(context, info.mdt.getTimeInMillis());
        }

        return res;
    }

    public String getPath(Context context) {
        return TextUtils.isEmpty(info.path) ? context.getString(R.string.folder_slash) : info.path;
    }

    public String getCreatedAt(Context context) {
        return info.cdt != null ? DateFormat.getMediumDateFormat(context).format(info.cdt.getTime()) + " " +
                DateFormat.getTimeFormat(context).format(info.cdt.getTime()) : "";
    }

    public String getCreatedBy() {
        return info.cru;
    }

    public String getModifiedAt(Context context) {
        return info.mdt != null ? DateFormat.getMediumDateFormat(context).format(info.mdt.getTime()) + " " +
                DateFormat.getTimeFormat(context).format(info.mdt.getTime()) : "";
    }

    public long getModifiedTs() {
        return info.mdt != null ? info.mdt.getTimeInMillis() : 0;
    }

    public String getModifiedBy() {
        return info.mdu;
    }

    public void setMimeType(MimeType mime) {
        this.mime = mime;
    }

    public MimeType getMimeType() {
        return mime;
    }

    public String getMimeDescription(Context context) {
        if (mime != null) {
            return mime.getDescription(context);
        }

        return context.getString(getType() == Type.FOLDER ? R.string.node_folder : R.string.node_unknown);
    }

    public boolean canCreateFolder() {
        return (info.permissions & NodeInfo.CAN_CREATE_FOLDER) != 0;
    }

    public boolean canCreateDocument() {
        return (info.permissions & NodeInfo.CAN_CREATE_DOCUMENT) != 0;
    }

    public boolean canDelete() {
        return (info.permissions & NodeInfo.CAN_DELETE) != 0;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canEdit() {
        return (info.permissions & NodeInfo.CAN_EDIT) != 0;
    }

    public void setName(String name) {
        info.name = name;
    }

    public CmisObject getCmisObject(CmisSession session, StatusContext status) {
        if (cmis == null) {
            update(session.getObjectById(getUuid(), status));
        }

        return cmis;
    }

    public long getSize() {
        return info.size;
    }

    public void setParentId(long val) {
        parentId = val;
    }

    public long getCacheEntryId() {
        return info.local;
    }

    public void setCacheEntry(CacheEntry ce) {
        info.local = ce.getId();
    }

    public void setProgress(final int value) {
        progress = value;
    }

    public int getProgress() {
        return progress;
    }
}
