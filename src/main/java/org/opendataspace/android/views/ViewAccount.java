package org.opendataspace.android.views;

import org.opendataspace.android.event.EventDaoAccount;
import org.opendataspace.android.objects.Account;

public class ViewAccount extends ViewBase<Account> {

    public void onEventMainThread(EventDaoAccount event) {
        processEvent(event);
    }
}
