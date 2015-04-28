package org.opendataspace.android.view;

import org.opendataspace.android.app.CompatDisposable;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.Task;
import org.opendataspace.android.data.DataBase;
import org.opendataspace.android.event.EventSelectAccount;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.operation.OperationRepoSync;

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

    public void setCurrentAccount(final Account current) {
        OdsApp app = OdsApp.get();
        DataBase db = app.getDatabase();
        app.getPrefs().setLastAccountId(current);

        if (this.current == null) {
            accounts.sync(db.getAccounts(), null);
        }

        this.current = current;
        repos.sync(db.getRepos(), current);
        OdsApp.bus.post(new EventSelectAccount());

        if (this.current != null) {
            app.getPool().execute(new Task() {

                @Override
                public void onExecute() throws Exception {
                    OperationRepoSync sync = new OperationRepoSync(current);
                    sync.execute();
                }
            });
        }
    }
}
