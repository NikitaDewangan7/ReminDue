package blocker.com.newalarmservice.Activity.mainDrawerActivity.unpaidFragment;

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
import blocker.com.newalarmservice.Activity.mainDrawerActivity.CommonRecyclerAdapter;
import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.database.DueDetailContentProvider;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.interfaces.INonRepeatRefreshFragment;
import blocker.com.newalarmservice.interfaces.IRefreshFragment;
import blocker.com.newalarmservice.utilities.VerticalSpaceItemDecorator;


public class UnpaidFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, IRefreshFragment, INonRepeatRefreshFragment {
    private static final int LOADER_SEARCH_RESULTS = 1;
    private Fragment fragment;
    private RecyclerView recyclerView;
    private CommonRecyclerAdapter adapter;
    private UnpaidFragmentHelper unpaidFragmentHelper;
    private TextView tvTotalUnPaidAmount, tvTotalUnPaidBill, tvTotalReceived, tvTextPaid, tvTextReceived;
    private View layoutTop;
    private LinearLayout layoutNodata;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unpaidFragmentHelper = new UnpaidFragmentHelper(getContext());
        fragment = this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.upcoming_fragment, null);
        tvTotalUnPaidAmount = (TextView) view.findViewById(R.id.tvTotalUpcomingAmount);
        tvTotalUnPaidBill = (TextView) view.findViewById(R.id.tvTotalUpcomingBills);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_upcoming);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecorator(15));
        tvTextPaid = (TextView) view.findViewById(R.id.tvtotalPayable);
        tvTextReceived = (TextView) view.findViewById(R.id.tvtotalReceivable);
        tvTextPaid.setText("Total UnPaid");
        tvTextReceived.setText("Total UnReceived");
        tvTotalReceived = (TextView) view.findViewById(R.id.tvTotalReceivableAmount);
        layoutNodata = (LinearLayout) view.findViewById(R.id.layoutNodata);
        layoutTop =  view.findViewById(R.id.relativeTop);
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

        switch (loader.getId()) {
            case LOADER_SEARCH_RESULTS:
                recyclerView.setAdapter(null);
                Cursor cursor = getContext().getContentResolver().query(DueDetailContentProvider.CONTENT_URI, null, null, null, null);
                unpaidFragmentHelper.setTotalUnpaidAmount(0);
                unpaidFragmentHelper.setTotalUnReceiveAmount(0);
                ArrayList<DueUpcomingModel> allDbList = unpaidFragmentHelper.getAllDatabaseList(cursor);
                ArrayList<DueUpcomingModel> updatedUnPaidList = unpaidFragmentHelper.getUnPaidList(allDbList);
                tvTotalUnPaidAmount.setText("Rs " + unpaidFragmentHelper.getTotalUnpaidAmount());
                tvTotalReceived.setText("Rs " + unpaidFragmentHelper.getTotalUnReceiveAmount());
                tvTotalUnPaidBill.setText(updatedUnPaidList.size() + " Bills");
                Collections.sort(updatedUnPaidList, new Comparator<DueUpcomingModel>() {
                    @Override
                    public int compare(DueUpcomingModel lhs, DueUpcomingModel rhs) {
                        if (lhs.getDuedate() > rhs.getDuedate()) {
                            return 1;
                        } else if (lhs.getDuedate() < rhs.getDuedate()) {
                            return -1;
                        } else
                            return 0;
                    }
                });
                if (updatedUnPaidList.size() > 0) {
                    layoutTop.setVisibility(View.VISIBLE);
                    layoutNodata.setVisibility(View.GONE);
                    UnPaidRecyclerAdapter adapter = new UnPaidRecyclerAdapter(getContext(), updatedUnPaidList);
                    adapter.setOnUnPaidItemClickListener(listener);
                    recyclerView.setAdapter(adapter);
                } else {
                    layoutTop.setVisibility(View.GONE);
                    layoutNodata.setVisibility(View.VISIBLE);
                }

                break;
        }
    }

    UnPaidRecyclerAdapter.OnUnPaidItemClickListener listener = new UnPaidRecyclerAdapter.OnUnPaidItemClickListener() {
        @Override
        public void onItemClicked(View view, DueUpcomingModel model) {
            Intent intent = new Intent(getContext(), DueDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("model", model);
            bundle.putString("category", "unpaid");
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
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
