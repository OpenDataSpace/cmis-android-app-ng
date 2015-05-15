package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.NodeAdapter;
import org.opendataspace.android.operation.OperationFolderBrowse;

@SuppressLint("ValidFragment")
public class FragmentFolder extends FragmentBaseList {

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
        setEmptyText(getString(R.string.folder_empty));
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
}
