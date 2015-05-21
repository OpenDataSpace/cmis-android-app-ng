package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.operation.OperationFolderLocal;
import org.opendataspace.android.operation.OperationLoader;
import org.opendataspace.android.operation.OperationStatus;
import org.opendataspace.android.storage.FileAdapter;
import org.opendataspace.android.storage.FileInfo;

import java.io.File;

@SuppressLint("ValidFragment")
public class FragmentFolderLocal extends FragmentBaseList implements LoaderManager.LoaderCallbacks<OperationStatus> {

    private final OperationFolderLocal op;
    private FileAdapter adapter;
    private boolean inProgress;

    public FragmentFolderLocal(OperationFolderLocal op) {
        this.op = op;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new FileAdapter(getActivity());
        setListAdapter(adapter);
        setEmptyText(getString(R.string.folder_empty));
        selectFile(op.getRoot());
    }

    @Override
    void onListItemClick(int position) {
        selectFile(((FileInfo) adapter.getItem(position)).getFile());
    }

    private void selectFile(File file) {
        if (inProgress || !file.isDirectory()) {
            return;
        }

        op.setRoot(file);
        inProgress = true;
        setListShown(false, false);
        getLoaderManager().restartLoader(1, null, this);
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

    @Override
    public Loader<OperationStatus> onCreateLoader(int id, Bundle args) {
        return new OperationLoader(op, getActivity());
    }

    @Override
    public void onLoadFinished(Loader<OperationStatus> loader, OperationStatus data) {
        if (data.isOk()) {
            adapter.update(op.getData());
        }

        loadingDone();
    }

    @Override
    public void onLoaderReset(Loader<OperationStatus> loader) {
        loadingDone();
    }

    private void loadingDone() {
        if (getView() != null) {
            setListShown(true, false);
        }

        inProgress = false;
    }
}
