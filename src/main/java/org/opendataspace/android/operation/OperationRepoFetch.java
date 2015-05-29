package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.CloseableIterator;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.Cmis;
import org.opendataspace.android.data.DaoBase;
import org.opendataspace.android.data.DaoRepo;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Repo;

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
        super.doExecute(status);

        if (!isCancel() && shouldConfig) {
            OdsApp.get().getPool().execute(new OperationAccountConfig(account));
        }

        status.setOk();
    }

    @Override
    protected CloseableIterator<Repo> localObjects(DaoBase<Repo> dao) throws SQLException {
        return ((DaoRepo) dao).allRepos(account);
    }

    @Override
    protected DaoBase<Repo> dao() {
        return OdsApp.get().getDatabase().getRepos();
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
    protected Repo createObject(Repository val) {
        return new Repo(val, account);
    }

    @Override
    protected boolean merge(Repo obj, Repository val) {
        return obj.merge(val);
    }

    public void withoutConfig() {
        shouldConfig = false;
    }
}
