package org.opendataspace.android.object;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.chemistry.opencmis.client.api.Repository;

@DatabaseTable(tableName = "repo")
public class Repo extends ObjectBase {

    public static final String FIELD_ACCID = "aid";

    @Expose
    @DatabaseField(index = true, columnName = FIELD_ACCID, canBeNull = false)
    private long accountId;

    @Expose
    @DatabaseField(columnName = "data", canBeNull = false, persisterClass = RepoSerializer.class)
    private final RepoInfo info;

    private final transient Repository cmis;

    public Repo() {
        info = new RepoInfo();
        cmis = null;
    }

    public Repo(Repository repo, Account account) {
        if (account != null) {
            accountId = account.getId();
        }

        info = new RepoInfo();

        if (repo != null) {
            info.update(repo);
        }

        cmis = repo;
    }

    public Repo(Repo other) {
        setId(other.getId());
        accountId = other.accountId;
        info = new RepoInfo(other.info);
        cmis = other.cmis;
    }

    public String getName() {
        return info.name;
    }

    String getUuid() {
        return info.uuid;
    }

    public boolean merge(Repository repo) {
        return info.update(repo);
    }

    public long getAccountId() {
        return accountId;
    }
}
