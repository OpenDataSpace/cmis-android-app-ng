package org.opendataspace.android.views;

import org.opendataspace.android.app.CompatDisposable;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DataBase;
import org.opendataspace.android.objects.Account;

public class ViewManager implements CompatDisposable {

    private final ViewAccount accounts;
    private final ViewRepo repos;
    private Account current;

    public ViewManager() {
        accounts = new ViewAccount();
        repos = new ViewRepo();
    }

    public ViewAccount getAccounts() {
        return accounts;
    }

    public ViewRepo getRepos() {
        return repos;
    }

    public void sync(DataBase db) {
        current = OdsApp.get().getPrefs().getLastAccount();
        accounts.sync(db.getAccounts(), null);
        repos.sync(db.getRepos(), current);
    }

    @Override
    public void dispose() {
        accounts.dispose();
        repos.dispose();
    }
}
