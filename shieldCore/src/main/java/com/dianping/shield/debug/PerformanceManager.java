package com.dianping.shield.debug;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.provider.BaseColumns;

import com.dianping.agentsdk.framework.AgentInterface;
import com.dianping.agentsdk.framework.SectionCellInterface;
import com.dianping.agentsdk.sectionrecycler.section.PieceAdapter;

/**
 * Created by runqi.wei
 * 14:15
 * 10.01.2017.
 */

public class PerformanceManager {


    private static final String DB_FILE = "section-performance.db";
    private static final int DB_VERSION = 1;

    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";
    private static final String TIME_TYPE = " DATETIME";
    private static final String COMMA_SEP = ",";

    private Context context;
    private SQLiteOpenHelper sqliteOpenHelper;
    private Handler handler;

    public PerformanceManager(Context context) {
        this.context = context;
        sqliteOpenHelper = new DatabaseHelper(context, DB_FILE, null, DB_VERSION);
        handler = new Handler();
    }

    public void insertRecord(final String pageName, final PieceAdapter adapter,
                             final String methodName, final long startTime, final long endTime) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                AgentInterface ai = null;
                SectionCellInterface ci = null;
                if (adapter != null) {
                    ai = adapter.getAgentInterface();
                    ci = adapter.getSectionCellInterface();
                }
                String agentName = null;
                String agentHashCode = null;
                String cellName = null;
                String hostName = null;
                if (ai != null) {
                    agentName = ai.getClass().getCanonicalName();
                    agentHashCode = "" + ai.hashCode();
                    hostName = ai.getHostName();
                }

                if (ci != null) {
                    cellName = ci.getClass().getCanonicalName();
                }
                insertPerformanceRecord(pageName, hostName, agentName,
                        agentHashCode, cellName, methodName, startTime, endTime);
            }
        });
    }

    public long insertPerformanceRecord(String pageNaem, String hostName,
                                        String agentName, String agentHashCode,
                                        String cellName, String methodName,
                                        long startTime, long endTime) {

        ContentValues values = new ContentValues();
        values.put(PerfEntry.PAGE_NAME, pageNaem);
        values.put(PerfEntry.HOST_NAME, hostName);
        values.put(PerfEntry.AGENT_NAME, agentName);
        values.put(PerfEntry.AGENT_HASH_CODE, agentHashCode);
        values.put(PerfEntry.CELL_NAME, cellName);
        values.put(PerfEntry.METHOD_NAME, methodName);
        values.put(PerfEntry.START_TIME, startTime);
        values.put(PerfEntry.END_TIME, endTime);

        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        long res = db.insert(PerfEntry.TABLE_NAME, null, values);
        db.close();
        return res;

    }

    public Cursor searchPage(String pageName) {

        String sql = "SELECT *, SUM(" + PerfEntry.END_TIME + " - " + PerfEntry.START_TIME + ") AS TimeCost, "
                + " COUNT(*) AS RunTimes, "
                + " SUM(" + PerfEntry.END_TIME + " - " + PerfEntry.START_TIME + ") * 1.0 / COUNT(*) AS AvgTime"
                + " FROM " + PerfEntry.TABLE_NAME
                + " WHERE " + PerfEntry.PAGE_NAME + " = '" + pageName + "' "
                + " GROUP BY " + PerfEntry.HOST_NAME + COMMA_SEP + PerfEntry.AGENT_NAME + COMMA_SEP
                + PerfEntry.CELL_NAME + COMMA_SEP + PerfEntry.METHOD_NAME
                + " ORDER BY AvgTime DESC";

        Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public Cursor findPages() {
        String sql = "SELECT " + " DISTINCT " + PerfEntry.PAGE_NAME + COMMA_SEP + PerfEntry._ID + " FROM " + PerfEntry.TABLE_NAME
                + " WHERE " + PerfEntry.PAGE_NAME + " IS NOT NULL GROUP BY " + PerfEntry.PAGE_NAME;
        Cursor cursor = sqliteOpenHelper.getReadableDatabase().rawQuery(sql, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public void clearData() {
        String sql = "DELETE FROM " + PerfEntry.TABLE_NAME;
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    public void clearData(String pageName) {
        String sql = "DELETE FROM " + PerfEntry.TABLE_NAME + " WHERE " + PerfEntry.PAGE_NAME + " = '" + pageName + "' ";
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + PerfEntry.TABLE_NAME + " ("
                        + PerfEntry._ID + INTEGER_TYPE + PRIMARY_KEY + COMMA_SEP
                        + PerfEntry.PAGE_NAME + TEXT_TYPE + COMMA_SEP
                        + PerfEntry.HOST_NAME + TEXT_TYPE + COMMA_SEP
                        + PerfEntry.AGENT_NAME + TEXT_TYPE + COMMA_SEP
                        + PerfEntry.AGENT_HASH_CODE + TEXT_TYPE + COMMA_SEP
                        + PerfEntry.CELL_NAME + TEXT_TYPE + COMMA_SEP
                        + PerfEntry.METHOD_NAME + TEXT_TYPE + COMMA_SEP
                        + PerfEntry.START_TIME + TIME_TYPE + COMMA_SEP
                        + PerfEntry.END_TIME + TIME_TYPE + COMMA_SEP
                        + PerfEntry.TIME_STAMP + TIME_TYPE + " DEFAULT CURRENT_TIMESTAMP"
                        + " )";

        private static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + PerfEntry.TABLE_NAME;

        protected SQLiteDatabase db;
        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            db = getWritableDatabase();
            db.close();
        }

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
            db = getWritableDatabase();
            db.close();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_TABLE);
            onCreate(db);
        }
    }

    public static class PerfEntry implements BaseColumns {
        public static final String TABLE_NAME = "PerformanceTable";
        public static final String TIME_STAMP = "Timestame";
        public static final String HOST_NAME = "HostName";
        public static final String PAGE_NAME = "PageName";
        public static final String AGENT_NAME = "AgentName";
        public static final String AGENT_HASH_CODE = "AgentHashCode";
        public static final String CELL_NAME = "CellName";
        public static final String METHOD_NAME = "MethodName";
        public static final String START_TIME = "StartTime";
        public static final String END_TIME = "EndTime";
    }
}
