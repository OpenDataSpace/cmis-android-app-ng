package org.opendataspace.android.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.TaskOperation;
import org.opendataspace.android.app.WeakCallback;
import org.opendataspace.android.app.beta.R;
import org.opendataspace.android.event.Event;
import org.opendataspace.android.event.EventDaoBase;
import org.opendataspace.android.event.EventDaoNode;
import org.opendataspace.android.event.EventLinkUpdate;
import org.opendataspace.android.object.Link;
import org.opendataspace.android.object.LinkAdapter;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.operation.OperationBase;
import org.opendataspace.android.operation.OperationLinkBrowse;
import org.opendataspace.android.operation.OperationLinkCreate;
import org.opendataspace.android.operation.OperationLinkDelete;
import org.opendataspace.android.operation.OperationLinkUpdate;

@SuppressLint("ValidFragment")
public class FragmentLink extends FragmentBaseList {

    private final OperationLinkBrowse op;
    private LinkAdapter adapter;
    private Link moreItem;

    public FragmentLink(final OperationLinkBrowse op) {
        this.op = op;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new LinkAdapter(op.getView(), getActivity(), this::showPopup);

        OdsApp.bus.register(this);
        setListAdapter(adapter);
        setEmptyText(getString(R.string.folder_empty));
        setListShown(false, false);
        new TaskOperation<>(op, new WeakCallback<>(this, FragmentLink::browseDone)).start();
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
        switch (op.getType()) {
        case DOWNLOAD:
            return context.getString(R.string.link_download, op.getNode().getName());

        case UPLOAD:
            return context.getString(R.string.link_upload, op.getNode().getName());

        default:
            return super.getTile(context);
        }
    }

    @Override
    protected int getMenuResource() {
        return R.menu.menu_link_default;
    }

    private void browseDone(final OperationLinkBrowse op) {
        op.reportError(getActivity());
        browseFinished(false);
    }

    private void browseFinished(final boolean isFinal) {
        if (getView() == null) {
            return;
        }

        if (isFinal || adapter.getCount() != 0) {
            setListShown(true, false);
        }
    }

    private void showPopup(final View view) {
        moreItem = adapter.resolve(view.getParent());

        if (moreItem == null) {
            return;
        }

        final Activity ac = getActivity();
        final PopupMenu popup = new PopupMenu(ac, view);
        ac.getMenuInflater().inflate(R.menu.menu_link_more, popup.getMenu());

        onPrepareOptionsMenu(popup.getMenu());
        popup.setOnDismissListener(menu -> moreItem = null);
        popup.setOnMenuItemClickListener(this::onOptionsItemSelected);
        popup.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = Event.PRIORITY_UI)
    public void onEvent(final EventLinkUpdate event) {
        if (TextUtils.equals(op.getNode().getUuid(), event.getNodeUuid()) && op.getType() == event.getType()) {
            browseFinished(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = Event.PRIORITY_UI)
    public void onEvent(final EventDaoNode event) {
        for (EventDaoBase.Event<Node> cur : event.getEvents()) {
            if (cur.getObject().equals(op.getNode())) {
                if (cur.getOperation() == EventDaoBase.Operation.DELETE) {
                    getNavigation().backPressed();
                } else {
                    getNavigation().updateTitle();
                }

                break;
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_link_create:
            actionCreate();
            break;

        case R.id.menu_link_delete:
            actionDelete();
            break;

        case R.id.menu_link_details:
            actionEdit();
            break;

        case R.id.menu_link_copyurl:
            actionCopyUrl();
            break;

        default:
            return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void actionCopyUrl() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && moreItem != null) {
            final ClipboardManager clipboard =
                    (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

            clipboard.setPrimaryClip(ClipData.newPlainText("url", moreItem.getUrl()));
        }
    }

    private void actionDelete() {
        if (moreItem == null) {
            return;
        }

        new TaskOperation<>(new OperationLinkDelete(moreItem, op.getSession()),
                new WeakCallback<>(this, FragmentLink::operationDone)).start();
    }

    private void operationDone(final OperationBase op) {
        op.reportError(getActivity());
    }

    private void actionCreate() {
        final Link link = new Link();

        new DialogLink(link, false, () -> new TaskOperation<>(new OperationLinkCreate(link, op.getSession()),
                new WeakCallback<>(this, FragmentLink::operationDone)).start()).show(getFragmentManager(), "DLG");
    }

    private void actionEdit() {
        if (moreItem == null) {
            return;
        }

        new DialogLink(moreItem, false, () -> new TaskOperation<>(new OperationLinkUpdate(moreItem, op.getSession()),
                new WeakCallback<>(this, FragmentLink::operationDone)).start()).show(getFragmentManager(), "DLG");
    }
}
