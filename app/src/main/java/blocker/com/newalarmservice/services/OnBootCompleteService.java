package blocker.com.newalarmservice.services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;

import blocker.com.newalarmservice.database.DueDetailContentProvider;
import blocker.com.newalarmservice.database.DueDetailTable;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatContentProvider;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatTable;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.models.RepeatModel;
import blocker.com.newalarmservice.receiver.AlarmReceiver;
import blocker.com.newalarmservice.utilities.CommonUtilities;
import blocker.com.newalarmservice.sharedpreferences.MReminderTimeSharedPreference;


public class OnBootCompleteService extends IntentService {
    private static final String TAG = "MyService";

    public OnBootCompleteService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("onBootService","Called");
        Cursor cursor = getApplicationContext().getContentResolver().query(DueDetailContentProvider.CONTENT_URI, null, null, null, null);
        if (cursor != null) {
            ArrayList<DueUpcomingModel> dueUpcomingModelArrayList = getAllDatabaseList(cursor);
            for (DueUpcomingModel model : dueUpcomingModelArrayList) {
                try {
                    setAlarm(model);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.e("Error in setting alarm", "Error is " + e.toString());
                }
            }
        }
    }

    public void setAlarm(DueUpcomingModel model) throws ParseException {
        NotificationManager notificationmanager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationmanager.cancel(model.getId());
        Intent alarmIntent;
        AlarmManager manager;
        PendingIntent pendingIntent;
        String timeArr[] = MReminderTimeSharedPreference.getReminderTimeSharedPreference().getActualReminderTime().split("/");
        String minStr;
        int min = Integer.parseInt(timeArr[1]);
        Log.e("min Str is", "  " + min);
        if (min < 9) {
            minStr = "0" + min;

        } else {
            minStr = "" + min;
        }
        int id = model.getId() * 1000;
        if (model.getRepeatFlag() == 1) {
            for (RepeatModel repeatModel : model.getRepeatModelArrayList()) {
                if (repeatModel.getPaymentStatus() == 0 && repeatModel.getNotificationStatus() == 0) {
                    String date = CommonUtilities.convertDateIntoNumFormet(repeatModel.getDueTime());
                    String actualDate = date + " " + timeArr[0] + ":" + minStr + ":" + "00";
                    Log.e("actualDate ", actualDate);
                    long actualTime = CommonUtilities.convertFullDateToMs(actualDate);
                    actualTime = actualTime - model.getDueReminderNotification() * (24 * 60 * 60 * 1000);
                    alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
                    alarmIntent.putExtra("model", model);
                    alarmIntent.putExtra("repeat", repeatModel);
                    pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                  //  if (flag) {
                        if (System.currentTimeMillis() > actualTime - 1000) {
                            manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
                        } else {
                            manager.set(AlarmManager.RTC_WAKEUP, actualTime, pendingIntent);
                        }
                    //    flag = false;
                    //} else {
                       // manager.set(AlarmManager.RTC_WAKEUP, actualTime, pendingIntent);
                    //}
                    id++;
                }
            }
        }
        // for non repeating alarm
        else {
            if (model.getPaymentStatus() == 0 && model.getNotificationFlag() == 0) {
                String date = CommonUtilities.convertDateIntoNumFormet(model.getDuedate());
                String actualDate = date + " " + timeArr[0] + ":" + minStr + ":" + "00";
                Log.e("actualDate ", actualDate);
                long actualTime = CommonUtilities.convertFullDateToMs(actualDate);
                actualTime = actualTime - model.getDueReminderNotification() * (24 * 60 * 60 * 1000);
                alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
                alarmIntent.putExtra("model", model);
                pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                //if (flag) {
                    if (System.currentTimeMillis() > actualTime ) {
                        manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);

                    } else {
                        Log.e("current time <", "actual time is >");
                        manager.set(AlarmManager.RTC_WAKEUP, actualTime, pendingIntent);
                    }
                //}
            }
        }
    }

    public ArrayList<DueUpcomingModel> getAllDatabaseList(Cursor cursor) {
        ArrayList<DueUpcomingModel> dueUpcomingModelArrayList = new ArrayList<>();
        while (cursor.moveToNext()) {
            DueUpcomingModel duePojo = new DueUpcomingModel();
            String category = cursor.getString(cursor.getColumnIndex(DueDetailTable.COLUMN_CATEGORY));
            String type = cursor.getString(cursor.getColumnIndex(DueDetailTable.COLUMN_Type));
            int reminderNotification = cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_REMINDER_NOTIFICATION));
            int repeatstatus = cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_REPEAT));
            int repeatEverydDay = cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_REPEATEVERY_input));
            String repeatEveryCategory = cursor.getString(cursor.getColumnIndex(DueDetailTable.COLUMN_REPEATEVERY_category));
            long repeatsUpto = cursor.getLong(cursor.getColumnIndex(DueDetailTable.COLUMN_REPEAT_UPTo));
            int id = cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_ID));
            long amount = cursor.getLong(cursor.getColumnIndex(DueDetailTable.COLUMN_AMOUNT));
            String payee = cursor.getString(cursor.getColumnIndex(DueDetailTable.COLUMN_PAYEENAME));
            long duedatems = cursor.getLong(cursor.getColumnIndex(DueDetailTable.COLUMN_DUEDATE));
            int repeatStatus = cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_REPEAT));
            int paymentStatus = cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_PAYMENT_STATUS));
            int notificationStatus = cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_NOTIFICATION_FLAG));

            duePojo.setNotificationFlag(notificationStatus);
            duePojo.setPaymentStatus(paymentStatus);
            duePojo.setRepeatupto(repeatsUpto);
            duePojo.setDueRepeatEveryCategory(repeatEveryCategory);
            duePojo.setDueRepeatEvery(repeatEverydDay);
            duePojo.setDueReminderNotification(reminderNotification);
            duePojo.setDueType(type);
            duePojo.setDuedate(duedatems);
            duePojo.setDueCategory(category);
            duePojo.setRepeatFlag(repeatstatus);
            duePojo.setDueAmount(amount);
            duePojo.setPayeeName(payee);
            duePojo.setRepeatFlag(repeatStatus);
            duePojo.setId(id);
            dueUpcomingModelArrayList.add(duePojo);
        }
        cursor.close();
        for (DueUpcomingModel model : dueUpcomingModelArrayList) {
            if (model.getRepeatFlag() == 1) {
                ArrayList<RepeatModel> repeatModelList = new ArrayList<>();
                Uri uri = ContentUris.withAppendedId(DueRepeatContentProvider.CONTENT_URI, model.getId());
                Cursor repeatcursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null, null);
                if (repeatcursor != null) {
                    repeatcursor.moveToFirst();
                    String duearray = repeatcursor.getString(repeatcursor.getColumnIndex(DueRepeatTable.COLUMN_REPEATARR));
                    String[] duelist = duearray.split(",");
                    String paymentArray = repeatcursor.getString(repeatcursor.getColumnIndex(DueRepeatTable.COLUMN_PAID));
                    String[] paymentList = paymentArray.split(",");
                    String notificationArray = repeatcursor.getString(repeatcursor.getColumnIndex(DueRepeatTable.COLUMN_NOTIFICATION_FLAG));
                    String[] notificationList = notificationArray.split(",");
                    repeatcursor.close();
                    for (int i = 0; i < duelist.length; i++) {
                        RepeatModel repeat = new RepeatModel();
                        long duems = Long.parseLong(duelist[i]);
                        int notificationStatus = Integer.parseInt(notificationList[i]);
                        int paymentStatus = Integer.parseInt(paymentList[i]);
                        repeat.setPaymentStatus(paymentStatus);
                        repeat.setDueTime(duems);
                        repeat.setNotificationStatus(notificationStatus);
                        repeatModelList.add(repeat);
                    }
                }
                model.setRepeatModelArrayList(repeatModelList);
            }
        }
        return dueUpcomingModelArrayList;
    }
}
