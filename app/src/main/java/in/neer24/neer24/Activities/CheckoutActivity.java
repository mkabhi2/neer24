package in.neer24.neer24.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import in.neer24.neer24.Adapters.SetAddressSelectorDialogAdapter;
import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.CustomerAddress;
import in.neer24.neer24.CustomObjects.NormalCart;
import in.neer24.neer24.CustomObjects.OrderDetails;
import in.neer24.neer24.CustomObjects.OrderTable;
import in.neer24.neer24.Fragments.CheckoutFragment;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;
import in.neer24.neer24.Utilities.UtilityClass;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckoutActivity extends AppCompatActivity implements PaymentResultListener {

    private TextView cartSummaryTextView;
    public static Button proceedToPayButton;
    public static View addressView;
    private ImageView addressIconIV;
    private TextView addressTitleTV, addressDescTV, addressChangeTV;
    private Button selectAddressBtn, addAddressBtn;
    private Toolbar toolbar;
    public static boolean isCouponApplied;
    ArrayList<CustomerAddress> addressesInCurrentLocation;
    public static int selectedAddressID;
    int selectedAddressIndex;

    String returnedOrderID = "";
    String isOrderDetailsInsertionSuccessFull;
    SharedPreferenceUtility sharedPreferenceUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        sharedPreferenceUtility=new SharedPreferenceUtility(this);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            isCouponApplied = bundle.getBoolean("isCouponApplied");
        }

        instantiateViewObjects();
        setViewObjects();
        setUpFragment();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void instantiateViewObjects() {

        cartSummaryTextView = (TextView) findViewById(R.id.checkoutActivityCartSummaryTextView);
        addressView = findViewById(R.id.addressSelector);
        addressIconIV = (ImageView) findViewById(R.id.addressIconIV);
        addressTitleTV = (TextView) findViewById(R.id.addressTitleTV);
        addressDescTV = (TextView) findViewById(R.id.addressDescTV);
        addressChangeTV = (TextView) findViewById(R.id.addressChangeTV);
        selectAddressBtn = (Button) findViewById(R.id.selectAddressBtn);
        addAddressBtn = (Button) findViewById(R.id.addAddressBtn);
        proceedToPayButton = (Button) findViewById(R.id.proceedToPayCheckoutActivity);
        proceedToPayButton = (Button) findViewById(R.id.proceedToPayCheckoutActivity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

    }

    private void setViewObjects() {

        cartSummaryTextView.setGravity(Gravity.CENTER_VERTICAL);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));

    }

    private void setUpFragment() {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CheckoutFragment checkoutFragment = new CheckoutFragment();
        fragmentTransaction.add(R.id.cartFragmentContainer, checkoutFragment, "checkoutFragment");
        fragmentTransaction.commit();
    }

    private void setUpOnClickListeners() {

        proceedToPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap.Entry<Can, Integer> entry = NormalCart.getCartList().entrySet().iterator().next();
                Can can = entry.getKey();
                //insertDataIntoOrderTable(createObjectForOrderTable());
                startPayment();
            }
        });

        addAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("className", "CheckoutActivity");
                intent.setClass(CheckoutActivity.this, AddAddressActivity.class);
                startActivity(intent);
            }
        });

        addressChangeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(addressesInCurrentLocation.size() == 1) {
                    Intent intent = new Intent();
                    intent.putExtra("className", "CheckoutActivity");
                    intent.setClass(CheckoutActivity.this, AddAddressActivity.class);
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

    }

    private void showAddressSelectionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setAdapter(new SetAddressSelectorDialogAdapter(this,R.layout.select_address_custom_layout, addressesInCurrentLocation), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int itemIndex) {


                if(itemIndex == addressesInCurrentLocation.size()) {
                    Intent intent = new Intent();
                    intent.setClass(CheckoutActivity.this, AddAddressActivity.class);
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


    private void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setImage(R.drawable.logo);
        final Activity activity = this;
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Neer24");
            options.put("description", "Order #123456");
            options.put("currency", "INR");
            options.put("amount", "100");
            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("error", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        insertDataIntoOrderTable(createObjectForOrderTable());
        Log.d("payment",s);
        Toast.makeText(CheckoutActivity.this, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.d("error", s);
        Toast.makeText(CheckoutActivity.this, "Error", Toast.LENGTH_SHORT).show();
    }

    public void insertDataIntoOrderTable(OrderTable orderTable) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                //.baseUrl("http://192.168.0.2:8080/")
                .baseUrl("http://192.168.0.3:8080/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);

        Call<String> call = retroFitNetworkClient.insertIntoOrderTable(orderTable);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.body() != null) {
                    returnedOrderID = response.body().toString();
                    if (!returnedOrderID.equals("0")) {
                        Toast.makeText(CheckoutActivity.this,"Order Succesfull",Toast.LENGTH_SHORT).show();
                        //insertIntoOrderDetailsTable(returnedOrderID);
                        //updateWarehouseCansTable(c.getCanID(),c.getWarehouseID());
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });
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


    public ArrayList<OrderDetails> createObjectForOrderDetails(String orderID) {
        ArrayList<OrderDetails> al = new ArrayList<OrderDetails>();
        HashMap<Can, Integer> cans = NormalCart.getCartList();
        for (Can can : cans.keySet()) {
            int canID = can.getCanID();
            int isNew = 0;
            int isNewDespenser = 0;
            int orderIDs = Integer.parseInt(orderID);
            int quantity = cans.get(can);
            OrderDetails orderDetails = new OrderDetails(canID, isNew, isNewDespenser, orderIDs, quantity, sharedPreferenceUtility.getCustomerUniqueID());
            al.add(orderDetails);
        }
        return al;
    }

    public OrderTable createObjectForOrderTable() {
        Date oDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
        Date dTime = cal.getTime();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        int customerID = sharedPreferenceUtility.getCustomerID();
        int warehouseID = sharedPreferenceUtility.getWareHouseID();
        int orderPaymentID = 1001;

        String orderDate = sdf.format(oDate);
        String deliveryTime = sdf1.format(dTime);

        double totalAmount = 100;
        double discountedAmount = 100;
        double amountPaid = 50;

        String paymentMode = "Debit Card";

        String couponCode = "NEER24";
        int numberOfFreeCansAvailed = 2;

        int customerAddressID = 2;

        int isNormalDelivery = 1;
        int isNightDelivery = 0;
        int isScheduleDelivery = 0;
        int isRecurringDelivery = 0;


        if (UtilityClass.checkIfOrderIsNightDeliveryOrNot(deliveryTime)) {
            isNightDelivery = 1;
        } else {
            isNightDelivery = 0;
        }

        OrderTable orderTable = null;
        //= new OrderTable(customerID, warehouseID, orderPaymentID, orderDate, deliveryTime, totalAmount, discountedAmount, amountPaid, paymentMode, couponCode, numberOfFreeCansAvailed, customerAddressID, isNormalDelivery, isNightDelivery, isScheduleDelivery, isRecurringDelivery, sharedPreferenceUtility.getCustomerUniqueID(),1,0,0,0,null,0,0,1);
        return orderTable;
    }

    public void insertIntoOrderDetailsTable(String returnedOrderID) {
        ArrayList<OrderDetails> orderDetails = createObjectForOrderDetails(returnedOrderID);
        Retrofit.Builder builder = new Retrofit.Builder()
                //.baseUrl("http://192.168.0.2:8080/")
                .baseUrl("http://192.168.0.3:8080")      //
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);

        Call<String> call = retroFitNetworkClient.insertIntoOrderDetailsTable(orderDetails);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body() != null) {
                    isOrderDetailsInsertionSuccessFull = response.body().toString();
                    if (isOrderDetailsInsertionSuccessFull.contains("true")) {
                        Toast.makeText(CheckoutActivity.this, "Insertion Successfull Order Details", Toast.LENGTH_SHORT);
                    } else {
                        Toast.makeText(CheckoutActivity.this, "Data Completely not inserter into Order Details", Toast.LENGTH_SHORT);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Insertion Failed Due to Server Error", Toast.LENGTH_SHORT);
            }
        });
    }

    public void updateWarehouseCansTable(int canID, int warehouseID) {
        Retrofit.Builder builder = new Retrofit.Builder()
                //.baseUrl("http://192.168.0.2:8080/")
                .baseUrl("http://18.220.28.118")       //
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);

        Call<String> call = retroFitNetworkClient.updateWarehouseCanTable(canID, warehouseID);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body() != null) {
                    returnedOrderID = response.body().toString();
                    if (response.body().toString().contains("true")) {
                        Toast.makeText(CheckoutActivity.this, " data updated", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HomeScreenActivity.showCartDetailsSummary();
    }

    @Override
    protected void onPause() {
        super.onPause();
        selectedAddressID = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAddressSelector();
        setUpOnClickListeners();
    }
}
