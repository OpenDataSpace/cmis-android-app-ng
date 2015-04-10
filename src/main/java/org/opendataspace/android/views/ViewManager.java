package org.opendataspace.android.views;

import org.opendataspace.android.app.CompatDisposable;
import org.opendataspace.android.data.DataBase;

public class ViewManager implements CompatDisposable {

    private final ViewAccount accounts;

    public ViewManager() {
        accounts = new ViewAccount();
    }

    public ViewAccount getAccounts() {
        return accounts;
    }

    public void sync(DataBase db) {
        accounts.sync(db.getAccounts());
    }

    @Override
    public void dispose() {
        accounts.dispose();
    }
}
