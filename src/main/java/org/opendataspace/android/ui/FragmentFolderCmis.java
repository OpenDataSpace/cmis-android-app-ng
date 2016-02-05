package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.event.Event;
import org.opendataspace.android.event.EventDaoBase;
import org.opendataspace.android.event.EventDaoNode;
import org.opendataspace.android.event.EventNodeUpdate;
import org.opendataspace.android.navigation.NavigationInterface;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.object.NodeAdapter;
import org.opendataspace.android.object.ObjectBase;
import org.opendataspace.android.operation.OperationBase;
import org.opendataspace.android.operation.OperationFolderBrowse;
import org.opendataspace.android.operation.OperationFolderCreate;
import org.opendataspace.android.operation.OperationLoader;
import org.opendataspace.android.operation.OperationLocalBrowse;
import org.opendataspace.android.operation.OperationNodeCopyMove;
import org.opendataspace.android.operation.OperationNodeDelete;
import org.opendataspace.android.operation.OperationNodeDownload;
import org.opendataspace.android.operation.OperationNodeInfo;
import org.opendataspace.android.operation.OperationNodeUpload;
import org.opendataspace.android.operation.OperationResult;
import org.opendataspace.android.storage.FileInfo;

import java.util.Collections;
import java.util.List;

@SuppressLint("ValidFragment")
public class FragmentFolderCmis extends FragmentBaseList
        implements LoaderManager.LoaderCallbacks<OperationResult>, ActionMode.Callback {

    private final static int LOADER_BROWSE = 1;
    private final static int LOADER_NEWFOLDER = 2;
    private final static int LOADER_DELETE = 3;
    private static final int LOADER_COPYMOVE = 4;

    private OperationFolderBrowse op;
    private NodeAdapter adapter;
    private OperationFolderCreate create;
    private ActionMode selection;
    private OperationNodeDelete delete;
    private Node moreItem;
    private OperationNodeCopyMove copymove;

    public FragmentFolderCmis(OperationFolderBrowse op) {
        this.op = op;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new NodeAdapter(op.getView(), getActivity(),
                op.getMode() == OperationFolderBrowse.Mode.DEFAULT ? this::showPopup : null);

        setListAdapter(adapter);
        setEmptyText(getString(R.string.folder_empty));
        OdsApp.bus.register(this, Event.PRIORITY_UI);
        selectNode(op.getFolder(), false);
    }

    @Override
    public void onDestroyView() {
        OdsApp.bus.unregister(this);
        op.getView().dispose();
        adapter.dispose();
        super.onDestroyView();
    }

    @Override
    public String getTile(Context context) {
        switch (op.getMode()) {
        case DEFAULT: {
            Node node = op.getFolder();
            return node != null ? node.getPath(context) : context.getString(R.string.folder_slash);
        }

        case SEL_FOLDER:
            return context.getString(R.string.folder_pickfolder);

        case SEL_FILES:
            return context.getString(R.string.folder_pickfile);

        default:
            return super.getTile(context);
        }
    }

    @Override
    protected int getMenuResource() {
        return R.menu.menu_folder_default;
    }

    @Override
    public Loader<OperationResult> onCreateLoader(int id, Bundle args) {
        switch (id) {
        case LOADER_BROWSE:
            return new OperationLoader(op, getActivity());

        case LOADER_NEWFOLDER:
            return new OperationLoader(create, getActivity());

        case LOADER_DELETE:
            return new OperationLoader(delete, getActivity());

        case LOADER_COPYMOVE:
            return new OperationLoader(copymove, getActivity());

        default:
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<OperationResult> loader, OperationResult data) {
        if (!data.isOk()) {
            Activity ac = getActivity();
            new AlertDialog.Builder(ac).setMessage(data.getMessage(ac)).setCancelable(true)
                    .setPositiveButton(R.string.common_ok, (dialogInterface, i) -> dialogInterface.cancel()).show();
        }

        if (loader.getId() == LOADER_BROWSE) {
            browseFinished(false);
        }
    }

    private void browseFinished(boolean isFinal) {
        if (getView() == null) {
            return;
        }

        NavigationInterface nav = getNavigation();
        nav.updateMenu();
        nav.updateTitle();

        if (isFinal || adapter.getCount() != 0) {
            setListShown(true, false);
        }
    }

    @Override
    public void onLoaderReset(Loader<OperationResult> loader) {
        if (loader.getId() == LOADER_BROWSE) {
            browseFinished(false);
        }
    }

    private void selectNode(Node node, boolean cdup) {
        Node.Type type = node != null ? node.getType() : Node.Type.FOLDER;

        if (type == Node.Type.DOCUMENT) {
            switch (op.getMode()) {
            case DEFAULT:
                getNavigation().openFile(FragmentNodeInfo.class, new OperationNodeInfo(node, op.getSession()));
                break;

            case SEL_FILES:
                actionApply();
                break;
            }

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
        if (selection != null) {
            adapter.select(position);
        } else {
            selectNode(adapter.getObject(position), false);
        }
    }

    @Override
    public boolean backPressed() {
        Node node = op.getFolder();

        if (node != null && node.getId() != ObjectBase.INVALID_ID) {
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

        case R.id.menu_folder_delete:
            actionDelete();
            break;

        case R.id.menu_folder_selectall:
            adapter.selectAll();
            break;

        case R.id.menu_folder_details:
            if (moreItem != null) {
                getNavigation().openFile(FragmentNodeInfo.class, new OperationNodeInfo(moreItem, op.getSession()));
            }
            break;

        case R.id.menu_folder_copy:
            actionCopyCut(true);
            break;

        case R.id.menu_folder_cut:
            actionCopyCut(false);
            break;

        case R.id.menu_folder_paste:
            actionPaste();
            break;

        case R.id.menu_folder_apply:
            actionApply();
            break;

        case R.id.menu_folder_upload:
            actionUpload();
            break;

        case R.id.menu_folder_download:
            actionDownload();
            break;

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void actionCreateFolder() {
        Activity ac = getActivity();
        @SuppressLint("InflateParams") View view = ac.getLayoutInflater().inflate(R.layout.dialog_folder_create, null);
        EditText et = (EditText) view.findViewById(R.id.edit_dialog_name);

        new AlertDialog.Builder(ac).setTitle(R.string.folder_createdlg).setView(view).setCancelable(true)
                .setPositiveButton(R.string.common_ok, (di, i) -> createFolder(et.getText().toString().trim()))
                .setNegativeButton(R.string.common_cancel, (di, i) -> di.dismiss()).show();
    }

    private void startLoader(int id) {
        getLoaderManager().restartLoader(id, null, this);
    }

    private void createFolder(String name) {
        if (TextUtils.isEmpty(name)) {
            return;
        }

        Node node = op.getFolder();

        if (node == null || !node.canCreateFolder()) {
            return;
        }

        create = new OperationFolderCreate(op.getSession(), node, name);
        startLoader(LOADER_NEWFOLDER);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Node node = op.getFolder();
        boolean isDefault = op.getMode() == OperationFolderBrowse.Mode.DEFAULT;

        setMenuVisibility(menu, R.id.menu_folder_create, isDefault && node != null && node.canCreateFolder());
        setMenuVisibility(menu, R.id.menu_folder_paste,
                isDefault && node != null && copymove != null && copymove.canPaste(node));
        setMenuVisibility(menu, R.id.menu_folder_apply, !isDefault);
        setMenuVisibility(menu, R.id.menu_folder_delete, isDefault);
        setMenuVisibility(menu, R.id.menu_folder_upload, isDefault && node != null && node.canCreateDocument());
        setMenuVisibility(menu, R.id.menu_folder_download, isDefault);
        setMenuVisibility(menu, R.id.menu_folder_cut, isDefault);
        setMenuVisibility(menu, R.id.menu_folder_copy, isDefault);
    }

    @Override
    protected boolean onListItemLongClick(int position) {
        if (selection != null || op.getMode() != OperationFolderBrowse.Mode.DEFAULT) {
            return false;
        }

        adapter.select(position);
        selection = getMainActivity().startSupportActionMode(this);
        return true;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.menu_folder_select, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return onOptionsItemSelected(menuItem);
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        selection = null;
        adapter.clearSelection();
    }

    private void actionDelete() {
        List<Node> ls = getSelection();

        if (ls.isEmpty()) {
            return;
        }

        delete = new OperationNodeDelete(ls, op.getSession());
        startLoader(LOADER_DELETE);
    }

    @SuppressWarnings({"UnusedParameters", "unused"})
    public void onEventMainThread(EventNodeUpdate event) {
        Node node = op.getFolder();

        if (node != null && TextUtils.equals(node.getUuid(), event.getNodeUuid())) {
            browseFinished(true);
        }
    }

    @SuppressWarnings({"UnusedParameters", "unused"})
    public void onEventMainThread(EventDaoNode event) {
        for (EventDaoBase.Event<Node> cur : event.getEvents()) {
            if (cur.getObject().equals(op.getFolder())) {
                if (cur.getOperation() == EventDaoBase.Operation.DELETE) {
                    getNavigation().backPressed();
                } else {
                    op.setFolder(cur.getObject());
                    getNavigation().updateMenu();
                }

                break;
            }
        }
    }

    private void showPopup(View view) {
        moreItem = adapter.resolve(view.getParent());

        if (moreItem == null) {
            return;
        }

        Activity ac = getActivity();
        PopupMenu popup = new PopupMenu(ac, view);
        ac.getMenuInflater().inflate(R.menu.menu_folder_more, popup.getMenu());
        onPrepareOptionsMenu(popup.getMenu());
        popup.setOnDismissListener(menu -> moreItem = null);
        popup.setOnMenuItemClickListener(this::onOptionsItemSelected);
        popup.show();
    }

    private void actionCopyCut(boolean isCopy) {
        List<Node> ls = getSelection();

        if (ls.isEmpty()) {
            return;
        }

        copymove = new OperationNodeCopyMove(op.getSession());
        copymove.setContext(ls, isCopy);
    }

    private void actionPaste() {
        Node node = op.getFolder();

        if (copymove == null || !copymove.canPaste(node)) {
            return;
        }

        copymove.setTarget(node);
        startLoader(LOADER_COPYMOVE);
    }

    private void actionApply() {
        switch (op.getMode()) {
        case DEFAULT:
            return;

        case SEL_FOLDER:
            OdsApp.get().getPool().execute(new OperationNodeUpload(op.getSession(), op.getFolder(), op.getContext()));
            break;

        case SEL_FILES: {
            List<Node> ls = getSelection();
            List<FileInfo> context = op.getContext();

            if (!ls.isEmpty() && context != null && !context.isEmpty()) {
                OdsApp.get().getPool()
                        .execute(new OperationNodeDownload(op.getSession(), op.getContext().get(0).getFile(), ls));
            }
        }
        break;
        }

        getNavigation().backPressed();
    }

    private void actionDownload() {
        List<Node> ls = getSelection();

        if (ls.isEmpty()) {
            return;
        }

        OperationLocalBrowse browse =
                new OperationLocalBrowse(op.getSession().getAccount(), OperationLocalBrowse.Mode.SEL_FOLDER);
        browse.setContext(ls);
        browse.setSession(op.getSession());
        getNavigation().openDialog(FragmentFolderLocal.class, browse);
    }

    private void actionUpload() {
        OperationLocalBrowse browse =
                new OperationLocalBrowse(op.getSession().getAccount(), OperationLocalBrowse.Mode.SEL_FILES);
        browse.setContext(Collections.singletonList(op.getFolder()));
        browse.setSession(op.getSession());
        getNavigation().openDialog(FragmentFolderLocal.class, browse);
    }

    private List<Node> getSelection() {
        if (selection != null) {
            List<Node> ls = adapter.getSelected();
            selection.finish();
            return ls;
        } else if (moreItem != null) {
            return Collections.singletonList(moreItem);
        } else {
            return Collections.emptyList();
        }
    }

    public OperationNodeCopyMove getCopyMove() {
        return copymove;
    }
}
