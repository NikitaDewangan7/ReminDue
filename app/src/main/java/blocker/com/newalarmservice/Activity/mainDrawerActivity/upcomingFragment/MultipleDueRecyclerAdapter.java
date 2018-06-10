package blocker.com.newalarmservice.Activity.mainDrawerActivity.upcomingFragment;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.models.RepeatModel;
import blocker.com.newalarmservice.utilities.CommonUtilities;

/**
 * Created by abhishekdewa on 4/7/2016.
 */
public class MultipleDueRecyclerAdapter extends RecyclerView.Adapter<MultipleDueRecyclerAdapter.MultipleItemViewHolder> {
    private Context context;
    private ArrayList<RepeatModel> repeatModelArrayList;
    private OnMultipleDueItemClickListener listener;
    private String fragmentType;

    public void setOnMultipleItemClickListener(OnMultipleDueItemClickListener listener) {
        this.listener = listener;
    }

    public MultipleDueRecyclerAdapter(Context context, ArrayList<RepeatModel> list, String fragmentType) {
        this.context = context;
        repeatModelArrayList = list;
        this.fragmentType = fragmentType;

    }

    @Override
    public MultipleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.new_upcoming_multipleitem_recycler, null);
        return new MultipleItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MultipleItemViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return repeatModelArrayList.size();
    }

    public class MultipleItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvDay, tvMonth, tvYear, tvDaysRemaing;
        private LinearLayout layoutDate;

        public MultipleItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvDay = (TextView) itemView.findViewById(R.id.tvday);
            tvMonth = (TextView) itemView.findViewById(R.id.tvmonth);
            tvYear = (TextView) itemView.findViewById(R.id.tvYear);
            tvDaysRemaing = (TextView) itemView.findViewById(R.id.tvDueDays);
            layoutDate = (LinearLayout) itemView.findViewById(R.id.layoutDate);
            setColors();
        }

        public void bindData(int position) {
            long time = repeatModelArrayList.get(position).getDueTime();
            String[] dateArr = CommonUtilities.getDateStringFromMs(time).split("/");
            tvDay.setText(dateArr[0]);
            tvMonth.setText(dateArr[1]);
            tvYear.setText(dateArr[2]);
            long timeRemaning = time - CommonUtilities.conMstoActualMs(System.currentTimeMillis());
            if (timeRemaning >= 0) {
                int daysRemaing = (int) (timeRemaning / (24 * 60 * 60 * 1000));
                tvDaysRemaing.setText("" + daysRemaing);
            }
        }

        public void setColors() {
            GradientDrawable dateDrawable = (GradientDrawable) layoutDate.getBackground();
            GradientDrawable dueDrawable = (GradientDrawable) tvDaysRemaing.getBackground();
            dateDrawable.setColor(context.getResources().getColor(R.color.themelightblue));
            dueDrawable.setColor(context.getResources().getColor(R.color.themeorangeDark));
            if (fragmentType.equalsIgnoreCase("paid")) {
                tvDaysRemaing.setVisibility(View.GONE);
            }

        /*    switch (fragmentType)
         {
             case "upcoming":
                 dateDrawable.setColor(context.getResources().getColor(R.color.themelightblue));
                 dueDrawable.setColor(context.getResources().getColor(R.color.themeorangeDark));
                 break;
             case "overdue":
                 dateDrawable.setColor(context.getResources().getColor(R.color.themepink));
                 dueDrawable.setColor(context.getResources().getColor(R.color.themeorangeDark));
                 break;
             case "paid":
                 dateDrawable.setColor(context.getResources().getColor(R.color.themedarkgreenDark));
                 dueDrawable.setColor(context.getResources().getColor(R.color.themepink));
                 break;
             case "unpaid":
                 dateDrawable.setColor(context.getResources().getColor(R.color.themepurple));
                 dueDrawable.setColor(context.getResources().getColor(R.color.themeorangeDark));
                 break;
         }*/
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onMulitipleDueItemClick(v, getAdapterPosition(), repeatModelArrayList.get(getAdapterPosition()));
            }
        }
    }

    public interface OnMultipleDueItemClickListener {
        public void onMulitipleDueItemClick(View view, int position, RepeatModel repeatModel);
    }
}
