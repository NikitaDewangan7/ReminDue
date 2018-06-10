package blocker.com.newalarmservice.Activity.mainDrawerActivity.overdueFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.utilities.CommonUtilities;
import blocker.com.newalarmservice.dialogs.PayDialog;
import blocker.com.newalarmservice.dialogs.PayDialogSingleItem;


public class OverdueRecyclerAdapter extends RecyclerView.Adapter<OverdueRecyclerAdapter.UpcomingViewHolder> {
    private ArrayList<DueUpcomingModel> upcomingModelArrayList;
    private Context context;
    private HashMap<String, Integer> cateogryImageList;
    private OnOverdueItemClickListener listener;

    public void setOnOverdueItemClickLIstener(OnOverdueItemClickListener lIstener) {
        this.listener = lIstener;
    }

    public OverdueRecyclerAdapter(Context context, ArrayList<DueUpcomingModel> list) {
        upcomingModelArrayList = list;
        this.context = context;
        cateogryImageList = CommonUtilities.getCategoryImageHashMap();
    }

    @Override
    public UpcomingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.new_due_item, null);
        return new UpcomingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UpcomingViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return upcomingModelArrayList.size();
    }

    public class UpcomingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvPayeeName, tvDueText, tvRepeatText, tvDueDate, tvAmount, tvCategoryName;
        private ImageView imgRepeat, imgDueStatus, imgCategory;
        private LinearLayout duePaymentLayout, clickLayout;


        public UpcomingViewHolder(View itemView) {
            super(itemView);
            clickLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout2);
            clickLayout.setOnClickListener(this);
            tvCategoryName = (TextView) itemView.findViewById(R.id.tvCategory);
            imgCategory = (ImageView) itemView.findViewById(R.id.imgCategory);
            tvDueText = (TextView) itemView.findViewById(R.id.tv_duedays);
            tvDueDate = (TextView) itemView.findViewById(R.id.tvDueDate);
            tvPayeeName = (TextView) itemView.findViewById(R.id.tv_payeename);
            tvRepeatText = (TextView) itemView.findViewById(R.id.tv_repeattext);
            tvAmount = (TextView) itemView.findViewById(R.id.tvAmount);
            imgRepeat = (ImageView) itemView.findViewById(R.id.img_repeat);
            imgDueStatus = (ImageView) itemView.findViewById(R.id.img_due_status);
            imgDueStatus.setImageResource(R.drawable.indian_rupee);
            imgDueStatus.setColorFilter(context.getResources().getColor(R.color.themeorangeDark));
            duePaymentLayout = (LinearLayout) itemView.findViewById(R.id.layout_due_money);
            duePaymentLayout.setOnClickListener(this);
        }

        private void setSelectedImage(String categoryName) {
            if (cateogryImageList.containsKey(categoryName.toLowerCase())) {
                imgCategory.setImageResource(cateogryImageList.get(categoryName.toLowerCase()));
            } else {
                imgCategory.setImageResource(R.drawable.other);
            }
        }

        public void bindData(int position) {
            String type = upcomingModelArrayList.get(position).getDueType().toLowerCase();
            if (type.equalsIgnoreCase("payable")) {
                imgDueStatus.setColorFilter(context.getResources().getColor(R.color.payableColor));
                tvAmount.setTextColor(context.getResources().getColor(R.color.payableColor));
                tvDueText.setTextColor(context.getResources().getColor(R.color.payableColor));
                GradientDrawable drawable = (GradientDrawable) tvDueDate.getBackground();
                drawable.setColor(context.getResources().getColor(R.color.payableColor));
            }
            if (type.equalsIgnoreCase("receivable")) {
                imgDueStatus.setColorFilter(context.getResources().getColor(R.color.receivableColor));
                tvAmount.setTextColor(context.getResources().getColor(R.color.receivableColor));
                tvDueText.setTextColor(context.getResources().getColor(R.color.receivableColor));
                GradientDrawable drawable = (GradientDrawable) tvDueDate.getBackground();
                drawable.setColor(context.getResources().getColor(R.color.receivableColor));
            }

            tvCategoryName.setText(upcomingModelArrayList.get(position).getDueCategory());
            setSelectedImage(upcomingModelArrayList.get(position).getDueCategory());
            tvDueDate.setText(CommonUtilities.getDateStringFromMs(upcomingModelArrayList.get(position).getDuedate()));
            tvPayeeName.setText(upcomingModelArrayList.get(position).getPayeeName());
            tvAmount.setText("Rs " + upcomingModelArrayList.get(position).getDueAmount());
            if (upcomingModelArrayList.get(position).getRepeatFlag() == 1) {
                imgRepeat.setVisibility(View.VISIBLE);
                tvRepeatText.setVisibility(View.VISIBLE);
                tvRepeatText.setText("( " + upcomingModelArrayList.get(position).getDueRepeatEvery() + " " + upcomingModelArrayList.get(position).getDueRepeatEveryCategory() + " )");
            } else {
                imgRepeat.setVisibility(View.GONE);
                tvRepeatText.setVisibility(View.GONE);
            }

            long diff = CommonUtilities.conMstoActualMs(System.currentTimeMillis()) - CommonUtilities.conMstoActualMs(upcomingModelArrayList.get(position).getDuedate());
            int calDay = (int) (diff / (1000 * 60 * 60));
            if (calDay < 24) {
                tvDueText.setText("Overdue Tomorrow");
            } else {
                int actualDay = calDay / 24;
                if (actualDay >= 1) {
                    tvDueText.setText("Overdue " + actualDay + " days");
                }
            }


            if (upcomingModelArrayList.get(position).getRepeatFlag() == 1) {

                if (upcomingModelArrayList.get(position).getRepeatModelArrayList().size() == 1) {
                    imgDueStatus.setImageResource(R.drawable.indian_rupee);
                    tvAmount.setVisibility(View.VISIBLE);
                }
                if (upcomingModelArrayList.get(position).getRepeatModelArrayList().size() > 1) {
                    imgDueStatus.setImageResource(R.drawable.multiples);
                    tvAmount.setVisibility(View.GONE);
                }

            } else {
                imgDueStatus.setImageResource(R.drawable.indian_rupee);
                tvAmount.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            Log.e("onClick", "Called");
            if (v.getId() == R.id.layout_due_money) {
                if (upcomingModelArrayList.get(getAdapterPosition()).getRepeatFlag() == 1) {
                    if (upcomingModelArrayList.get(getAdapterPosition()).getRepeatModelArrayList().size() > 1) {
                        Intent intent = new Intent(context, OverdueMultipleDueActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("overdue", upcomingModelArrayList.get(getAdapterPosition()));
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                    if (upcomingModelArrayList.get(getAdapterPosition()).getRepeatModelArrayList().size() == 1) {
                        PayDialog dialog = new PayDialog();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("upcoming", upcomingModelArrayList.get(getAdapterPosition()));
                        bundle.putSerializable("repeat", upcomingModelArrayList.get(getAdapterPosition()).getRepeatModelArrayList().get(0));
                        bundle.putString("fragment", "overdue");
                        dialog.setArguments(bundle);
                        AppCompatActivity activity = (AppCompatActivity) context;
                        dialog.show(activity.getSupportFragmentManager(), "");
                    }
                } else {
                    PayDialogSingleItem dialogSingleItem = new PayDialogSingleItem();
                    AppCompatActivity activity = (AppCompatActivity) context;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("upcoming", upcomingModelArrayList.get(getAdapterPosition()));
                    bundle.putString("fragment", "overdue");
                    dialogSingleItem.setArguments(bundle);
                    dialogSingleItem.show(activity.getSupportFragmentManager(), "");
                }
            }
            if (v.getId() == R.id.linearLayout2) {
                if (listener != null) {
                    listener.onItemClicked(v, upcomingModelArrayList.get(getAdapterPosition()));
                }
            }

        }
    }

    public interface OnOverdueItemClickListener {
        public void onItemClicked(View view, DueUpcomingModel model);
    }
}
