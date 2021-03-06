package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.Task;
import org.opendataspace.android.app.TaskOperation;
import org.opendataspace.android.app.WeakCallback;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.navigation.NavigationInterface;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.operation.OperationFolderBrowse;
import org.opendataspace.android.operation.OperationLocalBrowse;
import org.opendataspace.android.operation.OperationLocalCopyMove;
import org.opendataspace.android.operation.OperationLocalInfo;
import org.opendataspace.android.operation.OperationNodeDownload;
import org.opendataspace.android.operation.OperationNodeUpload;
import org.opendataspace.android.storage.FileAdapter;
import org.opendataspace.android.storage.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressLint("ValidFragment")
public class FragmentFolderLocal extends FragmentBaseList implements ActionMode.Callback {

    private final OperationLocalBrowse op;
    private FileAdapter adapter;
    private ActionMode selection;
    private FileInfo moreItem;
    private OperationLocalCopyMove copymove;

    public FragmentFolderLocal(final OperationLocalBrowse op) {
        this.op = op;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new FileAdapter(getActivity(),
                op.getMode() == OperationLocalBrowse.Mode.DEFAULT ? this::showPopup : null);

        setListAdapter(adapter);
        setEmptyText(getString(R.string.folder_empty));
        selectFile(op.getFolder());
    }

    @Override
    void onListItemClick(final int position) {
        if (selection != null) {
            adapter.select(position);
            return;
        }

        final FileInfo info = (FileInfo) adapter.getItem(position);

        if (!info.isDirectory()) {
            switch (op.getMode()) {
            case DEFAULT:
                getNavigation().openFile(FragmentNodeLocal.class, new OperationLocalInfo(info, op.getAccount()));
                break;

            case SEL_FILES:
                actionApply();
                break;
            }

            return;
        }

        selectFile(info.getFile());
    }

    private void selectFile(final File file) {
        if (file != null && !file.isDirectory()) {
            return;
        }

        op.setFolder(file);
        setListShown(false, false);
        new TaskOperation<>(op, new WeakCallback<>(this, FragmentFolderLocal::browseDone)).start();
    }

    private void browseDone(final OperationLocalBrowse op) {
        browseFinished();
        op.reportError(getActivity());
        adapter.update(op.getData());
    }

    @Override
    public String getTile(final Context context) {
        switch (op.getMode()) {
        case DEFAULT: {
            final File file = op.getFolder();

            if (file == null) {
                return context.getString(R.string.folder_libraries);
            }

            final File root = Environment.getExternalStorageDirectory();
            return file.equals(root) ? context.getString(R.string.folder_slash) :
                    file.getAbsolutePath().replaceFirst(root.getAbsolutePath(), "");
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
    public boolean backPressed() {
        File file = op.getFolder();

        if (file == null) {
            return false;
        }

        if (file.equals(op.getTop())) {
            file = null;
        } else {
            file = file.getParentFile();
        }

        selectFile(file);
        return true;
    }

    private void browseFinished() {
        if (getView() == null) {
            return;
        }

        final NavigationInterface nav = getNavigation();
        nav.updateMenu();
        nav.updateTitle();
        setListShown(true, false);
    }

    @Override
    protected boolean onListItemLongClick(final int position) {
        if (selection != null || op.getMode() != OperationLocalBrowse.Mode.DEFAULT) {
            return false;
        }

        adapter.select(position);
        selection = getMainActivity().startSupportActionMode(this);
        return true;
    }

    @Override
    public boolean onCreateActionMode(final ActionMode actionMode, final Menu menu) {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.menu_local_select, menu);
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

    @Override
    protected int getMenuResource() {
        return R.menu.menu_local_default;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
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
                getNavigation().openFile(FragmentNodeLocal.class, new OperationLocalInfo(moreItem, op.getAccount()));
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

    private void showPopup(final View view) {
        moreItem = adapter.resolve(view.getParent());

        if (moreItem == null) {
            return;
        }

        final Activity ac = getActivity();
        final PopupMenu popup = new PopupMenu(ac, view);
        ac.getMenuInflater().inflate(R.menu.menu_local_more, popup.getMenu());

        onPrepareOptionsMenu(popup.getMenu());
        popup.setOnDismissListener(menu -> moreItem = null);
        popup.setOnMenuItemClickListener(this::onOptionsItemSelected);
        popup.show();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        final boolean isDefault = op.getMode() == OperationLocalBrowse.Mode.DEFAULT;
        final boolean hasContext = op.getFolder() != null;

        setMenuVisibility(menu, R.id.menu_folder_create, isDefault && hasContext);
        setMenuVisibility(menu, R.id.menu_folder_paste, isDefault && hasContext);
        setMenuVisibility(menu, R.id.menu_folder_apply, !isDefault);
        setMenuVisibility(menu, R.id.menu_folder_delete, isDefault && hasContext);
        setMenuVisibility(menu, R.id.menu_folder_upload, isDefault && hasContext);
        setMenuVisibility(menu, R.id.menu_folder_download, isDefault && hasContext);
        setMenuVisibility(menu, R.id.menu_folder_cut, isDefault && hasContext);
        setMenuVisibility(menu, R.id.menu_folder_copy, isDefault && hasContext);
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

    private void createFolder(String name) {
        final File root = op.getFolder();

        if (root == null) {
            return;
        }

        OdsApp.get().getPool().execute(new Task() {

            private FileInfo toAdd;

            @Override
            public void onExecute() throws Exception {
                final File folder = new File(root, name);

                if (!folder.mkdir()) {
                    return;
                }

                toAdd = new FileInfo(folder, OdsApp.get().getDatabase().getMime());
            }

            @Override
            public void onDone() throws Exception {
                adapter.append(toAdd);
            }
        }, false);
    }

    private void actionDelete() {
        final List<FileInfo> ls = getSelection();

        if (ls == null || ls.isEmpty()) {
            return;
        }

        OdsApp.get().getPool().execute(new Task() {

            private final List<FileInfo> toRemove = new ArrayList<>();

            @Override
            public void onExecute() throws Exception {
                for (FileInfo cur : ls) {
                    if (cur.getFile().delete()) {
                        toRemove.add(cur);
                    }
                }
            }

            @Override
            public void onDone() throws Exception {
                adapter.remove(toRemove);
            }
        }, false);
    }

    private void actionCopyCut(boolean isCopy) {
        final List<FileInfo> ls = getSelection();

        if (ls.isEmpty()) {
            return;
        }

        copymove = new OperationLocalCopyMove();
        copymove.setContext(ls, isCopy);
    }

    private void actionPaste() {
        final File node = op.getFolder();

        if (copymove == null || copymove.isEmpty()) {
            return;
        }

        copymove.setTarget(node);
        new TaskOperation<>(copymove, new WeakCallback<>(this, FragmentFolderLocal::copymoveDone)).start();
    }

    private void copymoveDone(final OperationLocalCopyMove op) {
        op.reportError(getActivity());
        selectFile(this.op.getFolder());
    }

    private List<FileInfo> getSelection() {
        if (selection != null) {
            final List<FileInfo> ls = adapter.getSelected();
            selection.finish();
            return ls;
        } else if (moreItem != null) {
            return Collections.singletonList(moreItem);
        } else {
            return Collections.emptyList();
        }
    }

    private void actionApply() {
        switch (op.getMode()) {
        case DEFAULT:
            return;

        case SEL_FOLDER:
            OdsApp.get().getPool().execute(new OperationNodeDownload(op.getSession(), op.getFolder(), op.getContext()));
            break;

        case SEL_FILES: {
            final List<FileInfo> ls = getSelection();
            final List<Node> context = op.getContext();

            if (!ls.isEmpty() && context != null && !context.isEmpty()) {
                OdsApp.get().getPool().execute(new OperationNodeUpload(op.getSession(), context.get(0), ls));
            }
        }
        break;
        }

        getNavigation().backPressed();
    }

    private void actionUpload() {
        final List<FileInfo> ls = getSelection();

        if (ls.isEmpty()) {
            return;
        }

        OperationFolderBrowse browse =
                new OperationFolderBrowse(op.getAccount(), null, OperationFolderBrowse.Mode.SEL_FOLDER);
        browse.setContext(ls);
        getNavigation().openDialog(FragmentRepoList.class, browse);
    }

    private void actionDownload() {
        final File folder = op.getFolder();

        if (folder == null) {
            return;
        }

        OperationFolderBrowse browse =
                new OperationFolderBrowse(op.getAccount(), null, OperationFolderBrowse.Mode.SEL_FILES);
        browse.setContext(Collections.singletonList(new FileInfo(folder, null)));
        getNavigation().openDialog(FragmentRepoList.class, browse);
    }

    public OperationLocalCopyMove getCopyMove() {
        return copymove;
    }
}
