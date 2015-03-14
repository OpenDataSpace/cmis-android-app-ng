package org.opendataspace.android.navigation;

import android.support.v4.app.Fragment;

import com.google.gson.annotations.SerializedName;

public class NavState {

    @SerializedName("scope")
    private NavScope scope;
    @SerializedName("class")
    private Class<? extends Fragment> cls;

    public NavState(NavScope scope, Class<? extends Fragment> cls) {
        this.scope = scope;
        this.cls = cls;
    }

    public NavScope getNavigationScope() {
        return scope;
    }

    public void setNavigationScope(NavScope scope) {
        this.scope = scope;
    }

    public Class<? extends Fragment> getFragmentClass() {
        return cls;
    }

    public void setFragmentClass(Class<? extends Fragment> cls) {
        this.cls = cls;
    }
}
