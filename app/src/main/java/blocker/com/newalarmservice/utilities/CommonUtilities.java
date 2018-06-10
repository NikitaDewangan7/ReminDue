package blocker.com.newalarmservice.utilities;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import blocker.com.newalarmservice.R;


public class CommonUtilities {
    private static DateFormat dateFormat, fullDateFormat, dateWithMonth;

    static {
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        fullDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateWithMonth = new SimpleDateFormat("dd/MMM/yyyy");
    }

    public static String[] convertMstoStr(long ms) {
        Date date = new Date(ms);
        String[] datearr = dateFormat.format(date).split("/");
        return datearr;
    }

    public static long convertFullDateToMs(String fullDate) throws ParseException {
        Date date = fullDateFormat.parse(fullDate);
        return date.getTime();
    }

    public static String convertDateIntoNumFormet(long ms) {
        String date = dateFormat.format(ms);
        return date;
    }

    public static long conMstoActualMs(long ms) {
        Date date = new Date(ms);
        String str = dateFormat.format(date);
        String actualdatestr = str + " 00:00:00";
        try {
            Date date1 = fullDateFormat.parse(actualdatestr);
            return date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long convertMonthStrToMs(String date) {
        long datems = 0;
        try {
            Log.e("date is", date);
            Date date1 = dateWithMonth.parse(date);
            return date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("err dateParse", e.toString());
        }
        return datems;
    }

    public static String getDateStringFromMs(long ms) {
        Date date = new Date(ms);
        return dateWithMonth.format(date);
    }

    public static long convertFullDateToMs(int day, int month, int year, int hr, int min, int sec) {
        String strday, strMonth, fulldate;
        if (day < 10)
            strday = "0" + day;
        else
            strday = "" + day;
        if (month < 10)
            strMonth = "0" + month;
        else
            strMonth = "" + month;
        fulldate = strday + "/" + strMonth + "/" + year;
        //dd/MM/yyyy HH:mm:ss
        Log.e("fulldate str is", "dd/MM/yyyy " + "---->" + fulldate);
        try {
            return dateFormat.parse(fulldate).getTime();
        } catch (Exception e) {
            Log.e("parsing exception", e.toString());
        }
        return 0;
    }

    public static HashMap<String, Integer> getCategoryImageHashMap() {
        HashMap<String, Integer> categoryImageList = new HashMap<>();
        categoryImageList.put("accommodation", R.drawable.accomodation);
        categoryImageList.put("credit cards", R.drawable.creditcard);
        categoryImageList.put("mobile recharge", R.mipmap.technology);
        categoryImageList.put("car", R.drawable.car);
        categoryImageList.put("education", R.drawable.education);
        categoryImageList.put("electricity", R.drawable.electricity);
        categoryImageList.put("dth", R.drawable.dth);
        categoryImageList.put("two wheeler", R.drawable.twowheeler);
        categoryImageList.put("food", R.drawable.food);
        categoryImageList.put("gifts", R.drawable.gifts);
        categoryImageList.put("insurance", R.drawable.insurance);
        categoryImageList.put("medicare", R.drawable.medicare);
        categoryImageList.put("pets", R.drawable.pet);
        categoryImageList.put("sports", R.drawable.sports);
        categoryImageList.put("shopping", R.drawable.shopping);
        categoryImageList.put("tax", R.drawable.indian_rupee);
        categoryImageList.put("vacation", R.drawable.vacation);
        categoryImageList.put("investment", R.drawable.investment);
        categoryImageList.put("saving", R.drawable.saving);
        categoryImageList.put("child care", R.drawable.childcare);
        categoryImageList.put("cigarette", R.mipmap.cigarette);
        categoryImageList.put("liquor", R.mipmap.liquor);
        categoryImageList.put("landline", R.mipmap.telephone);
        categoryImageList.put("other", R.drawable.other);
        return categoryImageList;
    }
}
