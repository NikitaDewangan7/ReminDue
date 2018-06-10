package blocker.com.newalarmservice.Activity.mainDrawerActivity.unpaidFragment;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;

import blocker.com.newalarmservice.database.DueDetailTable;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatContentProvider;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatTable;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.models.RepeatModel;


public class UnpaidFragmentHelper {
    private Context context;
    private long totalUnpaidAmount, totalUnReceiveAmount;

    public void setTotalUnpaidAmount(long totalUnpaidAmount) {
        this.totalUnpaidAmount = totalUnpaidAmount;
    }

    public void setTotalUnReceiveAmount(long totalUnReceiveAmount) {
        this.totalUnReceiveAmount = totalUnReceiveAmount;
    }

    public long getTotalUnpaidAmount() {
        return totalUnpaidAmount;
    }

    public long getTotalUnReceiveAmount() {
        return totalUnReceiveAmount;
    }

    public UnpaidFragmentHelper(Context context) {
        this.context = context;
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
                Cursor repeatcursor = context.getContentResolver().query(uri, null, null, null, null, null);
                repeatcursor.moveToFirst();
                String duearray = repeatcursor.getString(repeatcursor.getColumnIndex(DueRepeatTable.COLUMN_REPEATARR));
                String[] duelist = duearray.split(",");
                String paymentArray = repeatcursor.getString(repeatcursor.getColumnIndex(DueRepeatTable.COLUMN_PAID));
                String[] paymentList = paymentArray.split(",");
                repeatcursor.close();
                for (int i = 0; i < duelist.length; i++) {
                    RepeatModel repeat = new RepeatModel();
                    long duems = Long.parseLong(duelist[i]);
                    int paymentStatus = Integer.parseInt(paymentList[i]);
                    repeat.setPaymentStatus(paymentStatus);
                    repeat.setDueTime(duems);
                    repeatModelList.add(repeat);
                }
                model.setRepeatModelArrayList(repeatModelList);
            }
        }
        return dueUpcomingModelArrayList;
    }

    public ArrayList<DueUpcomingModel> getUnPaidList(ArrayList<DueUpcomingModel> list) {
        ArrayList<DueUpcomingModel> unpaidList = new ArrayList<>();
        for (DueUpcomingModel model : list) {
            // repeat type
            String type = model.getDueType().toLowerCase();
            if (model.getRepeatFlag() == 1) {
                ArrayList<RepeatModel> repeatModelList = new ArrayList<>();
                for (RepeatModel repeat : model.getRepeatModelArrayList()) {
                    if (repeat.getPaymentStatus() == 0) {
                        repeatModelList.add(repeat);
                    }
                }
                if (repeatModelList.size() > 0) {
                    model.setDuedate(repeatModelList.get(0).getDueTime());
                    model.setRepeatModelArrayList(repeatModelList);
                    if (type.equalsIgnoreCase("payable")) {
                        totalUnpaidAmount = totalUnpaidAmount + model.getDueAmount();
                    }
                    if (type.equalsIgnoreCase("receivable")) {
                        totalUnReceiveAmount = totalUnReceiveAmount + model.getDueAmount();
                    }
                    unpaidList.add(model);
                }
            }
            if (model.getRepeatFlag() == 0) {
                if (model.getPaymentStatus() == 0) {
                    unpaidList.add(model);
                    if (type.equalsIgnoreCase("payable")) {
                        totalUnpaidAmount = totalUnpaidAmount + model.getDueAmount();
                    }
                    if (type.equalsIgnoreCase("receivable")) {
                        totalUnReceiveAmount = totalUnReceiveAmount + model.getDueAmount();
                    }
                }

            }
        }
        return unpaidList;
    }
}
