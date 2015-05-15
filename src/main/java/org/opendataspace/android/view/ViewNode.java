package org.opendataspace.android.view;

import com.j256.ormlite.dao.CloseableIterator;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.data.DaoBase;
import org.opendataspace.android.data.DaoNode;
import org.opendataspace.android.event.EventDaoNode;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.object.ObjectBase;
import org.opendataspace.android.object.Repo;

import java.sql.SQLException;

public class ViewNode extends ViewBase<Node> {

    private Repo repo;
    private long parentId = ObjectBase.INVALID_ID;

    public void onEventMainThread(EventDaoNode event) {
        processEvent(event);
    }

    @Override
    protected void notifyAdapters() {
        OdsApp.bus.post(new EventDaoNode());
    }

    @Override
    protected CloseableIterator<Node> iterate(DaoBase<Node> dao) throws SQLException {
        DaoNode rep = (DaoNode) dao;
        return rep.forParent(repo, parentId);
    }

    @Override
    protected boolean isValid(Node val) {
        return repo != null && val.getParentId() != parentId && val.getRepoId() == repo.getId();
    }

    public void setScope(Repo repo, Node parent) {
        this.repo = repo;
        parentId = parent != null ? parent.getId() : ObjectBase.INVALID_ID;
    }
}
