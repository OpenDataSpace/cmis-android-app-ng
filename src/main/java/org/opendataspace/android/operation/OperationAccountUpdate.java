package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DataBase;
import org.opendataspace.android.object.Account;

public class OperationAccountUpdate extends OperationBase {

    @Expose
    private final Account account;

    public OperationAccountUpdate(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        final OdsApp app = OdsApp.get();
        final DataBase db = app.getDatabase();

        boolean isFirst = db.getAccounts().countOf() == 0;

        db.transact(() -> {
            db.getAccounts().createOrUpdate(account);

            if (isCancel()) {
                throw new InterruptedException();
            }

            OperationRepoFetch fetch = new OperationRepoFetch(account);
            fetch.execute();

            if (isCancel()) {
                throw new InterruptedException();
            }

            return null;
        });

        if ((isFirst || app.getPrefs().getLastAccountId() == account.getId()) && !isCancel()) {
            app.getPool().execute(new OperationAccountSelect(account));
        }

        status.setOk();
    }
}
