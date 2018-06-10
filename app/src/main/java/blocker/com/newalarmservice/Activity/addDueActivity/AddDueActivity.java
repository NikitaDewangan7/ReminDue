package blocker.com.newalarmservice.Activity.addDueActivity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import blocker.com.newalarmservice.R;
import blocker.com.newalarmservice.database.DueDetailContentProvider;
import blocker.com.newalarmservice.database.DueDetailTable;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatContentProvider;
import blocker.com.newalarmservice.database.duerepeattable.DueRepeatTable;
import blocker.com.newalarmservice.dialogs.MyDialog;
import blocker.com.newalarmservice.interfaces.ICategoryDeleteNotify;
import blocker.com.newalarmservice.models.Due;
import blocker.com.newalarmservice.models.DueUpcomingModel;
import blocker.com.newalarmservice.sharedpreferences.MSharedPreferance;
import blocker.com.newalarmservice.utilities.CommonUtilities;
import blocker.com.newalarmservice.utilities.MyApplication;

public class AddDueActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, ICategoryDeleteNotify {
    private Spinner spinnerCategory, spinnerType, spinnerReminderNotification, spinnerRepeatEvery;
    private RadioButton rb_repeat;
    private RadioGroup radioGroup;
    private LinearLayout layoutRepeat;
    private Toolbar toolbar;
    private AddDueHelper addDueHelper;
    private Button btnAddDue, btnCancelDue;
    private TextView tvSetDueDate, tvRepeatUpto;
    private LinearLayout layoutAddCategory;
    private ImageView imgCategory;
    private EditText editAddCategory, editPayee, editAmount, editRepeatEvery;
    private ImageView imgAddCategory;
    private CategorySpinnerAdapter adapter;
    private String[] arr;
    private boolean flag = true;
    private Due due;
    private HashMap<String, Integer> categoryImageList;
    private DueUpcomingModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_due);
        MyApplication.getApplicationInstance().setCurrentActivity(this);
        addDueHelper = new AddDueHelper(this);
        initUiElements();
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        categoryImageList = CommonUtilities.getCategoryImageHashMap();
        MSharedPreferance sharedPreferance = MSharedPreferance.getSharedPreferance();
        arr = sharedPreferance.getSPCategoryList();

        adapter = new CategorySpinnerAdapter(this, R.layout.spinner_category_item, arr);
        adapter.setCategoryDeleteNotify(this);
        spinnerCategory.setAdapter(adapter);

        ArrayList<String> paymentTypeList = addDueHelper.getPaymentTypeList();
        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, paymentTypeList);
        spinnerType.setAdapter(paymentAdapter);

        ArrayList<String> reminderList = addDueHelper.getReminderList();
        ArrayAdapter<String> reminderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, reminderList);
        spinnerReminderNotification.setAdapter(reminderAdapter);

        ArrayList<String> repeatEveryList = addDueHelper.getRepeatEveryList();
        ArrayAdapter<String> repeatEveryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, repeatEveryList);
        spinnerRepeatEvery.setAdapter(repeatEveryAdapter);

        Bundle bundle = getIntent().getExtras();
        if (getSupportActionBar() != null) {
            if (bundle != null)
                getSupportActionBar().setTitle("Edit Due");
            else
                getSupportActionBar().setTitle("Add Due");
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }
        if (bundle != null) {
            model = (DueUpcomingModel) bundle.getSerializable("model");
            editPayee.setText(model.getPayeeName());
            editAmount.setText(model.getDueAmount() + "");
            tvSetDueDate.setText(CommonUtilities.getDateStringFromMs(model.getDuedate()));

            for (int i = 0; i < arr.length; i++) {
                if (arr[i].equalsIgnoreCase(model.getDueCategory())) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < paymentTypeList.size(); i++) {
                if (paymentTypeList.get(i).equalsIgnoreCase(model.getDueType())) {
                    spinnerType.setSelection(i);
                    break;
                }
            }
            spinnerReminderNotification.setSelection(model.getDueReminderNotification());
            if (model.getRepeatFlag() == 1) {
                rb_repeat.setChecked(true);
                tvRepeatUpto.setText(CommonUtilities.getDateStringFromMs(model.getRepeatupto()));
                editRepeatEvery.setText("" + model.getDueRepeatEvery());
                for (int i = 0; i < repeatEveryList.size(); i++) {
                    if (repeatEveryList.get(i).equalsIgnoreCase(model.getDueRepeatEveryCategory())) {
                        spinnerRepeatEvery.setSelection(i);
                        break;
                    }
                }

            }


        } else {
            tvSetDueDate.setText(addDueHelper.convertMstoStr(System.currentTimeMillis()));
        }


        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String imgName = arr[i].toLowerCase();
                Log.e("category name", imgName);
                if (arr[i].equalsIgnoreCase("Other")) {
                    layoutAddCategory.setVisibility(View.VISIBLE);
                    setSelectedImage(arr[i]);
                } else {
                    HashMap<String, Integer> imageList = CommonUtilities.getCategoryImageHashMap();

                    if (imageList.containsKey(imgName)) {
                        imgCategory.setImageResource(imageList.get(imgName));
                    } else {
                        imgCategory.setImageResource(R.drawable.other);
                    }
                    //setSelectedImage(arr[i]);
                    layoutAddCategory.setVisibility(View.GONE);
                    editAddCategory.setText("");
                    editAddCategory.setHint("Enter Category Name");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    private void initUiElements() {

        imgCategory = (ImageView) findViewById(R.id.img_category);
        spinnerCategory = (Spinner) findViewById(R.id.spinner_category);
        spinnerType = (Spinner) findViewById(R.id.spinner_type);
        spinnerReminderNotification = (Spinner) findViewById(R.id.spinner_reminderNotification);
        rb_repeat = (RadioButton) findViewById(R.id.radio_repeat);
        layoutRepeat = (LinearLayout) findViewById(R.id.repeatLayout);
        spinnerRepeatEvery = (Spinner) findViewById(R.id.spinnerRepeatsEvery);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnAddDue = (Button) findViewById(R.id.btnAddDue);
        btnCancelDue = (Button) findViewById(R.id.btnDueCancel);
        btnCancelDue.setOnClickListener(this);
        btnAddDue.setOnClickListener(this);

        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(this);
        tvSetDueDate = (TextView) findViewById(R.id.tvDueDate);
        tvSetDueDate.setOnClickListener(this);
        tvRepeatUpto = (TextView) findViewById(R.id.tv_upto_date);
        tvRepeatUpto.setOnClickListener(this);
        layoutAddCategory = (LinearLayout) findViewById(R.id.layoutAddCategory);
        editAddCategory = (EditText) findViewById(R.id.editAddCategoryItem);
        imgAddCategory = (ImageView) findViewById(R.id.img_addCategory_item);
        imgAddCategory.setOnClickListener(this);

        editPayee = (EditText) findViewById(R.id.editPayeeName);
        editAmount = (EditText) findViewById(R.id.edit_amount);
        editRepeatEvery = (EditText) findViewById(R.id.edit_repeats_every);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddDue:
                if (getDueObj()) {
                    if (model != null) {
                        Uri uri = ContentUris.withAppendedId(DueDetailContentProvider.CONTENT_URI, model.getId());
                        getContentResolver().update(uri, getEditDbContentValues(), null, null);
                        if (model.getRepeatFlag() == 1) {
                            Uri repeatUri = ContentUris.withAppendedId(DueRepeatContentProvider.CONTENT_URI, model.getId());
                            if (rb_repeat.isChecked()) {
                                ContentValues values = new ContentValues();
                                values.put(DueRepeatTable.COLUMN_PAID, calRepeatedPaymentStatus(addDueHelper.calRepeatedDueTimes(due)));
                                values.put(DueRepeatTable.COLUMN_REPEATARR, addDueHelper.calRepeatedDueTimes(due));
                                values.put(DueRepeatTable.COLUMN_NOTIFICATION_FLAG, calRepeatedNotificationStatus(addDueHelper.calRepeatedDueTimes(due)));
                                getContentResolver().update(repeatUri, values, null, null);
                            } else {
                                getContentResolver().delete(repeatUri, null, null);
                            }
                        } else {
                            if (rb_repeat.isChecked()) {
                                ContentValues values = new ContentValues();
                                values.put(DueRepeatTable.COLUMN_PAID, calRepeatedPaymentStatus(addDueHelper.calRepeatedDueTimes(due)));
                                values.put(DueRepeatTable.COLUMN_REPEATARR, addDueHelper.calRepeatedDueTimes(due));
                                values.put(DueRepeatTable.COLUMN_NOTIFICATION_FLAG, calRepeatedNotificationStatus(addDueHelper.calRepeatedDueTimes(due)));
                                values.put(DueRepeatTable.COLUMN_ID, model.getId());
                                getContentResolver().insert(DueRepeatContentProvider.CONTENT_URI, values);
                            }
                        }
                        due.setId(model.getId());
                        DueUpcomingModel dueUpcomingModel = addDueHelper.getDueUPcomingMOdel(due);
                        try {
                            addDueHelper.setAlarm(dueUpcomingModel);
                        } catch (ParseException e) {
                            Log.e("exception in ", "Set Alarm ");
                            e.printStackTrace();
                        }
                        MyDialog dialog = new MyDialog();
                        Bundle bundle = new Bundle();
                        bundle.putInt("status", 1);
                        dialog.setArguments(bundle);
                        dialog.show(getSupportFragmentManager(), "");
                    } else {
                        if (due.getRepeatFlag() == 1) {
                            if (checkRepeatIsPossibleOrNot()) {
                                Uri uri1 = getContentResolver().insert(DueDetailContentProvider.CONTENT_URI, getDbContentValues());
                                Cursor cursor = getContentResolver().query(uri1, null, null, null, null);
                                if (cursor.getCount() != 0) {
                                    cursor.moveToFirst();
                                    int id = cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_ID));
                                    cursor.close();
                                    due.setId(id);
                                    DueUpcomingModel dueUpcomingModel = addDueHelper.getDueUPcomingMOdel(due);
                                    ContentValues contentValues = getRepeatTableContentValues(id);
                                    getContentResolver().insert(DueRepeatContentProvider.CONTENT_URI, contentValues);
                                    try {
                                        addDueHelper.setAlarm(dueUpcomingModel);
                                    } catch (ParseException e) {
                                        Log.e("error in due pojo", e.getMessage());
                                    }
                                    MyDialog dialog = new MyDialog();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("status", 1);
                                    dialog.setArguments(bundle);
                                    dialog.show(getSupportFragmentManager(), "");
                                }
                            } else {
                                View coordinator = findViewById(R.id.coordinator);
                                Snackbar.make(coordinator, "Change Repeat Upto date ", Snackbar.LENGTH_LONG).show();
                            }

                        } else {
                            Uri uri = getContentResolver().insert(DueDetailContentProvider.CONTENT_URI, getDbContentValues());
                            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                            if (cursor.getCount() != 0) {
                                cursor.moveToFirst();
                                int id = cursor.getInt(cursor.getColumnIndex(DueDetailTable.COLUMN_ID));
                                cursor.close();
                                due.setId(id);
                                DueUpcomingModel dueUpcomingModel = addDueHelper.getDueUPcomingMOdel(due);
                                try {
                                    addDueHelper.setAlarm(dueUpcomingModel);
                                } catch (ParseException e) {
                                    Log.e("error in due pojo", e.getMessage());
                                }
                            }
                            MyDialog dialog = new MyDialog();
                            Bundle bundle = new Bundle();
                            bundle.putInt("status", 1);
                            dialog.setArguments(bundle);
                            dialog.show(getSupportFragmentManager(), "");
                        }
                    }
                }
                break;
            case R.id.btnDueCancel:
                finish();
                break;
            case R.id.tvDueDate:
                flag = true;
                addDueHelper.showDatePicker();
                break;
            case R.id.tv_upto_date:
                flag = false;
                addDueHelper.showDatePicker();
                break;
            case R.id.img_addCategory_item:
                View view = findViewById(R.id.coordinator);
                if (editAddCategory.getText().toString().length() > 0) {
                    ArrayList list = new ArrayList<String>(Arrays.asList(arr));
                    boolean status = checkDuplicates(editAddCategory.getText().toString(), list);
                    if (status) {
                        Snackbar.make(view, "Category Item Already Added", Snackbar.LENGTH_SHORT).show();
                    } else {
                        MSharedPreferance.getSharedPreferance().addCategory(editAddCategory.getText().toString());
                        arr = MSharedPreferance.getSharedPreferance().getSPCategoryList();
                        adapter = new CategorySpinnerAdapter(this, R.layout.spinner_category_item, arr);
                        spinnerCategory.setAdapter(adapter);
                        layoutAddCategory.setVisibility(View.GONE);
                        Snackbar.make(view, "Category Item Added", Snackbar.LENGTH_SHORT).show();

                    }
                } else {
                    editAddCategory.setError("please enter some field");
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.radio_repeat:
                layoutRepeat.setVisibility(View.VISIBLE);
                tvRepeatUpto.setText(addDueHelper.convertMstoStr(System.currentTimeMillis()));
                break;
            case R.id.radio_dontRepeat:
                layoutRepeat.setVisibility(View.GONE);
                break;
        }
    }

    public void setDueDate(String selectedDate) {
        if (flag) {
            tvSetDueDate.setText(selectedDate);
            dueDateMs = addDueHelper.getDueDateTime();

        } else {
            tvRepeatUpto.setText(selectedDate);
            repeatUptoms = addDueHelper.getDueDateTime();
        }
    }

    private Long dueDateMs, repeatUptoms;

    private ContentValues getEditDbContentValues() {
        long duedate = CommonUtilities.convertMonthStrToMs(tvSetDueDate.getText().toString());
        long uptoDueDate = 0;
        if (rb_repeat.isChecked()) {
            uptoDueDate = CommonUtilities.convertMonthStrToMs(tvRepeatUpto.getText().toString());
        }
        ContentValues values = new ContentValues();
        values.put(DueDetailTable.COLUMN_AMOUNT, due.getAmount());
        values.put(DueDetailTable.COLUMN_CATEGORY, due.getCategory());
        values.put(DueDetailTable.COLUMN_DUEDATE, duedate);
        values.put(DueDetailTable.COLUMN_PAYEENAME, due.getPayee());
        values.put(DueDetailTable.COLUMN_PAYMENT_STATUS, 0);
        values.put(DueDetailTable.COLUMN_REMINDER_NOTIFICATION, due.getReminderNotification());
        values.put(DueDetailTable.COLUMN_REPEAT, due.getRepeatFlag());
        values.put(DueDetailTable.COLUMN_REPEAT_UPTo, uptoDueDate);
        values.put(DueDetailTable.COLUMN_REPEATEVERY_category, due.getRepeatEveryCatgory());
        values.put(DueDetailTable.COLUMN_Type, due.getPayentType());
        values.put(DueDetailTable.COLUMN_REPEATEVERY_input, due.getRepeatEvery());
        values.put(DueDetailTable.COLUMN_NOTIFICATION_FLAG, 0);
        return values;
    }

    private ContentValues getDbContentValues() {
        ContentValues values = new ContentValues();
        values.put(DueDetailTable.COLUMN_AMOUNT, due.getAmount());
        values.put(DueDetailTable.COLUMN_CATEGORY, due.getCategory());
        values.put(DueDetailTable.COLUMN_DUEDATE, due.getDueDate());
        values.put(DueDetailTable.COLUMN_PAYEENAME, due.getPayee());
        values.put(DueDetailTable.COLUMN_PAYMENT_STATUS, 0);
        values.put(DueDetailTable.COLUMN_REMINDER_NOTIFICATION, due.getReminderNotification());
        values.put(DueDetailTable.COLUMN_REPEAT, due.getRepeatFlag());
        values.put(DueDetailTable.COLUMN_REPEAT_UPTo, due.getRepeatUpto());
        values.put(DueDetailTable.COLUMN_REPEATEVERY_category, due.getRepeatEveryCatgory());
        values.put(DueDetailTable.COLUMN_Type, due.getPayentType());
        values.put(DueDetailTable.COLUMN_REPEATEVERY_input, due.getRepeatEvery());
        values.put(DueDetailTable.COLUMN_NOTIFICATION_FLAG, 0);
        return values;
    }

    private boolean getDueObj() {
        due = new Due();
        String payee = editPayee.getText().toString();
        if (payee.length() > 0)
            due.setPayee(payee);
        else {
            editPayee.setError("payee can't be blank");
            return false;
        }

        try {

            int amount = Integer.parseInt(editAmount.getText().toString());
            if (amount > 0)
                due.setAmount(amount);
            else {
                editAmount.setError("enter amount");
                return false;
            }
        } catch (Exception e) {
            editAmount.setError("enter some amount");
            return false;
        }
        if (dueDateMs != null)
            due.setDueDate(dueDateMs);
        else {
            if (model != null) {
                due.setDueDate(model.getDuedate());
            } else {
                due.setDueDate(System.currentTimeMillis());
            }
        }


        String category = spinnerCategory.getSelectedItem().toString();
        due.setCategory(category);

        String paymentType = spinnerType.getSelectedItem().toString();
        due.setPayentType(paymentType);

        int reminderNotification = spinnerReminderNotification.getSelectedItemPosition();
        due.setReminderNotification(reminderNotification);

        if (rb_repeat.isChecked()) {
            due.setRepeatFlag(1);
            try {
                int repeatEvery = Integer.parseInt(editRepeatEvery.getText().toString());
                due.setRepeatEvery(repeatEvery);
            } catch (Exception e) {
                due.setRepeatEvery(1);
            }
            due.setRepeatEveryCatgory(spinnerRepeatEvery.getSelectedItem().toString());
            if (repeatUptoms != null)
                due.setRepeatUpto(repeatUptoms);
            else {
                long date = CommonUtilities.convertMonthStrToMs(tvRepeatUpto.getText().toString());
                due.setRepeatUpto(date);
            }


        } else {
            due.setRepeatFlag(0);
            due.setRepeatEvery(0);
            due.setRepeatEveryCatgory("");
            due.setRepeatUpto(0);
        }
        return true;
    }

    private boolean checkDuplicates(String data, ArrayList<String> array) {
        for (String str : array) {
            if (str.equalsIgnoreCase(data)) {
                return true;
            }
        }
        return false;

    }

    private boolean checkRepeatIsPossibleOrNot() {
        long duems = due.getDueDate();
        long uptodue = due.getRepeatUpto();
        int repeatEvery = due.getRepeatEvery();
        int dueday, dueMonth, dueYear;
        String[] dueDateArr = CommonUtilities.convertMstoStr(duems);
        dueday = Integer.parseInt(dueDateArr[0]);
        dueMonth = Integer.parseInt(dueDateArr[1]);
        dueYear = Integer.parseInt(dueDateArr[2]);
        String category = due.getRepeatEveryCatgory().toLowerCase();
        switch (category) {
            case "week":
                duems = duems + (7 * 24 * 60 * 60 * 1000);
                if (duems > uptodue) {
                    return false;
                } else {
                    return true;
                }

            case "month":
                Calendar calendar = Calendar.getInstance();
                if (dueMonth == 12) {
                    dueMonth = 1;
                    dueYear = dueYear + 1;
                    calendar.set(dueYear, dueMonth, dueday, 0, 0, 0);
                    duems = calendar.getTimeInMillis();
                } else {
                    dueMonth = dueMonth + 1;
                    calendar.set(dueYear, dueMonth, dueday, 0, 0, 0);
                    duems = calendar.getTimeInMillis();
                }
                if (duems > uptodue)
                    return false;
                else
                    return true;
            case "year":
                Calendar cal = Calendar.getInstance();
                dueYear = dueYear + repeatEvery;
                cal.set(dueYear, dueMonth, dueday, 0, 0, 0);
                duems = cal.getTimeInMillis();
                if (duems > uptodue)
                    return false;
                else
                    return true;

        }
        return false;
    }


    private String calRepeatedPaymentStatus(String paymentStatus) {
        StringBuffer payment = new StringBuffer();
        String[] arr = paymentStatus.split(",");
        for (int i = 0; i < arr.length; i++) {
            payment.append("0,");
        }
        return payment.toString();
    }

    private String calRepeatedNotificationStatus(String repDueTimes) {
        StringBuffer notificationFlag = new StringBuffer();
        String[] arr = repDueTimes.split(",");
        for (int i = 0; i < arr.length; i++) {
            notificationFlag.append("0,");
        }
        return notificationFlag.toString();
    }

    private ContentValues getRepeatTableContentValues(int id) {
        ContentValues values = new ContentValues();
        values.put(DueRepeatTable.COLUMN_ID, id);
        values.put(DueRepeatTable.COLUMN_PAID, calRepeatedPaymentStatus(addDueHelper.calRepeatedDueTimes(due)));
        values.put(DueRepeatTable.COLUMN_REPEATARR, addDueHelper.calRepeatedDueTimes(due));
        values.put(DueRepeatTable.COLUMN_NOTIFICATION_FLAG, calRepeatedNotificationStatus(addDueHelper.calRepeatedDueTimes(due)));
        return values;
    }

    private void setSelectedImage(String categoryName) {
        if (categoryImageList.containsKey(categoryName.toLowerCase())) {
            imgCategory.setImageResource(categoryImageList.get(categoryName.toLowerCase()));
        } else {
            imgCategory.setImageResource(R.drawable.other);
        }
    }

    @Override
    public void categoryItemDelete() {
        arr = MSharedPreferance.getSharedPreferance().getSPCategoryList();
        adapter = new CategorySpinnerAdapter(this, R.layout.spinner_category_item, arr);
        adapter.setCategoryDeleteNotify(this);
        spinnerCategory.setAdapter(adapter);
    }
}
