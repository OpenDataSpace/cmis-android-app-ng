package org.opendataspace.android.navigation;

import android.content.Context;

public interface NavigationCallback {

    boolean needDrawer();

    boolean backPressed();

    String getTile(Context context);
}
