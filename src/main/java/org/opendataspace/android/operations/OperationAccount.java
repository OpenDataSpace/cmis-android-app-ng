package org.opendataspace.android.operations;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.account.Account;

public class OperationAccount extends OperatonBase {

    @Expose
    private final Account account;

    public OperationAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }
}
