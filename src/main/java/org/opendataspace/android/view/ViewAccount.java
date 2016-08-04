package org.opendataspace.android.view;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.event.EventDaoAccount;
import org.opendataspace.android.object.Account;

public class ViewAccount extends ViewBase<Account> {

    public ViewAccount() {
        super();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventDaoAccount event) {
        processEvent(event);
    }

    @Override
    protected void notifyAdapters() {
        OdsApp.bus.post(new EventDaoAccount());
    }
}
