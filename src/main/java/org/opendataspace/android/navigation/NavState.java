package org.opendataspace.android.navigation;

import android.support.v4.app.Fragment;
import com.google.gson.annotations.SerializedName;

public class NavState {

    @SuppressWarnings("CanBeFinal")
    @SerializedName("scope")
    private NavScope scope;
    @SuppressWarnings("CanBeFinal")
    @SerializedName("class")
    private Class<? extends Fragment> cls;

    public NavState(NavScope scope, Class<? extends Fragment> cls) {
        this.scope = scope;
        this.cls = cls;
    }

    public NavScope getNavigationScope() {
        return scope;
    }

    public Class<? extends Fragment> getFragmentClass() {
        return cls;
    }
}
