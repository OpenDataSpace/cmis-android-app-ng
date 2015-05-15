package org.opendataspace.android.view;

import org.opendataspace.android.app.CompatDisposable;
import org.opendataspace.android.app.CompatObjects;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.object.Account;

public class ViewManager implements CompatDisposable {

    private final ViewAccount accounts;
    private final ViewRepo repos;
    private final ViewNode nodes;
    private Account current;

    public ViewManager() {
        accounts = new ViewAccount();
        repos = new ViewRepo();
        nodes = new ViewNode();
    }

    public ViewAccount getAccounts() {
        return accounts;
    }

    public ViewRepo getRepos() {
        return repos;
    }

    public ViewNode getNodes() {
        return nodes;
    }

    @Override
    public void dispose() {
        accounts.dispose();
        repos.dispose();
        nodes.dispose();
    }

    public Account getCurrentAccount() {
        return current;
    }

    public void setCurrentAccount(final Account current) {
        OdsApp.get().getPrefs().setLastAccountId(current);
        this.current = current;
    }
}
