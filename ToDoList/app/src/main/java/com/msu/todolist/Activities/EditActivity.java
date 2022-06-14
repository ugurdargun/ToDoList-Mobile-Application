package com.msu.todolist.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.msu.todolist.Category;
import com.msu.todolist.CustomSpinnerAdapter;
import com.msu.todolist.DatabaseHelper;
import com.msu.todolist.DateConverter;
import com.msu.todolist.IntentService_JSONParsing;
import com.msu.todolist.IntentService_UpdateDB;
import com.msu.todolist.NotificationHelper;
import com.msu.todolist.R;
import com.msu.todolist.Task;

import java.util.ArrayList;
import java.util.Calendar;

public class EditActivity extends AppCompatActivity {

    Intent intent;
    Toast toast;
    DatabaseHelper dbHelper;
    NotificationHelper notificationHelper;

    CustomSpinnerAdapter custSpinAdapter;
    Spinner customSpinner;
    EditText txt_Name, txt_Details, txt_Date, txt_Time;

    private ArrayList<Category> catList;
    private Task taskObj = null;
    private Calendar TaskCalendar;
    private String task_category;
    private String db_date, db_time;
    private String task_name, task_details, task_date, task_time;
    private int task_status;
    private int mYear, mMonth, mDay, mHour, mMin;
    private boolean isDateSet, isTimeSet;
    private int [] dateArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        intent = getIntent();
        Bundle b = intent.getExtras();
        taskObj = b.getParcelable("Task");

        customSpinner = findViewById(R.id.editAct_spin1);
        txt_Name = findViewById(R.id.editAct_txt1);
        txt_Details = findViewById(R.id.editAct_txt2);
        txt_Date = findViewById(R.id.editAct_txt3);
        txt_Time = findViewById(R.id.editAct_txt4);
        task_category = taskObj.getCategory();

        fillFields();

        isDateSet = false;
        isTimeSet = false;

        dateArr = DateConverter.splitTaskDateTime(taskObj.getDate(), taskObj.getTime());

        txt_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dayOfMonth = dateArr[0];
                int month = dateArr[1];
                int year = dateArr[2];

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditActivity.this, R.style.DialogTheme2, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                isDateSet = true;
                                mDay = dayOfMonth;
                                mMonth = month;
                                mYear = year;
                                txt_Date.setText(String.format("%02d", mDay)+ "/" + String.format("%02d", (mMonth+1)) + "/" +  mYear);
                                db_date = mYear + "-" + String.format("%02d", (mMonth+1)) + "-" +  String.format("%02d", mDay);
                            }
                        },
                        year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        txt_Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = dateArr[3];
                int minute = dateArr[4];

                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(EditActivity.this, R.style.DialogTheme2, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        isTimeSet = true;
                        mHour = hourOfDay;
                        mMin = minute;
                        txt_Time.setText(String.format("%02d", (mHour))  + ":" + String.format("%02d", (mMin)));
                        db_time = String.format("%02d", (mHour)) + ":" + String.format("%02d", (mMin)) + ":00";
                    }
                }, hour, minute, true);
                timePicker.show();
            }
        });

        dbHelper = new DatabaseHelper(EditActivity.this);
        notificationHelper = new NotificationHelper(EditActivity.this);

        customSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(catList != null){
                    Category c = catList.get(position);
                    task_category = c.getName();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        startService(new Intent(EditActivity.this, IntentService_JSONParsing.class));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("JSON_PARSE_COMPLETED_ACTION");
        registerReceiver(mIntentReceiver_JSON, intentFilter);

        IntentFilter filterEdit = new IntentFilter();
        filterEdit .addAction( "EDIT_TASK_ACTION");
        registerReceiver(mIntentReceiver_UpdateDB, filterEdit);
    }

    private BroadcastReceiver mIntentReceiver_JSON = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent){
            Bundle bundle = intent.getExtras();
            catList = bundle.getParcelableArrayList("categoryList");
            custSpinAdapter = new CustomSpinnerAdapter(context, catList);
            customSpinner.setAdapter(custSpinAdapter);
            findCategoryPos();
        }};

    private BroadcastReceiver mIntentReceiver_UpdateDB = new BroadcastReceiver() {

        @Override public void onReceive(Context context, Intent intent) {
            boolean update_result = intent.getBooleanExtra("editResult", false);

            if (update_result) {
                updateTaskObj(task_category, task_name, task_details, task_date, task_time, task_status);

                if(isDateSet || isTimeSet ){
                    long id = (long) taskObj.getId();
                    notificationHelper.newNotification(id, TaskCalendar);
                }

                displayToast("Updated");
                unregisterReceiver(mIntentReceiver_UpdateDB);
                finish();
            } else {
                displayToast("Updating Error!");
            }
        }
    };

    public void fillFields() {
        txt_Name.setText(taskObj.getName());
        txt_Details.setText(taskObj.getDetails());
        txt_Date.setText(taskObj.getDate());
        txt_Time.setText(taskObj.getTime());
    }

    public void onClick(View view) {

        if (view.getId() == R.id.editAct_btn1) {

            if(fieldValidation()) {
                TaskCalendar = DateConverter.convertToCalendar(txt_Date.getText().toString(), txt_Time.getText().toString());

                if (timeValidation(TaskCalendar)) {
                    task_name = txt_Name.getText().toString().trim();
                    task_details = txt_Details.getText().toString().trim();
                    task_date = txt_Date.getText().toString();
                    task_time = txt_Time.getText().toString();

                    Intent intent=new Intent(this, IntentService_UpdateDB.class);
                    Bundle b = new Bundle();
                    Task taskObj_DB = new Task(taskObj.getId(), task_category,task_name, task_details, db_date, db_time, taskObj.getFavorite(), task_status );
                    b.putParcelable("taskItem", taskObj_DB);
                    intent.putExtras(b);
                    startService(intent);
                }
                else{
                    displayToast("Set valid date");
                }
            }
            else{
                displayToast("Fill in all fields");
            }

        }
        else if(view.getId() == R.id.editAct_btn2){
            finish();
        }
    }

    private void updateTaskObj(String category, String name, String details, String date, String time, int statusValue){
        taskObj.setCategory(category);
        taskObj.setName(name);
        taskObj.setDetails(details);
        taskObj.setDate(date);
        taskObj.setTime(time);
        taskObj.setStatus(statusValue);
    }

    private boolean fieldValidation(){
        if(txt_Name.getText().toString().isEmpty() || txt_Details.getText().toString().isEmpty() || txt_Date.getText().toString().isEmpty() || txt_Time.getText().toString().isEmpty()){
            return false;
        }
        return true;
    }

    private boolean timeValidation(Calendar task_calendar){
        task_status = taskObj.getStatus();
        if(isDateSet || isTimeSet ){
            task_status = 0; //reset task
            Calendar now = Calendar.getInstance();
            if(task_calendar.equals(now) || task_calendar.before(now) ){
                return false;
            }
        }

        if(!isDateSet){ db_date = DateConverter.convertToDBDate(taskObj.getDate()); }
        if(!isTimeSet){ db_time = DateConverter.convertToDBTime(taskObj.getTime()); }

        return true;
    }

    private void findCategoryPos(){
        for (int i=0; i<catList.size(); i++){
            if(catList.get(i).getName().equalsIgnoreCase(taskObj .getCategory())){
                customSpinner.setSelection(i);
            }
        }
    }

    private void displayToast(String msg){
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(EditActivity.this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

}