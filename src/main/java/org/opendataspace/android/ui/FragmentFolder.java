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
import org.opendataspace.android.operation.OperationFolderBrowse;
import org.opendataspace.android.operation.OperationLoader;
import org.opendataspace.android.operation.OperationStatus;

@SuppressLint("ValidFragment")
public class FragmentFolder extends FragmentBaseList implements LoaderManager.LoaderCallbacks<OperationStatus> {

    private final OperationFolderBrowse op;
    private NodeAdapter adapter;

    public FragmentFolder(OperationFolderBrowse op) {
        this.op = op;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new NodeAdapter(OdsApp.get().getViewManager().getNodes(), getActivity());
        setListAdapter(adapter);
        selectNode(null);
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
        if (getView() != null) {
            setEmptyText(getString(R.string.folder_empty));
        }
    }

    @Override
    public void onLoaderReset(Loader<OperationStatus> loader) {
        if (getView() != null) {
            setEmptyText(getString(R.string.folder_empty));
        }
    }

    private void selectNode(Node node) {
        if (node != null && node.getType() != Node.Type.FOLDER) {
            return;
        }

        setEmptyText(getString(R.string.common_pleasewait));
        op.setFolder(node);
        getLoaderManager().restartLoader(1, null, this);
    }

    @Override
    void onListItemClick(int position) {
        selectNode(adapter.getObject(position));
    }
}
