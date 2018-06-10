package blocker.com.newalarmservice.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;


public class DueDetailDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "duetable.db";
    private static final int DATABASE_VERSION = 1;

    public DueDetailDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DueDetailTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DueDetailTable.onUpgrade(db, oldVersion, newVersion);
    }
}
