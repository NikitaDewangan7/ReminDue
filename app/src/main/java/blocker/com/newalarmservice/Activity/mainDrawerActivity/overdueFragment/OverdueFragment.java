package blocker.com.newalarmservice.Activity.mainDrawerActivity.overdueFragment;

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
import java.util.Comparator;

import blocker.com.newalarmservice.Activity.dueDetailActivity.DueDetailActivity;
import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.database.DueDetailContentProvider;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.interfaces.INonRepeatRefreshFragment;
import blocker.com.newalarmservice.interfaces.IRefreshFragment;
import blocker.com.newalarmservice.utilities.VerticalSpaceItemDecorator;



public class OverdueFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, IRefreshFragment, INonRepeatRefreshFragment {
    private static final int LOADER_SEARCH_RESULTS = 3;
    private RecyclerView recyclerView;
    private OverdueFragmentHelper fragmentHelper;
    private TextView tvTotalOverdueAmount, tvTotalOverdueBill, tvTotalReceived;
    private View layoutTop;
    private LinearLayout layoutNodata;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upcoming_fragment, null);
        fragmentHelper = new OverdueFragmentHelper(getContext());
        tvTotalOverdueAmount = (TextView) view.findViewById(R.id.tvTotalUpcomingAmount);
        tvTotalOverdueBill = (TextView) view.findViewById(R.id.tvTotalUpcomingBills);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_upcoming);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecorator(15));
        tvTotalReceived = (TextView) view.findViewById(R.id.tvTotalReceivableAmount);
        layoutNodata = (LinearLayout) view.findViewById(R.id.layoutNodata);
        layoutTop = view.findViewById(R.id.relativeTop);
        layoutTop.setVisibility(View.GONE);
        layoutNodata.setVisibility(View.GONE);
        this.getLoaderManager().restartLoader(LOADER_SEARCH_RESULTS, null, this);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case LOADER_SEARCH_RESULTS:
                CursorLoader cursorLoader = new CursorLoader(getContext(),
                        DueDetailContentProvider.CONTENT_URI, null, null, null, null);
                return cursorLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        recyclerView.setAdapter(null);
        switch (loader.getId()) {
            case LOADER_SEARCH_RESULTS:
                Cursor cursor = getContext().getContentResolver().query(DueDetailContentProvider.CONTENT_URI, null, null, null, null, null);
                fragmentHelper.setTotalOverduePayableAmount(0);
                fragmentHelper.setTotalOverdueReceivableAmount(0);
                ArrayList<DueUpcomingModel> allDbList = fragmentHelper.getAllDatabaseList(cursor);
                ArrayList<DueUpcomingModel> updatedList = fragmentHelper.getOverDueList(allDbList);
                tvTotalOverdueAmount.setText("Rs " + fragmentHelper.getTotalOverduePayableAmount());
                tvTotalReceived.setText("Rs " + fragmentHelper.getTotalOverdueReceivableAmount());
                tvTotalOverdueBill.setText(updatedList.size() + " Bills");
                Collections.sort(updatedList, new Comparator<DueUpcomingModel>() {
                    @Override
                    public int compare(DueUpcomingModel lhs, DueUpcomingModel rhs) {
                        if (lhs.getDuedate() > rhs.getDuedate()) {
                            return -1;
                        } else if (lhs.getDuedate() < rhs.getDuedate()) {
                            return 1;
                        } else
                            return 0;

                    }
                });
                if (updatedList.size() > 0) {
                    layoutTop.setVisibility(View.VISIBLE);
                    layoutNodata.setVisibility(View.GONE);
                    OverdueRecyclerAdapter adapter = new OverdueRecyclerAdapter(getContext(), updatedList);
                    adapter.setOnOverdueItemClickLIstener(listener);
                    recyclerView.setAdapter(adapter);

                } else {
                    layoutTop.setVisibility(View.GONE);
                    layoutNodata.setVisibility(View.VISIBLE);
                    OverdueRecyclerAdapter adapter = new OverdueRecyclerAdapter(getContext(), updatedList);
                    adapter.setOnOverdueItemClickLIstener(listener);
                    recyclerView.setAdapter(adapter);
                }
                break;
        }
    }

    OverdueRecyclerAdapter.OnOverdueItemClickListener listener = new OverdueRecyclerAdapter.OnOverdueItemClickListener() {
        @Override
        public void onItemClicked(View view, DueUpcomingModel model) {
            Intent intent = new Intent(getContext(), DueDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("model", model);
            bundle.putString("category", "overdue");
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.e("onLoaderReset", "called");
    }

    @Override
    public void notifyFragmentRerfresh() {
        this.getLoaderManager().restartLoader(LOADER_SEARCH_RESULTS, null, this);
    }

    @Override
    public void refreshData() {
        this.getLoaderManager().restartLoader(LOADER_SEARCH_RESULTS, null, this);
    }
}
