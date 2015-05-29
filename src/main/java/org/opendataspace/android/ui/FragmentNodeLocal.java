package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.operation.OperationNodeLocal;
import org.opendataspace.android.operation.OperationNodeRename;
import org.opendataspace.android.storage.FileInfo;

import java.io.File;
import java.sql.SQLException;

@SuppressLint("ValidFragment")
public class FragmentNodeLocal extends FragmentBase {

    private final OperationNodeLocal op;
    private OperationNodeRename rename;

    public FragmentNodeLocal(OperationNodeLocal op) {
        this.op = op;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nodelocal, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateInfo();
    }

    private void updateInfo() {
        FileInfo info = op.getFileInfo();
        Activity ac = getActivity();
        TextView tvt = widget(R.id.text_node_title);

        tvt.setText(info.getName());
        tvt.setCompoundDrawablesWithIntrinsicBounds(info.getIcon(ac), 0, 0, 0);
        this.<TextView>widget(R.id.text_node_details).setText(info.getNodeDecription(ac));
        this.<TextView>widget(R.id.text_node_name).setText(info.getName());
        this.<TextView>widget(R.id.text_node_path).setText(info.getFile().getParentFile().getAbsolutePath());
        this.<TextView>widget(R.id.text_node_type).setText(info.getMimeDescription(ac));
        this.<TextView>widget(R.id.text_node_size).setText(Formatter.formatShortFileSize(ac, info.getFile().length()));
        this.<TextView>widget(R.id.text_node_modified).setText(info.getModifiedAt(ac));
    }

    @Override
    public String getTile(Context context) {
        return context.getString(R.string.node_title);
    }

    @Override
    int getMenuResource() {
        return R.menu.menu_node_local;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_node_delete:
            actionDelete();
            break;

        case R.id.menu_node_rename:
            actionRename();
            break;

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void actionDelete() {
        FileInfo info = op.getFileInfo();

        if (info == null) {
            return;
        }

        new AlertDialog.Builder(getActivity())
                .setMessage(String.format(getString(R.string.common_delete), info.getName())).setCancelable(true)
                .setPositiveButton(R.string.common_ok, (di, i) -> deleteFile(info))
                .setNegativeButton(R.string.common_cancel, (di, i) -> di.cancel()).show();
    }

    private void actionRename() {
        FileInfo info = op.getFileInfo();

        if (info == null) {
            return;
        }

        Activity ac = getActivity();
        @SuppressLint("InflateParams") View view = ac.getLayoutInflater().inflate(R.layout.dialog_node_rename, null);
        EditText et = (EditText) view.findViewById(R.id.edit_dialog_name);
        et.setText(info.getName());

        new AlertDialog.Builder(ac).setTitle(R.string.node_rename).setView(view).setCancelable(true)
                .setPositiveButton(R.string.common_ok, (di, i) -> renameNode(info, et.getText().toString().trim()))
                .setNegativeButton(R.string.common_cancel, (di, i) -> di.dismiss()).show();
    }

    private void renameNode(FileInfo info, String name) {
        if (TextUtils.isEmpty(name) || info == null) {
            return;
        }

        File of = info.getFile();
        File nf = new File(of.getParent(), name);

        if (!of.renameTo(nf)) {
            new AlertDialog.Builder(getActivity()).setMessage(R.string.common_error).setCancelable(true)
                    .setPositiveButton(R.string.common_ok, (dialogInterface, i) -> dialogInterface.cancel()).show();
            return;
        }

        try {
            op.setInfo(new FileInfo(nf, OdsApp.get().getDatabase().getMime()));
        } catch (SQLException ex) {
            OdsLog.ex(getClass(), ex);
        }

        updateInfo();
    }

    private void deleteFile(FileInfo info) {
        if (info.getFile().delete()) {
            getMainActivity().getNavigation().backPressed();
        } else {
            new AlertDialog.Builder(getActivity()).setMessage(R.string.common_error).setCancelable(true)
                    .setPositiveButton(R.string.common_ok, (dialogInterface, i) -> dialogInterface.cancel()).show();
        }
    }
}
