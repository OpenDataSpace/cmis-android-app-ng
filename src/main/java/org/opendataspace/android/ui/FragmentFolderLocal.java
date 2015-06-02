package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.Task;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.operation.*;
import org.opendataspace.android.storage.FileAdapter;
import org.opendataspace.android.storage.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressLint("ValidFragment")
public class FragmentFolderLocal extends FragmentBaseList
        implements LoaderManager.LoaderCallbacks<OperationStatus>, ActionMode.Callback {

    private final static int LOADER_BROWSE = 1;
    private final static int LOADER_COPYMOVE = 2;

    private final OperationLocalBrowse op;
    private FileAdapter adapter;
    private ActionMode selection;
    private FileInfo moreItem;
    private OperationLocalCopyMove copymove;

    public FragmentFolderLocal(OperationLocalBrowse op) {
        this.op = op;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new FileAdapter(getActivity(),
                op.getMode() == OperationLocalBrowse.Mode.DEFAULT ? this::showPopup : null);

        setListAdapter(adapter);
        setEmptyText(getString(R.string.folder_empty));
        selectFile(op.getFolder());
    }

    @Override
    void onListItemClick(int position) {
        if (selection != null) {
            adapter.select(position);
            return;
        }

        FileInfo info = (FileInfo) adapter.getItem(position);

        if (!info.isDirectory()) {
            switch (op.getMode()) {
            case DEFAULT:
                getNavigation().openFile(FragmentNodeLocal.class, new OperationLocalInfo(info));
                break;

            case SEL_FILES:
                actionApply();
                break;
            }

            return;
        }

        selectFile(info.getFile());
    }

    private void selectFile(File file) {
        if (file != null && !file.isDirectory()) {
            return;
        }

        op.setFolder(file);
        setListShown(false, false);
        getLoaderManager().restartLoader(LOADER_BROWSE, null, this);
        getNavigation().updateTitle();
    }

    @Override
    public String getTile(Context context) {
        switch (op.getMode()) {
        case DEFAULT: {
            File file = op.getFolder();

            if (file == null) {
                return context.getString(R.string.folder_libraries);
            }

            File root = Environment.getExternalStorageDirectory();
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

    @Override
    public Loader<OperationStatus> onCreateLoader(int id, Bundle args) {
        switch (id) {
        case LOADER_BROWSE:
            return new OperationLoader(op, getActivity());

        case LOADER_COPYMOVE:
            return new OperationLoader(copymove, getActivity());

        default:
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<OperationStatus> loader, OperationStatus data) {
        loadingDone();

        if (!data.isOk()) {
            Activity ac = getActivity();
            new AlertDialog.Builder(ac).setMessage(data.getMessage(ac)).setCancelable(true)
                    .setPositiveButton(R.string.common_ok, (dialogInterface, i) -> dialogInterface.cancel()).show();
            return;
        }

        switch (loader.getId()) {
        case LOADER_BROWSE:
            adapter.update(op.getData());
            break;

        case LOADER_COPYMOVE:
            selectFile(op.getFolder());
            break;
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
    }

    @Override
    protected boolean onListItemLongClick(int position) {
        if (selection != null || op.getMode() != OperationLocalBrowse.Mode.DEFAULT) {
            return false;
        }

        adapter.select(position);
        selection = getMainActivity().startSupportActionMode(this);
        return true;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater inflater = actionMode.getMenuInflater();
        inflater.inflate(R.menu.menu_local_select, menu);
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

    @Override
    protected int getMenuResource() {
        return R.menu.menu_local_default;
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
                getNavigation().openFile(FragmentNodeLocal.class, new OperationLocalInfo(moreItem));
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

    private void showPopup(View view) {
        moreItem = adapter.resolve(view.getParent());

        if (moreItem == null) {
            return;
        }

        Activity ac = getActivity();
        PopupMenu popup = new PopupMenu(ac, view);
        ac.getMenuInflater().inflate(R.menu.menu_local_more, popup.getMenu());
        onPrepareOptionsMenu(popup.getMenu());
        popup.setOnDismissListener(menu -> moreItem = null);
        popup.setOnMenuItemClickListener(this::onOptionsItemSelected);
        popup.show();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        boolean isDefault = op.getMode() == OperationLocalBrowse.Mode.DEFAULT;

        setMenuVisibility(menu, R.id.menu_folder_create, isDefault);
        setMenuVisibility(menu, R.id.menu_folder_paste, isDefault);
        setMenuVisibility(menu, R.id.menu_folder_apply, !isDefault);
        setMenuVisibility(menu, R.id.menu_folder_upload, isDefault);
        setMenuVisibility(menu, R.id.menu_folder_download, isDefault);
        setMenuVisibility(menu, R.id.menu_folder_cut, isDefault);
        setMenuVisibility(menu, R.id.menu_folder_copy, isDefault);
    }

    private void actionCreateFolder() {
        Activity ac = getActivity();
        @SuppressLint("InflateParams") View view = ac.getLayoutInflater().inflate(R.layout.dialog_folder_create, null);
        EditText et = (EditText) view.findViewById(R.id.edit_dialog_name);

        new AlertDialog.Builder(ac).setTitle(R.string.folder_createdlg).setView(view).setCancelable(true)
                .setPositiveButton(R.string.common_ok, (di, i) -> createFolder(et.getText().toString().trim()))
                .setNegativeButton(R.string.common_cancel, (di, i) -> di.dismiss()).show();
    }

    private void createFolder(String name) {
        File root = op.getFolder();

        if (root == null) {
            return;
        }

        OdsApp.get().getPool().execute(new Task() {

            private FileInfo toAdd;

            @Override
            public void onExecute() throws Exception {
                File folder = new File(root, name);

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
        List<FileInfo> ls = getSelection();

        if (ls == null || ls.isEmpty()) {
            return;
        }

        final List<FileInfo> finalLs = ls;

        OdsApp.get().getPool().execute(new Task() {

            private final List<FileInfo> toRemove = new ArrayList<>();

            @Override
            public void onExecute() throws Exception {
                for (FileInfo cur : finalLs) {
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
        List<FileInfo> ls = getSelection();

        if (ls.isEmpty()) {
            return;
        }

        copymove = new OperationLocalCopyMove();
        copymove.setContext(ls, isCopy);
    }

    private void actionPaste() {
        File node = op.getFolder();

        if (copymove == null || copymove.isEmpty()) {
            return;
        }

        copymove.setTarget(node);
        getLoaderManager().restartLoader(LOADER_COPYMOVE, null, this);
    }

    private List<FileInfo> getSelection() {
        if (selection != null) {
            List<FileInfo> ls = adapter.getSelected();
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
            List<FileInfo> ls = getSelection();
            List<Node> context = op.getContext();

            if (!ls.isEmpty() && context != null && !context.isEmpty()) {
                OdsApp.get().getPool().execute(new OperationNodeUpload(op.getSession(), context.get(0), ls));
            }
        }
        break;
        }

        getNavigation().backPressed();
    }

    private void actionUpload() {
        List<FileInfo> ls = getSelection();

        if (ls.isEmpty()) {
            return;
        }

        // TODO select repo
        CmisSession session = op.getSession();

        if (session == null) {
            return;
        }

        OperationFolderBrowse browse = new OperationFolderBrowse(session.getAccount(), session.getRepo(),
                OperationFolderBrowse.Mode.SEL_FOLDER);

        browse.setContext(ls);
        getNavigation().openDialog(FragmentFolderCmis.class, browse);
    }

    private void actionDownload() {
        CmisSession session = op.getSession();

        if (session == null) {
            return;
        }

        // TODO select repo
        OperationFolderBrowse browse = new OperationFolderBrowse(session.getAccount(), session.getRepo(),
                OperationFolderBrowse.Mode.SEL_FILES);

        browse.setContext(Collections.singletonList(new FileInfo(op.getFolder(), null)));
        getNavigation().openDialog(FragmentFolderCmis.class, browse);
    }
}
