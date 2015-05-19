package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.object.NodeAdapter;
import org.opendataspace.android.operation.OperationBase;
import org.opendataspace.android.operation.OperationFolderBrowse;
import org.opendataspace.android.operation.OperationLoader;
import org.opendataspace.android.operation.OperationStatus;

@SuppressLint("ValidFragment")
public class FragmentFolderCmis extends FragmentBaseList implements LoaderManager.LoaderCallbacks<OperationStatus> {

    private OperationFolderBrowse op;
    private NodeAdapter adapter;
    private boolean inProgress;

    public FragmentFolderCmis(OperationFolderBrowse op) {
        this.op = op;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new NodeAdapter(OdsApp.get().getViewManager().getNodes(), getActivity());
        setListAdapter(adapter);
        setEmptyText(getString(R.string.folder_empty));
        selectNode(null, false);
    }

    @Override
    public void onDestroyView() {
        adapter.dispose();
        super.onDestroyView();
    }

    @Override
    public String getTile(Context context) {
        return context.getString(R.string.folder_title);
    }

    @Override
    protected int getMenuResource() {
        return R.menu.menu_folder;
    }

    @Override
    public Loader<OperationStatus> onCreateLoader(int id, Bundle args) {
        return new OperationLoader(op, getActivity());
    }

    @Override
    public void onLoadFinished(Loader<OperationStatus> loader, OperationStatus data) {
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

    private void selectNode(Node node, boolean cdup) {
        if (inProgress || (node != null && node.getType() != Node.Type.FOLDER)) {
            return;
        }

        inProgress = true;
        setListShown(false, false);
        op.setFolder(node);
        op.setCdup(cdup);
        getLoaderManager().restartLoader(1, null, this);
    }

    @Override
    void onListItemClick(int position) {
        selectNode(adapter.getObject(position), false);
    }

    @Override
    public boolean backPressed() {
        Node node = op.getFolder();

        if (node != null) {
            selectNode(node, true);
            return true;
        }

        return false;
    }

    @Override
    public void navigationRequest(OperationBase op) {
        if (op instanceof OperationFolderBrowse) {
            this.op = (OperationFolderBrowse) op;
            selectNode(null, false);
        }
    }
}
