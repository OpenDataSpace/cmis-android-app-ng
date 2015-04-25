package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.app.OdsApp;
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
        final DataBase db = OdsApp.get().getDatabase();

        db.transact(() -> {
            db.getAccounts().delete(account);

            if (isCancel()) {
                throw new InterruptedException();
            }

            DaoRepo repos = db.getRepos();
            CloseableIterator<Repo> it =
                    repos.iterate(repos.queryBuilder().where().eq(Repo.FIELD_ACCID, account.getId()).prepare());

            try {
                while (it.hasNext()) {
                    if (isCancel()) {
                        throw new InterruptedException();
                    }

                    repos.delete(it.nextThrow());
                }
            } finally {
                it.closeQuietly();
            }

            status.setOk();
            return null;
        });
    }
}
