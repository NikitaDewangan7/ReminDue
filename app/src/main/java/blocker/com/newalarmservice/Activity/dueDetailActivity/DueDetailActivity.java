package blocker.com.newalarmservice.Activity.dueDetailActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

import blocker.com.newalarmservice.Activity.addDueActivity.AddDueActivity;
import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.dialogs.DeleteDueDialog;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.models.RepeatModel;
import blocker.com.newalarmservice.utilities.CommonUtilities;
import blocker.com.newalarmservice.dialogs.PayDialog;
import blocker.com.newalarmservice.dialogs.PayDialogSingleItem;


public class DueDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvMonth, tvDay, tvPayeeName, tvCategory, tvRemainingDays, tvAmount,
            tvReminder, tvRepepatEvery, tvRepeat, tvRepeatUpto, tvPaymentType;
    private ImageView imgCategory;
    private FloatingActionButton fabDelete, fabEdit, fabPaid;
    private DueUpcomingModel model;
    private LinearLayout topLayout;
    private RepeatModel repeatModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail);
        initUiElement();
        Bundle bundle = getIntent().getExtras();
        model = (DueUpcomingModel) bundle.getSerializable("model");
        repeatModel = (RepeatModel) bundle.getSerializable("repeat");
        if (repeatModel == null) {
            Log.e("Repeat Model", "Is Null");
        } else {

        }
        if (model != null) {
            long diff = model.getDuedate() - CommonUtilities.conMstoActualMs(System.currentTimeMillis());
            if (model.getDueType().equalsIgnoreCase("payable")) {
                tvAmount.setBackgroundColor(getResources().getColor(R.color.payableColor));
            }
            if (model.getDueType().equalsIgnoreCase("receivable")) {
                tvAmount.setBackgroundColor(getResources().getColor(R.color.receivableColor));

            }
            if (diff >= 0) {
                int calDay = (int) (diff / (1000 * 60 * 60));
                if (calDay < 24) {
                    tvRemainingDays.setText(" Due Today ");
                } else {
                    int actualDay = calDay / 24;
                    if (actualDay == 1) {
                        tvRemainingDays.setText("Due Yesterday");
                    }
                    if (actualDay >= 2) {
                        tvRemainingDays.setText("Due in " + actualDay + " days");
                    }
                }
            } else {
                diff = -(diff);
                int calDay = (int) (diff / (1000 * 60 * 60));
                if (calDay < 24) {
                    tvRemainingDays.setText(" Overdue Today");
                } else {
                    int actualDay = calDay / 24;
                    if (actualDay == 1) {
                        tvRemainingDays.setText("Overdue Tomorrow");
                    }
                    if (actualDay >= 2) {
                        tvRemainingDays.setText("Overdue " + actualDay + " days");
                    }
                }
            }
            String[] dateArr = CommonUtilities.getDateStringFromMs(model.getDuedate()).split("/");
            tvPaymentType.setText(model.getDueType());
            tvMonth.setText(dateArr[1]);
            tvDay.setText(dateArr[0]);
            tvPayeeName.setText(model.getPayeeName());
            tvCategory.setText(model.getDueCategory());
            tvAmount.setText("Rs " + model.getDueAmount() + " ");

            if (model.getDueReminderNotification() == 0) {
                tvReminder.setText("On Due Date");
            } else {
                tvReminder.setText(model.getDueReminderNotification() + " day before due date");
            }
            if (model.getRepeatFlag() == 1) {
                tvRepeat.setText("repetition enable");
                tvRepepatEvery.setText(model.getDueRepeatEvery() + " " + model.getDueRepeatEveryCategory());
                if (model.getRepeatupto() > 0)
                    tvRepeatUpto.setText(CommonUtilities.getDateStringFromMs(model.getRepeatupto()));
            } else {
                tvRepeat.setText("repetition disable");
                tvRepepatEvery.setText("Not Set");
                tvRepeatUpto.setText("Not Set");
            }

            HashMap<String, Integer> imageList = CommonUtilities.getCategoryImageHashMap();
            if (imageList.containsKey(model.getDueCategory().toLowerCase())) {
                imgCategory.setImageResource(imageList.get(model.getDueCategory().toLowerCase()));
            }
        }
        animateFAB();

    }

    private void animateFAB() {
        float y = fabPaid.getY();
        fabPaid.setY(y + 350);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(400);
        ObjectAnimator fPaid = ObjectAnimator.ofFloat(fabPaid, "translationY", fabPaid.getY(), y - 60, y);
        fPaid.setDuration(1000);
        fPaid.start();

        y = fabEdit.getY();
        fabEdit.setY(y + 350);
        ObjectAnimator fEdit = ObjectAnimator.ofFloat(fabEdit, "translationY", fabEdit.getY(), y - 60, y);
        fEdit.setDuration(700);

        y = fabDelete.getY();
        fabDelete.setY(y + 350);
        final ObjectAnimator fDelete = ObjectAnimator.ofFloat(fabDelete, "translationY", fabDelete.getY(), y - 60, y);
        fDelete.setDuration(700);

        if (fPaid.isRunning()) {
            fEdit.setStartDelay(200);
            fEdit.start();
            fEdit.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    fDelete.setStartDelay(200);
                    fDelete.start();
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

        }

        set.start();
    }

    private void initUiElement() {
        topLayout = (LinearLayout) findViewById(R.id.linearLayout);
        tvRepeatUpto = (TextView) findViewById(R.id.tvRepeatUpto);
        tvPaymentType = (TextView) findViewById(R.id.tvPaymentType);
        tvMonth = (TextView) findViewById(R.id.tvMonth);
        tvDay = (TextView) findViewById(R.id.tvDay);
        tvPayeeName = (TextView) findViewById(R.id.tvPayeeName);
        tvCategory = (TextView) findViewById(R.id.tvCategory);
        tvRemainingDays = (TextView) findViewById(R.id.tvRemainingDue);
        tvAmount = (TextView) findViewById(R.id.tvAmount);
        tvReminder = (TextView) findViewById(R.id.tvReminder);
        tvRepepatEvery = (TextView) findViewById(R.id.tvRepeatEvery);
        tvRepeat = (TextView) findViewById(R.id.tvRepeat);
        imgCategory = (ImageView) findViewById(R.id.imgCategory);
        fabDelete = (FloatingActionButton) findViewById(R.id.fabDelete);
        fabEdit = (FloatingActionButton) findViewById(R.id.fabEdit);
        fabPaid = (FloatingActionButton) findViewById(R.id.fabPaid);

        fabPaid.setOnClickListener(this);
        fabEdit.setOnClickListener(this);
        fabDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabDelete:
                DeleteDueDialog dialogDelete = new DeleteDueDialog();
                Bundle bundleDelete = new Bundle();
                bundleDelete.putSerializable("model", model);
                dialogDelete.setArguments(bundleDelete);
                dialogDelete.show(getSupportFragmentManager(), "");

                break;
            case R.id.fabEdit:
                Intent intent = new Intent(this, AddDueActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("model", model);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            case R.id.fabPaid:
                if (model.getRepeatFlag() == 1) {
                    if (model.getRepeatModelArrayList().size() == 1) {
                        PayDialog dialog = new PayDialog();
                        Bundle bundle1 = new Bundle();
                        bundle1.putSerializable("upcoming", model);
                        bundle1.putSerializable("repeat", model.getRepeatModelArrayList().get(0));
                        bundle1.putString("detail", "detail");
                        dialog.setArguments(bundle1);
                        dialog.show(getSupportFragmentManager(), "");
                    }
                    if (model.getRepeatModelArrayList().size() > 1) {
                        RepeatModel repeatModel = null;
                        for (RepeatModel rep : model.getRepeatModelArrayList()) {
                            if (rep.getPaymentStatus() == 0) {
                                repeatModel = rep;
                                break;
                            }
                        }
                        PayDialog dialog = new PayDialog();
                        Bundle bundle1 = new Bundle();
                        bundle1.putSerializable("upcoming", model);
                        bundle1.putSerializable("repeat", repeatModel);
                        bundle1.putString("detail", "detail");
                        dialog.setArguments(bundle1);
                        dialog.show(getSupportFragmentManager(), "");
                    }
                } else {
                    PayDialogSingleItem dialogSingleItem = new PayDialogSingleItem();

                    Bundle bundle3 = new Bundle();
                    bundle3.putSerializable("upcoming", model);
                    bundle3.putString("detail", "detail");
                    dialogSingleItem.setArguments(bundle3);
                    dialogSingleItem.show(getSupportFragmentManager(), "");
                }

                break;
        }
    }
}
