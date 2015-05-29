package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.event.Event;
import org.opendataspace.android.event.EventDaoBase;
import org.opendataspace.android.event.EventDaoNode;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.operation.OperationLoader;
import org.opendataspace.android.operation.OperationNodeBrowse;
import org.opendataspace.android.operation.OperationNodeDelete;
import org.opendataspace.android.operation.OperationNodeRename;
import org.opendataspace.android.operation.OperationStatus;

import java.lang.ref.WeakReference;

@SuppressLint("ValidFragment")
public class FragmentNodeInfo extends FragmentBase implements LoaderManager.LoaderCallbacks<OperationStatus> {

    private final static int LOADER_DELETE = 1;
    private final static int LOADER_RENAME = 2;

    private final OperationNodeBrowse op;
    private OperationNodeRename rename;

    public FragmentNodeInfo(OperationNodeBrowse op) {
        this.op = op;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nodeinfo, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        OdsApp.bus.register(this, Event.PRIORITY_UI);
        updateInfo();

        WeakReference<FragmentNodeInfo> weak = new WeakReference<>(this);

        OdsApp.get().getCmisCache().load(op.getNode(), op.getSession(), widget(R.id.image_node_preview), result -> {
            FragmentNodeInfo fgm = weak.get();

            if (fgm == null) {
                return;
            }

            if (result) {
                ViewSwitcher vs = fgm.widget(R.id.view_node_preview);

                if (vs != null) {
                    vs.showNext();
                }
            } else {
                TextView tve = fgm.widget(R.id.text_node_preview);

                if (tve != null) {
                    tve.setText(R.string.node_nopreview);
                }
            }
        });
    }

    private void updateInfo() {
        Node node = op.getNode();
        Activity ac = getActivity();
        TextView tvt = widget(R.id.text_node_title);

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
    public String getTile(Context context) {
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
        Node node = op.getNode();

        if (node == null || !node.canDelete()) {
            return;
        }

        new AlertDialog.Builder(getActivity())
                .setMessage(String.format(getString(R.string.common_delete), node.getName())).setCancelable(true)
                .setPositiveButton(R.string.common_ok, (di, i) -> startLoader(LOADER_DELETE))
                .setNegativeButton(R.string.common_cancel, (di, i) -> di.cancel()).show();
    }

    private void startLoader(int id) {
        ActivityMain ac = getMainActivity();

        if (ac.isWaiting()) {
            return;
        }

        ac.startWaitDialog(getTile(ac), getString(R.string.common_pleasewait),
                di -> getLoaderManager().destroyLoader(id));

        getLoaderManager().restartLoader(id, null, this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem mi = menu.findItem(R.id.menu_node_delete);

        if (mi != null) {
            Node node = op.getNode();
            mi.setVisible(node != null && node.canDelete());
        }
    }

    @Override
    public Loader<OperationStatus> onCreateLoader(int id, Bundle args) {
        switch (id) {
        case LOADER_DELETE:
            return new OperationLoader(new OperationNodeDelete(op.getNode(), op.getSession()), getActivity());

        case LOADER_RENAME:
            return new OperationLoader(rename, getActivity());

        default:
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<OperationStatus> loader, OperationStatus data) {
        ActivityMain ac = getMainActivity();
        ac.stopWait();

        if (!data.isOk()) {
            new AlertDialog.Builder(ac).setMessage(data.getMessage(ac)).setCancelable(true)
                    .setPositiveButton(R.string.common_ok, (dialogInterface, i) -> dialogInterface.cancel()).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<OperationStatus> loader) {
        getMainActivity().stopWait();
    }

    @SuppressWarnings({"UnusedParameters", "unused"})
    public void onEventMainThread(EventDaoNode event) {
        for (EventDaoBase.Event<Node> cur : event.getEvents()) {
            if (cur.getObject().equals(op.getNode())) {
                if (cur.getOperation() == EventDaoBase.Operation.DELETE) {
                    getMainActivity().stopWait();
                    getMainActivity().getNavigation().backPressed();
                } else {
                    op.setNode(cur.getObject());
                    updateInfo();
                    getMainActivity().getNavigation().updateMenu();
                }

                break;
            }
        }
    }

    private void actionRename() {
        Node node = op.getNode();

        if (node == null || !node.canEdit()) {
            return;
        }

        Activity ac = getActivity();
        @SuppressLint("InflateParams") View view = ac.getLayoutInflater().inflate(R.layout.dialog_node_rename, null);
        EditText et = (EditText) view.findViewById(R.id.edit_dialog_name);
        et.setText(node.getName());

        new AlertDialog.Builder(ac).setTitle(R.string.node_rename).setView(view).setCancelable(true)
                .setPositiveButton(R.string.common_ok, (di, i) -> renameNode(node, et.getText().toString().trim()))
                .setNegativeButton(R.string.common_cancel, (di, i) -> di.dismiss()).show();
    }

    private void renameNode(Node node, String name) {
        if (TextUtils.isEmpty(name) || node == null || !node.canEdit()) {
            return;
        }

        rename = new OperationNodeRename(op.getSession(), node, name);
        startLoader(LOADER_RENAME);
    }
}
