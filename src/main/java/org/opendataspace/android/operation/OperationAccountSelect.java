package org.opendataspace.android.operation;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DataBase;
import org.opendataspace.android.event.EventAccountSelect;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.ObjectBase;
import org.opendataspace.android.view.ViewManager;

public class OperationAccountSelect extends OperationBase {

    @Expose
    private Account account;

    public OperationAccountSelect(Account account) {
        this.account = account;
    }

    @Override
    protected void doExecute(OperationStatus status) throws Exception {
        OdsApp app = OdsApp.get();
        DataBase db = app.getDatabase();
        ViewManager vm = app.getViewManager();

        if (account == null) {
            long id = app.getPrefs().getLastAccountId();

            if (id != ObjectBase.INVALID_ID) {
                account = db.getAccounts().get(id);
            }
        }

        vm.setCurrentAccount(account);

        if (account == null) {
            OdsApp.bus.post(new EventAccountSelect());
            return;
        }

        if (vm.getAccounts().getCount() == 0) {
            vm.getAccounts().sync(db.getAccounts());
        }

        vm.getRepos().setAccount(account);
        vm.getRepos().sync(db.getRepos());
        OdsApp.bus.post(new EventAccountSelect());
        app.getPool().execute(new OperationRepoFetch(account));
        status.setOk();
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
