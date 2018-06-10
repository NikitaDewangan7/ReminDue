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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;

import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.database.DueDetailContentProvider;
import blocker.com.newalarmservice.database.DueDetailTable;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatContentProvider;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatTable;


public class TotalPaymentDashboard extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private PieChart pieChartOverall;
    private static final int LOADER_SEARCH_RESULTS = 10;
    private TextView tvTotalPayable, tvTotalReceivable, tvStatus;
    private NestedScrollView scrollview;
    private LinearLayout layoutNodata;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.total_payment_dashboard, null);
        this.getLoaderManager().restartLoader(LOADER_SEARCH_RESULTS, null, this);
        pieChartOverall = (PieChart) view.findViewById(R.id.piechart);
        tvTotalPayable = (TextView) view.findViewById(R.id.tvPayable);
        tvTotalReceivable = (TextView) view.findViewById(R.id.tvReceivable);
        scrollview = (NestedScrollView) view.findViewById(R.id.scrollview);
        layoutNodata = (LinearLayout) view.findViewById(R.id.layoutNodata);
        tvStatus = (TextView) view.findViewById(R.id.tvStatus);
        return view;
    }

    private void populatePieDate(PieChart pieChart, int[] colors, float totalPayable, float totalReceivable) {
       ArrayList<Integer>clrs =new ArrayList<>();
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setHoleColorTransparent(true);

        pieChart.setRotationEnabled(true);
        pieChart.setRotationAngle(0);

        ArrayList<Entry> yValues = new ArrayList<>();
        ArrayList<String> xValues = new ArrayList<>();
        if (totalPayable > 0) {
            xValues.add("Payable");
            yValues.add(new Entry(totalPayable, 0));
            clrs.add(colors[0]);

        }
        if (totalReceivable > 0) {
            xValues.add("Receivable");
            yValues.add(new Entry(totalReceivable, 1));
            clrs.add(colors[1]);
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
        if (id == LOADER_SEARCH_RESULTS) {
            CursorLoader cursorLoader = new CursorLoader(getContext(),
                    DueDetailContentProvider.CONTENT_URI, null, null, null, null);
            return cursorLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        long totalPayable = 0, totalReceivable = 0;
        Cursor cursor = getContext().getContentResolver().query(DueDetailContentProvider.CONTENT_URI, null, null, null, null);
        calData(totalPayable, totalReceivable, cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void calData(long totalPayable, long totalReceivable, Cursor cursor) {
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String type = cursor.getString(cursor.getColumnIndex(DueDetailTable.COLUMN_Type));
                int repeat = cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_REPEAT));
                int id = cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_ID));
                long amount = cursor.getLong(cursor.getColumnIndex(DueDetailTable.COLUMN_AMOUNT));
                int paymentStatus = cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_PAYMENT_STATUS));
                String typeStr = type.toLowerCase();
                switch (typeStr) {
                    case "payable":
                        if (repeat == 1) {
                            Uri uri = ContentUris.withAppendedId(DueRepeatContentProvider.CONTENT_URI, id);
                            Cursor repeatCursor = getContext().getContentResolver().query(uri, null, null, null, null);
                            repeatCursor.moveToFirst();
                            String[] repeatArr = repeatCursor.getString(repeatCursor.getColumnIndex(DueRepeatTable.COLUMN_REPEATARR)).split(",");
                            String[] paymentArr = repeatCursor.getString(repeatCursor.getColumnIndex(DueRepeatTable.COLUMN_PAID)).split(",");
                            repeatCursor.close();
                            for (int i = 0; i < repeatArr.length; i++) {
                                int integer = Integer.parseInt(paymentArr[i]);
                                if (integer == 0) {
                                    totalPayable = totalPayable + amount;
                                    break;
                                }
                            }
                        } else {
                            if (paymentStatus == 0) {
                                totalPayable = totalPayable + amount;
                            }
                        }
                        break;
                    case "receivable":
                        if (repeat == 1) {
                            Uri uri = ContentUris.withAppendedId(DueRepeatContentProvider.CONTENT_URI, id);
                            Cursor repeatCursor = getContext().getContentResolver().query(uri, null, null, null, null);
                            repeatCursor.moveToFirst();
                            String[] repeatArr = repeatCursor.getString(repeatCursor.getColumnIndex(DueRepeatTable.COLUMN_REPEATARR)).split(",");
                            String[] paymentArr = repeatCursor.getString(repeatCursor.getColumnIndex(DueRepeatTable.COLUMN_PAID)).split(",");
                            repeatCursor.close();
                            for (int i = 0; i < repeatArr.length; i++) {
                                Integer integer = Integer.parseInt(paymentArr[i]);
                                if (integer == 0) {
                                    totalReceivable = totalReceivable + amount;
                                    break;
                                }
                            }
                        } else {
                            if (paymentStatus == 0) {
                                totalReceivable = totalReceivable + amount;
                            }
                        }
                        break;
                }

            }
        }
        cursor.close();
        int[] colors = {getResources().getColor(R.color.errorbg), getResources().getColor(R.color.themelightgreenDark)};
        if (totalPayable == 0 && totalReceivable == 0) {
            scrollview.setVisibility(View.GONE);
            layoutNodata.setVisibility(View.VISIBLE);

        } else {
            scrollview.setVisibility(View.VISIBLE);
            layoutNodata.setVisibility(View.GONE);

            populatePieDate(pieChartOverall, colors, totalPayable, totalReceivable);
            pieChartOverall.animateXY(1000, 1000);
            tvTotalPayable.setText("Rs " + totalPayable );
            tvTotalReceivable.setText("Rs " + totalReceivable );

            long status = totalReceivable - totalPayable;
            if (status < 0) {
                tvStatus.setText("You have to Pay  Rs " + (-status) );
            } else {
                tvStatus.setText("You get back Rs " + status);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.getLoaderManager().restartLoader(LOADER_SEARCH_RESULTS, null, this);
    }
}
