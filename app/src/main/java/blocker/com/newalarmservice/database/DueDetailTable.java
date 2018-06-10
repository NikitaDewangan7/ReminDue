package blocker.com.newalarmservice.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class DueDetailTable {

    // Database table
    public static final String TABLE_DUE = "due";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PAYEENAME = "payee";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_DUEDATE = "date";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_Type = "type";
    public static final String COLUMN_REMINDER_NOTIFICATION = "reminder";
    public static final String COLUMN_REPEAT = "repeat";
    public static final String COLUMN_REPEATEVERY_input = "repeat_input";
    public static final String COLUMN_REPEATEVERY_category = "repeat_category";
    public static final String COLUMN_REPEAT_UPTo = "repeat_upto";
    public static final String COLUMN_PAYMENT_STATUS = "payment_status";
    public static final String COLUMN_PAYMENT_DATE = "payment_date";
    public static final String COLUMN_NOTIFICATION_FLAG = "notification_flag";


    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_DUE
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PAYEENAME + " text not null, "
            + COLUMN_AMOUNT + " real not null,"
            + COLUMN_DUEDATE
            + " real not null ,"
            + COLUMN_CATEGORY + " text not null ,"
            + COLUMN_Type + " text not null, "
            + COLUMN_REMINDER_NOTIFICATION + " text not null, "
            + COLUMN_REPEAT + " integer not null, "
            + COLUMN_REPEATEVERY_input + " integer not null, "
            + COLUMN_REPEATEVERY_category + " text not null, "
            + COLUMN_REPEAT_UPTo + " real not null ,"
            + COLUMN_PAYMENT_STATUS + " integer not null ,"
            + COLUMN_PAYMENT_DATE + " real ,"
            + COLUMN_NOTIFICATION_FLAG + " integer not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        Log.e("query", DATABASE_CREATE);
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(DueDetailTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_DUE);
        onCreate(database);
    }
}
