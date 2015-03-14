package org.opendataspace.android.account;

import org.opendataspace.android.data.DataGsonSerializer;

class AccountSerializer extends DataGsonSerializer<AccountInfo> {

    private static final AccountSerializer instance = new AccountSerializer();

    private AccountSerializer() {
        super(AccountInfo.class);
    }

    public static AccountSerializer getSingleton() {
        return instance;
    }
}
