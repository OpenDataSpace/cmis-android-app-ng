package org.opendataspace.android.operations;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.objects.Account;

public class OperationAccount extends OperationBase {

    @Expose
    private final Account account;

    public OperationAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        account.getRepositories();

        if (isCancel()) {
            return;
        }

        //OdsApp.get().getDatabase().getAccounts().createOrUpdate(account);
        status.setOk();
    }
}
