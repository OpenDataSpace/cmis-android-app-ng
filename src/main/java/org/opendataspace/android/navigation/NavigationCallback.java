package org.opendataspace.android.navigation;

import android.content.Context;

public interface NavigationCallback {

    boolean backPressed();

    String getTile(Context context);
}
