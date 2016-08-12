package org.opendataspace.android.object;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Calendar;

@DatabaseTable(tableName = "lnk")
public class Link extends ObjectBase {

    public final static String NODE_ID_FIELD = "nodeid";
    public final static String TYPE_FIELD = "type";

    public enum Type {
        DOWNLOAD, UPLOAD
    }

    @Expose
    @DatabaseField(columnName = "data", canBeNull = false, persisterClass = LinkSerializer.class)
    private final LinkInfo info = new LinkInfo();

    @DatabaseField(columnName = NODE_ID_FIELD, index = true)
    private String nodeId;

    @DatabaseField(index = true, dataType = DataType.ENUM_INTEGER, unknownEnumName = "DOWNLOAD",
            columnName = TYPE_FIELD)
    private Type type = Type.DOWNLOAD;

    private transient String relationId = "";

    public Link() {
        Calendar exp = Calendar.getInstance();
        exp.add(Calendar.DAY_OF_MONTH, 7);
        info.expires = exp.getTime();
    }

    public String getName() {
        return info.name;
    }

    public void setName(String name) {
        info.name = name;
    }

    public String getUrl() {
        return info.url;
    }

    public void setUrl(String url) {
        info.url = url;
    }

    public String getObjectId() {
        return info.objectId;
    }

    public void setObjectId(String objectId) {
        info.objectId = objectId;
    }

    public String getMessage() {
        return info.message;
    }

    public void setMessage(String message) {
        info.message = message;
    }

    public String getEmail() {
        return info.email;
    }

    public void setEmail(String email) {
        info.email = email;
    }

    public String getPassword() {
        return info.password;
    }

    public void setPassword(String password) {
        info.password = password;
    }

    public Calendar getExpires() {
        Calendar c = Calendar.getInstance();
        c.setTime(info.expires);
        return c;
    }

    public void setExpires(Calendar expires) {
        if (expires != null) {
            info.expires = expires.getTime();
        }
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(info.name) && !TextUtils.isEmpty(info.message) && info.expires != null;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String val) {
        nodeId = val;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }
}
