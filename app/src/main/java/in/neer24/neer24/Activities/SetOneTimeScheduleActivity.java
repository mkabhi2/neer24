package in.neer24.neer24.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.R;

public class SetOneTimeScheduleActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Button btnDatePicker, btnTimePicker, btnCart, increaseByOne, decreaseByOne, displayItemCount ;
    TextView dateTV, timeTV, productPriceTV, productNameTV, priceDetailsTV, totalCostTV;
    ImageView productImage;
    private int currentYear, currentMonth, currentDay, currentHour, currentMinute, selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    Date date;
    Can can;
    int numOfCans = 1;
    String rupeeSymbol;
    double total;
    Toast checkoutToast, timeTVToast;
    boolean isDateSelected = false, isTimeSelected = false;


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
        productNameTV = (TextView) findViewById(R.id.productName);
        priceDetailsTV = (TextView) findViewById(R.id.price_details);
        totalCostTV = (TextView) findViewById(R.id.totalCost);
        increaseByOne = (Button) findViewById(R.id.btn_qty_increase);
        decreaseByOne = (Button) findViewById(R.id.btn_qty_decrease);
        displayItemCount = (Button) findViewById(R.id.btn_order_or_qty);

        displayItemCount.setText("" + numOfCans);

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        btnCart.setOnClickListener(this);
        increaseByOne.setOnClickListener(this);
        decreaseByOne.setOnClickListener(this);

        rupeeSymbol = getResources().getString(R.string.Rs);

        productPriceTV.setText(rupeeSymbol + " " + can.getPrice() + " / can");
        productNameTV.setText(can.getName());

        updateOrderValue();

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

            currentDay = c.get(Calendar.DAY_OF_MONTH);
            currentYear = c.get(Calendar.YEAR);
            currentMonth = c.get(Calendar.MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            timeTV.setText("No Time Selected");
                            isTimeSelected = false;
                            isDateSelected = true;

                            selectedDay = dayOfMonth;
                            selectedMonth = monthOfYear;
                            selectedYear = year;

                            dateTV.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        }
                    }, currentYear, currentMonth, currentDay);
            datePickerDialog.getDatePicker().setMinDate(c.getTime().getTime());
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            if(!isDateSelected) {
                if (timeTVToast != null) {
                    timeTVToast.cancel();
                }
                timeTVToast = Toast.makeText(this, "Please select delivery date first", Toast.LENGTH_SHORT);
                timeTVToast.show();
                return;
            }

            // Get Current Time
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.HOUR,1);
            currentHour = c.get(Calendar.HOUR_OF_DAY);
            currentMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            selectedHour = hourOfDay;
                            selectedMinute = minute;

                            if (selectedYear == currentYear
                                    && selectedMonth == currentMonth
                                    && selectedDay == currentDay
                                    && (selectedHour < currentHour || (selectedHour <= currentHour))) {

                                timeTV.setText("No Time Selected");
                                isTimeSelected = false;
                                Toast.makeText(SetOneTimeScheduleActivity.this, "Set date and time at least 1 hour from now", Toast.LENGTH_LONG).show();
                            }
                            else {
                                isTimeSelected = true;
                                timeTV.setText(hourOfDay + ":" + minute);
                            }
                        }
                    }, currentHour, currentMinute, false);
            timePickerDialog.show();
        }

        if( v == btnCart ){
            //TODO CREATE INTENT FOR GOING TO CART PAGE WITH DETAILS
            if(!isDateSelected || !isTimeSelected){

                if (checkoutToast != null) {
                    checkoutToast.cancel();
                }
                checkoutToast = Toast.makeText(this, "Please select schedule date and Time", Toast.LENGTH_SHORT);
                checkoutToast.show();
            }
            else {
                date = new Date(selectedYear, selectedMonth, selectedDay);
                date.setHours(selectedHour);
                date.setMinutes(selectedMinute);
                Intent intent = new Intent();
                intent.putExtra("dateTime",date);
            }

        }

        if( v == increaseByOne ) {

            numOfCans++;
            displayItemCount.setText("" + numOfCans);
            updateOrderValue();

        }

        if( v == decreaseByOne ) {

            if( numOfCans != 1 ) {

                numOfCans--;
                displayItemCount.setText("" + numOfCans);
                updateOrderValue();
            }
        }
    }

    void updateOrderValue(){

        String temp  = " (can)";
        if(numOfCans > 1) {
            temp = " (cans)";
        }
        priceDetailsTV.setText(rupeeSymbol + " " + (int)can.getPrice() + " x " + numOfCans + temp);
        total = can.getPrice() * numOfCans;
        totalCostTV.setText(rupeeSymbol + " " + total);
    }

}
