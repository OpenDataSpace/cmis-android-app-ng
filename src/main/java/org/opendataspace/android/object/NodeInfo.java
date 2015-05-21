package org.opendataspace.android.object;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.opendataspace.android.app.CompatObjects;

import java.math.BigInteger;
import java.util.Calendar;

class NodeInfo {

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

    public boolean update(CmisObject cmis) {
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

        return res;
    }

    private <T> T getProperty(String name, CmisObject cmis) {
        Property<T> prop = cmis.getProperty(name);
        return prop != null ? prop.getValue() : null;
    }
}
