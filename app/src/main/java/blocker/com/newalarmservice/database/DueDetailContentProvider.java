package blocker.com.newalarmservice.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;


public class DueDetailContentProvider extends ContentProvider {

    private DueDetailDatabaseHelper database;

    // used for the UriMacher
    private static final int Dues = 10;
    private static final int Due_ID = 20;

    private static final String AUTHORITY = "blocker.com.newalarmservice.database.DueDetailContentProvider";

    private static final String BASE_PATH = "due";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/dues";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/due";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, Dues);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", Due_ID);
    }

    @Override
    public boolean onCreate() {
        database = new DueDetailDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(DueDetailTable.TABLE_DUE);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case Dues:
                break;
            case Due_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(DueDetailTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case Dues:
                id = sqlDB.insert(DueDetailTable.TABLE_DUE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse("content://" + AUTHORITY
                + "/" + BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case Dues:
                rowsDeleted = sqlDB.delete(DueDetailTable.TABLE_DUE, selection,
                        selectionArgs);
                break;
            case Due_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(DueDetailTable.TABLE_DUE,
                            DueDetailTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(DueDetailTable.TABLE_DUE,
                            DueDetailTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case Dues:
                rowsUpdated = sqlDB.update(DueDetailTable.TABLE_DUE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case Due_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(DueDetailTable.TABLE_DUE,
                            values,
                            DueDetailTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(DueDetailTable.TABLE_DUE,
                            values,
                            DueDetailTable.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = {DueDetailTable.COLUMN_CATEGORY,
                DueDetailTable.COLUMN_DUEDATE, DueDetailTable.COLUMN_REPEAT,
                DueDetailTable.COLUMN_ID, DueDetailTable.COLUMN_REPEAT_UPTo,
                DueDetailTable.COLUMN_REPEATEVERY_category
                , DueDetailTable.COLUMN_REPEATEVERY_input, DueDetailTable.COLUMN_AMOUNT,
                DueDetailTable.COLUMN_PAYEENAME
                , DueDetailTable.COLUMN_REMINDER_NOTIFICATION, DueDetailTable.COLUMN_Type, DueDetailTable.COLUMN_PAYMENT_STATUS
                , DueDetailTable.COLUMN_PAYMENT_DATE};
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
