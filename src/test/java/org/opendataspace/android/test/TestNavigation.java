package org.opendataspace.android.test;

import org.opendataspace.android.navigation.NavigationInterface;
import org.opendataspace.android.operations.OperationBase;
import org.opendataspace.android.ui.FragmentBase;

public class TestNavigation implements NavigationInterface {

    @Override
    public boolean backPressed() {
        return true;
    }

    @Override
    public FragmentBase getTopFragment() {
        return null;
    }

    @Override
    public void openDialog(Class<? extends FragmentBase> cls, OperationBase op) {
        // nothing
    }

    @Override
    public void openRootFolder(Class<? extends FragmentBase> cls, OperationBase op) {
        // nothing
    }

    @Override
    public void openFile(Class<? extends FragmentBase> cls, OperationBase op) {
        // nothing
    }
}
