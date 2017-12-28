package in.neer24.neer24.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.R;

public class SetRecurringScheduleActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Button btnDatePicker, btnTimePicker, btnCart, increaseByOne, decreaseByOne, displayItemCount ;
    TextView endDateTV, deliveryTimeTV, productPriceTV, productNameTV, priceDetailsTV, totalCostTV;
    ImageView productImage;
    Spinner spinnerInterval;
    private int currentYear, currentMonth, currentDay, currentHour, currentMinute, selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    Date date;
    Can can;
    int numOfCans = 1, recurrenceInterval;
    String rupeeSymbol;
    double total;
    Toast toast;
    Time time;
    View calculationView;
    String monthName[] = {"Jan","Feb","Mar","Apr","May","June","July","Aug","Sep","Oct","Nov","Dec"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_recurring_schedule);

        Bundle bundle = getIntent().getExtras();
        can = bundle.getParcelable("item");

        btnDatePicker=(Button)findViewById(R.id.btn_date);
        btnTimePicker=(Button)findViewById(R.id.btn_time);
        btnCart = (Button) findViewById(R.id.submit);
        endDateTV =(TextView) findViewById(R.id.in_date);
        deliveryTimeTV =(TextView) findViewById(R.id.in_time);
        productImage = (ImageView) findViewById(R.id.productImage);
        productPriceTV = (TextView) findViewById(R.id.productPrice);
        productNameTV = (TextView) findViewById(R.id.productName);
        priceDetailsTV = (TextView) findViewById(R.id.price_details);
        totalCostTV = (TextView) findViewById(R.id.totalCost);
        spinnerInterval = (Spinner) findViewById(R.id.spinner_interval);
        calculationView = findViewById(R.id.calculationView);
        increaseByOne = (Button) findViewById(R.id.btn_qty_increase);
        decreaseByOne = (Button) findViewById(R.id.btn_qty_decrease);
        displayItemCount = (Button) findViewById(R.id.btn_order_or_qty);

        displayItemCount.setText("" + numOfCans);

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        btnCart.setOnClickListener(this);
        increaseByOne.setOnClickListener(this);
        decreaseByOne.setOnClickListener(this);

        endDateTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateOrderValue();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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

        List<String> intervalList = new ArrayList<String>();
        intervalList.add("Everyday");
        intervalList.add("Alternate Days");
        intervalList.add("Every 3rd day");

        for(int i=4;i<7;i++){
            intervalList.add("Every " + i + "th day");
        }

        intervalList.add("Weekly");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,intervalList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInterval.setAdapter(adapter);
        spinnerInterval.setOnItemSelectedListener(this);
        spinnerInterval.setSelection(0);
        recurrenceInterval = 1;
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
            c.add(Calendar.DATE, 2);

            currentDay = c.get(Calendar.DAY_OF_MONTH);
            currentYear = c.get(Calendar.YEAR);
            currentMonth = c.get(Calendar.MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            String showDay, showYear;
                            selectedDay = dayOfMonth;
                            selectedMonth = monthOfYear;
                            selectedYear = year;

                            showDay = Integer.valueOf(selectedDay).toString();
                            showYear = Integer.valueOf(selectedYear).toString();

                            if(selectedDay < 10) {
                                showDay = "0" + showDay;
                            }

                            endDateTV.setText(showDay + " - " + monthName[selectedMonth] + " - " + showYear);

                        }
                    }, currentYear, currentMonth, currentDay);
            datePickerDialog.getDatePicker().setMinDate(c.getTime().getTime());
            datePickerDialog.show();
        }
        if (v == btnTimePicker) {

            // Get Current Time
            final Calendar c = Calendar.getInstance();
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
                            String showHour;
                            String showMinute;
                            boolean isAM;

                            if(selectedHour > 12) {
                                isAM = false;
                                showHour = Integer.valueOf(hourOfDay - 12).toString();
                            }
                            else {
                                isAM = true;
                                showHour = Integer.valueOf(hourOfDay).toString();
                            }

                            showMinute = Integer.valueOf(minute).toString();

                            if(selectedHour != 12) {
                                showHour = "0" + showHour;
                            }

                            if(selectedMinute < 10) {
                                showMinute = "0" + showMinute;
                            }

                            if(isAM) {
                                deliveryTimeTV.setText(showHour + " : " + showMinute + " AM");
                            }
                            else {
                                deliveryTimeTV.setText(showHour + " : " + showMinute + " PM");
                            }
                        }
                    }, currentHour, currentMinute, false);
            timePickerDialog.show();
        }

        if( v == btnCart ){
            //TODO CREATE INTENT FOR GOING TO CART PAGE WITH DETAILS
            if(selectedDay == 0 || (selectedHour == 0 && selectedMinute ==0)){
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(this, "Please select starting date and Time", Toast.LENGTH_SHORT);
                toast.show();
            }
            else {
                date = new Date(selectedYear, selectedMonth, selectedDay);
                Date startDate = new Date(currentYear, currentMonth, currentDay);

                long diff = date.getTime() - startDate.getTime();
                float days = (diff / (1000*60*60*24));


                time = new Time(selectedHour, selectedMinute, 0);
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

        if( selectedDay != 0 ) {

            calculationView.setVisibility(View.VISIBLE);

            Date endDate = new Date(selectedYear, selectedMonth, selectedDay);
            Date startDate = new Date(currentYear, currentMonth, currentDay);

            long diff = endDate.getTime() - startDate.getTime();
            int days = (int)(diff / (1000*60*60*24)) + 2;

            priceDetailsTV.setText(rupeeSymbol + " " + (int)can.getPrice() + " x " + numOfCans + " (can)" + " x " + days + " (days)");
            total = can.getPrice() * numOfCans * days;
            totalCostTV.setText(rupeeSymbol + " " + total);
        }
    }

}

