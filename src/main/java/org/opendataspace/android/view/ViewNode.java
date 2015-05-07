package org.opendataspace.android.view;

import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DaoBase;
import org.opendataspace.android.data.DaoNode;
import org.opendataspace.android.event.EventDaoNode;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.object.Repo;

import java.sql.SQLException;

public class ViewNode extends ViewBase<Node> {

    private Repo repo;
    private Node parent;

    public void onEventMainThread(EventDaoNode event) {
        processEvent(event);
    }

    @Override
    protected void notifyAdapters() {
        OdsApp.bus.post(new EventDaoNode());
    }

    @Override
    protected CloseableIterator<Node> iterate(DaoBase<Node> dao, Account acc) throws SQLException {
        DaoNode rep = (DaoNode) dao;
        return acc != null ? rep.forParent(repo, parent) : null;
    }

    @Override
    protected boolean isValid(Node val) {
        return parent != null && repo != null && val.getParentId() != parent.getId() && val.getRepoId() == repo.getId();
    }

    public void setScope(Repo repo, Node parent) {
        this.repo = repo;
        this.parent = parent;
        sync(OdsApp.get().getDatabase().getNodes(), null);
    }
}
