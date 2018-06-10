package blocker.com.newalarmservice.Activity.mainDrawerActivity.unpaidFragment;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import blocker.com.newalarmservice.Activity.mainDrawerActivity.upcomingFragment.MultipleDueRecyclerAdapter;
import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatContentProvider;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatTable;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.models.RepeatModel;
import blocker.com.newalarmservice.utilities.CommonUtilities;
import blocker.com.newalarmservice.dialogs.PayDialog;
import blocker.com.newalarmservice.utilities.MultipleVerticalSpaceItemDecorator;
import blocker.com.newalarmservice.utilities.VerticalSpaceItemDecorator;


public class UnPaidMultipleItemActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private DueUpcomingModel unPaidModel;
    private static final int LOADER_SEARCH_RESULTS = 20;
    //private TextView tvAmount;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private ArrayList<RepeatModel> adapterDataList;
    private MultipleDueRecyclerAdapter adapter;
    private TextView tvDueDate, tvUptoDate, tvAmount;
    private ImageView imgCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_upcoming_multiple_items);
        initUiElement();
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.back);
        getSupportLoaderManager().restartLoader(LOADER_SEARCH_RESULTS, null, this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Bundle bundle = getIntent().getExtras();
        unPaidModel = (DueUpcomingModel) bundle.getSerializable("unpaid");

        HashMap<String, Integer> imageList = CommonUtilities.getCategoryImageHashMap();
        String category = unPaidModel.getDueCategory().toLowerCase();
        if (imageList.containsKey(category)) {
            imgCategory.setImageResource(imageList.get(category));
        } else {
            imgCategory.setImageResource(R.drawable.other);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" " + unPaidModel.getPayeeName());
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }
        tvUptoDate.setText(CommonUtilities.getDateStringFromMs(unPaidModel.getRepeatupto()));
        tvDueDate.setText(CommonUtilities.getDateStringFromMs(unPaidModel.getDuedate()));
        tvAmount.setText("" + unPaidModel.getDueAmount());
        adapterDataList = unPaidModel.getRepeatModelArrayList();
        adapter = new MultipleDueRecyclerAdapter(this, adapterDataList, "unpaid");
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.addItemDecoration(new MultipleVerticalSpaceItemDecorator(5));
        adapter.setOnMultipleItemClickListener(listener);
    }

    private MultipleDueRecyclerAdapter.OnMultipleDueItemClickListener listener = new MultipleDueRecyclerAdapter.OnMultipleDueItemClickListener() {
        @Override
        public void onMulitipleDueItemClick(View view, int position, RepeatModel repeatModel) {
            PayDialog dialog = new PayDialog();
            Bundle bundle = new Bundle();
            bundle.putSerializable("repeat", repeatModel);
            bundle.putSerializable("upcoming", unPaidModel);
            bundle.putString("fragment", "unpaid");
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "");
        }
    };

    private void initUiElement() {
        // tvAmount = (TextView) findViewById(R.id.tvAmount);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_upcoming_multiple);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvAmount = (TextView) findViewById(R.id.tvAmount);
        tvDueDate = (TextView) findViewById(R.id.tvDueDate);
        tvUptoDate = (TextView) findViewById(R.id.tvUptoDate);
        imgCategory = (ImageView) findViewById(R.id.imgCategory);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_SEARCH_RESULTS:
                CursorLoader cursorLoader = new CursorLoader(this,
                        DueRepeatContentProvider.CONTENT_URI, null, null, null, null);
                return cursorLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Uri uri = ContentUris.withAppendedId(DueRepeatContentProvider.CONTENT_URI, unPaidModel.getId());
        Cursor cursor = getContentResolver().query(uri, null, null, null, null, null);
        cursor.moveToFirst();
        ArrayList<RepeatModel> repeatModelList = new ArrayList<>();
        String[] arr = cursor.getString(cursor.getColumnIndex(DueRepeatTable.COLUMN_REPEATARR)).split(",");
        String[] paymentArr = cursor.getString(cursor.getColumnIndex(DueRepeatTable.COLUMN_PAID)).split(",");
        cursor.close();
        for (int i = 0; i < arr.length; i++) {
            RepeatModel model = new RepeatModel();
            model.setDueTime(Long.parseLong(arr[i]));
            model.setPaymentStatus(Integer.parseInt(paymentArr[i]));
            repeatModelList.add(model);
        }

        adapterDataList = getOriginalRepeatedModelList(repeatModelList, unPaidModel.getDuedate());
        adapter = new MultipleDueRecyclerAdapter(this, adapterDataList, "upcoming");
        adapter.setOnMultipleItemClickListener(listener);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLoaderManager().destroyLoader(LOADER_SEARCH_RESULTS);
    }

    private ArrayList<RepeatModel> getOriginalRepeatedModelList(ArrayList<RepeatModel> list, long dueDate) {
        ArrayList<RepeatModel> originalList = new ArrayList<>();
        for (RepeatModel model : list) {
            if (model.getDueTime() >= dueDate && model.getPaymentStatus() == 0) {
                originalList.add(model);
            }
        }
        return originalList;
    }
}
