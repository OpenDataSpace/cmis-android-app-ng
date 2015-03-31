package org.opendataspace.android.objects;

import org.opendataspace.android.data.DataGsonSerializer;

public class AccountSerializer extends DataGsonSerializer<AccountInfo> {

    private static final AccountSerializer instance = new AccountSerializer();

    private AccountSerializer() {
        super(AccountInfo.class);
    }

    public static AccountSerializer getSingleton() {
        return instance;
    }
}
