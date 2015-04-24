package org.opendataspace.android.navigation;

import android.os.Bundle;

import org.opendataspace.android.operation.OperationBase;
import org.opendataspace.android.ui.FragmentBase;

public interface NavigationInterface {

    boolean backPressed();

    FragmentBase getTopFragment();

    void openDialog(Class<? extends FragmentBase> cls, OperationBase op);

    void openRootFolder(Class<? extends FragmentBase> cls, OperationBase op);

    void openFile(Class<? extends FragmentBase> cls, OperationBase op);

    void save(Bundle state);

    void openDrawer();
}
