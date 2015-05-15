package org.opendataspace.android.object;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import org.apache.chemistry.opencmis.client.api.Repository;

class RepoInfo {

    @Expose
    public String name = "";

    @Expose
    public String uuid = "";

    @Expose
    public String rootUuid = "";

    public RepoInfo() {
        // nothing
    }

    public RepoInfo(RepoInfo other) {
        name = other.name;
        uuid = other.uuid;
        rootUuid = other.rootUuid;
    }

    boolean update(Repository repo) {
        boolean res = false;

        if (!TextUtils.equals(name, repo.getName())) {
            name = repo.getName();
            res = true;
        }

        if (!TextUtils.equals(uuid, repo.getId())) {
            uuid = repo.getId();
            res = true;
        }

        if (!TextUtils.equals(rootUuid, repo.getRootFolderId())) {
            rootUuid = repo.getRootFolderId();
            res = true;
        }

        return res;
    }
}
