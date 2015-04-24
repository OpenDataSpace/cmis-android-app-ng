package org.opendataspace.android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.ReferenceObjectCache;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;
import org.opendataspace.android.app.OdsLog;
import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Repo;

import java.sql.SQLException;
import java.util.concurrent.Callable;

public class DataBase extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "odsdata.db";
    private static final int DATABASE_VERSION = 1;

    private final ReferenceObjectCache cache = ReferenceObjectCache.makeWeakCache();

    private DaoAccount accounts;
    private DaoRepo repos;

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Account.class);
            TableUtils.createTable(connectionSource, Repo.class);
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

    public <T> T transact(Callable<T> action) throws SQLException {
        ConnectionSource source = getConnectionSource();
        DatabaseConnection conn = source.getReadWriteConnection();
        T res;

        try {
            boolean saved = source.saveSpecialConnection(conn);
            res = TransactionManager.callInTransaction(conn, saved, source.getDatabaseType(), action);

            if (accounts != null) {
                accounts.fire(conn);
            }

            if (repos != null) {
                repos.fire(conn);
            }
        } finally {
            connectionSource.clearSpecialConnection(conn);
            connectionSource.releaseConnection(conn);
        }

        return res;
    }
}
