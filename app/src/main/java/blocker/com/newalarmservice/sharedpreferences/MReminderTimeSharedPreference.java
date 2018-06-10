package blocker.com.newalarmservice.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import blocker.com.newalarmservice.utilities.MyApplication;


public class MReminderTimeSharedPreference {
    private static MReminderTimeSharedPreference preference;
    private SharedPreferences spTime;
    private String spRemiderTime = "reminderTime";

    private MReminderTimeSharedPreference() {
        spTime = MyApplication.getApplicationInstance().getMyApplicationContext().getSharedPreferences(spRemiderTime, Context.MODE_PRIVATE);
        if (!(spTime.contains("remHr") && spTime.contains("remMin"))) {
            SharedPreferences.Editor editor = spTime.edit();
            editor.putInt("remHr", 10);
            editor.putInt("remMin", 0);
            editor.apply();
        }
    }

    public static MReminderTimeSharedPreference getReminderTimeSharedPreference() {
        if (preference == null) {
            preference = new MReminderTimeSharedPreference();
        }
        return preference;
    }

    public void setReminderTime(int hr, int min) {
        SharedPreferences.Editor editor = spTime.edit();
        editor.putInt("remHr", hr);
        editor.putInt("remMin", min);
        editor.apply();
    }

    public String getActualReminderTime() {
        int hr, min;
        hr = spTime.getInt("remHr", 0);
        min = spTime.getInt("remMin", 0);
        return hr + "/" + min;
    }

    public String getReminderTime() {
        int hr, min;
        StringBuilder time = new StringBuilder();
        hr = spTime.getInt("remHr", 0);
        min = spTime.getInt("remMin", 0);
        if (hr > 12) {
            time.append(hr - 12).append("/");
            time.append(min).append("/");
            time.append("PM");
        } else {
            time.append(hr).append("/");
            time.append(min).append("/");
            time.append("AM");
        }
        Log.e("Reminder Time",time.toString());
        return time.toString();
    }

}
