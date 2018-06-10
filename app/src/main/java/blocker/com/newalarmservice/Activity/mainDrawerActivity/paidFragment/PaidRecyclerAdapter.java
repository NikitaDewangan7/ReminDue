package blocker.com.newalarmservice.Activity.mainDrawerActivity.paidFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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


public class PaidRecyclerAdapter extends RecyclerView.Adapter<PaidRecyclerAdapter.PaidViewHolder> {
    private ArrayList<DueUpcomingModel> upcomingModelArrayList;
    private Context context;
    private OnPaidItemClickListener listener;
    private HashMap<String, Integer> cateogryImageList;

    public void setOnUnPaidItemClickListener(OnPaidItemClickListener listener) {
        this.listener = listener;

    }

    public PaidRecyclerAdapter(Context context, ArrayList<DueUpcomingModel> list) {
        upcomingModelArrayList = list;
        this.context = context;
        cateogryImageList = CommonUtilities.getCategoryImageHashMap();
    }

    @Override
    public PaidViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.new_due_item, null);
        return new PaidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PaidViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return upcomingModelArrayList.size();
    }

    public class PaidViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvPayeeName, tvDueText, tvRepeatText, tvDueDate, tvAmount, tvCategoryName;
        private ImageView imgRepeat, imgDueStatus, imgCategory;
        private LinearLayout duePaymentLayout;

        public PaidViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvCategoryName = (TextView) itemView.findViewById(R.id.tvCategory);
            imgCategory = (ImageView) itemView.findViewById(R.id.imgCategory);
            tvDueText = (TextView) itemView.findViewById(R.id.tv_duedays);
            tvDueDate = (TextView) itemView.findViewById(R.id.tvDueDate);
            tvPayeeName = (TextView) itemView.findViewById(R.id.tv_payeename);
            tvRepeatText = (TextView) itemView.findViewById(R.id.tv_repeattext);
            tvAmount = (TextView) itemView.findViewById(R.id.tvAmount);
            imgRepeat = (ImageView) itemView.findViewById(R.id.img_repeat);
            imgDueStatus = (ImageView) itemView.findViewById(R.id.img_due_status);
            imgDueStatus.setImageResource(R.drawable.paid);
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
            tvDueDate.setText(CommonUtilities.getDateStringFromMs(upcomingModelArrayList.get(position).getDuedate()));
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
            tvAmount.setText("Rs " + upcomingModelArrayList.get(position).getDueAmount());
            tvCategoryName.setText(upcomingModelArrayList.get(position).getDueCategory());
            setSelectedImage(upcomingModelArrayList.get(position).getDueCategory());
            tvPayeeName.setText(upcomingModelArrayList.get(position).getPayeeName());
            if (upcomingModelArrayList.get(position).getRepeatFlag() == 1) {
                imgRepeat.setVisibility(View.VISIBLE);
                tvRepeatText.setVisibility(View.VISIBLE);
                tvRepeatText.setText("( " + upcomingModelArrayList.get(position).getDueRepeatEvery() + " " + upcomingModelArrayList.get(position).getDueRepeatEveryCategory() + " )");
            } else {
                imgRepeat.setVisibility(View.GONE);
                tvRepeatText.setVisibility(View.GONE);
            }
            setPaymentdate(upcomingModelArrayList.get(position));
            if (upcomingModelArrayList.get(position).getRepeatFlag() == 1) {
                if (upcomingModelArrayList.get(position).getRepeatModelArrayList().size() == 1) {
                    imgDueStatus.setImageResource(R.drawable.paid);
                    tvAmount.setVisibility(View.VISIBLE);

                }
                if (upcomingModelArrayList.get(position).getRepeatModelArrayList().size() > 1) {
                    imgDueStatus.setImageResource(R.drawable.multiples);
                    tvAmount.setVisibility(View.GONE);
                }
            } else {
                imgDueStatus.setImageResource(R.drawable.paid);
                tvAmount.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.layout_due_money) {
                if (upcomingModelArrayList.get(getAdapterPosition()).getRepeatFlag() == 1) {
                    if (upcomingModelArrayList.get(getAdapterPosition()).getRepeatModelArrayList().size() > 1) {
                        Intent intent = new Intent(context, PaidMultipleItemActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("paid", upcomingModelArrayList.get(getAdapterPosition()));
                        intent.putExtras(bundle);
                        context.startActivity(intent);

                    }
                }
            }
            if (listener != null) {
                listener.onItemClicked(v, upcomingModelArrayList.get(getAdapterPosition()));
            }
        }

        private void setPaymentdate(DueUpcomingModel model) {
            long diff = CommonUtilities.conMstoActualMs(model.getPaymentDate()) - CommonUtilities.conMstoActualMs(System.currentTimeMillis());
            StringBuffer stringBuffer = new StringBuffer();
            if (model.getDueType().equalsIgnoreCase("payable")) {
                stringBuffer.append("Paid");
            } else {
                stringBuffer.append("Received");
            }
            if (diff >= 0) {
                int calDay = (int) (diff / (1000 * 60 * 60));
                if (calDay < 24) {
                    stringBuffer.append(" Today");
                } else {
                    int actualDay = calDay / 24;
                    if (actualDay == 1) {
                        stringBuffer.append(" Tomorrow");
                    }
                    if (actualDay >= 2) {
                        stringBuffer.append(" ").append(CommonUtilities.getDateStringFromMs(model.getPaymentDate()));
                    }
                }
            } else {
                diff = -(diff);
                int calDay = (int) (diff / (1000 * 60 * 60));
                if (calDay < 24) {
                    stringBuffer.append(" Today");

                } else {
                    int actualDay = calDay / 24;
                    if (actualDay == 1) {
                        stringBuffer.append(" Tomorrow");
                    }
                    if (actualDay >= 2) {
                        stringBuffer.append(" ").append(CommonUtilities.getDateStringFromMs(model.getPaymentDate()));

                    }
                }
            }
            tvDueText.setText(stringBuffer.toString());
        }
    }

    public interface OnPaidItemClickListener {
        public void onItemClicked(View view, DueUpcomingModel model);
    }


}
