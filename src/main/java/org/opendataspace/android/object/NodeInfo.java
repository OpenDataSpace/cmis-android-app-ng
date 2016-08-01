package org.opendataspace.android.object;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.AllowableActions;
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.opendataspace.android.app.CompatObjects;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Set;

class NodeInfo {

    public final static int CAN_EDIT = 0x1;
    public final static int CAN_DELETE = 0x2;
    public final static int CAN_CREATE_FOLDER = 0x4;
    public final static int CAN_CREATE_DOCUMENT = 0x8;

    @Expose
    public String name = "";

    @Expose
    public String uuid = "";

    @Expose
    public String pid = "";

    @Expose
    public String path = "";

    @Expose
    public String cru = "";

    @Expose
    public String mdu = "";

    @Expose
    public Calendar cdt;

    @Expose
    public Calendar mdt;

    @Expose
    public long size = 0;

    @Expose
    int permissions = 0;

    @Expose
    long local = ObjectBase.INVALID_ID;

    public NodeInfo() {
        // nothing
    }

    public NodeInfo(NodeInfo other) {
        name = other.name;
        uuid = other.uuid;
        pid = other.pid;
        path = other.path;
        cru = other.cru;
        mdu = other.mdu;
        cdt = other.cdt;
        mdt = other.mdt;
        size = other.size;
    }

    public boolean update(CmisObject cmis, Node.Type type) {
        boolean res = false;

        if (!TextUtils.equals(name, cmis.getName())) {
            name = cmis.getName();
            res = true;
        }

        if (!TextUtils.equals(uuid, cmis.getId())) {
            uuid = cmis.getId();
            res = true;
        }

        String path = getProperty(PropertyIds.PATH, cmis);

        if (!TextUtils.equals(this.path, path)) {
            this.path = path;
            res = true;
        }

        String creator = getProperty(PropertyIds.CREATED_BY, cmis);

        if (!TextUtils.equals(creator, cru)) {
            cru = creator;
            res = true;
        }

        String modifier = getProperty(PropertyIds.LAST_MODIFIED_BY, cmis);

        if (!TextUtils.equals(modifier, mdu)) {
            mdu = creator;
            res = true;
        }

        Calendar created = getProperty(PropertyIds.CREATION_DATE, cmis);

        if (!CompatObjects.equals(created, cdt)) {
            cdt = created;
            res = true;
        }

        Calendar modified = getProperty(PropertyIds.LAST_MODIFICATION_DATE, cmis);

        if (!CompatObjects.equals(modified, mdt)) {
            mdt = modified;
            res = true;
        }

        BigInteger bi = getProperty(PropertyIds.CONTENT_STREAM_LENGTH, cmis);
        long size = bi != null ? bi.longValue() : 0;

        if (this.size != size) {
            this.size = size;
            res = true;
        }

        int perm = permissions(cmis, type);

        if (perm != permissions) {
            permissions = perm;
            res = true;
        }

        return res;
    }

    private <T> T getProperty(String name, CmisObject cmis) {
        Property<T> prop = cmis.getProperty(name);
        return prop != null ? prop.getValue() : null;
    }

    private int permissions(CmisObject cmis, Node.Type type) {
        AllowableActions perms = cmis.getAllowableActions();

        if (perms == null) {
            return 0;
        }

        int res = 0;
        Set<Action> actions = perms.getAllowableActions();

        if (actions.contains(Action.CAN_UPDATE_PROPERTIES)) {
            res |= CAN_EDIT;
        }

        if (actions.contains(Action.CAN_CREATE_FOLDER)) {
            res |= CAN_CREATE_FOLDER;
        }

        if (actions.contains(Action.CAN_CREATE_DOCUMENT)) {
            res |= CAN_CREATE_DOCUMENT;
        }

        if (type == Node.Type.DOCUMENT && actions.contains(Action.CAN_DELETE_OBJECT)) {
            res |= CAN_DELETE;
        }

        if (type == Node.Type.FOLDER && actions.contains(Action.CAN_DELETE_TREE)) {
            res |= CAN_DELETE;
        }

        return res;
    }
}
