package org.opendataspace.android.object;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;

@DatabaseTable(tableName = "node")
public class Node extends ObjectBase {

    public static final String FIELD_RID = "rid";
    public static final String FIELD_PID = "pid";
    public static final String FIELD_TYPE = "t";

    public enum Type {UNKNOWN, DOCUMENT, FOLDER}

    @Expose
    @DatabaseField(index = true, columnName = FIELD_RID, canBeNull = false)
    private final long repoId;

    @Expose
    @DatabaseField(index = true, columnName = FIELD_PID, canBeNull = false)
    private final long parentId;

    @Expose
    @DatabaseField(columnName = FIELD_TYPE, canBeNull = false, dataType = DataType.ENUM_INTEGER,
            unknownEnumName = "UNKNOWN")
    private Type type;

    @Expose
    @DatabaseField(columnName = "data", canBeNull = false, persisterClass = NodeSerializer.class)
    private final NodeInfo info;

    private transient CmisObject cmis;

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
        cmis = val;
        parentId = INVALID_ID;
        updateType();

        if (repo != null) {
            repoId = repo.getId();
        } else {
            repoId = INVALID_ID;
        }
    }

    public Node(CmisObject val, Node parent) {
        info = new NodeInfo();
        cmis = val;
        updateType();

        if (parent != null) {
            info.pid = parent.getUuid();
            repoId = parent.getRepoId();
            parentId = parent.getId();
        } else {
            repoId = INVALID_ID;
            parentId = INVALID_ID;
        }
    }

    public boolean merge(CmisObject val) {
        if (cmis == null) {
            cmis = val;
        } else if (!cmis.getId().equals(val.getId())) {
            return false;
        }

        boolean res = info.update(val);

        if (res) {
            updateType();
        }

        return res;
    }

    private void updateType() {
        if (cmis != null) {
            info.update(cmis);

            if (cmis instanceof Folder) {
                type = Type.FOLDER;
            } else if (cmis instanceof Document) {
                type = Type.DOCUMENT;
            } else {
                type = Type.UNKNOWN;
            }
        } else {
            type = Type.UNKNOWN;
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
}
