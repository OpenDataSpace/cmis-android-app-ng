package org.opendataspace.android.object;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import org.apache.chemistry.opencmis.client.api.CmisObject;

class NodeInfo {

    @Expose
    public String name = "";

    @Expose
    public String uuid = "";

    @Expose
    public String pid = "";

    public NodeInfo() {
        // nothing
    }

    public NodeInfo(NodeInfo other) {
        name = other.name;
        uuid = other.uuid;
        pid = other.pid;
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

        return res;
    }
}
