package org.opendataspace.android.view;

import org.opendataspace.android.app.CompatDisposable;
import org.opendataspace.android.app.OdsApp;
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

    public ViewNode createNodeView() {
        return new ViewNode();
    }

    public ViewLink createLinkView() {
        return new ViewLink();
    }

    @Override
    public void dispose() {
        accounts.dispose();
        repos.dispose();
    }

    public Account getCurrentAccount() {
        return current;
    }

    public void setCurrentAccount(final Account current) {
        OdsApp.get().getPrefs().setLastAccountId(current);
        this.current = current;
    }
}
