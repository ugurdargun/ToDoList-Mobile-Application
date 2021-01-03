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
import com.msu.todolist.IntentService_JSONParsing;
import com.msu.todolist.NotificationHelper;
import com.msu.todolist.R;
import com.msu.todolist.TaskDB;

import java.util.ArrayList;
import java.util.Calendar;

public class AddActivity extends AppCompatActivity {

    Toast toast;
    DatabaseHelper dbHelper;
    NotificationHelper notificationHelper;

    Spinner customSpinner;
    EditText txt_Name, txt_Details, txt_Date, txt_Time;

    private ArrayList<Category> catList;
    private String task_category;
    private int mYear, mMonth, mDay, mHour, mMin;
    private String db_date, db_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        customSpinner = findViewById(R.id.addAct_spin1);
        txt_Name = findViewById(R.id.addAct_txt1);
        txt_Details = findViewById(R.id.addAct_txt2);
        txt_Date = findViewById(R.id.addAct_txt3);
        txt_Time = findViewById(R.id.addAct_txt4);
        task_category = "General";

        txt_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH) ;

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddActivity.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mMonth = month;
                        mYear = year;
                        mDay = dayOfMonth;
                        db_date = mYear + "-" + String.format("%02d", (mMonth + 1)) + "-" + String.format("%02d", mDay);
                        txt_Date.setText(String.format("%02d", mDay) + "/" + String.format("%02d", (mMonth + 1)) + "/" + mYear);
                    }
                },
                        year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        txt_Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar ct = Calendar.getInstance();
                int hour = ct.get(Calendar.HOUR_OF_DAY);
                int minute = ct.get(Calendar.MINUTE);

                TimePickerDialog timePicker;
                timePicker = new TimePickerDialog(AddActivity.this, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMin = minute;
                        db_time = String.format("%02d", (mHour)) + ":" + String.format("%02d", (mMin)) + ":00";
                        txt_Time.setText(String.format("%02d", (mHour))  + ":" + String.format("%02d", (mMin)));
                    }
                }, hour, minute, true);
                timePicker.show();

            }
        });

        dbHelper = new DatabaseHelper(AddActivity.this);
        notificationHelper = new NotificationHelper(AddActivity.this);

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

        startService(new Intent(AddActivity.this, IntentService_JSONParsing.class));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("JSON_PARSE_COMPLETED_ACTION");
        registerReceiver(mIntentReceiver_JSON, intentFilter);
    }

    private BroadcastReceiver mIntentReceiver_JSON = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent){
            Bundle bundle = intent.getExtras();
            catList = bundle.getParcelableArrayList("categoryList");
            CustomSpinnerAdapter custSpinAdapter = new CustomSpinnerAdapter(context, catList);
            customSpinner.setAdapter(custSpinAdapter);
        }};

    public void onClick(View view) {

        if (view.getId() == R.id.addAct_btn1) {
            if(fieldValidation()) {
                Calendar task_calendar = Calendar.getInstance();
                task_calendar.set(mYear, mMonth, mDay, mHour, mMin, 00);

                if (timeValidation(task_calendar)) {
                    String task_name = txt_Name.getText().toString().trim();
                    String task_details = txt_Details.getText().toString().trim();

                    long id = TaskDB.insert(dbHelper, task_category, task_name, task_details, db_date, db_time);
                    notificationHelper.newNotification(id, task_calendar);

                    if(id > 0) {
                        displayToast("Added");
                        clearFields();
                    } else {
                        displayToast("Adding Error!");
                    }
                }
                else{
                    displayToast("Set valid date");
                }
            }
            else{
                displayToast("Fill in all fields");
            }
        }
        else if(view.getId() == R.id.addAct_btn2){
            finish();
        }
    }

    private void clearFields(){
        txt_Name.setText("");
        txt_Details.setText("");
        txt_Date.setText("");
        txt_Time.setText("");
    }

    private boolean fieldValidation(){
        if(txt_Name.getText().toString().isEmpty() || txt_Details.getText().toString().isEmpty() || txt_Date.getText().toString().isEmpty() || txt_Time.getText().toString().isEmpty()){
            return false;
        }
        return true;
    }

    private boolean timeValidation(Calendar calendar){
        Calendar now = Calendar.getInstance();
        if(calendar.equals(now) || calendar.before(now) ){
            return false;
        }
        return true;
    }

    private void displayToast(String msg){
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(AddActivity.this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

}