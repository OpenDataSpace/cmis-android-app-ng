package org.opendataspace.android.view;

import org.opendataspace.android.app.CompatDisposable;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DataBase;
import org.opendataspace.android.event.EventSelectAccount;
import org.opendataspace.android.object.Account;

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

    @Override
    public void dispose() {
        accounts.dispose();
        repos.dispose();
    }

    public Account getCurrentAccount() {
        return current;
    }

    public void setCurrentAccount(Account current) {
        if (current != null && current.equals(this.current)) {
            return;
        }

        DataBase db = OdsApp.get().getDatabase();
        OdsApp.get().getPrefs().setLastAccountId(current);

        if (this.current == null) {
            accounts.sync(db.getAccounts(), null);
        }

        this.current = current;
        repos.sync(db.getRepos(), current);
        OdsApp.bus.post(new EventSelectAccount());
    }
}
