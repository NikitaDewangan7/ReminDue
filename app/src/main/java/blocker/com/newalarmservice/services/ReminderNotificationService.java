package blocker.com.newalarmservice.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import blocker.com.newalarmservice.database.DueDetailContentProvider;
import blocker.com.newalarmservice.database.DueDetailTable;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatContentProvider;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatTable;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.models.RepeatModel;


public class ReminderNotificationService extends IntentService {
    private static final String TAG = "MyService";

    public ReminderNotificationService() {
        super(TAG);
    }

/*    @Override
    protected void onHandleIntent(Intent intent) {
        int status = intent.getIntExtra("status", -1);
        int id = intent.getIntExtra("id", -1);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        DueUpcomingModel model = (DueUpcomingModel) intent.getSerializableExtra("model");
        RepeatModel repeatModel = (RepeatModel) intent.getSerializableExtra("repeat");
        switch (status) {
            case 1:
                if (model != null) {
                    if (model.getRepeatFlag() == 1) {
                        Log.e(" repeat "," in if repeat type");
                        Uri uri = ContentUris.withAppendedId(DueRepeatContentProvider.CONTENT_URI, model.getId());
                        Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            String repeatTimes = cursor.getString(cursor.getColumnIndex(DueRepeatTable.COLUMN_REPEATARR));
                            String[] repeatTimeArr = repeatTimes.split(",");
                            StringBuffer paidString = new StringBuffer();
                            String[] paidArr = cursor.getString(cursor.getColumnIndex(DueRepeatTable.COLUMN_PAID)).split(",");
                            cursor.close();
                            for (int i = 0; i < repeatTimeArr.length; i++) {
                                long comTime = Long.parseLong(repeatTimeArr[i]);
                                if (repeatModel.getDueTime() == comTime) {
                                    paidString.append("1,");
                                } else {
                                    paidString.append(paidArr[i]).append(",");
                                }
                            }
                            Log.e("Paid String is", paidString.toString());
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DueRepeatTable.COLUMN_PAID, paidString.toString());
                            getApplicationContext().getContentResolver().update(uri, contentValues, null, null);

                            Uri uri1 = ContentUris.withAppendedId(DueDetailContentProvider.CONTENT_URI, model.getId());
                            ContentValues values = new ContentValues();
                            values.put(DueDetailTable.COLUMN_PAYMENT_DATE, System.currentTimeMillis());
                            getApplicationContext().getContentResolver().update(uri1, values, null, null);

                        }
                    } else {
                        Log.e("not repeat ","in else not repeat type");
                        Uri uri = ContentUris.withAppendedId(DueDetailContentProvider.CONTENT_URI, model.getId());
                        ContentValues values = new ContentValues();
                        values.put(DueDetailTable.COLUMN_PAYMENT_STATUS, 1);
                        values.put(DueDetailTable.COLUMN_PAYMENT_DATE, System.currentTimeMillis());
                        getApplicationContext().getContentResolver().update(uri, values, null, null);
                    }
                    notificationManager.cancel(id);
                } else {
                    Log.e("model is ", "null");
                }

                break;
            case 2:
                notificationManager.cancel(id);
                break;

        }
    }*/

    @Override
    protected void onHandleIntent(Intent intent) {
        int status = intent.getIntExtra("status", -1);
        int id = intent.getIntExtra("id", -1);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        DueUpcomingModel model = (DueUpcomingModel) intent.getSerializableExtra("model");
        RepeatModel repeatModel = (RepeatModel) intent.getSerializableExtra("repeat");
        switch (status) {
            case 1:
                if (model != null) {
                    if (model.getRepeatFlag() == 1) {
                        Log.e(" repeat "," in if repeat type");
                        Uri uri = ContentUris.withAppendedId(DueRepeatContentProvider.CONTENT_URI, model.getId());
                        Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            String repeatTimes = cursor.getString(cursor.getColumnIndex(DueRepeatTable.COLUMN_REPEATARR));
                            String[] repeatTimeArr = repeatTimes.split(",");
                            StringBuffer paidString = new StringBuffer();
                            String[] paidArr = cursor.getString(cursor.getColumnIndex(DueRepeatTable.COLUMN_PAID)).split(",");
                            cursor.close();
                            for (int i = 0; i < repeatTimeArr.length; i++) {
                                long comTime = Long.parseLong(repeatTimeArr[i]);
                                if (repeatModel.getDueTime() == comTime) {
                                    paidString.append("1,");
                                } else {
                                    paidString.append(paidArr[i]).append(",");
                                }
                            }
                            Log.e("Paid String is", paidString.toString());
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DueRepeatTable.COLUMN_PAID, paidString.toString());
                            getApplicationContext().getContentResolver().update(uri, contentValues, null, null);

                            Uri uri1 = ContentUris.withAppendedId(DueDetailContentProvider.CONTENT_URI, model.getId());
                            ContentValues values = new ContentValues();
                            values.put(DueDetailTable.COLUMN_PAYMENT_DATE, System.currentTimeMillis());
                            getApplicationContext().getContentResolver().update(uri1, values, null, null);


                        }
                    } else {
                        Log.e("not repeat ","in else not repeat type");
                        Uri uri = ContentUris.withAppendedId(DueDetailContentProvider.CONTENT_URI, model.getId());
                        ContentValues values = new ContentValues();
                        values.put(DueDetailTable.COLUMN_PAYMENT_STATUS, 1);
                        values.put(DueDetailTable.COLUMN_PAYMENT_DATE, System.currentTimeMillis());
                        getApplicationContext().getContentResolver().update(uri, values, null, null);
                    }
                    notificationManager.cancel(id);
                } else {
                    Log.e("model is ", "null");
                }

                break;
            case 2:
                notificationManager.cancel(id);
                break;

        }
    }
}
