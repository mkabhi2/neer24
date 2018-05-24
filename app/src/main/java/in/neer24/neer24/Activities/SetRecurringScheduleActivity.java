package in.neer24.neer24.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.LocalTime;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.neer24.neer24.Adapters.SetAddressSelectorDialogAdapter;
import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.CustomerAddress;
import in.neer24.neer24.CustomObjects.OrderDetails;
import in.neer24.neer24.CustomObjects.OrderTable;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;

public class SetRecurringScheduleActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Button btnDatePicker, btnTimePicker, increaseByOne, decreaseByOne, displayItemCount, selectAddressBtn, addAddressBtn;
    TextView endDateTV, deliveryTimeTV, productPriceTV, productNameTV, priceDetailsTV, totalCostTV, addressTitleTV, addressDescTV,
            addressChangeTV, itemTotalTV, billItemTotalTV, billDiscountTV, couponTextTV, grandTotalTV, switchTV, deliveryChargesTV;
    ImageView productImage, addressIconIV;
    public static Button proceedToPayButton;
    public static View addressView, billView;
    Spinner spinnerInterval;
    LinearLayout billOffersLL;
    RelativeLayout couponLayout;
    SwitchCompat newCanSwitch;
    int deliveryCharge=0, freeCans = 0;
    double toPay = 0, discount = 0;
    int numberOfDeliveries;

    boolean isDateSelected = false, isTimeSelected = false;

    Date date;
    Can can;
    Toast toast;
    Time time;

    static int numOfCans = 1, recurrenceInterval, selectedAddressID, currentYear, currentMonth, currentDay, currentHour, currentMinute,
            selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute, selectedAddressIndex;
    String rupeeSymbol;
    double total;

    String monthName[] = {"Jan","Feb","Mar","Apr","May","June","July","Aug","Sep","Oct","Nov","Dec"};
    ArrayList<CustomerAddress> addressesInCurrentLocation;

    SharedPreferenceUtility sharedPreferenceUtility;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_recurring_schedule);

        Bundle bundle = getIntent().getExtras();
        can = bundle.getParcelable("item");
        rupeeSymbol = getResources().getString(R.string.Rs);

        sharedPreferenceUtility=new SharedPreferenceUtility(this);

        instantiateViewObjects();
        setUpViewObjects();

        updateOrderValue();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void instantiateViewObjects(){
        btnDatePicker=(Button)findViewById(R.id.btn_date);
        btnTimePicker=(Button)findViewById(R.id.btn_time);
        endDateTV =(TextView) findViewById(R.id.in_date);
        deliveryTimeTV =(TextView) findViewById(R.id.in_time);
        productImage = (ImageView) findViewById(R.id.productImage);
        productPriceTV = (TextView) findViewById(R.id.productPrice);
        productNameTV = (TextView) findViewById(R.id.productName);
        priceDetailsTV = (TextView) findViewById(R.id.price_details);
        totalCostTV = (TextView) findViewById(R.id.totalCost);
        spinnerInterval = (Spinner) findViewById(R.id.spinner_interval);
        increaseByOne = (Button) findViewById(R.id.btn_qty_increase);
        decreaseByOne = (Button) findViewById(R.id.btn_qty_decrease);
        displayItemCount = (Button) findViewById(R.id.btn_order_or_qty);
        addressView = findViewById(R.id.addressSelector);
        addressIconIV = (ImageView) findViewById(R.id.addressIconIV);
        addressTitleTV = (TextView) findViewById(R.id.addressTitleTV);
        addressDescTV = (TextView) findViewById(R.id.addressDescTV);
        addressChangeTV = (TextView) findViewById(R.id.addressChangeTV);
        selectAddressBtn = (Button) findViewById(R.id.selectAddressBtn);
        addAddressBtn = (Button) findViewById(R.id.addAddressBtn);
        proceedToPayButton = (Button) findViewById(R.id.proceedToPayCheckoutActivity);
        proceedToPayButton = (Button) findViewById(R.id.proceedToPayCheckoutActivity);
        billView = (View) findViewById(R.id.bill);
        itemTotalTV = (TextView) findViewById(R.id.itemTotal);
        billDiscountTV = (TextView) findViewById(R.id.bill_discount_tv);
        billOffersLL = (LinearLayout) findViewById(R.id.billOffersLL);
        couponLayout = (RelativeLayout) findViewById(R.id.couponLayout);
        couponTextTV = (TextView) findViewById(R.id.applyCouponSubTextTV);
        grandTotalTV = (TextView) findViewById(R.id.grand_total_tv);
        billItemTotalTV = (TextView) findViewById(R.id.bill_item_total_tv);
        newCanSwitch = (SwitchCompat) findViewById(R.id.switch_new_cans);
        switchTV = (TextView) findViewById(R.id.switchTV);
        deliveryChargesTV = (TextView) findViewById(R.id.bill_delivery_charges_tv);
    }

    void setUpViewObjects() {

        if(can.getIsReplacable()==0) {
            switchTV.setVisibility(View.GONE);
            newCanSwitch.setVisibility(View.GONE);
        }
        else {
            switchTV.setVisibility(View.VISIBLE);
            newCanSwitch.setVisibility(View.VISIBLE);
        }

        displayItemCount.setText("" + numOfCans);
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

        switchTV.setText("I don't have "+ can.getName() + " can(s)" +" to replace ( " + rupeeSymbol + " " + (int) can.getNewCanPrice() + "/new can )  ");

        if(can.getUserWantsNewCan()==1) {
            newCanSwitch.setChecked(true);
        }
        else {
            newCanSwitch.setChecked(false);
        }

        setUpSpinner();


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

        recurrenceInterval = position + 1;
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
                            isDateSelected = true;
                            updateOrderValue();

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
                            isTimeSelected=true;
                            updateOrderValue();
                        }
                    }, currentHour, currentMinute, false);
            timePickerDialog.show();
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

    public boolean isNightDelivery() {

        LocalTime now = new LocalTime(selectedHour, selectedMinute);

        LocalTime six = new LocalTime( "06:00:01" );
        LocalTime twelve = new LocalTime( "00:00:00" );
        LocalTime midnight = new LocalTime("23:59:59");
        LocalTime elevenPM = new LocalTime("22:59:59");

        boolean isBeforeSix = now.isBefore(six);
        boolean isAfterTwelve = now.isAfter(twelve);
        boolean isBeforeTwelve = now.isBefore(midnight);
        boolean isAfter11PM = now.isAfter(elevenPM);
        boolean is12AM = now.isEqual(twelve);

        return (isBeforeSix && isAfterTwelve) || (isBeforeTwelve && isAfter11PM) || is12AM;
    }

    void updateOrderValue(){

        if(isDateSelected && isTimeSelected) {

            billView.setVisibility(View.VISIBLE);
            addressView.setVisibility(View.VISIBLE);

            numberOfDeliveries = calculateDeliveryCount();

            String itemTotalText = "Item Total\n" + rupeeSymbol + " " + (int)can.getPrice() + " x " + numOfCans + " can(s)" + " x " + numberOfDeliveries + " time(s)";

            total = can.getPrice() * numOfCans * numberOfDeliveries;

            if(can.getUserWantsNewCan()==1){
                itemTotalText = itemTotalText + " + \n" + rupeeSymbol + " " + (int)can.getNewCanPrice() + " x " + numOfCans + " new can(s)";
                total = total + (numOfCans * can.getNewCanPrice());
            }

            if(isNightDelivery()) {
                deliveryChargesTV.setText(rupeeSymbol + " 20");
                deliveryCharge = 20;
            }
            else {
                deliveryChargesTV.setText(rupeeSymbol + " 0");
                deliveryCharge = 0;
            }



            itemTotalTV.setText(itemTotalText);

            billItemTotalTV.setText(rupeeSymbol + " " + total);

            if(can.getName().equals("Neer24")) {

                billOffersLL.setVisibility(View.VISIBLE);

                int totalCansNum = numOfCans * numberOfDeliveries;
                freeCans = totalCansNum/10;
                discount = freeCans*can.getPrice();

                billDiscountTV.setText(rupeeSymbol + " " + discount);
                couponLayout.setVisibility(View.VISIBLE);
                couponTextTV.setText("" + freeCans + " NEER24 CAN(S) FREE");

            }

            toPay = total - discount + deliveryCharge;
            grandTotalTV.setText(rupeeSymbol+ " " + toPay);
            proceedToPayButton.setText("Proceed To Pay " + rupeeSymbol + " " + toPay);

        }

        else {
            billView.setVisibility(View.GONE);
            addressView.setVisibility(View.GONE);
            billOffersLL.setVisibility(View.GONE);
            couponLayout.setVisibility(View.GONE);
        }
    }

    public int calculateDeliveryCount() {

        Calendar cal1 = Calendar.getInstance(); // creates calendar
        cal1.setTime(new Date());

        cal1.add(Calendar.DATE,1);

        currentDay = cal1.get(Calendar.DAY_OF_MONTH);
        currentYear = cal1.get(Calendar.YEAR);
        currentMonth = cal1.get(Calendar.MONTH);

        Date startDate = new Date(currentYear, currentMonth, currentDay,0,0);

        cal1.setTime(startDate); // sets calendar time/date

        //
        startDate = cal1.getTime();
        System.out.print(startDate);
        //

        Calendar cal2 = Calendar.getInstance(); // creates calendar
        Date endDate = new Date(selectedYear,selectedMonth,selectedDay,0,0);
        cal2.setTime(endDate); // sets calendar time/date

        int i = 0;

        while((cal2.getTime().compareTo(cal1.getTime()))==0 || (cal2.getTime().compareTo(cal1.getTime()))>0) {
            cal1.add(Calendar.DAY_OF_YEAR,recurrenceInterval);
            //
            startDate = cal1.getTime();
            endDate = cal2.getTime();
            System.out.print(startDate);
            System.out.print(endDate);
            //

            i++;
        }

        return i;
    }

    void setAddressSelector() {

        int warehouseID = sharedPreferenceUtility.getWareHouseID();

        addressesInCurrentLocation=new ArrayList<CustomerAddress>();

        if(HomeScreenActivity.addressList!=null && !HomeScreenActivity.addressList.isEmpty()) {
            for(CustomerAddress address : HomeScreenActivity.addressList) {

                if(address.getWarehouseID() == warehouseID) {
                    addressesInCurrentLocation.add(address);
                }

            }
        }

        switch(addressesInCurrentLocation.size()) {

            case 0 : addressTitleTV.setText("No address present for current location");
                addressDescTV.setText("");
                selectAddressBtn.setVisibility(View.GONE);
                addAddressBtn.setVisibility(View.VISIBLE);
                addAddressBtn.setTextColor(Color.WHITE);
                addAddressBtn.setText("ADD ADDRESS TO PROCEED");
                addAddressBtn.setBackgroundColor(getResources().getColor(R.color.app_color));
                addressIconIV.setImageResource(R.drawable.no_address);
                addressChangeTV.setVisibility(View.GONE);
                proceedToPayButton.setVisibility(View.GONE);
                break;

            case 1 : addressTitleTV.setText("Deliver to " + addressesInCurrentLocation.get(0).getAddressNickName());
                selectedAddressID = addressesInCurrentLocation.get(0).getCustomerAddressID();
                String savedAddress = addressesInCurrentLocation.get(0).getFullAddress();
                int addressStripCount = (30 > savedAddress.length() ? savedAddress.length() : 30);
                addressDescTV.setText(savedAddress.substring(0,addressStripCount));
                selectAddressBtn.setVisibility(View.GONE);
                addAddressBtn.setVisibility(View.GONE);
                addressIconIV.setImageResource(R.drawable.address_location_icon);
                proceedToPayButton.setVisibility(View.VISIBLE);
                addressChangeTV.setText("ADD ADDRESS");
                break;

            default :
                addressTitleTV.setText("Multiple addresses in current location");
                String addressNickNames = "";
                for(CustomerAddress address : addressesInCurrentLocation) {
                    addressNickNames = addressNickNames + address.getAddressNickName() + " . ";
                }
                int nickNameStripCount = (30 > addressNickNames.length() ? addressNickNames.length() : 30);
                addressDescTV.setText(addressNickNames.substring(0,nickNameStripCount));
                selectAddressBtn.setVisibility(View.VISIBLE);
                addAddressBtn.setVisibility(View.VISIBLE);
                addAddressBtn.setText("ADD ADDRESS");
                selectAddressBtn.setText("SELECT ADDRESS");
                addAddressBtn.setTextColor(getResources().getColor(R.color.app_color));
                addressIconIV.setImageResource(R.drawable.multiple_addres_icon);
                addressChangeTV.setVisibility(View.GONE);
                proceedToPayButton.setVisibility(View.GONE);

                if(selectedAddressID!=0) {
                    for(CustomerAddress address : addressesInCurrentLocation){
                        if(address.getCustomerAddressID() == selectedAddressID) {
                            addressTitleTV.setText("Deliver to " + address.getAddressNickName());
                            savedAddress = address.getFullAddress();
                            addressStripCount = (30 > savedAddress.length() ? savedAddress.length() : 30);
                            addressDescTV.setText(savedAddress.substring(0,addressStripCount));
                            selectAddressBtn.setVisibility(View.GONE);
                            addAddressBtn.setVisibility(View.GONE);
                            addressIconIV.setImageResource(R.drawable.address_location_icon);
                            proceedToPayButton.setVisibility(View.VISIBLE);
                            addressChangeTV.setVisibility(View.VISIBLE);
                            addressChangeTV.setText("CHANGE");
                            break;
                        }
                    }
                }

        }
    }

    private void setUpOnClickListeners() {

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
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

        deliveryTimeTV.addTextChangedListener(new TextWatcher() {
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

        proceedToPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderTable order = createOrderObject();
                //OrderDetails orderContents[] = createOrderContents();
                ArrayList<OrderDetails> orderContents = new ArrayList<OrderDetails>(Arrays.asList(createOrderContents()));
                Intent intent = new Intent(SetRecurringScheduleActivity.this, PaymentModeActivity.class);
                intent.putExtra("parentClassName", "SetRecurringScheduleActivity");
                intent.putExtra("order",order);
                //intent.putExtra("orderContents", orderContents);
                intent.putParcelableArrayListExtra("orderContents",orderContents);

                startActivity(intent);
            }
        });

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("className", "RecurringActivity");
                intent.setClass(SetRecurringScheduleActivity.this, AddAddressActivity.class);
                startActivity(intent);
            }
        });

        addressChangeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(addressesInCurrentLocation.size() == 1) {
                    Intent intent = new Intent();
                    intent.putExtra("className", "RecurringActivity");
                    intent.setClass(SetRecurringScheduleActivity.this, AddAddressActivity.class);
                    startActivity(intent);
                    return;
                }

                showAddressSelectionDialog();

            }
        });

        selectAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAddressSelectionDialog();

            }
        });

        newCanSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newCanSwitch.isChecked()) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SetRecurringScheduleActivity.this);
                    alertDialogBuilder.setMessage("This option lets you order new can, if you don't already have a can to replace with the can we deliver. You will be charged a refundable security deposit for the new can. We assure you the security deposit will be refunded back to you once you return the can. Are you sure you want a new can ?");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    can.setUserWantsNewCan(1);
                                    updateOrderValue();
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newCanSwitch.setChecked(false);
                            can.setUserWantsNewCan(0);
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                } else {
                    can.setUserWantsNewCan(0);
                    updateOrderValue();
                }
            }
        });

    }

    private void showAddressSelectionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setAdapter(new SetAddressSelectorDialogAdapter(this,R.layout.select_address_custom_layout, addressesInCurrentLocation), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int itemIndex) {


                if(itemIndex == addressesInCurrentLocation.size()) {
                    Intent intent = new Intent();
                    intent.setClass(SetRecurringScheduleActivity.this, AddAddressActivity.class);
                    intent.putExtra("className", "CheckoutActivity");
                    startActivity(intent);
                    return;
                }
                selectedAddressIndex = itemIndex;
                selectedAddressID = addressesInCurrentLocation.get(itemIndex).getCustomerAddressID();
                setAddressSelector();

            }
        });
        builder.setIcon(R.drawable.saved_address_icon);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.dialog_custom_title, null);
        builder.setCustomTitle(linearLayout);
        builder.create();
        builder.show();


    }

    public OrderTable createOrderObject() {

        int customerID = sharedPreferenceUtility.getCustomerID();
        int warehouseID = sharedPreferenceUtility.getWareHouseID();
        int deliveryBoyID = 0;
        String orderPaymentID = null;

        String orderDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.DAY_OF_YEAR, 1);


        Date dTime = new Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), selectedHour, selectedMinute);
        String deliveryTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dTime);

        double totalAmount = total + deliveryCharge;
        double discountedAmount = discount;
        double amountPaid = toPay;
        String paymentMode = null;

        String couponCode = null;
        if(discount>0) {
            couponCode = "NEERFREE";
        }

        int numberOfFreeCansAvailed = freeCans;
        int customerAddressID = selectedAddressID;
        int isNormalDelivery = 0;

        int isNightDelivery = isNightDelivery() ? 1 : 0;
        int isScheduleDelivery = 0;
        int isRecurringDelivery = 1;
        String customerUniqueID = sharedPreferenceUtility.getCustomerUniqueID();
        int isOrdered = 1;
        int isDispatched = 0;
        int isDelivered = 0;
        int isCancelled = 0;
        String endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute));
        int deliveryLeft = numberOfDeliveries;
        int recurringOrderFrequency = recurrenceInterval;

        int totalCansOrdered = numOfCans;

        OrderTable order = new OrderTable(customerID, warehouseID, deliveryBoyID, orderPaymentID, orderDate,
                deliveryTime, totalAmount, discountedAmount, amountPaid, paymentMode, couponCode,
                numberOfFreeCansAvailed, customerAddressID, isNormalDelivery, isNightDelivery,
                isScheduleDelivery, isRecurringDelivery, customerUniqueID, isOrdered, isDispatched,
                isDelivered, isCancelled, endDate, deliveryLeft, recurringOrderFrequency, totalCansOrdered);

        return order;

    }

    public OrderDetails[] createOrderContents() {
        OrderDetails orderDetails[] = new OrderDetails[1];
        orderDetails[0] = new OrderDetails(can.getCanID(), can.getUserWantsNewCan(), 0,0, numOfCans);
        return  orderDetails;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAddressSelector();
        setUpOnClickListeners();
        if(HomeScreenActivity.cansList==null || HomeScreenActivity.cansList.isEmpty() || HomeScreenActivity.locationName==null || HomeScreenActivity.locationName.isEmpty()){

            Intent intent  = new Intent();
            intent.setClass(SetRecurringScheduleActivity.this, FirstActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        final Intent intent = new Intent();
        intent.setClass(SetRecurringScheduleActivity.this, ScheduleDeliveryActivity.class);
        intent.putExtra("type","recurring");
        startActivity(intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


}

