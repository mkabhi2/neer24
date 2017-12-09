package in.neer24.neer24.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import in.neer24.neer24.R;

public class SetRecurringScheduleActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    Button btnDatePicker, btnTimePicker, btnCart;
    Spinner spinnerInterval;
    EditText txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute,  sYear, sMonth, sDay, sHour, sMinute;
    int interval;
    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_recurring_schedule);

        btnDatePicker=(Button)findViewById(R.id.btn_date);
        btnTimePicker=(Button)findViewById(R.id.btn_time);
        btnCart = (Button) findViewById(R.id.submit);
        txtDate=(EditText)findViewById(R.id.in_date);
        txtTime=(EditText)findViewById(R.id.in_time);
        spinnerInterval = (Spinner) findViewById(R.id.spinner_interval);

        setUpSpinner();

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        btnCart.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setUpSpinner(){
        List<Integer> intervalList = new ArrayList<Integer>();
        for(int i=1;i<31;i++){
            intervalList.add(i);
        }
        ArrayAdapter<Integer>adapter = new ArrayAdapter<Integer>(this,
                android.R.layout.simple_spinner_item,intervalList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInterval.setAdapter(adapter);
        spinnerInterval.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        interval = position+1;
        System.out.println(interval);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View v) {

        if (v == btnDatePicker) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 1);

            mDay = c.get(Calendar.DAY_OF_MONTH);
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            sDay = dayOfMonth;
                            sMonth = monthOfYear;
                            sYear = year;

                            txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(c.getTime().getTime());
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            sHour = hourOfDay;
                            sMinute = minute;

                            txtTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }

        if( v == btnCart ){
            //TODO CREATE INTENT FOR GOING TO CART PAGE WITH DETAILS
            if(sDay == 0 || (sHour == 0 && sMinute == 0)){
                Toast.makeText(this,
                        "Please select starting date and Time",
                        Toast.LENGTH_SHORT).show();
            }
            else {
                date = new Date(sYear,sMonth,sDay);
                date.setHours(sHour);
                date.setMinutes(sMinute);
                Intent intent = new Intent();
                intent.putExtra("dateTime",date);
                intent.putExtra("interval", interval);
            }
        }
    }

}
