package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.operation.OperationLoader;
import org.opendataspace.android.operation.OperationNodeBrowse;
import org.opendataspace.android.operation.OperationNodeDelete;
import org.opendataspace.android.operation.OperationStatus;

import java.lang.ref.WeakReference;

@SuppressLint("ValidFragment")
public class FragmentNodeInfo extends FragmentBase implements LoaderManager.LoaderCallbacks<OperationStatus> {

    private final OperationNodeBrowse op;

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
        Node node = op.getNode();
        Activity ac = getActivity();
        TextView tvt = widget(R.id.text_node_title);

        tvt.setText(node.getName());
        tvt.setCompoundDrawablesWithIntrinsicBounds(node.getIcon(ac), 0, 0, 0);
        this.<TextView>widget(R.id.text_node_details).setText(node.getNodeDecription(ac));
        this.<TextView>widget(R.id.text_node_name).setText(node.getName());
        this.<TextView>widget(R.id.text_node_path).setText(node.getPath(ac));
        this.<TextView>widget(R.id.text_node_type).setText(node.getMimeDescription(ac));
        this.<TextView>widget(R.id.text_node_created).setText(node.getCreatedAt(ac));
        this.<TextView>widget(R.id.text_node_creator).setText(node.getCreatedBy());
        this.<TextView>widget(R.id.text_node_modified).setText(node.getModifiedAt(ac));
        this.<TextView>widget(R.id.text_node_modifier).setText(node.getModifiedBy());

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

    @Override
    public String getTile(Context context) {
        return context.getString(R.string.node_title);
    }

    @Override
    public void onDestroyView() {
        OdsApp.get().getCmisCache().cancel(widget(R.id.image_node_preview));
        super.onDestroyView();
    }

    @Override
    int getMenuResource() {
        return R.menu.menu_node;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_node_delete:
            actionDelete();
            break;

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void actionDelete() {
        ActivityMain ac = getMainActivity();

        if (ac.isWaiting()) {
            return;
        }

        Node node = op.getNode();

        if (node == null || !node.canDelete()) {
            return;
        }

        ac.startWaitDialog(getTile(ac), getString(R.string.common_pleasewait),
                di -> getLoaderManager().destroyLoader(1));
        getLoaderManager().restartLoader(1, null, this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem mi = menu.findItem(R.id.menu_node_delete);

        if (mi != null) {
            Node node = op.getNode();
            mi.setVisible(node != null && node.canDelete());
        }
    }

    @Override
    public Loader<OperationStatus> onCreateLoader(int id, Bundle args) {
        return new OperationLoader(new OperationNodeDelete(op.getNode(), op.getSession()), getActivity());
    }

    @Override
    public void onLoadFinished(Loader<OperationStatus> loader, OperationStatus data) {
        ActivityMain ac = getMainActivity();
        ac.stopWait();

        if (data.isOk()) {
            ac.getNavigation().backPressed();
        } else {
            new AlertDialog.Builder(ac).setMessage(data.getMessage(ac)).setCancelable(true)
                    .setPositiveButton(R.string.common_ok, (dialogInterface, i) -> {
                        dialogInterface.cancel();
                    }).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<OperationStatus> loader) {
        getMainActivity().stopWait();
    }
}
