package blocker.com.newalarmservice.Activity.addDueActivity;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.interfaces.ICategoryDeleteNotify;
import blocker.com.newalarmservice.sharedpreferences.MSharedPreferance;


public class CategorySpinnerAdapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> catergoryList;
    private static ICategoryDeleteNotify categoryDeleteNotify;

    public void setCategoryDeleteNotify(ICategoryDeleteNotify deleteNotify) {
        categoryDeleteNotify = deleteNotify;
    }

    public CategorySpinnerAdapter(Context context, int resource, String[] arr) {
        super(context, resource, arr);
        this.context = context;
        catergoryList = new ArrayList<>(Arrays.asList(arr));
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_category_item, null);
        ImageView imgDelete = (ImageView) view.findViewById(R.id.imgDelete);
        imgDelete.setVisibility(View.GONE);
        TextView tvCategoryName = (TextView) view.findViewById(R.id.tvCategoryItemName);
        tvCategoryName.setText(catergoryList.get(position));
        return view;
    }

    public View getCustomView(final int position, final View convertView, final ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_category_item, null);
        TextView tvCategoryName = (TextView) view.findViewById(R.id.tvCategoryItemName);
        tvCategoryName.setText(catergoryList.get(position));
        ImageView imageView = (ImageView) view.findViewById(R.id.imgDelete);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String detail = catergoryList.get(position) + " Delete Successfully";
                Snackbar.make(parent.getRootView(), detail, Snackbar.LENGTH_SHORT).show();
                Log.e("delete btn", "clicked");
                MSharedPreferance.getSharedPreferance().deleteCategory(catergoryList.get(position));
                catergoryList.remove(position);
                if (categoryDeleteNotify != null)
                    categoryDeleteNotify.categoryItemDelete();
                notifyDataSetChanged();
            }
        });
        if (position == catergoryList.size() - 1)
            imageView.setVisibility(View.GONE);
        return view;
    }


    @Override
    public String getItem(int position) {
        return catergoryList.get(position);
    }

    @Override
    public int getCount() {
        return catergoryList.size();
    }


}
