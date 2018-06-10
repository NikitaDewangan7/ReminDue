package blocker.com.newalarmservice.dialogs;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.database.DueDetailContentProvider;
import blocker.com.newalarmservice.database.DueDetailTable;
import blocker.com.newalarmservice.interfaces.INonRepeatRefreshFragment;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.receiver.AlarmReceiver;
import blocker.com.newalarmservice.utilities.CommonUtilities;


public class PayDialogSingleItem extends DialogFragment implements View.OnClickListener {
    private DueUpcomingModel upcomingModel;
    private static INonRepeatRefreshFragment registeredFragment;
    private HashMap<String, Integer> categoryImageList;
    private String detail;

    public static void registerFragment(INonRepeatRefreshFragment item) {
        registeredFragment = item;
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
        detail = bundle.getString("detail");
        TextView tvPayeeName, tvAmount, tvDueDate;
        ImageView imgCategory;
        Button btnPaid, btnCancel;
        tvDueDate = (TextView) view.findViewById(R.id.tvDueDate);
        imgCategory = (ImageView) view.findViewById(R.id.imgCategory);
        tvPayeeName = (TextView) view.findViewById(R.id.tvPayeeName);
        tvAmount = (TextView) view.findViewById(R.id.tvAmount);

        tvDueDate.setText(CommonUtilities.getDateStringFromMs(upcomingModel.getDuedate()));
        btnPaid = (Button) view.findViewById(R.id.imgPaid);

        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        btnPaid.setOnClickListener(this);
        String tamount = upcomingModel.getDueAmount() + " Rs";
        tvAmount.setText(tamount);
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
                ContentValues values = new ContentValues();
                values.put(DueDetailTable.COLUMN_PAYMENT_STATUS, 1);
                values.put(DueDetailTable.COLUMN_PAYMENT_DATE, System.currentTimeMillis());
                Uri uris = ContentUris.withAppendedId(DueDetailContentProvider.CONTENT_URI, id);
                getContext().getContentResolver().update(uris, values, null, null);
                AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                int alarmId = upcomingModel.getId() * 1000;
                Intent intent = new Intent(getContext(), AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
                notificationManager.cancel(upcomingModel.getId());
                if (registeredFragment != null)
                    registeredFragment.notifyFragmentRerfresh();
                dismiss();
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

}
