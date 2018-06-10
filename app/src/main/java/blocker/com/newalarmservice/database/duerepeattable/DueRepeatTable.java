package blocker.com.newalarmservice.database.duerepeattable;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class DueRepeatTable {
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_REPEATARR = "duearr";
    public static final String COLUMN_PAID = "paidarr";
    public static final String TABLE_DUE = "duerepeat";
    public static final String COLUMN_NOTIFICATION_FLAG = "notification_flag";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_DUE
            + "("
            + COLUMN_ID + " integer primary key , "
            + COLUMN_REPEATARR + " text not null ,"
            + COLUMN_PAID + " text not null ,"
            + COLUMN_NOTIFICATION_FLAG + " text not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        Log.e("query", DATABASE_CREATE);
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(DueRepeatTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_DUE);
        onCreate(database);
    }

}
