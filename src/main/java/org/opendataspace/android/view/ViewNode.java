package org.opendataspace.android.view;

import com.j256.ormlite.dao.CloseableIterator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.cmis.CmisSession;
import org.opendataspace.android.data.DaoBase;
import org.opendataspace.android.data.DaoNode;
import org.opendataspace.android.event.Event;
import org.opendataspace.android.event.EventDaoNode;
import org.opendataspace.android.event.EventProgress;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.object.ObjectBase;
import org.opendataspace.android.object.Repo;

import java.sql.SQLException;

public class ViewNode extends ViewBase<Node> {

    private Repo repo;
    private long parentId = ObjectBase.INVALID_ID;
    private CmisSession session;

    public ViewNode() {
        super();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = Event.PRIORITY_VIEW)
    public void onEvent(final EventDaoNode event) {
        processEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, priority = Event.PRIORITY_VIEW)
    public void onEvent(final EventProgress event) {
        final Node node = event.getNode();

        if (isValid(node)) {
            return;
        }

        final long pos = event.getPos();
        final long max = event.getMax();
        final int progress = pos == max ? 100 : (int) (100 * pos / max);
        final Node local = find(node);
        node.setProgress(progress);

        if (local != node) {
            local.setProgress(progress);
        }
    }

    @Override
    protected void notifyAdapters() {
        OdsApp.bus.post(new EventDaoNode());
    }

    @Override
    protected CloseableIterator<Node> iterate(final DaoBase<Node> dao) throws SQLException {
        DaoNode rep = (DaoNode) dao;
        return rep.forParent(repo, parentId);
    }

    @Override
    boolean isValid(final Node val) {
        return repo != null && val.getParentId() == parentId && val.getRepoId() == repo.getId();
    }

    public CmisSession setScope(final Account account, final Repo repo, final Node parent) {
        this.repo = repo;
        parentId = parent != null ? parent.getId() : ObjectBase.INVALID_ID;

        if (session == null || !session.same(repo)) {
            session = new CmisSession(account, repo);
        }

        return session;
    }

    @Override
    boolean shouldReset(final long extra) {
        return repo != null && repo.getId() == extra;
    }
}
