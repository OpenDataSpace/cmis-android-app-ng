package org.opendataspace.android.test;

import android.os.Bundle;

import org.opendataspace.android.navigation.NavigationInterface;
import org.opendataspace.android.operation.OperationBase;
import org.opendataspace.android.ui.ActivityDialog;
import org.opendataspace.android.ui.FragmentBase;

@SuppressWarnings("unused")
public class TestNavigation implements NavigationInterface {

    private int backCnt = 0;

    @Override
    public boolean backPressed() {
        backCnt++;
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

    @Override
    public void save(Bundle state) {
        // nothing
    }

    @Override
    public void openDrawer() {
        // nothing
    }

    @Override
    public void updateTitle() {
        // nothing
    }

    @Override
    public void updateMenu() {
        // nothing
    }

    @Override
    public void setDialog(ActivityDialog val) {
        // nothing
    }

    public int getBackCnt() {
        return backCnt;
    }
}
