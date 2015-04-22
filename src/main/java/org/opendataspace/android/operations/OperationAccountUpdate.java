package org.opendataspace.android.operations;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DataBase;
import org.opendataspace.android.objects.Account;

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
        final DataBase db = OdsApp.get().getDatabase();

        db.transact(() -> {
            db.getAccounts().createOrUpdate(account);

            if (isCancel()) {
                throw new InterruptedException();
            }

            account.getRepositories().sync();

            if (isCancel()) {
                throw new InterruptedException();
            }

            return null;
        });

        status.setOk();
    }
}
