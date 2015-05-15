package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.object.Account;

public class OperationRepoSync extends OperationBase {

    @Expose
    private final Account account;

    public OperationRepoSync(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        OdsApp.get().getDatabase().transact(() -> {
            account.getRepositories().sync();

            if (isCancel()) {
                throw new InterruptedException();
            }

            return null;
        });

        OdsApp.get().getPool().execute(new OperationAccountConfig(account));
        status.setOk();
    }
}
