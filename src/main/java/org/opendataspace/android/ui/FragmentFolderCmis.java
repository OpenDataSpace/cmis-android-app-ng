package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.TaskOperation;
import org.opendataspace.android.app.WeakCallback;
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
import org.opendataspace.android.operation.OperationLocalBrowse;
import org.opendataspace.android.operation.OperationNodeCopyMove;
import org.opendataspace.android.operation.OperationNodeDelete;
import org.opendataspace.android.operation.OperationNodeDownload;
import org.opendataspace.android.operation.OperationNodeInfo;
import org.opendataspace.android.operation.OperationNodeUpload;
import org.opendataspace.android.storage.FileInfo;

import java.util.Collections;
import java.util.List;

@SuppressLint("ValidFragment")
public class FragmentFolderCmis extends FragmentBaseList implements ActionMode.Callback {

    private OperationFolderBrowse op;
    private NodeAdapter adapter;
    private ActionMode selection;
    private Node moreItem;
    private OperationNodeCopyMove copymove;

    public FragmentFolderCmis(final OperationFolderBrowse op) {
        this.op = op;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
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
    public String getTile(final Context context) {
        switch (op.getMode()) {
        case DEFAULT: {
            final Node node = op.getFolder();
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

    private void browseFinished(final boolean isFinal) {
        if (getView() == null) {
            return;
        }

        final NavigationInterface nav = getNavigation();
        nav.updateMenu();
        nav.updateTitle();

        if (isFinal || adapter.getCount() != 0) {
            setListShown(true, false);
        }
    }

    private void selectNode(final Node node, final boolean cdup) {
        final Node.Type type = node != null ? node.getType() : Node.Type.FOLDER;

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
        new TaskOperation<>(op, new WeakCallback<>(this, FragmentFolderCmis::browseDone)).start();
    }

    private void browseDone(final OperationFolderBrowse op) {
        op.reportError(getActivity());
        browseFinished(false);
    }

    @Override
    void onListItemClick(final int position) {
        if (selection != null) {
            adapter.select(position);
        } else {
            selectNode(adapter.getObject(position), false);
        }
    }

    @Override
    public boolean backPressed() {
        final Node node = op.getFolder();

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

    @SuppressLint("InflateParams")
    private void actionCreateFolder() {
        final Activity ac = getActivity();
        final View view = ac.getLayoutInflater().inflate(R.layout.dialog_folder_create, null);
        final EditText et = (EditText) view.findViewById(R.id.edit_dialog_name);

        new AlertDialog.Builder(ac).setTitle(R.string.folder_createdlg).setView(view).setCancelable(true)
                .setPositiveButton(R.string.common_ok, (di, i) -> createFolder(et.getText().toString().trim()))
                .setNegativeButton(R.string.common_cancel, (di, i) -> di.dismiss()).show();
    }

    private void createFolder(final String name) {
        if (TextUtils.isEmpty(name)) {
            return;
        }

        final Node node = op.getFolder();

        if (node == null || !node.canCreateFolder()) {
            return;
        }

        new TaskOperation<>(new OperationFolderCreate(op.getSession(), node, name),
                new WeakCallback<>(this, FragmentFolderCmis::operationDone)).start();
    }

    private void operationDone(final OperationBase op) {
        op.reportError(getActivity());
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        final Node node = op.getFolder();
        final boolean isDefault = op.getMode() == OperationFolderBrowse.Mode.DEFAULT;

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
    protected boolean onListItemLongClick(final int position) {
        if (selection != null || op.getMode() != OperationFolderBrowse.Mode.DEFAULT) {
            return false;
        }

        adapter.select(position);
        selection = getMainActivity().startSupportActionMode(this);
        return true;
    }

    @Override
    public boolean onCreateActionMode(final ActionMode actionMode, final Menu menu) {
        final MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.menu_folder_select, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(final ActionMode actionMode, final Menu menu) {
        onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onActionItemClicked(final ActionMode actionMode, final MenuItem menuItem) {
        return onOptionsItemSelected(menuItem);
    }

    @Override
    public void onDestroyActionMode(final ActionMode actionMode) {
        selection = null;
        adapter.clearSelection();
    }

    private void actionDelete() {
        List<Node> ls = getSelection();

        if (ls.isEmpty()) {
            return;
        }

        new TaskOperation<>(new OperationNodeDelete(ls, op.getSession()),
                new WeakCallback<>(this, FragmentFolderCmis::operationDone)).start();
    }

    @SuppressWarnings({"UnusedParameters", "unused"})
    public void onEventMainThread(final EventNodeUpdate event) {
        final Node node = op.getFolder();

        if (node != null && TextUtils.equals(node.getUuid(), event.getNodeUuid())) {
            browseFinished(true);
        }
    }

    @SuppressWarnings({"UnusedParameters", "unused"})
    public void onEventMainThread(final EventDaoNode event) {
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

    private void showPopup(final View view) {
        moreItem = adapter.resolve(view.getParent());

        if (moreItem == null) {
            return;
        }

        final Activity ac = getActivity();
        final PopupMenu popup = new PopupMenu(ac, view);
        ac.getMenuInflater().inflate(R.menu.menu_folder_more, popup.getMenu());

        onPrepareOptionsMenu(popup.getMenu());
        popup.setOnDismissListener(menu -> moreItem = null);
        popup.setOnMenuItemClickListener(this::onOptionsItemSelected);
        popup.show();
    }

    private void actionCopyCut(final boolean isCopy) {
        final List<Node> ls = getSelection();

        if (ls.isEmpty()) {
            return;
        }

        copymove = new OperationNodeCopyMove(op.getSession());
        copymove.setContext(ls, isCopy);
    }

    private void actionPaste() {
        final Node node = op.getFolder();

        if (copymove == null || !copymove.canPaste(node)) {
            return;
        }

        copymove.setTarget(node);
        new TaskOperation<>(copymove, new WeakCallback<>(this, FragmentFolderCmis::operationDone)).start();
    }

    private void actionApply() {
        switch (op.getMode()) {
        case DEFAULT:
            return;

        case SEL_FOLDER:
            OdsApp.get().getPool().execute(new OperationNodeUpload(op.getSession(), op.getFolder(), op.getContext()));
            break;

        case SEL_FILES: {
            final List<Node> ls = getSelection();
            final List<FileInfo> context = op.getContext();

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
        final List<Node> ls = getSelection();

        if (ls.isEmpty()) {
            return;
        }

        final OperationLocalBrowse browse =
                new OperationLocalBrowse(op.getSession().getAccount(), OperationLocalBrowse.Mode.SEL_FOLDER);
        browse.setContext(ls);
        browse.setSession(op.getSession());
        getNavigation().openDialog(FragmentFolderLocal.class, browse);
    }

    private void actionUpload() {
        final OperationLocalBrowse browse =
                new OperationLocalBrowse(op.getSession().getAccount(), OperationLocalBrowse.Mode.SEL_FILES);
        browse.setContext(Collections.singletonList(op.getFolder()));
        browse.setSession(op.getSession());
        getNavigation().openDialog(FragmentFolderLocal.class, browse);
    }

    private List<Node> getSelection() {
        if (selection != null) {
            final List<Node> ls = adapter.getSelected();
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
