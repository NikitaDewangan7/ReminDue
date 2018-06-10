package blocker.com.newalarmservice.dialogs;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.database.DueDetailContentProvider;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatContentProvider;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.models.RepeatModel;
import blocker.com.newalarmservice.receiver.AlarmReceiver;

public class DeleteDueDialog extends DialogFragment implements View.OnClickListener {
    private DueUpcomingModel model;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_delete, null);
        Bundle bundle = getArguments();
        model = (DueUpcomingModel) bundle.getSerializable("model");
        TextView btnCancel, btnDelete;
        btnCancel = (TextView) view.findViewById(R.id.btnClose);
        btnDelete = (TextView) view.findViewById(R.id.btnDelete);
        btnCancel.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClose:
                dismiss();
                break;
            case R.id.btnDelete:
                int id = model.getId();
                Uri uriDueDetail = ContentUris.withAppendedId(DueDetailContentProvider.CONTENT_URI, id);
                getContext().getContentResolver().delete(uriDueDetail, null, null);
                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(model.getId());
                int alarmId = id * 1000;
                if (model.getRepeatFlag() == 1) {
                    Uri uriRepeat = ContentUris.withAppendedId(DueRepeatContentProvider.CONTENT_URI, id);
                    getContext().getContentResolver().delete(uriRepeat, null, null);
                    for (RepeatModel repeat : model.getRepeatModelArrayList()) {
                        Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
                        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(getContext(), alarmId, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                        alarmManager.cancel(alarmPendingIntent);
                        alarmId++;
                    }

                } else {
                    Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
                    PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(getContext(), alarmId, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmManager.cancel(alarmPendingIntent);
                }
                getActivity().finish();
                break;
        }
    }
}
