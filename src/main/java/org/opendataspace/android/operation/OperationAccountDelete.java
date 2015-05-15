package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DaoAccount;
import org.opendataspace.android.data.DaoRepo;
import org.opendataspace.android.data.DataBase;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Repo;

public class OperationAccountDelete extends OperationBase {

    @Expose
    private final Account account;

    public OperationAccountDelete(Account account) {
        this.account = account;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        final OdsApp app = OdsApp.get();
        final DataBase db = app.getDatabase();
        DaoAccount accounts = db.getAccounts();

        db.transact(() -> {
            accounts.delete(account);

            if (isCancel()) {
                throw new InterruptedException();
            }

            DaoRepo repos = db.getRepos();
            CloseableIterator<Repo> itr = repos.allRepos(account);

            try {
                while (itr.hasNext()) {
                    if (isCancel()) {
                        throw new InterruptedException();
                    }

                    repos.delete(itr.nextThrow());
                }
            } finally {
                itr.closeQuietly();
            }

            return null;
        });

        if (app.getPrefs().getLastAccountId() == account.getId()) {
            CloseableIterator<Account> ita = accounts.iterate(accounts.queryBuilder().limit(1l).prepare());

            try {
                app.getPool().execute(new OperationAccountSelect(ita.hasNext() ? ita.nextThrow() : null));
            } finally {
                ita.closeQuietly();
            }
        }

        status.setOk();
    }
}
