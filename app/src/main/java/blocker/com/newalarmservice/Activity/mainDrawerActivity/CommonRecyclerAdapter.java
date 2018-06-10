package blocker.com.newalarmservice.Activity.mainDrawerActivity;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.database.DueDetailTable;
import blocker.com.newalarmservice.utilities.CommonUtilities;
import blocker.com.newalarmservice.utilities.CursorRecyclerAdapter;
import blocker.com.newalarmservice.models.Due;


public class CommonRecyclerAdapter extends CursorRecyclerAdapter<CommonRecyclerAdapter.UpcomingViewHolder> {
    private Context context;
    private String fragmentType;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        onItemClickListener = itemClickListener;
    }

    public CommonRecyclerAdapter(Context context, String fragmentType) {
        super();
        this.context = context;
        this.fragmentType = fragmentType;
    }

    @Override
    public void onBindViewHolder(UpcomingViewHolder holder, Cursor cursor, int position) {
        holder.bindData(cursor);
    }

    @Override
    public UpcomingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.due_item, null);
        return new UpcomingViewHolder(view);
    }

    public class UpcomingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvPayeeName, tvDueText, tvRepeatText, tvDay, tvMonth, tvAmount;
        private ImageView imgRepeat, imgDueStatus;
        private View dateColor;
        private LinearLayout duePaymentLayout;
        private Due duePojo;

        public UpcomingViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvDueText = (TextView) itemView.findViewById(R.id.tv_duedays);
            tvDay = (TextView) itemView.findViewById(R.id.tvday);
            tvMonth = (TextView) itemView.findViewById(R.id.tvmonth);
            tvPayeeName = (TextView) itemView.findViewById(R.id.tv_payeename);
            tvRepeatText = (TextView) itemView.findViewById(R.id.tv_repeattext);
            tvAmount = (TextView) itemView.findViewById(R.id.tvAmount);
            imgRepeat = (ImageView) itemView.findViewById(R.id.img_repeat);
            dateColor = itemView.findViewById(R.id.layoutDateColor);
            imgDueStatus = (ImageView) itemView.findViewById(R.id.img_due_status);
            switch (fragmentType) {
                case "upcoming":
                    dateColor.setBackgroundColor(context.getResources().getColor(R.color.themeindigoDark));
                    imgDueStatus.setImageResource(R.drawable.currency);
                    imgDueStatus.setColorFilter(context.getResources().getColor(R.color.themeorangeDark));
                    tvAmount.setTextColor(context.getResources().getColor(R.color.themeorangeDark));
                    break;
                case "overdue":
                    dateColor.setBackgroundColor(context.getResources().getColor(R.color.themepink));
                    imgDueStatus.setImageResource(R.drawable.currency);
                    imgDueStatus.setColorFilter(context.getResources().getColor(R.color.themepink));
                    tvAmount.setTextColor(context.getResources().getColor(R.color.themepink));
                    break;
                case "paid":
                    dateColor.setBackgroundColor(context.getResources().getColor(R.color.themeorangeDark));
                    imgDueStatus.setImageResource(R.drawable.paid);
                    imgDueStatus.setColorFilter(context.getResources().getColor(R.color.themeorangeDark));
                    tvAmount.setTextColor(context.getResources().getColor(R.color.themeorangeDark));
                    break;
                case "unpaid":
                    dateColor.setBackgroundColor(context.getResources().getColor(R.color.themedarkgreenDark));
                    imgDueStatus.setImageResource(R.drawable.unpaid);
                    imgDueStatus.setColorFilter(context.getResources().getColor(R.color.themedarkgreenDark));
                    tvAmount.setTextColor(context.getResources().getColor(R.color.themedarkgreenDark));
                    break;
            }
        }

        public void bindData(final Cursor cursor) {
            String[] arr = context.getResources().getStringArray(R.array.months);
            ArrayList<String> months = new ArrayList<String>(Arrays.asList(arr));

            String payee = cursor.getString(cursor.getColumnIndex(DueDetailTable.COLUMN_PAYEENAME));

            tvPayeeName.setText(payee);
            long amount = cursor.getLong(cursor.getColumnIndex(DueDetailTable.COLUMN_AMOUNT));

            tvAmount.setText("" + amount);
            int repeatStatus = cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_REPEAT));

            if (repeatStatus == 1) {
                String repeattext = "( " + cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_REPEATEVERY_input)) + " " + cursor.getString(cursor.getColumnIndex(DueDetailTable.COLUMN_REPEATEVERY_category)) + " )";
                tvRepeatText.setText(repeattext);

            } else {
                imgRepeat.setVisibility(View.GONE);
                tvRepeatText.setVisibility(View.GONE);
            }
            long duedatems = cursor.getLong(cursor.getColumnIndex(DueDetailTable.COLUMN_DUEDATE));
            String duestr[] = CommonUtilities.convertMstoStr(duedatems);
            tvDay.setText("" + duestr[0]);
            tvMonth.setText("" + months.get(Integer.parseInt(duestr[1])));
            duePojo = getDueItemPojo(cursor);
            tvDueText.setText("Due in " + calDueDays() + " days");
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClicked(v, getAdapterPosition(), duePojo);
            }
        }

        private Due getDueItemPojo(Cursor cursor) {
            Due duePojo = new Due();
            String category = cursor.getString(cursor.getColumnIndex(DueDetailTable.COLUMN_CATEGORY));
            String type = cursor.getString(cursor.getColumnIndex(DueDetailTable.COLUMN_Type));
            int reminderNotification = cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_REMINDER_NOTIFICATION));
            int repeatstatus = cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_REPEAT));
            int repeatEverydDay = cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_REPEATEVERY_input));
            String repeatEveryCategory = cursor.getString(cursor.getColumnIndex(DueDetailTable.COLUMN_REPEATEVERY_category));
            long repeatsUpto = cursor.getLong(cursor.getColumnIndex(DueDetailTable.COLUMN_REPEAT_UPTo));
            int id = cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_ID));
            long amount = cursor.getLong(cursor.getColumnIndex(DueDetailTable.COLUMN_AMOUNT));
            String payee = cursor.getString(cursor.getColumnIndex(DueDetailTable.COLUMN_PAYEENAME));
            long duedatems = cursor.getLong(cursor.getColumnIndex(DueDetailTable.COLUMN_DUEDATE));
            int repeatStatus = cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_REPEAT));

            duePojo.setRepeatUpto(repeatsUpto);
            duePojo.setRepeatEveryCatgory(repeatEveryCategory);
            duePojo.setRepeatEvery(repeatEverydDay);
            duePojo.setReminderNotification(reminderNotification);
            duePojo.setPayentType(type);
            duePojo.setDueDate(duedatems);
            duePojo.setCategory(category);
            duePojo.setRepeatFlag(repeatstatus);
            duePojo.setAmount(amount);
            duePojo.setPayee(payee);
            duePojo.setRepeatFlag(repeatStatus);
            duePojo.setId(id);
            return duePojo;
        }

        private int calDueDays() {
            long duems = CommonUtilities.conMstoActualMs(duePojo.getDueDate());
            long currentms = CommonUtilities.conMstoActualMs(System.currentTimeMillis());
            int days = (int) ((duems - currentms) / (24 * 60 * 60 * 1000));
            return days;
        }


    }

    public interface OnItemClickListener {
        public void onItemClicked(View view, int position, Due duePojo);
    }


}
