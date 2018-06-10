package blocker.com.newalarmservice.Activity.mainDrawerActivity.dashboard;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import blocker.com.newalarmservice.Activity.mainDrawerActivity.dashboard.dashboardTabsFragment.OverdueDashboard;
import blocker.com.newalarmservice.Activity.mainDrawerActivity.dashboard.dashboardTabsFragment.TotalPaymentDashboard;
import blocker.com.newalarmservice.Activity.mainDrawerActivity.dashboard.dashboardTabsFragment.UnPaidDashboard;
import blocker.com.newalarmservice.Activity.mainDrawerActivity.dashboard.dashboardTabsFragment.UpcomingDashboard;
import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.interfaces.INonRepeatRefreshFragment;
import blocker.com.newalarmservice.interfaces.IRefreshFragment;


public class DashboardFragment extends Fragment implements IRefreshFragment, INonRepeatRefreshFragment {
    private TabLayout scrollableTab;
    private ViewPager viewPager;
    private DashboardPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.dashboard, null);
        initUiElement(view1);

        adapter = new DashboardPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new TotalPaymentDashboard(), "Total Payment");
        adapter.addFragment(new UpcomingDashboard(), "Upcoming");
        adapter.addFragment(new OverdueDashboard(), "Overdue");
        adapter.addFragment(new UnPaidDashboard(), "Unpaid");
        viewPager.setAdapter(adapter);
        scrollableTab.setupWithViewPager(viewPager);
        scrollableTab.setTabTextColors(getResources().getColorStateList(R.color.tab_text_color));
        return view1;
    }

    private void initUiElement(View view) {
        scrollableTab = (TabLayout) view.findViewById(R.id.tabLayout);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
    }

    @Override
    public void notifyFragmentRerfresh() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void refreshData() {
        adapter.notifyDataSetChanged();
    }
}
