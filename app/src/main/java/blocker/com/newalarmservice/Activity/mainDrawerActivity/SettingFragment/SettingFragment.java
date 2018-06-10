package blocker.com.newalarmservice.Activity.mainDrawerActivity.SettingFragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.playlog.internal.LogEvent;

import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.sharedpreferences.MReminderTimeSharedPreference;


public class SettingFragment extends Fragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {
    private ImageView imgClock;
    private TextView tvReminderTime;
    private View rootView;
    private Button btnSave;
    private int actHr = -1, actMin = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, null);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        imgClock = (ImageView) view.findViewById(R.id.img);
        tvReminderTime = (TextView) view.findViewById(R.id.tvReminderTime);
        rootView = view.findViewById(R.id.rootLayout);
        String[] timeArr = MReminderTimeSharedPreference.getReminderTimeSharedPreference().getActualReminderTime().split("/");
        Integer hrTime = Integer.parseInt(timeArr[0]);
        Integer minute = Integer.parseInt(timeArr[1]);
        String hrString = "";
        Log.e("hr & min", " Hour is :" + hrTime + " Minute is :" + minute);
        StringBuffer minStr = new StringBuffer();
        String amOrPm = "";
        if (minute < 10) {
            minStr.append("0").append(minute);
        } else {
            minStr.append(minute);
        }
        if (hrTime == 0) {
            hrString = "12";
            amOrPm = "AM";
        } else if (hrTime == 12) {
            hrString = "" + hrTime;
            amOrPm = "PM";
        } else if (hrTime > 12) {
            hrString = "" + (hrTime - 12);
            amOrPm = "PM";
        } else {
            hrString = "" + hrTime;
            amOrPm = "AM";
        }
        StringBuffer reminderTime = new StringBuffer();
        reminderTime.append(hrString).append(":").append(minStr).append(" ").append(amOrPm);
        tvReminderTime.setText(reminderTime.toString());

        AnimatorSet set = new AnimatorSet();
        set.setStartDelay(150);
        ObjectAnimator animator = ObjectAnimator.ofFloat(imgClock, "rotation", 0, -10);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(imgClock, "rotation", -10, 10);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(imgClock, "rotation", 10, -10);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(imgClock, "rotation", -10, 10);
        ObjectAnimator animator6 = ObjectAnimator.ofFloat(imgClock, "rotation", 10, 0);
        set.setDuration(300);
        set.playSequentially(animator, animator1, animator2, animator3, animator6);
        set.start();

        tvReminderTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
        return view;
    }

    public void showTimePicker() {
        final int hr, min;
        if (actHr == -1 && actMin == -1) {
            String[] time = MReminderTimeSharedPreference.getReminderTimeSharedPreference().getActualReminderTime().split("/");
            hr = Integer.parseInt(time[0]);
            min = Integer.parseInt(time[1]);
        } else {
            hr = actHr;
            min = actMin;
        }
        TimePickerDialog dialog;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            dialog = new TimePickerDialog(getContext(), R.style.DialogTheme, this, hr, min, false);
            dialog.show();
            // Do something for lollipop and above versions
        } else {
            dialog = new TimePickerDialog(getContext(), this, hr, min, false);
            dialog.show();
            // do something for phones running an SDK before lollipop
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                Log.e("act Hr & min", "--->" + actHr + "----" + actMin);
                if (actHr == -1 && actMin == -1) {
                    Snackbar.make(rootView, "Reminder Time is not selected", Snackbar.LENGTH_SHORT).show();
                } else {
                    MReminderTimeSharedPreference.getReminderTimeSharedPreference().setReminderTime(actHr, actMin);
                    Snackbar.make(rootView, "Time is set", Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int hr;
        actHr = hourOfDay;
        actMin = minute;
        String amOrPm;
        if (hourOfDay == 12) {
            hr = hourOfDay;
            amOrPm = "PM";
        } else if (hourOfDay == 0) {
            hr = 12;
            amOrPm = "AM";
        } else if (hourOfDay > 12) {
            hr = hourOfDay - 12;
            amOrPm = "PM";
        } else {
            hr = hourOfDay;
            amOrPm = "AM";
        }

        if (minute < 10) {
            String timeStr = "" + hr + ":" + "0" + minute + " " + amOrPm;
            tvReminderTime.setText(timeStr);
        } else {
            String timeStr = "" + hr + ":" + "" + minute + " " + amOrPm;
            tvReminderTime.setText(timeStr);
        }
    }
}
