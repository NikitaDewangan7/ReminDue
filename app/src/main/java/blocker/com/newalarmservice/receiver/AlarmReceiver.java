package blocker.com.newalarmservice.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.HashMap;

import blocker.com.newalarmservice.Activity.dueDetailActivity.DueDetailActivity;
import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.database.DueDetailContentProvider;
import blocker.com.newalarmservice.database.DueDetailTable;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatContentProvider;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatTable;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.models.RepeatModel;
import blocker.com.newalarmservice.services.ReminderNotificationService;
import blocker.com.newalarmservice.utilities.CommonUtilities;


public class AlarmReceiver extends BroadcastReceiver {

    Context context;

    // show notification according to Alarm
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Alarm Receiver", "Called");
        this.context = context;
        DueUpcomingModel upcomingModel = (DueUpcomingModel) intent.getSerializableExtra("model");
        RepeatModel repeatModel = null;
        if (upcomingModel.getRepeatFlag() == 1) {
            repeatModel = (RepeatModel) intent.getSerializableExtra("repeat");
            Uri repeatUri = ContentUris.withAppendedId(DueRepeatContentProvider.CONTENT_URI, upcomingModel.getId());

            StringBuilder notificationString = new StringBuilder();
            for (RepeatModel repeat : upcomingModel.getRepeatModelArrayList()) {
                if (repeat.getDueTime() == repeatModel.getDueTime()) {
                    notificationString.append("1,");
                } else {
                    notificationString.append("0,");
                }
            }
            ContentValues values = new ContentValues();
            values.put(DueRepeatTable.COLUMN_NOTIFICATION_FLAG, notificationString.toString());
            context.getContentResolver().update(repeatUri, values, null, null);
        } else {
            Uri detailUri = ContentUris.withAppendedId(DueDetailContentProvider.CONTENT_URI, upcomingModel.getId());
            ContentValues contentValues = new ContentValues();
            contentValues.put(DueDetailTable.COLUMN_NOTIFICATION_FLAG, 1);
            context.getContentResolver().update(detailUri, contentValues, null, null);
        }
        createNotification(upcomingModel, repeatModel);
    }

    private void createNotification(DueUpcomingModel model, RepeatModel repeatModel) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.notification_layout);
        HashMap<String, Integer> imageMap = CommonUtilities.getCategoryImageHashMap();
        String categoryName = model.getDueCategory().toLowerCase();
        int drawable;
        if (imageMap.containsKey(categoryName)) {
            drawable = imageMap.get(categoryName);
        } else {
            drawable = R.drawable.other;
        }
        String type = model.getDueType().toLowerCase();
        String paymentType = "";
        if (type.equals("receivable")) {
            paymentType = "Received";
        } else {
            paymentType = "Paid";
        }
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawable);
        remoteViews.setImageViewBitmap(R.id.image, bitmap);
        remoteViews.setImageViewResource(R.id.imgCancel, R.mipmap.close);
        remoteViews.setImageViewResource(R.id.imgPaid, R.mipmap.right);
        remoteViews.setTextViewText(R.id.tvPaid, paymentType);
        remoteViews.setTextViewText(R.id.title, model.getPayeeName());
        String descText = "Rs " + model.getDueAmount() + "  (" + model.getDueCategory() + " )";
        remoteViews.setTextViewText(R.id.text, descText);
        remoteViews.setTextColor(R.id.title, context.getResources().getColor(R.color.notificationtint));
        remoteViews.setTextColor(R.id.text, context.getResources().getColor(R.color.subtextColor));
        remoteViews.setTextColor(R.id.tvPaid, context.getResources().getColor(R.color.subtextColor));

        Intent paidIntent;
        paidIntent = new Intent(Intent.ACTION_SYNC, null, context, ReminderNotificationService.class);
        paidIntent.putExtra("status", 1);
        paidIntent.putExtra("id", model.getId());
        paidIntent.putExtra("model", model);
        if (repeatModel != null) {
            paidIntent.putExtra("repeat", repeatModel);
        }
        int statusId = model.getId() * 100;


        PendingIntent paidPendingIntent = PendingIntent.getService(context, statusId, paidIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.tvPaid, paidPendingIntent);

        Intent cancelIntent = new Intent(Intent.ACTION_SYNC, null, context, ReminderNotificationService.class);
        cancelIntent.putExtra("status", 2);
        cancelIntent.putExtra("id", model.getId());
        PendingIntent cancelPendingIntent = PendingIntent.getService(context, statusId + 1, cancelIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.tvPaid, paidPendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.tvCancel, cancelPendingIntent);
        remoteViews.setTextColor(R.id.tvCancel, context.getResources().getColor(R.color.subtextColor));

        Intent intent = new Intent(context, DueDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("model", model);
        if (repeatModel != null) {
            bundle.putSerializable("repeat", repeatModel);
        } else {
            Log.e("repeat model", "Is NUll");
        }
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        int icon;
        if (imageMap.containsKey(categoryName)) {
            icon = imageMap.get(categoryName);
        } else {
            icon = R.drawable.other;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                // Set Icon
                .setSmallIcon(icon)
                // Set Ticker Message
                .setTicker(model.getDueCategory() + " Due")
                .setContentText(descText)
                .setContentTitle(model.getPayeeName())
                // Dismiss Notification
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(pendingIntent);
        // Set PendingIntent into Notification

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        builder.setVibrate(new long[]{1000, 1000});
        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        Notification notification = builder.build();
        notification.bigContentView = remoteViews;
        notificationmanager.notify(model.getId(), notification);
    }

    public void removeAlarm(DueUpcomingModel model) {

    }
}
