package blocker.com.newalarmservice.Activity.mainDrawerActivity.dashboard.dashboardTabsFragment;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;
import java.util.HashMap;

import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.database.DueDetailContentProvider;
import blocker.com.newalarmservice.database.DueDetailTable;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatContentProvider;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatTable;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.models.RepeatModel;
import blocker.com.newalarmservice.utilities.CommonUtilities;


public class UnPaidDashboard extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    private PieChart pieChartUnPaid;
    private LinearLayout unPaidLayout;
    private static final int LOADER_SEARCH_RESULTS = 12;
    private NestedScrollView scrollview;
    private LinearLayout layoutNodata;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.unpaid_dashboard, null);
        pieChartUnPaid = (PieChart) view.findViewById(R.id.piechartUnPaid);
        unPaidLayout = (LinearLayout) view.findViewById(R.id.unPaidLayout);
        scrollview = (NestedScrollView) view.findViewById(R.id.scrollview1);
        layoutNodata = (LinearLayout) view.findViewById(R.id.layoutNodata1);
        scrollview.setVisibility(View.GONE);
        layoutNodata.setVisibility(View.GONE);
        getLoaderManager().restartLoader(LOADER_SEARCH_RESULTS, null, this);
        return view;
    }

    private void populatePieDate(PieChart pieChart, int[] colors, long unPaid, long unReceive) {
        ArrayList<Integer> clr = new ArrayList<>();
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setHoleColorTransparent(true);
        pieChart.setRotationEnabled(true);
        pieChart.setRotationAngle(0);
        ArrayList<Entry> yValues = new ArrayList<>();
        ArrayList<String> xValues = new ArrayList<>();
        if (unPaid > 0) {
            xValues.add("Unpaid");
            yValues.add(new Entry(unPaid, 0));
            clr.add(colors[0]);
        }
        if (unReceive > 0) {
            xValues.add("Unreceived");
            yValues.add(new Entry(unReceive, 1));
            clr.add(colors[1]);
        }

        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(0f);
        dataSet.setColors(clr);
        PieData pieData = new PieData(xValues, dataSet);
        pieData.setValueTextSize(15f);
        pieData.setValueTextColor(getResources().getColor(R.color.white));
        pieChart.setDescription("");
        pieChart.setData(pieData);
        pieChart.invalidate();

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_SEARCH_RESULTS) {
            CursorLoader cursorLoader = new CursorLoader(getContext(),
                    DueDetailContentProvider.CONTENT_URI, null, null, null, null);
            return cursorLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Cursor cursor = getContext().getContentResolver().query(DueDetailContentProvider.CONTENT_URI, null, null, null, null);
        ArrayList<DueUpcomingModel> list = getAllDatabaseList(cursor);
        setUpdatedResult(list);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void setUpdatedResult(ArrayList<DueUpcomingModel> list) {
        ArrayList<DueUpcomingModel> dueList = new ArrayList<>();
        long totalUnPaid = 0, totalUnReceive = 0;
        for (DueUpcomingModel model : list) {
            String dueType = model.getDueType().toLowerCase();
            if (dueType.equalsIgnoreCase("payable")) {
                if (model.getRepeatFlag() == 1) {
                    for (RepeatModel repeatModel : model.getRepeatModelArrayList()) {
                        if (repeatModel.getPaymentStatus() == 0) {
                            totalUnPaid = totalUnPaid + model.getDueAmount();
                            if (dueList.size() < 2) {
                                dueList.add(model);
                            }
                            break;
                        }
                    }
                } else {
                    if (model.getPaymentStatus() == 0) {
                        totalUnPaid = totalUnPaid + model.getDueAmount();
                        if (dueList.size() < 2) {
                            dueList.add(model);
                        }
                    }
                }
            }
            if (dueType.equalsIgnoreCase("receivable")) {
                if (model.getRepeatFlag() == 1) {
                    for (RepeatModel repeatModel : model.getRepeatModelArrayList()) {
                        if (repeatModel.getPaymentStatus() == 0) {
                            totalUnReceive = totalUnReceive + model.getDueAmount();
                            if (dueList.size() < 2) {
                                dueList.add(model);
                            }
                            break;
                        }
                    }
                } else {
                    if (model.getPaymentStatus() == 0) {
                        totalUnReceive = totalUnReceive + model.getDueAmount();
                        if (dueList.size() < 2) {
                            dueList.add(model);
                        }
                    }
                }
            }
        }
        if (totalUnPaid == 0 && totalUnReceive == 0) {
            scrollview.setVisibility(View.GONE);
            layoutNodata.setVisibility(View.VISIBLE);
        } else {
            scrollview.setVisibility(View.VISIBLE);
            layoutNodata.setVisibility(View.GONE);
            int[] unPaidPieColors = {getResources().getColor(R.color.themepink), getResources().getColor(R.color.themeindigo)};
            populatePieDate(pieChartUnPaid, unPaidPieColors, totalUnPaid, totalUnReceive);
            pieChartUnPaid.animateXY(1000, 1000);
            unPaidLayout.removeAllViews();
            for (DueUpcomingModel model : dueList) {
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.pie_item, null);
                unPaidLayout.addView(view1);
                inflateView(view1, model);
            }
        }
    }

    private void inflateView(View view, DueUpcomingModel model) {
        TextView tvPayee = (TextView) view.findViewById(R.id.tvPayeeName);
        TextView tvCategory = (TextView) view.findViewById(R.id.tvCategory);
        TextView tvAmount = (TextView) view.findViewById(R.id.tvAmount);
        tvAmount.setText("Rs " + model.getDueAmount());
        tvPayee.setText(model.getPayeeName());
        tvCategory.setText(model.getDueCategory());
        ImageView imgCategory = (ImageView) view.findViewById(R.id.imgCategory);
        HashMap<String, Integer> cateogryImageList = CommonUtilities.getCategoryImageHashMap();
        if (cateogryImageList.containsKey(model.getDueCategory().toLowerCase())) {
            imgCategory.setImageResource(cateogryImageList.get(model.getDueCategory().toLowerCase()));
        } else {
            imgCategory.setImageResource(R.drawable.other);
        }
        String dueType = model.getDueType().toLowerCase();
        if (dueType.equalsIgnoreCase("payable")) {
            tvAmount.setTextColor(getResources().getColor(R.color.payableColor));
        } else {
            tvAmount.setTextColor(getResources().getColor(R.color.themeindigo));
        }
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
                Cursor repeatcursor = getContext().getContentResolver().query(uri, null, null, null, null, null);
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

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_SEARCH_RESULTS, null, this);
    }
}
