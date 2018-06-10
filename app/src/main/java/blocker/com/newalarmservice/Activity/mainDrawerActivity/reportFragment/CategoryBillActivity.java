package blocker.com.newalarmservice.Activity.mainDrawerActivity.reportFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.models.DueUpcomingModel;


public class CategoryBillActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_bill_activity);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ArrayList<DueUpcomingModel> list = (ArrayList<DueUpcomingModel>) bundle.getSerializable("list");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(list.get(0).getDueCategory());
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }
        if (list != null) {
            recyclerView = (RecyclerView) findViewById(R.id.recycler_category);
            recyclerView.setAdapter(new CategoryRecyclerAdapter(this, list));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }
}
