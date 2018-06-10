package blocker.com.newalarmservice.Activity.addDueActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.models.Due;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.models.RepeatModel;
import blocker.com.newalarmservice.receiver.AlarmReceiver;
import blocker.com.newalarmservice.sharedpreferences.MReminderTimeSharedPreference;
import blocker.com.newalarmservice.utilities.CommonUtilities;
import blocker.com.newalarmservice.utilities.MyApplication;

public class AddDueHelper implements DatePickerDialog.OnDateSetListener {
    private String selectedDate;
    private ArrayList<String> months;
    private Context context;


    public AddDueHelper(Context context) {
        String[] arr = context.getResources().getStringArray(R.array.months);
        months = new ArrayList<>(Arrays.asList(arr));
        this.context = context;
    }

    public void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = null;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            dialog = new DatePickerDialog(context, R.style.DialogTheme, this, year, month, day);
            // Do something for lollipop and above versions
        } else {
            dialog = new DatePickerDialog(context, this, year, month, day);
            // do something for phones running an SDK before lollipop
        }
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.getDatePicker().setCalendarViewShown(false);
        dialog.show();

    }

    public ArrayList<String> getReminderList() {
        ArrayList<String> reminderList = new ArrayList<>();
        reminderList.add("On due date");
        reminderList.add("1 day before due date");
        reminderList.add("2 day before due date");
        reminderList.add("3 day before due date");
        reminderList.add("4 day before due date");
        reminderList.add("5 day before due date");
        reminderList.add("6 day before due date");
        reminderList.add("7 day before due date");
        reminderList.add("8 day before due date");
        reminderList.add("9 day before due date");
        reminderList.add("10 day before due date");
        return reminderList;
    }

    public ArrayList<String> getPaymentTypeList() {
        ArrayList<String> paymentTypeList = new ArrayList<>();
        paymentTypeList.add("Payable");
        paymentTypeList.add("Receivable");
        return paymentTypeList;
    }

    public ArrayList<String> getRepeatEveryList() {
        ArrayList<String> repeatEveryList = new ArrayList<>();
        repeatEveryList.add("Week");
        repeatEveryList.add("Month");
        repeatEveryList.add("Year");
        return repeatEveryList;
    }

    public String convertMstoStr(long ms) {
        Date date = new Date(ms);
        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String[] datearr = sdf.format(date).split("/");
        int month = Integer.parseInt(datearr[1]);
        String dateIs = datearr[0] + " " + months.get(month - 1) + " " + datearr[2];
        return dateIs;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

        String date = dayOfMonth + "/" + months.get(monthOfYear) + "/" + year;
        if (MyApplication.getApplicationInstance().getCurrentActivity() instanceof AddDueActivity) {
            AddDueActivity activity = (AddDueActivity) MyApplication.getApplicationInstance().getCurrentActivity();
            activity.setDueDate(date);
        }
    }

    public long getDueDateTime() {
        String[] ar = selectedDate.split("[/]");
        String day, month, year;
        if (ar[0].length() == 1) {
            day = "0" + ar[0];
        } else {
            day = ar[0];
        }

        int monthint = Integer.parseInt(ar[1]);
        String monthstr = "" + monthint;
        if (monthstr.length() == 1) {
            month = "0" + monthstr;
        } else {
            month = monthstr;
        }
        year = "" + ar[2];

        String strDateFormet = day + "-" + month + "-" + year + " 00:00:00 ";
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date dueDate = null;
        try {
            dueDate = dateFormat.parse(strDateFormet);
            return dueDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void setAlarm(DueUpcomingModel model) throws ParseException {
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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
                String date = CommonUtilities.convertDateIntoNumFormet(repeatModel.getDueTime());
                String actualDate = date + " " + timeArr[0] + ":" + minStr + ":" + "00";
                Log.e("actualDate ", actualDate);
                long actualTime = CommonUtilities.convertFullDateToMs(actualDate);
                actualTime = actualTime - model.getDueReminderNotification() * (24 * 60 * 60 * 1000);
                alarmIntent = new Intent(context, AlarmReceiver.class);
                alarmIntent.putExtra("model", model);
                alarmIntent.putExtra("repeat", repeatModel);
                pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (System.currentTimeMillis() > actualTime - 1000) {
                    manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
                } else {
                    manager.set(AlarmManager.RTC_WAKEUP, actualTime, pendingIntent);
                }
                id++;
            }
        }
        // for non repeating alarm
        else {
            String date = CommonUtilities.convertDateIntoNumFormet(model.getDuedate());
            String actualDate = date + " " + timeArr[0] + ":" + minStr + ":" + "00";
            Log.e("actualDate ", actualDate);
            long actualTime = CommonUtilities.convertFullDateToMs(actualDate);
            actualTime = actualTime - model.getDueReminderNotification() * (24 * 60 * 60 * 1000);
            alarmIntent = new Intent(context, AlarmReceiver.class);
            alarmIntent.putExtra("model", model);
            pendingIntent = PendingIntent.getBroadcast(context, id, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (System.currentTimeMillis() > actualTime - 1000) {
                Log.e("current time >", "actual time is <");
                manager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);

            } else {
                Log.e("current time <", "actual time is >");
                manager.set(AlarmManager.RTC_WAKEUP, actualTime, pendingIntent);
            }
        }
    }

    public String calRepeatedDueTimes(Due due) {
        StringBuilder repeatDueTimeArrString = new StringBuilder();
        long duems = due.getDueDate();
        long uptodue = due.getRepeatUpto();
        int repeatEvery = due.getRepeatEvery();
        int dueday, dueMonth, dueYear;
        String[] dueDateArr = CommonUtilities.convertMstoStr(duems);
        dueday = Integer.parseInt(dueDateArr[0]);
        dueMonth = Integer.parseInt(dueDateArr[1]);
        dueYear = Integer.parseInt(dueDateArr[2]);
        String category = due.getRepeatEveryCatgory().toLowerCase();
        switch (category) {
            case "week":
                while (duems < uptodue) {
                    repeatDueTimeArrString.append(duems).append(",");
                    duems += (repeatEvery * 7 * 24 * 60 * 60 * 1000);
                }
                break;
            case "month":
                while (duems < uptodue) {
                    repeatDueTimeArrString.append(duems).append(",");
                    if (dueMonth == 12) {
                        dueMonth = repeatEvery;
                        dueYear++;
                        duems = CommonUtilities.convertFullDateToMs(dueday, dueMonth, dueYear, 0, 0, 0);
                    } else {
                        dueMonth = dueMonth + repeatEvery;
                        duems = CommonUtilities.convertFullDateToMs(dueday, dueMonth, dueYear, 0, 0, 0);
                    }
                }
                break;
            case "year":
                duems = CommonUtilities.convertFullDateToMs(dueday, dueMonth, dueYear, 0, 0, 0);
                while (duems < uptodue) {
                    repeatDueTimeArrString.append(duems).append(",");
                    dueYear = dueYear + repeatEvery;
                    duems = CommonUtilities.convertFullDateToMs(dueday, dueMonth, dueYear, 0, 0, 0);
                }

                break;
        }
        return repeatDueTimeArrString.toString();
    }

    public DueUpcomingModel getDueUPcomingMOdel(Due due) {
        DueUpcomingModel upcomingModel = new DueUpcomingModel();
        upcomingModel.setId(due.getId());
        upcomingModel.setPayeeName(due.getPayee());
        upcomingModel.setDueAmount(due.getAmount());
        upcomingModel.setDuedate(due.getDueDate());
        upcomingModel.setDueCategory(due.getCategory());
        upcomingModel.setDueType(due.getPayentType());
        upcomingModel.setDueReminderNotification(due.getReminderNotification());
        upcomingModel.setRepeatFlag(due.getRepeatFlag());
        upcomingModel.setDueRepeatEvery(due.getRepeatEvery());
        upcomingModel.setDueRepeatEveryCategory(due.getRepeatEveryCatgory());
        upcomingModel.setRepeatupto(due.getRepeatUpto());
        upcomingModel.setPaymentStatus(0);
        ArrayList<RepeatModel> repeatList = new ArrayList<>();

        if (due.getRepeatFlag() == 1) {
            Log.e("repeat array is ", calRepeatedDueTimes(due));
            String[] repDueDates = calRepeatedDueTimes(due).split(",");

            for (int i = 0; i < repDueDates.length; i++) {
                RepeatModel repeatModel = new RepeatModel();
                long dueTime = Long.parseLong(repDueDates[i]);
                int payment = 0;
                repeatModel.setPaymentStatus(payment);
                repeatModel.setDueTime(dueTime);
                repeatList.add(i, repeatModel);
            }
        }
        upcomingModel.setRepeatModelArrayList(repeatList);
        return upcomingModel;
    }

}
