package org.opendataspace.android.data;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ObjectCache;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import org.opendataspace.android.event.EventDaoBase;
import org.opendataspace.android.event.EventDaoLink;
import org.opendataspace.android.object.Link;
import org.opendataspace.android.object.Node;

import java.sql.SQLException;

public class DaoLink extends DaoBaseView<Link> {

    DaoLink(final ConnectionSource source, final ObjectCache cache) throws SQLException {
        super(source, cache, Link.class);
    }

    @Override
    protected EventDaoBase<Link> createEvent() {
        return new EventDaoLink();
    }

    public CloseableIterator<Link> getLinksByNode(final Node node, final Link.Type type) throws SQLException {
        QueryBuilder<Link, Long> queryBuilder = queryBuilder();
        queryBuilder.where().eq(Link.NODE_ID_FIELD, node.getId()).and().eq(Link.TYPE_FIELD, type);
        return iterate(queryBuilder.prepare());
    }
}
