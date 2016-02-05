package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.*;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Repo;

public class OperationAccountDelete extends OperationBase {

    @Expose
    private final Account account;

    public OperationAccountDelete(Account account) {
        this.account = account;
    }

    @Override
    protected void doExecute(OperationResult result) throws Exception {
        final OdsApp app = OdsApp.get();
        final DataBase db = app.getDatabase();
        DaoAccount accounts = db.getAccounts();

        db.transact(() -> {
            accounts.delete(account);

            if (isCancel()) {
                throw new InterruptedException();
            }

            DaoRepo repos = db.getRepos();
            DaoNode nodes = db.getNodes();
            DaoCacheEntry ce = db.getCacheEntries();
            CloseableIterator<Repo> it = repos.allRepos(account);

            try {
                while (it.hasNext()) {
                    if (isCancel()) {
                        throw new InterruptedException();
                    }

                    Repo repo = it.nextThrow();
                    repos.delete(repo);
                    nodes.deleteByRepo(repo);
                    ce.deleteByRepo(repo);
                }
            } finally {
                it.closeQuietly();
            }

            return null;
        });

        OdsApp.get().getCacheManager().accountDeleted(account);

        if (app.getPrefs().getLastAccountId() == account.getId()) {
            CloseableIterator<Account> ita = accounts.iterate(accounts.queryBuilder().limit(1l).prepare());

            try {
                app.getPool().execute(new OperationAccountSelect(ita.hasNext() ? ita.nextThrow() : null));
            } finally {
                ita.closeQuietly();
            }
        }

        result.setOk();
    }
}
