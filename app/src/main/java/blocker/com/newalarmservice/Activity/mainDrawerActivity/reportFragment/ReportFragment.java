package blocker.com.newalarmservice.Activity.mainDrawerActivity.reportFragment;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.database.DueDetailContentProvider;
import blocker.com.newalarmservice.database.DueDetailTable;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatContentProvider;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatTable;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.models.RepeatModel;
import blocker.com.newalarmservice.utilities.CommonUtilities;


public class ReportFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private TextView tvTotalBills, tvTotalAmount;
    private LinearLayout layout;
    private PieChart categoryPieChart;
    private static final int LOADER_SEARCH_RESULTS = 15;
    private ArrayList<String> cList;
    private HashMap<String, ArrayList<DueUpcomingModel>> cDetialList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report, null);
        getLoaderManager().initLoader(LOADER_SEARCH_RESULTS, null, this);

        tvTotalAmount = (TextView) view.findViewById(R.id.tvTotalAmount);
        tvTotalBills = (TextView) view.findViewById(R.id.tvTotalBills);

        layout = (LinearLayout) view.findViewById(R.id.categoryLayout);
        categoryPieChart = (PieChart) view.findViewById(R.id.piechartCategory);

        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_SEARCH_RESULTS) {
            CursorLoader loader = new CursorLoader(getContext(), DueDetailContentProvider.CONTENT_URI, null, null, null, null);
            return loader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            ArrayList<DueUpcomingModel> allDbList = getAllDatabaseList(data);
            HashMap<String, ArrayList<DueUpcomingModel>> listHashMap = getCategoryList(allDbList);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void inflateViews(HashMap<String, ArrayList<DueUpcomingModel>> categoryWiseList, HashMap<String, Long> categoryWiseAmount, ArrayList<String> categoryList) {
        HashMap<String, Integer> imageList = CommonUtilities.getCategoryImageHashMap();
        int count = 0;
        for (String str : categoryList) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.pie_item, null);
            TextView tvPayee = (TextView) view.findViewById(R.id.tvPayeeName);
            TextView tvCategory = (TextView) view.findViewById(R.id.tvCategory);
            TextView tvAmount = (TextView) view.findViewById(R.id.tvAmount);
            ImageView imgCategory = (ImageView) view.findViewById(R.id.imgCategory);
            tvAmount.setText("Rs " + categoryWiseAmount.get(str));
            tvPayee.setText(str);
            tvCategory.setText("" + categoryWiseList.get(str).size() + " Bills");
            if (imageList.containsKey(str.toLowerCase())) {
                imgCategory.setImageResource(imageList.get(str.toLowerCase()));
            } else {
                imgCategory.setImageResource(R.drawable.other);
            }
            view.setId(count);
            view.setOnClickListener(this);
            layout.addView(view);
            count++;
        }
    }

    public HashMap<String, ArrayList<DueUpcomingModel>> getCategoryList(ArrayList<DueUpcomingModel> list) {
        long totalAmount = 0;
        ArrayList<String> categoryList = new ArrayList<>();
        HashMap<String, ArrayList<DueUpcomingModel>> categoryWiseList = new HashMap<>();

        for (DueUpcomingModel model : list) {
            totalAmount = totalAmount + model.getDueAmount();
            if (!categoryList.contains(model.getDueCategory())) {
                categoryList.add(model.getDueCategory());
            }
            if (!categoryWiseList.containsKey(model.getDueCategory())) {
                ArrayList<DueUpcomingModel> modelList = new ArrayList<>();
                modelList.add(model);
                categoryWiseList.put(model.getDueCategory(), modelList);
            } else {
                categoryWiseList.get(model.getDueCategory()).add(model);
            }
        }
        tvTotalAmount.setText("Rs " + totalAmount);
        tvTotalBills.setText(list.size() + " Bills");
        int[] colors = {getResources().getColor(R.color.themebluegray), getResources().getColor(R.color.themebrown)
                , getResources().getColor(R.color.themedarkgreen)
                , getResources().getColor(R.color.themeindigo), getResources().getColor(R.color.themegray)
                , getResources().getColor(R.color.themeorange), getResources().getColor(R.color.themelightblue)
                , getResources().getColor(R.color.themepink), getResources().getColor(R.color.themepurple),
                getResources().getColor(R.color.themeteal),getResources().getColor(R.color.errorbg),getResources().getColor(R.color.themegrayDark)
        ,getResources().getColor(R.color.themelightgreenDark),getResources().getColor(R.color.themepurpleDark),getResources().getColor(R.color.toolbarColor)
        ,getResources().getColor(R.color.payableColor),getResources().getColor(R.color.receivableColor),};
        cList = categoryList;
        cDetialList = categoryWiseList;
        populatePieDate(categoryPieChart, colors, categoryWiseList, categoryList, totalAmount);
        return categoryWiseList;
    }

    private void populatePieDate(PieChart pieChart, int[] colors, HashMap<String, ArrayList<DueUpcomingModel>> categoryDetails, ArrayList<String> categoryList, long totalAmount) {
        HashMap<String, Long> categoryWiseAmount = new HashMap<>();
        layout.removeAllViews();
        for (String key : categoryList) {
            Long amount = (long) 0;
            ArrayList<DueUpcomingModel> list = categoryDetails.get(key);
            for (DueUpcomingModel model : list) {
                amount = amount + model.getDueAmount();
            }
            categoryWiseAmount.put(key, amount);
        }
        inflateViews(categoryDetails, categoryWiseAmount, categoryList);
        pieChart.setFitsSystemWindows(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f);
        pieChart.setHoleColorTransparent(true);
        pieChart.setRotationEnabled(true);
        pieChart.setRotationAngle(0);
        ArrayList<Entry> yValues = new ArrayList<>();
        pieChart.setUsePercentValues(true);

        ArrayList<String> xValues = new ArrayList<>();
        ArrayList<String> xValues1 = new ArrayList<>();
        int count = 0;
        for (String str : categoryList) {
            xValues.add(str);
            xValues1.add("");
            long pievalue = (categoryWiseAmount.get(str) / totalAmount) * 100;
            yValues.add(new Entry(categoryWiseAmount.get(str), count));
            count++;
        }
        pieChart.getLegend().setWordWrapEnabled(true);
        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setValueFormatter(new PercentFormatter());
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(0f);
        dataSet.setColors(colors);
        PieData pieData = new PieData(xValues, dataSet);
        pieData.setValueTextColor(getResources().getColor(R.color.white));
        pieData.setValueTextSize(15f);
        pieChart.setDrawSliceText(false);
        pieChart.setDescription("");
        pieChart.setData(pieData);
        pieChart.invalidate();
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
            long paymentDate = cursor.getLong(cursor.getColumnIndex(DueDetailTable.COLUMN_PAYMENT_DATE));
            duePojo.setPaymentStatus(paymentStatus);
            duePojo.setRepeatupto(repeatsUpto);
            duePojo.setPaymentDate(paymentDate);
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
    public void onClick(View v) {
        if (cList != null) {
            String key = cList.get(v.getId());
            if (cDetialList.containsKey(key)) {
                Intent intent = new Intent(getContext(), CategoryBillActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", cDetialList.get(key));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.getLoaderManager().restartLoader(LOADER_SEARCH_RESULTS, null, this);
    }

}
