package blocker.com.newalarmservice.Activity.mainDrawerActivity.overdueFragment;

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
import blocker.com.newalarmservice.utilities.CommonUtilities;


public class OverdueFragmentHelper {
    private Context context;
    private long totalOverduePayableAmount, totalOverdueReceivableAmount;

    public void setTotalOverduePayableAmount(long totalOverduePayableAmount) {
        this.totalOverduePayableAmount = totalOverduePayableAmount;
    }

    public void setTotalOverdueReceivableAmount(long totalOverdueReceivableAmount) {
        this.totalOverdueReceivableAmount = totalOverdueReceivableAmount;
    }

    public long getTotalOverduePayableAmount() {
        return totalOverduePayableAmount;
    }


    public long getTotalOverdueReceivableAmount() {
        return totalOverdueReceivableAmount;
    }

    public OverdueFragmentHelper(Context context) {
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

    public ArrayList<DueUpcomingModel> getOverDueList(ArrayList<DueUpcomingModel> list) {
        ArrayList<DueUpcomingModel> updatedList = new ArrayList<>();
        for (DueUpcomingModel model : list) {
            String type = model.getDueType().toLowerCase();
            if (model.getRepeatFlag() == 1) {
                if (model.getDuedate() > CommonUtilities.conMstoActualMs(System.currentTimeMillis())) {
                    ArrayList<RepeatModel> repeatList = model.getRepeatModelArrayList();
                    model.setRepeatModelArrayList(getUPdatedRepeatList(repeatList));
                    for (RepeatModel repeat : repeatList) {
                        if (repeat.getDueTime() < CommonUtilities.conMstoActualMs(System.currentTimeMillis()) && repeat.getPaymentStatus() == 0) {
                            model.setDuedate(repeat.getDueTime());
                            updatedList.add(model);
                            if (type.equalsIgnoreCase("payable")) {
                                totalOverduePayableAmount = totalOverduePayableAmount + model.getDueAmount();
                            }
                            if (type.equalsIgnoreCase("receivable")) {
                                totalOverdueReceivableAmount = totalOverdueReceivableAmount + model.getDueAmount();
                            }
                            break;
                        }
                    }

                } else {
                    ArrayList<RepeatModel> repeatList = model.getRepeatModelArrayList();
                    ArrayList<RepeatModel> updatedList1 = getUPdatedRepeatList(repeatList);
                    if (updatedList1.size() > 0) {
                        model.setRepeatModelArrayList(updatedList1);
                        updatedList.add(model);
                        if (type.equalsIgnoreCase("payable")) {
                            totalOverduePayableAmount = totalOverduePayableAmount + model.getDueAmount();
                        }
                        if (type.equalsIgnoreCase("receivable")) {
                            totalOverdueReceivableAmount = totalOverdueReceivableAmount + model.getDueAmount();
                        }
                    }
                }
            } else {
                if (model.getPaymentStatus() == 0) {
                    if (model.getDuedate() < CommonUtilities.conMstoActualMs(System.currentTimeMillis())) {
                        updatedList.add(model);
                        if (type.equalsIgnoreCase("payable")) {
                            totalOverduePayableAmount = totalOverduePayableAmount + model.getDueAmount();
                        }
                        if (type.equalsIgnoreCase("receivable")) {
                            totalOverdueReceivableAmount = totalOverdueReceivableAmount + model.getDueAmount();
                        }
                    }
                }
            }
        }
        return updatedList;
    }

    private ArrayList<RepeatModel> getUPdatedRepeatList(ArrayList<RepeatModel> list) {
        ArrayList<RepeatModel> newList = new ArrayList<>();
        for (RepeatModel model : list) {
            if (model.getDueTime() < CommonUtilities.conMstoActualMs(System.currentTimeMillis()) && model.getPaymentStatus() == 0) {
                newList.add(model);
            }
        }
        return newList;
    }

}
