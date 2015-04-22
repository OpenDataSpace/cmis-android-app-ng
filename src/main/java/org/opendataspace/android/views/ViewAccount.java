package org.opendataspace.android.views;

import de.greenrobot.event.EventBus;
import org.opendataspace.android.data.DaoAccountEvent;
import org.opendataspace.android.objects.Account;

public class ViewAccount extends ViewBase<Account> {

    public void onEventMainThread(DaoAccountEvent event) {
        if (processEvent(event)) {
            EventBus.getDefault().post(new ViewAccountEvent());
        }
    }
}
