package org.opendataspace.android.navigation;

import com.google.gson.annotations.Expose;
import org.opendataspace.android.app.ClassWrapper;
import org.opendataspace.android.operations.OperatonBase;
import org.opendataspace.android.ui.FragmentBase;

public class NavigationState {

    @Expose
    private final NavigationScope scope;

    @Expose
    private final Class<? extends FragmentBase> cls;

    @Expose
    private final ClassWrapper op;

    public NavigationState(NavigationScope scope, Class<? extends FragmentBase> cls, OperatonBase op) {
        this.scope = scope;
        this.cls = cls;
        this.op = new ClassWrapper(op);
    }

    public NavigationScope getNavigationScope() {
        return scope;
    }

    public Class<? extends FragmentBase> getFragmentClass() {
        return cls;
    }

    public OperatonBase getOperation() {
        return (OperatonBase) op.getObject();
    }
}
