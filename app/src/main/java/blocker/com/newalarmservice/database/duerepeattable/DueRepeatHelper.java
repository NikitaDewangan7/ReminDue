package blocker.com.newalarmservice.database.duerepeattable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DueRepeatHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "duerepeat.db";
    private static final int DATABASE_VERSION = 1;

    public DueRepeatHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DueRepeatTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DueRepeatTable.onUpgrade(db, oldVersion, newVersion);
    }
}
