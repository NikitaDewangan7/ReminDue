package blocker.com.newalarmservice.Activity.mainDrawerActivity.upcomingFragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import blocker.com.newalarmservice.Activity.dueDetailActivity.DueDetailActivity;
import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.database.DueDetailContentProvider;
import blocker.com.newalarmservice.database.DueDetailTable;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.models.DueUpcomingModelDateComparator;
import blocker.com.newalarmservice.interfaces.INonRepeatRefreshFragment;
import blocker.com.newalarmservice.interfaces.IRefreshFragment;
import blocker.com.newalarmservice.utilities.VerticalSpaceItemDecorator;

/**
 * Created by abhishekdewa on 3/30/2016.
 */


public class UpcomingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, IRefreshFragment, INonRepeatRefreshFragment {
    private static final int LOADER_SEARCH_RESULTS = 1;
    private RecyclerView recyclerView;
    private UpcomingFragmentHelper upcomingFragmentHelper;
    private TextView tvTotalUpcomingAmount, tvTotalUpcomingBill, tvTotalReceived;
    private View layoutTop;
    private LinearLayout layoutNodata;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        upcomingFragmentHelper = new UpcomingFragmentHelper(getContext());
        this.getLoaderManager().restartLoader(LOADER_SEARCH_RESULTS, null, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upcoming_fragment, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_upcoming);
        tvTotalUpcomingAmount = (TextView) view.findViewById(R.id.tvTotalUpcomingAmount);
        tvTotalUpcomingBill = (TextView) view.findViewById(R.id.tvTotalUpcomingBills);
        tvTotalReceived = (TextView) view.findViewById(R.id.tvTotalReceivableAmount);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecorator(20));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutNodata = (LinearLayout) view.findViewById(R.id.layoutNodata);
        layoutTop = view.findViewById(R.id.relativeTop);
        layoutTop.setVisibility(View.GONE);
        layoutNodata.setVisibility(View.GONE);
        return view;
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
        Log.e("OnLoadFinished", "called");
        recyclerView.setAdapter(null);
        switch (loader.getId()) {
            case LOADER_SEARCH_RESULTS:
                Cursor cursor = getContext().getContentResolver().query(DueDetailContentProvider.CONTENT_URI, null, null, null, null);
                upcomingFragmentHelper.setTotalPayableAmount();
                upcomingFragmentHelper.setTotalReceivedAmount();
                ArrayList<DueUpcomingModel> allDbList = upcomingFragmentHelper.getAllDatabaseList(cursor);
                ArrayList<DueUpcomingModel> updatedList = upcomingFragmentHelper.getUpcomingDueList(allDbList);
                tvTotalUpcomingBill.setText(updatedList.size() + " bills");
                tvTotalUpcomingAmount.setText("Rs " + upcomingFragmentHelper.getTotalPayableAmount());
                tvTotalReceived.setText("Rs " + upcomingFragmentHelper.getTotalReceivedAmount());
                upcomingFragmentHelper.setTotalAmount();
                Collections.sort(updatedList, new DueUpcomingModelDateComparator());
                if (updatedList.size() > 0) {
                    layoutTop.setVisibility(View.VISIBLE);
                    layoutNodata.setVisibility(View.GONE);
                    UpcomingRecyclerAdapter upcomingAdapter = new UpcomingRecyclerAdapter(getContext(), updatedList);
                    upcomingAdapter.setOnUpcomingItemClickListener(upcomingListener);
                    recyclerView.setAdapter(upcomingAdapter);

                } else {
                    layoutTop.setVisibility(View.GONE);
                    layoutNodata.setVisibility(View.VISIBLE);
                    UpcomingRecyclerAdapter upcomingAdapter = new UpcomingRecyclerAdapter(getContext(), updatedList);
                    upcomingAdapter.setOnUpcomingItemClickListener(upcomingListener);
                    recyclerView.setAdapter(upcomingAdapter);

                }
                break;
        }

    }

    UpcomingRecyclerAdapter.OnUpcomingItemClickListener upcomingListener = new UpcomingRecyclerAdapter.OnUpcomingItemClickListener() {
        @Override
        public void onItemClicked(View view, DueUpcomingModel model) {
            Intent intent = new Intent(getContext(), DueDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("model", model);
            bundle.putString("category", "upcoming");
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onResume() {
        super.onResume();
        this.getLoaderManager().restartLoader(LOADER_SEARCH_RESULTS, null, this);
    }

    @Override
    public void refreshData() {
        this.getLoaderManager().restartLoader(LOADER_SEARCH_RESULTS, null, this);
    }

    @Override
    public void notifyFragmentRerfresh() {
        this.getLoaderManager().restartLoader(LOADER_SEARCH_RESULTS, null, this);
    }
}
