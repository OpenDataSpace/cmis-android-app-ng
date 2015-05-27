package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.object.NodeAdapter;
import org.opendataspace.android.object.ObjectBase;
import org.opendataspace.android.operation.OperationBase;
import org.opendataspace.android.operation.OperationFolderBrowse;
import org.opendataspace.android.operation.OperationFolderCreate;
import org.opendataspace.android.operation.OperationLoader;
import org.opendataspace.android.operation.OperationNodeBrowse;
import org.opendataspace.android.operation.OperationStatus;

@SuppressLint("ValidFragment")
public class FragmentFolderCmis extends FragmentBaseList implements LoaderManager.LoaderCallbacks<OperationStatus> {

    private final static int LOADER_BROWSE = 1;
    private final static int LOADER_NEWFOLDER = 2;

    private OperationFolderBrowse op;
    private NodeAdapter adapter;
    private boolean inProgress;
    private OperationFolderCreate create;

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
        return context.getString(R.string.folder_slash);
    }

    @Override
    protected int getMenuResource() {
        return R.menu.menu_folder;
    }

    @Override
    public Loader<OperationStatus> onCreateLoader(int id, Bundle args) {
        switch (id) {
        case LOADER_BROWSE:
            return new OperationLoader(op, getActivity());

        case LOADER_NEWFOLDER:
            return new OperationLoader(create, getActivity());

        default:
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<OperationStatus> loader, OperationStatus data) {
        ActivityMain ac = getMainActivity();
        loadingDone();

        if (!data.isOk()) {
            new AlertDialog.Builder(ac).setMessage(data.getMessage(ac)).setCancelable(true)
                    .setPositiveButton(R.string.common_ok, (dialogInterface, i) -> {
                        dialogInterface.cancel();
                    }).show();
        } else {
            switch (loader.getId()) {
            case LOADER_BROWSE:
                ac.getNavigation().updateMenu();
                break;
            }
        }
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
        if (inProgress) {
            return;
        }

        Node.Type type = node != null ? node.getType() : Node.Type.FOLDER;

        if (type == Node.Type.DOCUMENT) {
            getMainActivity().getNavigation()
                    .openFile(FragmentNodeInfo.class, new OperationNodeBrowse(node, op.getSession()));

            return;
        }

        if (type != Node.Type.FOLDER) {
            return;
        }

        setListShown(false, false);
        op.setFolder(node);
        op.setCdup(cdup);
        startLoader(LOADER_BROWSE);
    }

    @Override
    void onListItemClick(int position) {
        selectNode(adapter.getObject(position), false);
    }

    @Override
    public boolean backPressed() {
        Node node = op.getFolder();

        if (node.getId() != ObjectBase.INVALID_ID) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_folder_create:
            actionCreateFolder();
            break;

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void actionCreateFolder() {
        if (inProgress) {
            return;
        }

        Activity ac = getActivity();
        @SuppressLint("InflateParams") View view = ac.getLayoutInflater().inflate(R.layout.dialog_folder_create, null);
        EditText et = (EditText) view.findViewById(R.id.edit_dialog_name);

        new AlertDialog.Builder(ac).setTitle(R.string.folder_createdlg).setView(view).setCancelable(true)
                .setPositiveButton(R.string.common_ok, (di, i) -> createFolder(et.getText().toString().trim()))
                .setNegativeButton(R.string.common_cancel, (di, i) -> di.dismiss()).show();
    }

    private void startLoader(int id) {
        if (inProgress) {
            return;
        }

        inProgress = true;
        getLoaderManager().restartLoader(id, null, this);
    }

    private void createFolder(String name) {
        if (TextUtils.isEmpty(name) || inProgress) {
            return;
        }

        create = new OperationFolderCreate(op.getSession(), op.getFolder(), name);
        startLoader(LOADER_NEWFOLDER);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem mi = menu.findItem(R.id.menu_folder_create);

        if (mi != null) {
            Node node = op.getFolder();
            mi.setVisible(node != null && node.canCreateFolder());
        }
    }
}
