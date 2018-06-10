package blocker.com.newalarmservice.Activity.mainDrawerActivity.reportFragment;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.models.RepeatModel;
import blocker.com.newalarmservice.utilities.CommonUtilities;


public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.CategoryViewHolder> {
    private Context context;
    private ArrayList<DueUpcomingModel> list;

    public CategoryRecyclerAdapter(Context context, ArrayList<DueUpcomingModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.due_item, null);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        holder.populateData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvday, tvMonth, tvPayeeName, tvCategory, tvAmount;
        private ImageView imgRepeat, imgRupee;
        private RelativeLayout dayColor;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            dayColor = (RelativeLayout) itemView.findViewById(R.id.layoutDateColor);
            tvday = (TextView) itemView.findViewById(R.id.tvday);
            tvMonth = (TextView) itemView.findViewById(R.id.tvmonth);
            tvPayeeName = (TextView) itemView.findViewById(R.id.tv_payeename);
            tvCategory = (TextView) itemView.findViewById(R.id.tv_duedays);
            tvAmount = (TextView) itemView.findViewById(R.id.tvAmount);
            imgRepeat = (ImageView) itemView.findViewById(R.id.img_repeat);
            imgRupee = (ImageView) itemView.findViewById(R.id.img_due_status);
        }

        public void populateData(DueUpcomingModel model) {
            String date = CommonUtilities.getDateStringFromMs(model.getDuedate());
            String[] arr = date.split("/");
            tvday.setText(arr[0]);
            tvMonth.setText(arr[1]);
            tvPayeeName.setText(model.getPayeeName());
            /*if (model.getRepeatFlag() == 1)
                imgRepeat.setVisibility(View.VISIBLE);
            else*/
            imgRepeat.setVisibility(View.GONE);
            tvAmount.setText("Rs " + model.getDueAmount());
            GradientDrawable drawable = (GradientDrawable) dayColor.getBackground();
            String type = model.getDueType().toLowerCase();
            if (model.getRepeatFlag() == 1) {
                ArrayList<RepeatModel> repeatArray = model.getRepeatModelArrayList();
                if (repeatArray.get(0).getPaymentStatus() == 1) {
                    setPaymentdate(model.getPaymentDate(), model.getDueType().toLowerCase());
                } else {
                    calDueREmaing(model);
                }
            } else {
                if (model.getPaymentStatus() == 1) {
                    setPaymentdate(model.getPaymentDate(), model.getDueType().toLowerCase());
                } else {
                    calDueREmaing(model);
                }
            }
            if (type.equalsIgnoreCase("payable")) {
                drawable.setColor(context.getResources().getColor(R.color.payableColor));
                imgRupee.setColorFilter(context.getResources().getColor(R.color.payableColor));
                tvAmount.setTextColor(context.getResources().getColor(R.color.payableColor));
            } else {
                drawable.setColor(context.getResources().getColor(R.color.receivableColor));
                imgRupee.setColorFilter(context.getResources().getColor(R.color.receivableColor));
                tvAmount.setTextColor(context.getResources().getColor(R.color.receivableColor));
            }
        }

        private void calDueREmaing(DueUpcomingModel model) {
            long diff = model.getDuedate() - CommonUtilities.conMstoActualMs(System.currentTimeMillis());

            if (diff > 0) {
                int calDay = (int) (diff / (1000 * 60 * 60));
                if (calDay < 24) {
                    tvCategory.setText("Due Today");
                } else {
                    int actualDay = calDay / 24;
                    if (actualDay == 1) {
                        tvCategory.setText("Due Tomorrow");
                    }
                    if (actualDay >= 2) {
                        tvCategory.setText("Due in " + actualDay + " days");
                    }
                }
            } else {
                diff = -(diff);
                int calDay = (int) (diff / (1000 * 60 * 60));
                if (calDay < 24) {
                    tvCategory.setText("Overdue Today");
                } else {
                    int actualDay = calDay / 24;
                    if (actualDay == 1) {
                        tvCategory.setText("Overdue Today");
                    }
                    if (actualDay == 2) {
                        tvCategory.setText("Overdue Yesterday");
                    }
                    if (actualDay > 2) {
                        tvCategory.setText("Overdue " + actualDay + " days");
                    }
                }
            }
        }

        private void setPaymentdate(long paymentdate, String type) {
            long diff = paymentdate - CommonUtilities.conMstoActualMs(System.currentTimeMillis());

            if (type.equalsIgnoreCase("payable")) {
                if (diff > 0) {
                    int calDay = (int) (diff / (1000 * 60 * 60));
                    if (calDay < 24) {
                        tvCategory.setText("Paid Today");
                    } else {
                        int actualDay = calDay / 24;
                        if (actualDay == 1) {
                            tvCategory.setText("Paid Today");
                        }
                        if (actualDay == 2) {
                            tvCategory.setText("Paid  Tomorrow");
                        }
                        if (actualDay > 2) {
                            tvCategory.setText("Paid at " + CommonUtilities.getDateStringFromMs(paymentdate));
                        }
                    }
                } else {
                    diff = -(diff);
                    int calDay = (int) (diff / (1000 * 60 * 60));
                    if (calDay < 24) {
                        tvCategory.setText("Paid Today");
                    } else {
                        int actualDay = calDay / 24;
                        if (actualDay == 1) {
                            tvCategory.setText("Paid Yesterday");
                        }
                        if (actualDay >= 2) {
                            tvCategory.setText("Paid at " + CommonUtilities.getDateStringFromMs(paymentdate) + " days");
                        }
                    }
                }
            }
            if (type.equalsIgnoreCase("receivable")) {
                if (diff > 0) {
                    int calDay = (int) (diff / (1000 * 60 * 60));
                    if (calDay < 24) {
                        tvCategory.setText("Received Today");
                    } else {
                        int actualDay = calDay / 24;
                        if (actualDay == 1) {
                            tvCategory.setText("Received Tomorrow");
                        }
                        if (actualDay >= 2) {
                            tvCategory.setText("Received at " + CommonUtilities.getDateStringFromMs(paymentdate));
                        }
                    }
                } else {
                    diff = -(diff);
                    int calDay = (int) (diff / (1000 * 60 * 60));
                    if (calDay < 24) {
                        tvCategory.setText(" Received Today");
                    } else {
                        int actualDay = calDay / 24;
                        if (actualDay == 1) {
                            tvCategory.setText("Received Yesterday");
                        }
                        if (actualDay >= 2) {
                            tvCategory.setText("Received at " + CommonUtilities.getDateStringFromMs(paymentdate) + " days");
                        }
                    }
                }
            }


        }

    }
}
