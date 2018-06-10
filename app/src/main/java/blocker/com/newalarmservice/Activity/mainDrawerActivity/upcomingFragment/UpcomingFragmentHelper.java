package blocker.com.newalarmservice.Activity.mainDrawerActivity.upcomingFragment;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

import blocker.com.newalarmservice.database.DueDetailTable;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatContentProvider;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatTable;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.models.RepeatModel;
import blocker.com.newalarmservice.utilities.CommonUtilities;

/**
 * Created by Abhishek Dewangan on 09-04-2016.
 */
public class UpcomingFragmentHelper {
    private Context context;
    private long totalPayableAmount, totalReceivedAmount;

    public void setTotalPayableAmount() {
        this.totalPayableAmount = 0;
    }

    public void setTotalReceivedAmount() {
        this.totalReceivedAmount = 0;
    }

    public long getTotalPayableAmount() {
        return totalPayableAmount;
    }

    public long getTotalReceivedAmount() {
        return totalReceivedAmount;
    }

    public UpcomingFragmentHelper(Context context) {
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

    public ArrayList<DueUpcomingModel> getUpcomingDueList(ArrayList<DueUpcomingModel> list) {
        ArrayList<DueUpcomingModel> updatedList = new ArrayList<>();

        for (DueUpcomingModel model : list) {
            String paymentType = model.getDueType().toLowerCase();
            if (model.getRepeatFlag() == 1) {
                if (model.getDuedate() < CommonUtilities.conMstoActualMs(System.currentTimeMillis())) {
                    ArrayList<RepeatModel> repeatList = model.getRepeatModelArrayList();
                    ArrayList<RepeatModel> repeatModelArrayList = getUPdatedRepeatList(repeatList);
                    if (repeatModelArrayList.size() > 0) {
                        for (RepeatModel repeat : repeatList) {
                            if (repeat.getDueTime() >= CommonUtilities.conMstoActualMs(System.currentTimeMillis()) && repeat.getPaymentStatus() == 0) {
                                if (paymentType.equalsIgnoreCase("payable")) {
                                    totalPayableAmount = totalPayableAmount + model.getDueAmount();
                                }
                                if (paymentType.equalsIgnoreCase("receivable")) {
                                    totalReceivedAmount = totalReceivedAmount + model.getDueAmount();
                                }
                                model.setDuedate(repeat.getDueTime());
                                model.setRepeatModelArrayList(repeatModelArrayList);
                                updatedList.add(model);
                                break;
                            }
                        }
                    }
                } else {
                    ArrayList<RepeatModel> repeatList = model.getRepeatModelArrayList();
                    ArrayList<RepeatModel> updatedList1 = getUPdatedRepeatList(repeatList);
                    if (updatedList1.size() > 0) {
                        model.setDuedate(updatedList1.get(0).getDueTime());
                        model.setRepeatModelArrayList(updatedList1);
                        if (paymentType.equalsIgnoreCase("payable")) {
                            totalPayableAmount = totalPayableAmount + model.getDueAmount();
                        }
                        if (paymentType.equalsIgnoreCase("receivable")) {
                            totalReceivedAmount = totalReceivedAmount + model.getDueAmount();
                        }
                        updatedList.add(model);
                    }
                }
            } else {
                if (model.getPaymentStatus() == 0) {
                    if (model.getDuedate() >= CommonUtilities.conMstoActualMs(System.currentTimeMillis())) {
                        if (paymentType.equalsIgnoreCase("payable")) {
                            totalPayableAmount = totalPayableAmount + model.getDueAmount();
                        }
                        if (paymentType.equalsIgnoreCase("receivable")) {
                            totalReceivedAmount = totalReceivedAmount + model.getDueAmount();
                        }
                        updatedList.add(model);
                    }
                }
            }
        }
        return updatedList;
    }

    private ArrayList<RepeatModel> getUPdatedRepeatList(ArrayList<RepeatModel> list) {
        ArrayList<RepeatModel> newList = new ArrayList<>();
        for (RepeatModel model : list) {
            if (model.getDueTime() >= CommonUtilities.conMstoActualMs(System.currentTimeMillis()) && model.getPaymentStatus() == 0) {
                newList.add(model);
            }
        }
        return newList;
    }

    public long getTotalAmount() {
        return totalPayableAmount;
    }

    public void setTotalAmount() {
        totalPayableAmount = 0;
    }
}
