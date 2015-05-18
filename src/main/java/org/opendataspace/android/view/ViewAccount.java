package org.opendataspace.android.view;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.event.EventDaoAccount;
import org.opendataspace.android.object.Account;

public class ViewAccount extends ViewBase<Account> {

    public ViewAccount() {
        super();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(EventDaoAccount event) {
        processEvent(event);
    }

    @Override
    protected void notifyAdapters() {
        OdsApp.bus.post(new EventDaoAccount());
    }
}
