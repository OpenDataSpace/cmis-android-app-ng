package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.FileAdapter;
import org.opendataspace.android.operation.OperationFolderLocal;

import java.io.File;

@SuppressLint("ValidFragment")
public class FragmentFolderLocal extends FragmentBaseList {

    private final OperationFolderLocal op;
    private FileAdapter adapter;

    public FragmentFolderLocal(OperationFolderLocal op) {
        this.op = op;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new FileAdapter(getActivity(), op.getRoot());
        setListAdapter(adapter);
        setEmptyText(getString(R.string.folder_empty));
    }

    @Override
    void onListItemClick(int position) {
        selectFile((File) adapter.getItem(position));
    }

    private void selectFile(File file) {
        if (!file.isDirectory()) {
            return;
        }

        op.setRoot(file);
        adapter.update(file);
        getMainActivity().getNavigation().updateTitle();
    }

    @Override
    public String getTile(Context context) {
        File file = op.getRoot();
        File root = Environment.getExternalStorageDirectory();
        return file.equals(root) ? context.getString(R.string.folder_title) :
                op.getRoot().getAbsolutePath().replaceFirst(root.getAbsolutePath(), "");
    }

    @Override
    public boolean backPressed() {
        File file = op.getRoot();

        if (file.equals(op.getTop())) {
            return false;
        }

        file = file.getParentFile();

        if (file != null) {
            selectFile(file);
            return true;
        }

        return false;
    }
}
