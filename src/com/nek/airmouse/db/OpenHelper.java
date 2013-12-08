
package com.nek.airmouse.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.nek.airmouse.db.dao.ServerDao;
import com.nek.airmouse.db.dto.ServerObj;

import java.sql.SQLException;

public class OpenHelper extends OrmLiteSqliteOpenHelper {

    public static final String TAG = OpenHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "airmouse.db";
    private static int DATABASE_VERSION = 1;
    private ServerDao serverDao = null;
    private Context context;

    public OpenHelper(Context context, String databaseName, CursorFactory factory, int databaseVersion) {
        super(context, databaseName, factory, databaseVersion);
        this.context = context;
    }

    public OpenHelper(Context con) {
        super(con, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = con;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource conSourse) {
        try {
            TableUtils.createTable(conSourse, ServerObj.class);
        } catch (SQLException e) {
            Log.e(TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource conSourse, int old, int next) {
        try {
            TableUtils.dropTable(conSourse, ServerObj.class, true);
        } catch (SQLException e) {
            Log.e(TAG, "Can't drop databases", e);
            throw new RuntimeException(e);
        }
        onCreate(db);
    }

    public ServerDao getServerDao() throws SQLException {
        if (serverDao == null) {
            serverDao = new ServerDao(getConnectionSource());
        }
        return serverDao;
    }

}
