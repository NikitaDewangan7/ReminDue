package blocker.com.newalarmservice.dialogs;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.database.DueDetailContentProvider;
import blocker.com.newalarmservice.database.DueDetailTable;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatContentProvider;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatTable;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.models.RepeatModel;
import blocker.com.newalarmservice.receiver.AlarmReceiver;
import blocker.com.newalarmservice.utilities.CommonUtilities;
import blocker.com.newalarmservice.interfaces.IRefreshFragment;


public class PayDialog extends DialogFragment implements View.OnClickListener {
    private DueUpcomingModel upcomingModel;
    private RepeatModel upcomingRepeatModel;
    private static IRefreshFragment registerdFragment;
    private HashMap<String, Integer> categoryImageList;
    private String detail;

    public static void registerFragment(IRefreshFragment fragment) {
        registerdFragment = fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
        categoryImageList = CommonUtilities.getCategoryImageHashMap();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_mark_paid_dialog, null);
        Bundle bundle = getArguments();
        upcomingModel = (DueUpcomingModel) bundle.getSerializable("upcoming");
        upcomingRepeatModel = (RepeatModel) bundle.getSerializable("repeat");
        detail = bundle.getString("detail");
        TextView tvPayeeName, tvAmount, tvDueDate;
        ImageView imgCategory;
        Button btnPaid, btnCancel;
        tvDueDate = (TextView) view.findViewById(R.id.tvDueDate);
        imgCategory = (ImageView) view.findViewById(R.id.imgCategory);
        tvPayeeName = (TextView) view.findViewById(R.id.tvPayeeName);
        tvAmount = (TextView) view.findViewById(R.id.tvAmount);

        tvDueDate.setText(CommonUtilities.getDateStringFromMs(upcomingRepeatModel.getDueTime()));
        btnPaid = (Button) view.findViewById(R.id.imgPaid);

        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        btnPaid.setOnClickListener(this);

        tvAmount.setText(upcomingModel.getDueAmount() + "");
        tvPayeeName.setText(upcomingModel.getPayeeName());
        setSelectedImage(upcomingModel.getDueCategory(), imgCategory);

        String type = upcomingModel.getDueType().toLowerCase();
        if (type.equalsIgnoreCase("payable")) {
            btnPaid.setText("Paid");
            btnPaid.setBackgroundColor(getResources().getColor(R.color.payableColor));
            btnCancel.setBackgroundColor(getResources().getColor(R.color.payableColor));
        }
        if (type.equalsIgnoreCase("receivable")) {
            btnPaid.setText("Received");
            btnPaid.setBackgroundColor(getResources().getColor(R.color.receivableColor));
            btnCancel.setBackgroundColor(getResources().getColor(R.color.receivableColor));
        }
        return view;

    }

    private void setSelectedImage(String categoryName, ImageView imgCategory) {
        if (categoryImageList.containsKey(categoryName.toLowerCase())) {
            imgCategory.setImageResource(categoryImageList.get(categoryName.toLowerCase()));
        } else {
            imgCategory.setImageResource(R.drawable.other);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgPaid:
                int id = upcomingModel.getId();
                Uri dueUri = ContentUris.withAppendedId(DueDetailContentProvider.CONTENT_URI, id);
                ContentValues contentValues = new ContentValues();
                contentValues.put(DueDetailTable.COLUMN_PAYMENT_DATE, System.currentTimeMillis());
                getContext().getContentResolver().update(dueUri, contentValues, null, null);
                Uri uri1 = ContentUris.withAppendedId(DueRepeatContentProvider.CONTENT_URI, upcomingModel.getId());
                Cursor cursor1 = getContext().getContentResolver().query(uri1, null, null, null, null);
                if (cursor1 != null) {
                    cursor1.moveToFirst();
                    String payarr[] = cursor1.getString(cursor1.getColumnIndex(DueRepeatTable.COLUMN_PAID)).split(",");
                    String databaseTimeStr = cursor1.getString(cursor1.getColumnIndex(DueRepeatTable.COLUMN_REPEATARR));
                    String timeArray[] = databaseTimeStr.split(",");
                    cursor1.close();
                    StringBuilder dueTimeArr = new StringBuilder();
                    StringBuilder paymentArr = new StringBuilder();

                    if (upcomingModel.getRepeatModelArrayList().size() > 1) {
                        //int i = 0;
                        for (int k = 0; k < timeArray.length; k++) {
                            long duetime = Long.parseLong(timeArray[k]);
                            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            int alarmId = upcomingModel.getId() * 1000;
                            Intent intent = new Intent(getContext(), AlarmReceiver.class);
                            dueTimeArr.append(duetime + ",");
                            if (duetime == upcomingRepeatModel.getDueTime()) {
                                paymentArr.append("1,");
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarmManager.cancel(pendingIntent);
                                notificationManager.cancel(upcomingModel.getId());
                            } else {
                                paymentArr.append(payarr[k]).append(",");
                            }
                        }
                        Uri uri = ContentUris.withAppendedId(DueRepeatContentProvider.CONTENT_URI, upcomingModel.getId());
                        ContentValues values = new ContentValues();
                        values.put(DueRepeatTable.COLUMN_ID, id);
                        values.put(DueRepeatTable.COLUMN_PAID, paymentArr.toString());
                        values.put(DueRepeatTable.COLUMN_REPEATARR, databaseTimeStr);
                        getContext().getContentResolver().update(uri, values, null, null);
                        checkDueDateChangesOccourOrNot(dueTimeArr.toString(), paymentArr.toString());
                        dismiss();
                    }
                    if (upcomingModel.getRepeatModelArrayList().size() == 1) {
                        for (int i = 0; i < timeArray.length; i++) {
                            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                            NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            int alarmId = upcomingModel.getId() * 1000;
                            Intent intent = new Intent(getContext(), AlarmReceiver.class);
                            Long time = Long.parseLong(timeArray[i]);
                            if (time == upcomingRepeatModel.getDueTime()) {
                                paymentArr.append("1,");
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarmManager.cancel(pendingIntent);
                                notificationManager.cancel(upcomingModel.getId());
                            } else {
                                paymentArr.append(payarr[i]).append(",");
                            }
                        }
                        Uri uri = ContentUris.withAppendedId(DueRepeatContentProvider.CONTENT_URI, upcomingModel.getId());
                        ContentValues values = new ContentValues();
                        values.put(DueRepeatTable.COLUMN_ID, id);
                        values.put(DueRepeatTable.COLUMN_PAID, paymentArr.toString());
                        values.put(DueRepeatTable.COLUMN_REPEATARR, databaseTimeStr);
                        getContext().getContentResolver().update(uri, values, null, null);
                        if (registerdFragment != null) {
                            registerdFragment.refreshData();
                        }
                        dismiss();
                    }
                } else {
                    Log.e("cursor is", "NUll");
                }
                if (detail != null) {
                    if (detail.equalsIgnoreCase("detail")) {
                        getActivity().finish();
                    }
                }
                break;
            case R.id.btnCancel:
                dismiss();
                break;
        }
    }

    private void checkDueDateChangesOccourOrNot(String dueDate, String payment) {
        if (checkDueDatePaymentStatus(dueDate, payment)) {
            Long updatedms = null;
            long actualDueDate = upcomingModel.getDuedate();
            String[] dudateArray, paymentArray;
            dudateArray = dueDate.split(",");
            paymentArray = payment.split(",");
            for (int i = 0; i < dudateArray.length; i++) {
                long time = Long.parseLong(dudateArray[i]);
                int paymentStatus = Integer.parseInt(paymentArray[i]);
                if (time > actualDueDate && paymentStatus == 0) {
                    updatedms = time;
                    break;
                }
            }
            if (updatedms != null) {
                ContentValues values = new ContentValues();
                values.put(DueDetailTable.COLUMN_DUEDATE, updatedms);
                Uri uri = ContentUris.withAppendedId(DueDetailContentProvider.CONTENT_URI, upcomingModel.getId());
                getContext().getContentResolver().update(uri, values, null, null);
            }
        }
    }

    private boolean checkDueDatePaymentStatus(String duedate, String payment) {
        long ms = upcomingModel.getDuedate();
        String[] dudateArray, paymentArray;
        dudateArray = duedate.split(",");
        paymentArray = payment.split(",");
        for (int i = 0; i < dudateArray.length; i++) {
            long time = Long.parseLong(dudateArray[i]);
            int payStatus = Integer.parseInt(paymentArray[i]);
            if (time == ms && payStatus == 1)
                return true;
        }
        return false;
    }
}
