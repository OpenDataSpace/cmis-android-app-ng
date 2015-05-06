package org.opendataspace.android.object;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.opendataspace.android.app.beta.R;

@DatabaseTable(tableName = "repo")
public class Repo extends ObjectBase {

    public static final String FIELD_ACCID = "aid";
    public static final String FIELD_TYPE = "t";

    public enum Type {DEFAULT, PRIVATE, SHARED, GLOBAL, CONFIG}

    @Expose
    @DatabaseField(index = true, columnName = FIELD_ACCID, canBeNull = false)
    private final long accountId;

    @Expose
    @DatabaseField(columnName = FIELD_TYPE, canBeNull = false, dataType = DataType.ENUM_INTEGER,
            unknownEnumName = "DEFAULT")
    private Type type;

    @Expose
    @DatabaseField(columnName = "data", canBeNull = false, persisterClass = RepoSerializer.class)
    private final RepoInfo info;

    private transient Repository cmis;

    public Repo() {
        info = new RepoInfo();
        cmis = null;
        type = Type.DEFAULT;
        accountId = INVALID_ID;
    }

    public Repo(Repository repo, Account account) {
        if (account != null) {
            accountId = account.getId();
        } else {
            accountId = INVALID_ID;
        }

        info = new RepoInfo();

        if (repo != null) {
            info.update(repo);
        }

        cmis = repo;
        updateType();
    }

    public Repo(Repo other) {
        setId(other.getId());
        accountId = other.accountId;
        info = new RepoInfo(other.info);
        cmis = other.cmis;
        type = other.type;
    }

    public String getName() {
        return info.name;
    }

    public String getUuid() {
        return info.uuid;
    }

    public boolean merge(Repository repo) {
        if (cmis == null) {
            cmis = repo;
        } else if (!cmis.getId().equals(repo.getId())) {
            return false;
        }

        boolean res = info.update(repo);

        if (res) {
            updateType();
        }

        return res;
    }

    public long getAccountId() {
        return accountId;
    }

    public String getDisplayName(Context context) {
        switch (type) {
        case PRIVATE:
            return context.getString(R.string.nav_personal);

        case SHARED:
            return context.getString(R.string.nav_shared);

        case GLOBAL:
            return context.getString(R.string.nav_global);

        default:
            return info.name;
        }
    }

    public int getIcon() {
        switch (type) {
        case SHARED:
            return R.drawable.ic_shared;

        case GLOBAL:
            return R.drawable.ic_global;

        default:
            return R.drawable.ic_folder;
        }
    }

    public Type getType() {
        return type;
    }

    private void updateType() {
        switch (info.name) {
        case "my":
            type = Type.PRIVATE;
            break;

        case "shared":
            type = Type.SHARED;
            break;

        case "global":
            type = Type.GLOBAL;
            break;

        case "config":
            type = Type.CONFIG;
            break;

        default:
            type = Type.DEFAULT;
        }
    }
}
