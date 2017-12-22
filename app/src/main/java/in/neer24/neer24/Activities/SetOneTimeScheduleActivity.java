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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.R;

public class SetOneTimeScheduleActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Button btnDatePicker, btnTimePicker, btnCart ;
    TextView dateTV, timeTV, productPriceTV, productNameTV, priceDetailsTV, totalCostTV;
    ImageView productImage;
    Spinner spinnerNumOfCans;
    private int mYear, mMonth, mDay, mHour, mMinute,  sYear, sMonth, sDay, sHour, sMinute;
    Date date;
    Can can;
    int numOfCans;
    String rupeeSymbol;
    double total;
    Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Bundle bundle = getIntent().getExtras();
        can = bundle.getParcelable("item");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_one_time_schedule);

        btnDatePicker=(Button)findViewById(R.id.btn_date);
        btnTimePicker=(Button)findViewById(R.id.btn_time);
        btnCart = (Button) findViewById(R.id.submit);
        dateTV =(TextView) findViewById(R.id.in_date);
        timeTV =(TextView) findViewById(R.id.in_time);
        productImage = (ImageView) findViewById(R.id.productImage);
        productPriceTV = (TextView) findViewById(R.id.productPrice);
        spinnerNumOfCans = (Spinner) findViewById(R.id.spinner_numOfCans);
        productNameTV = (TextView) findViewById(R.id.productName);
        priceDetailsTV = (TextView) findViewById(R.id.price_details);
        totalCostTV = (TextView) findViewById(R.id.totalCost);

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        btnCart.setOnClickListener(this);

        rupeeSymbol = getResources().getString(R.string.Rs);

        productPriceTV.setText(rupeeSymbol + " " + can.getPrice() + " / can");
        productNameTV.setText(can.getName());

        String canName = can.getName().toLowerCase();
        switch (canName) {
            case "aquasure":
                productImage.setImageResource(R.drawable.aquasure);
                break;
            case "bisleri":
                productImage.setImageResource(R.drawable.bisleri);
                break;
            case "despenser":
                productImage.setImageResource(R.drawable.dispencer);
                break;
            case "kinley":
                productImage.setImageResource(R.drawable.kinley);
                break;
            default:
                productImage.setImageResource(R.drawable.normal);
        }

        setUpSpinner();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void setUpSpinner(){
        List<Integer> numberList = new ArrayList<Integer>();
        for(int i=1;i<101;i++){
            numberList.add(i);
        }
        ArrayAdapter<Integer> numberAdapter = new ArrayAdapter<Integer>(this,
                android.R.layout.simple_spinner_item,numberList);

        numberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNumOfCans.setAdapter(numberAdapter);
        spinnerNumOfCans.setOnItemSelectedListener(this);
        spinnerNumOfCans.setSelection(0);
        numOfCans = 1;
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        numOfCans = position+1;
        updateOrderValue();
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
                            sMonth = monthOfYear + 1;
                            sYear = year;

                            dateTV.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

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

                            timeTV.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }

        if( v == btnCart ){
            //TODO CREATE INTENT FOR GOING TO CART PAGE WITH DETAILS
            if(sDay == 0 || (sHour == 0 && sMinute==0)){

                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(this, "Please select starting date and Time", Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                date = new Date(sYear,sMonth,sDay);
                date.setHours(sHour);
                date.setMinutes(sMinute);
                Intent intent = new Intent();
                intent.putExtra("dateTime",date);
            }

        }
    }

    void updateOrderValue(){

        priceDetailsTV.setText(rupeeSymbol + " " + (int)can.getPrice() + " * " + numOfCans + " =");
        total = can.getPrice() * numOfCans;
        totalCostTV.setText(rupeeSymbol + " " + total);
    }

}
