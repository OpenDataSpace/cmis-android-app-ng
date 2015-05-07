package org.opendataspace.android.view;

import org.opendataspace.android.app.CompatDisposable;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.TaskPool;
import org.opendataspace.android.data.DataBase;
import org.opendataspace.android.event.EventAccountSelect;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.operation.OperationAccountConfig;
import org.opendataspace.android.operation.OperationRepoSync;

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
        OdsApp app = OdsApp.get();
        DataBase db = app.getDatabase();
        app.getPrefs().setLastAccountId(current);

        if (this.current == null) {
            accounts.sync(db.getAccounts(), null);
        }

        this.current = current;
        repos.sync(db.getRepos(), current);
        OdsApp.bus.post(new EventAccountSelect());

        if (this.current != null) {
            TaskPool pool = app.getPool();

            pool.execute(new OperationRepoSync(current));
            pool.execute(new OperationAccountConfig(current));
        }
    }
}
