package org.opendataspace.android.view;

import com.j256.ormlite.dao.CloseableIterator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DaoBase;
import org.opendataspace.android.data.DaoLink;
import org.opendataspace.android.event.EventDaoLink;
import org.opendataspace.android.object.Link;
import org.opendataspace.android.object.Node;

import java.sql.SQLException;

public class ViewLink extends ViewBase<Link> {

    private Node node;
    private Link.Type type;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final EventDaoLink event) {
        processEvent(event);
    }

    @Override
    protected void notifyAdapters() {
        OdsApp.bus.post(new EventDaoLink());
    }

    @Override
    protected CloseableIterator<Link> iterate(final DaoBase<Link> dao) throws SQLException {
        final DaoLink rep = (DaoLink) dao;
        return rep.getLinksByNode(node, type);
    }

    @Override
    boolean isValid(final Link val) {
        return val.getNodeId() == node.getId() && val.getType() == type;
    }

    public void setScope(final Node node, final Link.Type type) {
        this.node = node;
        this.type = type;
    }
}
