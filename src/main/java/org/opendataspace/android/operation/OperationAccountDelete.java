package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DaoAccount;
import org.opendataspace.android.data.DaoRepo;
import org.opendataspace.android.data.DataBase;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.ObjectBase;
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

        db.transact(() -> {
            DaoAccount accounts = db.getAccounts();
            accounts.delete(account);

            if (isCancel()) {
                throw new InterruptedException();
            }

            DaoRepo repos = db.getRepos();
            CloseableIterator<Repo> itr =
                    repos.iterate(repos.queryBuilder().where().eq(Repo.FIELD_ACCID, account.getId()).prepare());

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

            if (app.getPrefs().getLastAccountId() == account.getId()) {
                CloseableIterator<Account> ita = accounts.iterate(
                        accounts.queryBuilder().limit(1l).where().ne(ObjectBase.FIELD_ID, account.getId()).prepare());

                try {
                    if (isCancel()) {
                        throw new InterruptedException();
                    }

                    app.getViewManager().setCurrentAccount(ita.hasNext() ? ita.nextThrow() : null);
                } finally {
                    ita.closeQuietly();
                }
            }

            return null;
        });

        status.setOk();
    }
}
