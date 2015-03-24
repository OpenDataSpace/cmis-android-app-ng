package org.opendataspace.android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.opendataspace.android.objects.Account;

import java.sql.SQLException;

public class DataBase extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "odsdata.db";
    private static final int DATABASE_VERSION = 2;

    private DaoAccount accounts;

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Account.class);
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
    }

    public DaoAccount getAccounts() {
        if (accounts == null) {
            try {
                accounts = new DaoAccount(getConnectionSource(), Account.class);
            } catch (SQLException ex) {
                Log.w(getClass().getSimpleName(), ex);
            }
        }

        return accounts;
    }
}
