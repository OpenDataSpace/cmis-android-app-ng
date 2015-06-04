package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.CloseableIterator;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.Cmis;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.storage.CacheManager;

import java.sql.SQLException;
import java.util.List;

public class OperationRepoFetch extends OperationBaseFetch<Repo, Repository> {

    @Expose
    private final Account account;

    @Expose
    private boolean shouldConfig = true;

    public OperationRepoFetch(Account account) {
        this.account = account;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        process(this, this);

        if (!isCancel() && shouldConfig) {
            OdsApp.get().getPool().execute(new OperationAccountConfig(account));
        }

        status.setOk();
    }

    @Override
    protected CloseableIterator<Repo> localObjects() throws SQLException {
        return OdsApp.get().getDatabase().getRepos().allRepos(account);
    }

    @Override
    protected List<Repository> fetch() {
        return Cmis.factory.getRepositories(Cmis.createSessionSettings(account));
    }

    @Override
    protected Repo find(Repository val, List<Repo> ls) {
        String uuid = val.getId();

        for (Repo cur : ls) {
            if (cur.getUuid().equals(uuid)) {
                return cur;
            }
        }

        return null;
    }

    @Override
    protected void create(Repository val) throws SQLException {
        OdsApp.get().getDatabase().getRepos().create(new Repo(val, account));
    }

    @Override
    protected void merge(Repo obj, Repository val) throws SQLException {
        if (obj.merge(val)) {
            OdsApp.get().getDatabase().getRepos().update(obj);
        }
    }

    @Override
    protected void delete(Repo obj) throws SQLException {
        OdsApp.get().getDatabase().getRepos().delete(obj);
        OdsApp.get().getDatabase().getCacheEntries().deleteByRepo(obj);
    }

    @Override
    protected void cleanup(List<Repo> ls) throws SQLException {
        CacheManager cm = OdsApp.get().getCacheManager();

        for (Repo cur : ls) {
            cm.repoDeleted(account, cur);
        }
    }

    public void withoutConfig() {
        shouldConfig = false;
    }
}
