package blocker.com.newalarmservice.Activity.mainDrawerActivity.dashboard.dashboardTabsFragment;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
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


public class OverdueDashboard extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private PieChart pieChartOverdue;
    private LinearLayout overdueLayout;
    private static final int LOADER_SEARCH_RESULTS = 12;
    private NestedScrollView scrollview;
    private LinearLayout layoutNodata;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.overdue_dashboard, null);
        getLoaderManager().initLoader(LOADER_SEARCH_RESULTS, null, this);
        overdueLayout = (LinearLayout) view.findViewById(R.id.overdueLayout);
        pieChartOverdue = (PieChart) view.findViewById(R.id.piechartOverDue);
        scrollview = (NestedScrollView) view.findViewById(R.id.scrollview);
        layoutNodata = (LinearLayout) view.findViewById(R.id.layoutNodata);
        return view;
    }

    private void populatePieDate(PieChart pieChart, int[] colors, long paid, long unpaid, long receive, long unreceive) {
        ArrayList<Integer> clrs = new ArrayList<>();
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setHoleColorTransparent(true);
        pieChart.setRotationEnabled(true);
        pieChart.setRotationAngle(0);
        ArrayList<Entry> yValues = new ArrayList<>();
        ArrayList<String> xValues = new ArrayList<>();

        if (paid > 0) {
            yValues.add(new Entry(paid, 0));
            xValues.add("Paid");
            clrs.add(colors[0]);
        }
        if (unpaid > 0) {
            yValues.add(new Entry(unpaid, 1));
            xValues.add("Unpaid");
            clrs.add(colors[1]);
        }
        if (receive > 0) {
            yValues.add(new Entry(receive, 2));
            xValues.add("Received");
            clrs.add(colors[2]);
        }
        if (unreceive > 0) {
            yValues.add(new Entry(unreceive, 3));
            xValues.add("Unreceived");
            clrs.add(colors[3]);
        }
        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(0f);
        dataSet.setColors(clrs);
        PieData pieData = new PieData(xValues, dataSet);
        pieData.setValueTextColor(getResources().getColor(R.color.white));
        pieData.setValueTextSize(15f);
        pieChart.setDescription("");
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (LOADER_SEARCH_RESULTS == id) {
            CursorLoader loader = new CursorLoader(getContext(), DueDetailContentProvider.CONTENT_URI, null, null, null, null);
            return loader;
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
        long totalPaid = 0, totalUnpaid = 0, totalReceive = 0, totalUnReceive = 0;
        for (DueUpcomingModel model : list) {
            String paymentType = model.getDueType().toLowerCase();
            if (paymentType.equalsIgnoreCase("payable")) {
                if (model.getRepeatFlag() == 1) {
                    boolean flagPaid = false;
                    for (RepeatModel repeatModel : model.getRepeatModelArrayList()) {
                        if (repeatModel.getDueTime() < CommonUtilities.conMstoActualMs(System.currentTimeMillis()) && repeatModel.getPaymentStatus() == 0) {
                            totalUnpaid = totalUnpaid + model.getDueAmount();
                            if (dueList.size() < 2) {
                                dueList.add(model);
                            }
                            flagPaid = true;
                            break;
                        }
                    }
                    if (!flagPaid) {
                        boolean status = false;
                        for (RepeatModel model1 : model.getRepeatModelArrayList()) {
                            if (model1.getPaymentStatus() == 1) {
                                status = true;
                            } else {
                                status = false;
                            }
                        }
                        if (status) {
                            totalPaid = totalPaid + model.getDueAmount();
                        }
                    }
                } else {

                    if (model.getDuedate() < CommonUtilities.conMstoActualMs(System.currentTimeMillis()) && model.getPaymentStatus() == 1) {
                        totalPaid = totalPaid + model.getDueAmount();
                    }
                    if (model.getDuedate() < CommonUtilities.conMstoActualMs(System.currentTimeMillis()) && model.getPaymentStatus() == 0) {
                        totalUnpaid = totalUnpaid + model.getDueAmount();
                        if (dueList.size() < 2) {
                            dueList.add(model);
                        }
                    }
                }
            }
            if (paymentType.equalsIgnoreCase("receivable")) {
                if (model.getRepeatFlag() == 1) {
                    boolean flagPaid = false;
                    for (RepeatModel repeatModel : model.getRepeatModelArrayList()) {
                        if (repeatModel.getDueTime() < CommonUtilities.conMstoActualMs(System.currentTimeMillis()) && repeatModel.getPaymentStatus() == 0) {
                            totalUnReceive = totalUnReceive + model.getDueAmount();
                            if (dueList.size() < 2) {
                                dueList.add(model);
                            }
                            flagPaid = true;
                            break;
                        }
                    }
                    if (!flagPaid) {
                        boolean status = false;
                        for (RepeatModel model1 : model.getRepeatModelArrayList()) {
                            if (model1.getPaymentStatus() == 1) {
                                status = true;
                            } else {
                                status = false;
                            }
                        }
                        if (status) {
                            totalReceive = totalReceive + model.getDueAmount();
                        }
                    }
                } else {
                    if (model.getDuedate() < CommonUtilities.conMstoActualMs(System.currentTimeMillis()) && model.getPaymentStatus() == 1) {
                        totalReceive = totalReceive + model.getDueAmount();
                    }
                    if (model.getDuedate() < CommonUtilities.conMstoActualMs(System.currentTimeMillis()) && model.getPaymentStatus() == 0) {
                        totalUnReceive = totalUnReceive + model.getDueAmount();
                        if (dueList.size() < 2) {
                            dueList.add(model);
                        }
                    }
                }
            }

        }
        if (totalPaid == 0 && totalUnpaid == 0 && totalReceive == 0 && totalUnReceive == 0) {
            scrollview.setVisibility(View.GONE);
            layoutNodata.setVisibility(View.VISIBLE);
        } else {
            scrollview.setVisibility(View.VISIBLE);
            layoutNodata.setVisibility(View.GONE);
            int[] colors1 = {getResources().getColor(R.color.themelightgreen), getResources().getColor(R.color.themepink),
                    getResources().getColor(R.color.themegrayDark), getResources().getColor(R.color.themeindigo)};
            populatePieDate(pieChartOverdue, colors1, totalPaid, totalUnpaid, totalReceive, totalUnReceive);
            overdueLayout.removeAllViews();
            for (DueUpcomingModel model : dueList) {
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.pie_item, null);
                overdueLayout.addView(view1);
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
        String type = model.getDueType().toLowerCase();
        if (type.equalsIgnoreCase("payable")) {
            tvAmount.setTextColor(getResources().getColor(R.color.themepink));
        }
        if (type.equalsIgnoreCase("receivable")) {
            tvAmount.setTextColor(getResources().getColor(R.color.themeindigo));
        }
    }

    public ArrayList<DueUpcomingModel> getAllDatabaseList(Cursor cursor) {
        ArrayList<DueUpcomingModel> dueUpcomingModelArrayList = new ArrayList<>();
        if (cursor.getCount() > 0) {
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
