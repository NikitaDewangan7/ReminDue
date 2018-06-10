package blocker.com.newalarmservice.Activity.mainDrawerActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.util.HashMap;

import blocker.com.newalarmservice.Activity.addDueActivity.AddDueActivity;
import blocker.com.newalarmservice.Activity.mainDrawerActivity.SettingFragment.SettingFragment;
import blocker.com.newalarmservice.Activity.mainDrawerActivity.about.AboutFragment;
import blocker.com.newalarmservice.Activity.mainDrawerActivity.dashboard.DashboardFragment;
import blocker.com.newalarmservice.Activity.mainDrawerActivity.overdueFragment.OverdueFragment;
import blocker.com.newalarmservice.Activity.mainDrawerActivity.paidFragment.PaidFragment;
import blocker.com.newalarmservice.Activity.mainDrawerActivity.reportFragment.ReportFragment;
import blocker.com.newalarmservice.Activity.mainDrawerActivity.unpaidFragment.UnpaidFragment;
import blocker.com.newalarmservice.Activity.mainDrawerActivity.upcomingFragment.UpcomingFragment;
import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.interfaces.INonRepeatRefreshFragment;
import blocker.com.newalarmservice.interfaces.INotifyMenuItems;
import blocker.com.newalarmservice.interfaces.IRefreshFragment;
import blocker.com.newalarmservice.utilities.MyApplication;
import blocker.com.newalarmservice.dialogs.PayDialog;
import blocker.com.newalarmservice.dialogs.PayDialogSingleItem;


public class DrawerActivity extends AppCompatActivity implements INotifyMenuItems {
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerViewl;
    private ActionBarDrawerToggle toggleBtn;
    private FrameLayout frameLayout;
    private Toolbar toolbar;
    private DrawerRecyclerAdapter adapter;
    private HashMap<Integer, MenuDrawerItemPojo> drawerItemPojos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        recyclerViewl = (RecyclerView) findViewById(R.id.recyclerDrawer);
        recyclerViewl.setLayoutManager(new LinearLayoutManager(this));
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setTitle("Reminder");
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        }

        toolbar.setNavigationIcon(R.mipmap.menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(recyclerViewl);
            }
        });

        MyApplication.getApplicationInstance().setCurrentActivity(this);
        drawerItemPojos = getDrawerItems();
        adapter = new DrawerRecyclerAdapter(this, drawerItemPojos);
        recyclerViewl.setAdapter(adapter);
        adapter.setOnDrawerItemClickListener(listener);
        Fragment dashboardFragment = new DashboardFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, dashboardFragment).commit();

    }

    private DrawerRecyclerAdapter.OnDrawerItemClickListener listener = new DrawerRecyclerAdapter.OnDrawerItemClickListener() {
        @Override
        public void onDrawerClick(View view, int position) {
            Log.e("drawer item clicked", "success");
            Fragment fragment;
            switch (position) {
                case 0:
                    getSupportActionBar().setTitle("DashBoard");
                    updateDrawerList(position);
                    adapter.notifyDataSetChanged();
                    fragment = new DashboardFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
                    drawerLayout.closeDrawer(recyclerViewl);
                    PayDialog.registerFragment((IRefreshFragment) fragment);
                    PayDialogSingleItem.registerFragment((INonRepeatRefreshFragment) fragment);
                    break;
                case 1:
                    getSupportActionBar().setTitle("Upcoming");
                    updateDrawerList(position);
                    adapter.notifyDataSetChanged();
                    fragment = new UpcomingFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
                    drawerLayout.closeDrawer(recyclerViewl);
                  //  PayDialog.registerFragment((IRefreshFragment) fragment);
                    //PayDialogSingleItem.registerFragment((INonRepeatRefreshFragment) fragment);
                    //MSharedPreferance.getSharedPreferance().setDrawerSelectedPostion(1);
                    break;
                case 2:
                    getSupportActionBar().setTitle("Overdue");
                    fragment = new OverdueFragment();
                    updateDrawerList(position);
                    adapter.notifyDataSetChanged();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
                    drawerLayout.closeDrawer(recyclerViewl);
                    PayDialog.registerFragment((IRefreshFragment) fragment);
                    PayDialogSingleItem.registerFragment((INonRepeatRefreshFragment) fragment);
                    break;
                case 3:
                    getSupportActionBar().setTitle("Unpaid");
                    fragment = new UnpaidFragment();
                    updateDrawerList(position);
                    adapter.notifyDataSetChanged();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
                    drawerLayout.closeDrawer(recyclerViewl);
                    PayDialog.registerFragment((IRefreshFragment) fragment);
                    PayDialogSingleItem.registerFragment((INonRepeatRefreshFragment) fragment);
                    // MSharedPreferance.getSharedPreferance().setDrawerSelectedPostion(3);
                    break;
                case 4:
                    getSupportActionBar().setTitle("Paid");
                    fragment = new PaidFragment();
                    updateDrawerList(position);
                    adapter.notifyDataSetChanged();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
                    drawerLayout.closeDrawer(recyclerViewl);
                    PayDialog.registerFragment((IRefreshFragment) fragment);
                    PayDialogSingleItem.registerFragment((INonRepeatRefreshFragment) fragment);
                    break;
                case 6:
                    getSupportActionBar().setTitle("Report");
                    updateDrawerList(position);
                    adapter.notifyDataSetChanged();
                    fragment = new ReportFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
                    drawerLayout.closeDrawer(recyclerViewl);
                    break;
                case 7:
                    getSupportActionBar().setTitle("Setting");
                    updateDrawerList(position);
                    fragment = new SettingFragment();
                    adapter.notifyDataSetChanged();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
                    drawerLayout.closeDrawer(recyclerViewl);
                    break;
                case 8:
                    getSupportActionBar().setTitle("About");
                    updateDrawerList(position);
                    fragment = new AboutFragment();
                    adapter.notifyDataSetChanged();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
                    drawerLayout.closeDrawer(recyclerViewl);
                    break;
                case 9:
                    drawerLayout.closeDrawer(recyclerViewl);
                    String shareBody = "Download this app : https://play.google.com/store/apps/details?id=" + getPackageName();
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
                    break;

            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, AddDueActivity.class);
            this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.getApplicationInstance().setCurrentActivity(this);
    }

    @Override
    public void menuItemUpdated() {
        adapter = new DrawerRecyclerAdapter(this, drawerItemPojos);
        recyclerViewl.setAdapter(adapter);
    }

    private HashMap<Integer, MenuDrawerItemPojo> getDrawerItems() {
        HashMap<Integer, MenuDrawerItemPojo> imageList = new HashMap();
        imageList.put(0, new MenuDrawerItemPojo(R.drawable.home, "Home", true, 0));
        imageList.put(1, new MenuDrawerItemPojo(R.drawable.upcoming, "Upcoming", false, 1));
        imageList.put(2, new MenuDrawerItemPojo(R.drawable.overdue, "Overdue", false, 2));
        imageList.put(3, new MenuDrawerItemPojo(R.drawable.unpaid, "Unpaid", false, 3));
        imageList.put(4, new MenuDrawerItemPojo(R.drawable.paid, "Paid", false, 4));
        imageList.put(6, new MenuDrawerItemPojo(R.drawable.report, "Report", false, 6));
        imageList.put(7, new MenuDrawerItemPojo(R.drawable.settings, "Setting", false, 7));
        imageList.put(8, new MenuDrawerItemPojo(R.drawable.about, "About", false, 8));
        imageList.put(9, new MenuDrawerItemPojo(R.drawable.share, "Share", false, 9));
        return imageList;
    }

    private void updateDrawerList(int position) {
        for (int i = 0; i < 11; i++) {
            if (drawerItemPojos.containsKey(i)) {
                if (drawerItemPojos.get(i).getPosition() == position) {
                    drawerItemPojos.get(i).setClickedstatus(true);
                } else {
                    drawerItemPojos.get(i).setClickedstatus(false);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume()", "called");
        adapter.setOnDrawerItemClickListener(listener);
    }
}
