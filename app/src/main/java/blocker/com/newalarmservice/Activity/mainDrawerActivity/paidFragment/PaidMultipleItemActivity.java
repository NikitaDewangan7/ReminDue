package blocker.com.newalarmservice.Activity.mainDrawerActivity.paidFragment;

import android.os.Bundle;
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
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.models.RepeatModel;
import blocker.com.newalarmservice.utilities.CommonUtilities;
import blocker.com.newalarmservice.utilities.MultipleVerticalSpaceItemDecorator;
import blocker.com.newalarmservice.utilities.VerticalSpaceItemDecorator;


public class PaidMultipleItemActivity extends AppCompatActivity {
    private DueUpcomingModel paidModel;
    // private TextView tvAmount;
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
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Bundle bundle = getIntent().getExtras();
        paidModel = (DueUpcomingModel) bundle.getSerializable("paid");
        HashMap<String, Integer> imageList = CommonUtilities.getCategoryImageHashMap();
        String category = paidModel.getDueCategory().toLowerCase();
        if (imageList.containsKey(category)) {
            imgCategory.setImageResource(imageList.get(category));
        } else {
            imgCategory.setImageResource(R.drawable.other);
        }
        tvUptoDate.setText(CommonUtilities.getDateStringFromMs(paidModel.getRepeatupto()));
        tvDueDate.setText(CommonUtilities.getDateStringFromMs(paidModel.getDuedate()));
        tvAmount.setText("" + paidModel.getDueAmount());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" " + paidModel.getPayeeName());
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }
        adapterDataList = paidModel.getRepeatModelArrayList();
        adapter = new MultipleDueRecyclerAdapter(this, adapterDataList, "paid");
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.addItemDecoration(new MultipleVerticalSpaceItemDecorator(5));
    }

    private void initUiElement() {
        // tvAmount = (TextView) findViewById(R.id.tvAmount);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_upcoming_multiple);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvAmount = (TextView) findViewById(R.id.tvAmount);
        tvDueDate = (TextView) findViewById(R.id.tvDueDate);
        tvUptoDate = (TextView) findViewById(R.id.tvUptoDate);
        imgCategory = (ImageView) findViewById(R.id.imgCategory);
    }
}
