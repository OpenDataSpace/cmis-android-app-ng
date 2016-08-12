package org.opendataspace.android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.ReferenceObjectCache;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;

import org.opendataspace.android.app.OdsApp;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.CacheEntry;
import org.opendataspace.android.object.Link;
import org.opendataspace.android.object.MimeType;
import org.opendataspace.android.object.Node;
import org.opendataspace.android.object.Repo;

import java.sql.SQLException;
import java.util.concurrent.Callable;

public class DataBase extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "odsdata.db";
    private static final int DATABASE_VERSION = 1;

    private final ReferenceObjectCache cache = ReferenceObjectCache.makeWeakCache();

    private DaoAccount accounts;
    private DaoRepo repos;
    private DaoNode nodes;
    private DaoMime mime;
    private DaoCacheEntry ce;
    private DaoLink link;

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Account.class);
            TableUtils.createTable(connectionSource, Repo.class);
            TableUtils.createTable(connectionSource, Node.class);
            TableUtils.createTable(connectionSource, MimeType.class);
            TableUtils.createTable(connectionSource, CacheEntry.class);
            TableUtils.createTable(connectionSource, Link.class);

            if (OdsApp.get().isRealApp()) {
                transact(() -> {
                    getMime().createDefaults();
                    return null;
                });
            }
        } catch (Exception ex) {
            OdsLog.ex(getClass(), ex);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        // nothing
    }

    @Override
    public void close() {
        super.close();
        accounts = null;
        repos = null;
        nodes = null;
        mime = null;
        ce = null;
        link = null;
    }

    public DaoAccount getAccounts() {
        if (accounts == null) {
            try {
                accounts = new DaoAccount(getConnectionSource(), cache);
            } catch (SQLException ex) {
                OdsLog.ex(getClass(), ex);
            }
        }

        return accounts;
    }

    public DaoRepo getRepos() {
        if (repos == null) {
            try {
                repos = new DaoRepo(getConnectionSource(), cache);
            } catch (SQLException ex) {
                OdsLog.ex(getClass(), ex);
            }
        }

        return repos;
    }

    public DaoNode getNodes() {
        if (nodes == null) {
            try {
                nodes = new DaoNode(getConnectionSource(), cache);
            } catch (SQLException ex) {
                OdsLog.ex(getClass(), ex);
            }
        }

        return nodes;
    }

    public DaoMime getMime() {
        if (mime == null) {
            try {
                mime = new DaoMime(getConnectionSource(), cache);
            } catch (SQLException ex) {
                OdsLog.ex(getClass(), ex);
            }
        }

        return mime;
    }

    public DaoCacheEntry getCacheEntries() {
        if (ce == null) {
            try {
                ce = new DaoCacheEntry(getConnectionSource(), cache);
            } catch (SQLException ex) {
                OdsLog.ex(getClass(), ex);
            }
        }

        return ce;
    }

    public DaoLink getLinks() {
        if (link == null) {
            try {
                link = new DaoLink(getConnectionSource(), cache);
            } catch (SQLException ex) {
                OdsLog.ex(getClass(), ex);
            }
        }

        return link;
    }

    public <T> void transact(Callable<T> action) throws SQLException {
        ConnectionSource source = getConnectionSource();
        DatabaseConnection conn = source.getReadWriteConnection();

        try {
            boolean saved = source.saveSpecialConnection(conn);
            TransactionManager.callInTransaction(conn, saved, source.getDatabaseType(), action);

            if (accounts != null) {
                accounts.fire(conn);
            }

            if (repos != null) {
                repos.fire(conn);
            }

            if (nodes != null) {
                nodes.fire(conn);
            }
        } finally {
            connectionSource.clearSpecialConnection(conn);
            connectionSource.releaseConnection(conn);
        }
    }
}
