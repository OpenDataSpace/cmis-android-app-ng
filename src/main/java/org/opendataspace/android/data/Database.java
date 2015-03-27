package org.opendataspace.android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.ReferenceObjectCache;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.opendataspace.android.objects.Account;
import org.opendataspace.android.objects.Repo;

import java.sql.SQLException;

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
            Log.w(getClass().getSimpleName(), ex);
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
                accounts = new DaoAccount(getConnectionSource());
                accounts.setObjectCache(cache);
            } catch (SQLException ex) {
                Log.w(getClass().getSimpleName(), ex);
            }
        }

        return accounts;
    }

    public DaoRepo getRepos() {
        if (repos == null) {
            try {
                repos = new DaoRepo(getConnectionSource());
                repos.setObjectCache(cache);
            } catch (SQLException ex) {
                Log.w(getClass().getSimpleName(), ex);
            }
        }

        return repos;
    }
}
