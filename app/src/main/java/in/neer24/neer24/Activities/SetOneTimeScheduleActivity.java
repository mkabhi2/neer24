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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.joda.time.LocalTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import in.neer24.neer24.Adapters.SetAddressSelectorDialogAdapter;
import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.CustomerAddress;
import in.neer24.neer24.CustomObjects.OrderDetails;
import in.neer24.neer24.CustomObjects.OrderTable;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;

public class SetOneTimeScheduleActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Button btnDatePicker, btnTimePicker, btnCart, increaseByOne, decreaseByOne, displayItemCount, selectAddressBtn, addAddressBtn;
    private TextView dateTV, timeTV, productPriceTV, productNameTV, priceDetailsTV, totalCostTV, addressTitleTV, addressDescTV,
            addressChangeTV, itemTotalTV, billItemTotalTV, billDiscountTV, grandTotalTV, switchTV, deliveryChargesTV, floorChargesTV, floorChargesDetailsTV;
    private int deliveryCharge=0;
    static int hasFloorCharge = 0;
    private int floorCharge = 0;
    private ImageView productImage, addressIconIV;
    private Button proceedToPayButton;
    private View addressView, billView;
    private LinearLayout billOffersLL;
    private SwitchCompat newCanSwitch;
    private TextView deliveryChargeText;
    private double toPay = 0;

    private Date date;
    private Can can;
    private Toast checkoutToast, timeTVToast;

    private int numOfCans=1, currentYear, currentMonth, currentDay, currentHour, currentMinute, selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;

    public static int selectedAddressID;
    private String rupeeSymbol;
    private double total;

    private boolean isDateSelected = false, isTimeSelected = false;

    private String monthName[] = {"Jan","Feb","Mar","Apr","May","June","July","Aug","Sep","Oct","Nov","Dec"};
    private ArrayList<CustomerAddress> addressesInCurrentLocation;

    private SharedPreferenceUtility sharedPreferenceUtility;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Bundle bundle = getIntent().getExtras();
        can = bundle.getParcelable("item");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_one_time_schedule);
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

    private void instantiateViewObjects(){
        deliveryChargeText = (TextView) findViewById(R.id.bill_delivery_charges_details_tv);
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
        grandTotalTV = (TextView) findViewById(R.id.grand_total_tv);
        billItemTotalTV = (TextView) findViewById(R.id.bill_item_total_tv);
        newCanSwitch = (SwitchCompat) findViewById(R.id.switch_has_lift);
        switchTV = (TextView) findViewById(R.id.switchTV);
        deliveryChargesTV = (TextView) findViewById(R.id.bill_delivery_charges_tv);
        floorChargesTV = (TextView) findViewById(R.id.bill_floor_charges_tv);
        floorChargesDetailsTV = (TextView) findViewById(R.id.bill_floor_charges_details_tv);
    }

    private void setUpViewObjects(){

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

    }

    private void setUpOnClickListeners() {

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        increaseByOne.setOnClickListener(this);
        decreaseByOne.setOnClickListener(this);

        dateTV.addTextChangedListener(new TextWatcher() {
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

        timeTV.addTextChangedListener(new TextWatcher() {
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
                Intent intent = new Intent(SetOneTimeScheduleActivity.this, PaymentModeActivity.class);
                intent.putExtra("parentClassName", "SetOneTimeScheduleActivity");
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
                intent.putExtra("className", "OneTimeScheduleActivity");
                intent.setClass(SetOneTimeScheduleActivity.this, AddAddressActivity.class);
                startActivity(intent);
            }
        });

        addressChangeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(addressesInCurrentLocation.size() == 1) {
                    Intent intent = new Intent();
                    intent.putExtra("className", "OneTimeScheduleActivity");
                    intent.setClass(SetOneTimeScheduleActivity.this, AddAddressActivity.class);
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

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SetOneTimeScheduleActivity.this);
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
                    intent.setClass(SetOneTimeScheduleActivity.this, AddAddressActivity.class);
                    intent.putExtra("className", "OneTimeScheduleActivity");
                    startActivity(intent);
                    return;
                }
                selectedAddressID = addressesInCurrentLocation.get(itemIndex).getCustomerAddressID();
                if(Integer.parseInt(addressesInCurrentLocation.get(itemIndex).getFloorNumber())>2 && addressesInCurrentLocation.get(itemIndex).getHasLift()==0){
                    hasFloorCharge = 1;
                    updateOrderValue();
                }
                else{
                    hasFloorCharge = 0;
                    updateOrderValue();
                }
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

                            String showDay, showYear;

                            selectedDay = dayOfMonth;
                            selectedMonth = monthOfYear;
                            selectedYear = year;

                            showDay = Integer.valueOf(selectedDay).toString();
                            showYear = Integer.valueOf(selectedYear).toString();

                            if(selectedDay < 10) {
                                showDay = "0" + showDay;
                            }

                            dateTV.setText(showDay + " - " + monthName[selectedMonth] + " - " + showYear);

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
            c.setTime(new Date());
            c.add(Calendar.HOUR_OF_DAY,2);
            currentHour = c.get(Calendar.HOUR_OF_DAY);
            currentMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            Calendar cal = Calendar.getInstance();
                            cal.setTime(new Date());
                            cal.add(Calendar.HOUR_OF_DAY,2);
                            currentHour = cal.get(Calendar.HOUR_OF_DAY);
                            currentMinute = cal.get(Calendar.MINUTE);

                            selectedHour = hourOfDay;
                            selectedMinute = minute;
                            String showHour;
                            String showMinute;
                            boolean isAM;

                            if (selectedYear == currentYear
                                    && selectedMonth == currentMonth
                                    && selectedDay == currentDay
                                    && ((selectedHour < currentHour) || ((selectedHour == currentHour) && (selectedMinute < currentMinute)))) {

                                timeTV.setText("No Time Selected");
                                isTimeSelected = false;
                                updateOrderValue();
                                Toast.makeText(SetOneTimeScheduleActivity.this, "Set date and time at least 2 hours from now", Toast.LENGTH_LONG).show();
                            }
                            else {
                                isTimeSelected = true;
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
                                    timeTV.setText(showHour + " : " + showMinute + " AM");
                                }
                                else {
                                    timeTV.setText(showHour + " : " + showMinute + " PM");
                                }
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

    private void setAddressSelector() {

        int warehouseID = sharedPreferenceUtility.getWareHouseID();

        addressesInCurrentLocation=new ArrayList<CustomerAddress>();

        if(HomeScreenActivity.addressList!=null && !HomeScreenActivity.addressList.isEmpty()) {
            for(CustomerAddress address : HomeScreenActivity.addressList) {

                if(address.getWarehouseID() == warehouseID) {
                    addressesInCurrentLocation.add(address);
                }

            }
        }
        //

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
                if(Integer.parseInt(addressesInCurrentLocation.get(0).getFloorNumber())>2 && addressesInCurrentLocation.get(0).getHasLift()==0){
                    hasFloorCharge = 1;
                    updateOrderValue();
                }
                else{
                    hasFloorCharge = 0;
                    updateOrderValue();
                }
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

    private void updateOrderValue(){

        if( isTimeSelected && isDateSelected ) {

            billView.setVisibility(View.VISIBLE);
            addressView.setVisibility(View.VISIBLE);
            double discount = 0;

            String itemTotalText = "Item Total\n" + rupeeSymbol + " " + (int)can.getPrice() + " x " + numOfCans + " can(s)";

            if(can.getUserWantsNewCan()==1) {
                itemTotalText = itemTotalText + " + \n" + rupeeSymbol + " " + (int)can.getNewCanPrice() + " x " + numOfCans + " new can(s)";
            }

            itemTotalTV.setText(itemTotalText);
            total = can.getPrice() * numOfCans;

            if(can.getUserWantsNewCan()==1) {
                total = total + (can.getNewCanPrice() * numOfCans);
            }

            billItemTotalTV.setText(rupeeSymbol + " " + total);

            if(isNightDelivery()) {
                deliveryChargeText.setText("Delivery ChargeS ( 11 PM - 6 AM)");
                deliveryChargesTV.setText(rupeeSymbol + " 20");
                deliveryCharge = 20;
            }
            else {
                deliveryChargeText.setText("Delivery Charge");
                deliveryChargesTV.setText(rupeeSymbol + " 0");
                deliveryCharge = 0;
            }

            if(hasFloorCharge==1){

                String floorChargeString = "Floor Charges\n" + rupeeSymbol + " 5 x " + numOfCans + " cans(s)";
                floorChargesDetailsTV.setText(floorChargeString);
                floorCharge = 5 * numOfCans;
                floorChargesTV.setText(rupeeSymbol + " " + floorCharge);
            }
            else{
                String floorChargeString = "Floor Charges";
                floorChargesDetailsTV.setText(floorChargeString);
                floorCharge = 0;
                floorChargesTV.setText(rupeeSymbol + " " + floorCharge);
            }

            total = total + deliveryCharge + floorCharge;

            toPay = total-discount;
            grandTotalTV.setText(rupeeSymbol+ " " + toPay);
            proceedToPayButton.setText("Proceed To Pay " + rupeeSymbol + " " + toPay);

        }
        else {
            billView.setVisibility(View.GONE);
            addressView.setVisibility(View.GONE);
            billOffersLL.setVisibility(View.GONE);
        }
    }

    private boolean isNightDelivery() {

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

    private OrderTable createOrderObject() {


        int customerID = sharedPreferenceUtility.getCustomerID();
        int warehouseID = sharedPreferenceUtility.getWareHouseID();
        int deliveryBoyID = 0;
        String orderPaymentID = null;
        String orderDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        Date dTime = new Date(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
        String deliveryTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dTime);

        double totalAmount = toPay;
        double discountedAmount = 0;
        double amountPaid = toPay;
        String paymentMode = null;

        String couponCode = null;

        int numberOfFreeCansAvailed = 0;
        int customerAddressID = selectedAddressID;
        int isNormalDelivery = 0;
        int isNightDelivery = isNightDelivery() ? 1 : 0;
        int isScheduleDelivery = 1;
        int isRecurringDelivery = 0;
        String customerUniqueID = sharedPreferenceUtility.getCustomerUniqueID();
        int isOrdered = 1;
        int isDispatched = 0;
        int isDelivered = 0;
        int isCancelled = 0;
        String endDate = deliveryTime;
        int deliveryLeft = 0;
        int recurringOrderFrequency = 0;

        int totalCansOrdered = numOfCans;

        OrderTable order = new OrderTable(customerID, warehouseID, deliveryBoyID, orderPaymentID, orderDate,
                deliveryTime, totalAmount, discountedAmount, amountPaid, paymentMode, couponCode,
                numberOfFreeCansAvailed, customerAddressID, isNormalDelivery, isNightDelivery,
                isScheduleDelivery, isRecurringDelivery, customerUniqueID, isOrdered, isDispatched,
                isDelivered, isCancelled, endDate, deliveryLeft, recurringOrderFrequency, totalCansOrdered,hasFloorCharge);

        return order;
    }

    private OrderDetails[] createOrderContents() {
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
            intent.setClass(SetOneTimeScheduleActivity.this, FirstActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        final Intent intent = new Intent();
        intent.setClass(SetOneTimeScheduleActivity.this, ScheduleDeliveryActivity.class);
        intent.putExtra("type","schedule");
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

    @Override
    protected void onPause() {
        super.onPause();
        selectedAddressID = 0;
        hasFloorCharge = 0;
    }



}
