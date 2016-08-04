package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.app.TaskOperation;
import org.opendataspace.android.app.WeakCallback;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.event.Event;
import org.opendataspace.android.event.EventDaoBase;
import org.opendataspace.android.event.EventDaoNode;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.operation.OperationNodeDelete;
import org.opendataspace.android.operation.OperationNodeInfo;
import org.opendataspace.android.operation.OperationNodeOpen;
import org.opendataspace.android.operation.OperationNodeRename;

import java.io.File;
import java.lang.ref.WeakReference;

@SuppressLint("ValidFragment")
public class FragmentNodeInfo extends FragmentBase {

    private final OperationNodeInfo op;

    public FragmentNodeInfo(final OperationNodeInfo op) {
        this.op = op;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nodeinfo, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        OdsApp.bus.register(this);
        updateInfo();

        final WeakReference<FragmentNodeInfo> weak = new WeakReference<>(this);

        OdsApp.get().getCmisCache().load(op.getNode(), op.getSession(), widget(R.id.image_node_preview), result -> {
            final FragmentNodeInfo fgm = weak.get();

            if (fgm == null) {
                return;
            }

            if (result) {
                final ViewSwitcher vs = fgm.widget(R.id.view_node_preview);

                if (vs != null) {
                    vs.showNext();
                }
            } else {
                final TextView tve = fgm.widget(R.id.text_node_preview);

                if (tve != null) {
                    tve.setText(R.string.node_nopreview);
                }
            }
        });
    }

    private void updateInfo() {
        final Node node = op.getNode();
        final Activity ac = getActivity();
        final TextView tvt = widget(R.id.text_node_title);

        tvt.setText(node.getName());
        tvt.setCompoundDrawablesWithIntrinsicBounds(node.getIcon(ac), 0, 0, 0);
        this.<TextView>widget(R.id.text_node_details).setText(node.getNodeDecription(ac));
        this.<TextView>widget(R.id.text_node_name).setText(node.getName());
        this.<TextView>widget(R.id.text_node_path).setText(node.getPath(ac));
        this.<TextView>widget(R.id.text_node_type).setText(node.getMimeDescription(ac));
        this.<TextView>widget(R.id.text_node_size).setText(Formatter.formatShortFileSize(ac, node.getSize()));
        this.<TextView>widget(R.id.text_node_created).setText(node.getCreatedAt(ac));
        this.<TextView>widget(R.id.text_node_creator).setText(node.getCreatedBy());
        this.<TextView>widget(R.id.text_node_modified).setText(node.getModifiedAt(ac));
        this.<TextView>widget(R.id.text_node_modifier).setText(node.getModifiedBy());
    }

    @Override
    public String getTile(final Context context) {
        return context.getString(R.string.node_title);
    }

    @Override
    public void onDestroyView() {
        OdsApp.bus.unregister(this);
        OdsApp.get().getCmisCache().cancel(widget(R.id.image_node_preview));
        super.onDestroyView();
    }

    @Override
    int getMenuResource() {
        return R.menu.menu_node_info;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_node_delete:
            actionDelete();
            break;

        case R.id.menu_node_rename:
            actionRename();
            break;

        case R.id.menu_node_open:
            actionOpen();
            break;

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void actionDelete() {
        final Node node = op.getNode();

        if (node == null || !node.canDelete()) {
            return;
        }

        new AlertDialog.Builder(getActivity())
                .setMessage(String.format(getString(R.string.common_delete), node.getName())).setCancelable(true)
                .setPositiveButton(R.string.common_ok, (di, i) -> doDelete())
                .setNegativeButton(R.string.common_cancel, (di, i) -> di.cancel()).show();
    }

    private void doDelete() {
        final ActivityMain ac = getMainActivity();
        final OperationNodeDelete op = new OperationNodeDelete(this.op.getNode(), this.op.getSession());

        ac.startWaitDialog(getTile(ac), getString(R.string.common_pleasewait), di -> op.setCancel(true));
        new TaskOperation<>(op, new WeakCallback<>(this, FragmentNodeInfo::deleteDone)).start();
    }

    private void deleteDone(final OperationNodeDelete op) {
        final ActivityMain ac = getMainActivity();
        ac.stopWait();

        if (!op.reportError(ac)) {
            getNavigation().backPressed();
        }
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        final MenuItem mi = menu.findItem(R.id.menu_node_delete);

        if (mi != null) {
            final Node node = op.getNode();
            mi.setVisible(node != null && node.canDelete());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = Event.PRIORITY_UI)
    public void onEvent(final EventDaoNode event) {
        for (EventDaoBase.Event<Node> cur : event.getEvents()) {
            if (cur.getObject().equals(op.getNode())) {
                if (cur.getOperation() == EventDaoBase.Operation.DELETE) {
                    getMainActivity().stopWait();
                    getNavigation().backPressed();
                } else {
                    op.setNode(cur.getObject());
                    updateInfo();
                    getNavigation().updateMenu();
                }

                break;
            }
        }
    }

    @SuppressLint("InflateParams")
    private void actionRename() {
        final Node node = op.getNode();

        if (node == null || !node.canEdit()) {
            return;
        }

        final Activity ac = getActivity();
        final View view = ac.getLayoutInflater().inflate(R.layout.dialog_node_rename, null);
        final EditText et = (EditText) view.findViewById(R.id.edit_dialog_name);
        et.setText(node.getName());

        new AlertDialog.Builder(ac).setTitle(R.string.node_rename).setView(view).setCancelable(true)
                .setPositiveButton(R.string.common_ok, (di, i) -> renameNode(node, et.getText().toString().trim()))
                .setNegativeButton(R.string.common_cancel, (di, i) -> di.dismiss()).show();
    }

    private void renameNode(final Node node, final String name) {
        if (TextUtils.isEmpty(name) || node == null || !node.canEdit()) {
            return;
        }

        final ActivityMain ac = getMainActivity();
        final OperationNodeRename op = new OperationNodeRename(this.op.getSession(), node, name);

        ac.startWaitDialog(getTile(ac), getString(R.string.common_pleasewait), di -> op.setCancel(true));
        new TaskOperation<>(op, new WeakCallback<>(this, FragmentNodeInfo::renameDone)).start();
    }

    private void renameDone(final OperationNodeRename op) {
        final ActivityMain ac = getMainActivity();
        ac.stopWait();
        op.reportError(ac);
    }

    private void actionOpen() {
        final ActivityMain ac = getMainActivity();
        final OperationNodeOpen op = new OperationNodeOpen(this.op.getSession(), this.op.getNode());

        ac.startWaitDialog(getTile(ac), getString(R.string.common_pleasewait), di -> op.setCancel(true));
        new TaskOperation<>(op, new WeakCallback<>(this, FragmentNodeInfo::openDone)).start();
    }

    private void openDone(final OperationNodeOpen op) {
        final ActivityMain ac = getMainActivity();
        ac.stopWait();

        if (!op.reportError(ac)) {
            final File f = op.getFile();

            if (f != null && f.exists()) {
                try {
                    final Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(f), this.op.getNode().getMimeType().getType());
                    startActivity(intent);
                } catch (Exception ex) {
                    OdsLog.ex(getClass(), ex);
                }
            }
        }
    }
}
