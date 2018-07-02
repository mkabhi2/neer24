package in.neer24.neer24.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.neer24.neer24.Adapters.OrderDetailsRVAdapter;
import in.neer24.neer24.BuildConfig;
import in.neer24.neer24.CustomObjects.CustomerAddress;
import in.neer24.neer24.CustomObjects.CustomerOrder;
import in.neer24.neer24.CustomObjects.OrderTable;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RVItemDecoration;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderDetailsActivity extends AppCompatActivity {

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 35;

    private TextView orderIDTV, orderDateTV, orderStatusTV, orderTypeTV, grandTotalTV, deliveryAddressTV, recurrencesLeftTV, paymentModeTV,
            offTimeTV, deliveryChargeTV, discountTV, numFreeCansTV, floorChargeTV;
    private RecyclerView orderItemsRV;
    private OrderTable order;
    private ArrayList<CustomerOrder> ordersDetailsList = new ArrayList<CustomerOrder>();
    private LinearLayout ratingLayout, deliveryBoyContact, recurrencesLeftLayout, callCustomerCare;
    private String rupeeSymbol;
    private LinearLayout couponLayout;
    private ProgressDialog dialog;
    private Button orderCancelButton, ratingSubmitButton;
    private long timeCountInMilliSeconds = 7200000;
    private CountDownTimer countDownTimer;
    private TextView textViewTime;
    private RelativeLayout countDownTimerLayout;
    private EditText ratingDetailsET;
    private ImageView eRat1, eRat2, eRat3, eRat4, eRat5, fRat1, fRat2, fRat3, fRat4, fRat5;
    private int rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
            order = bundle.getParcelable("order");

        rupeeSymbol = getResources().getString(R.string.Rs);

        initialiseViewComponents();
        setUpViewComponents();
        setUpOnClickListeners();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initialiseViewComponents(){

        orderItemsRV = (RecyclerView) findViewById(R.id.orderItemsRV);
        fetchOrderDetails();

        ratingLayout = (LinearLayout) findViewById(R.id.ratings_layout);
        countDownTimerLayout = (RelativeLayout) findViewById(R.id.countDownTimerLayout);
        callCustomerCare = (LinearLayout) findViewById(R.id.callCustomerCare);
        deliveryBoyContact = (LinearLayout) findViewById(R.id.delivery_boy_contact);
        recurrencesLeftLayout = (LinearLayout) findViewById(R.id.recurrencesLeftLayout);
        recurrencesLeftTV = (TextView) findViewById(R.id.recurrencesLeft);
        paymentModeTV = (TextView) findViewById(R.id.paymentMode);
        offTimeTV = (TextView) findViewById(R.id.offTimeTV);
        deliveryChargeTV = (TextView) findViewById(R.id.deliveryCharge);
        discountTV = (TextView) findViewById(R.id.discount);
        couponLayout = (LinearLayout) findViewById(R.id.freeNeer24Cans);
        numFreeCansTV = (TextView) findViewById(R.id.numFreeCansTV);
        orderIDTV = (TextView) findViewById(R.id.orderID);
        orderDateTV = (TextView) findViewById(R.id.orderDate);
        orderStatusTV = (TextView) findViewById(R.id.orderStatus);
        orderTypeTV = (TextView) findViewById(R.id.orderType);
        grandTotalTV = (TextView) findViewById(R.id.grandTotalTV);
        grandTotalTV.setText("" + order.getAmountPaid());
        deliveryAddressTV = (TextView) findViewById(R.id.deliveryAddress);
        orderCancelButton = (Button) findViewById(R.id.orderCancelBtn);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        floorChargeTV = (TextView) findViewById(R.id.floorCharge);
        ratingSubmitButton = (Button) findViewById(R.id.ratingSubmitBtn);
        ratingDetailsET = (EditText) findViewById(R.id.commentET);
        eRat1 = (ImageView) findViewById(R.id.one_star_empty);
        eRat2 = (ImageView) findViewById(R.id.two_star_empty);
        eRat3 = (ImageView) findViewById(R.id.three_star_empty);
        eRat4 = (ImageView) findViewById(R.id.four_star_empty);
        eRat5 = (ImageView) findViewById(R.id.five_star_empty);
        fRat1 = (ImageView) findViewById(R.id.one_star_full);
        fRat2 = (ImageView) findViewById(R.id.two_star_full);
        fRat3 = (ImageView) findViewById(R.id.three_star_full);
        fRat4 = (ImageView) findViewById(R.id.four_star_full);
        fRat5 = (ImageView) findViewById(R.id.five_star_full);

    }

    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d H : %02d M : %02d S",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;


    }

    private void setUpViewComponents() {

        couponLayout.setVisibility(View.GONE);
        deliveryAddressTV.setVisibility(View.GONE);

        int floorChargeAmount = 5 * order.getTotalCansOrdered();
        int deliveryChargeAmount = 20;

        if(order.getIsDelivered()==1){
            ratingLayout.setVisibility(View.VISIBLE);
        }
        else {
            ratingLayout.setVisibility(View.GONE);
        }
        if(order.getIsDispatched()==1 && order.getIsDelivered()!=1 && order.getIsCancelled()!=1) {
            deliveryBoyContact.setVisibility(View.VISIBLE);
        }
        else {
            deliveryBoyContact.setVisibility(View.GONE);
        }

        orderIDTV.setText("" + order.getOrderID());
        orderDateTV.setText("" + order.getOrderDate().toString().substring(0,16));
        String orderStatus = "ORDER PLACED";

        if(order.getIsDispatched()==1){

            orderStatus = "DISPATCHED";
        }
        if(order.getIsDelivered()==1){
            orderStatus = "DELIVERED";
            orderCancelButton.setVisibility(View.GONE);
            countDownTimerLayout.setVisibility(View.GONE);
        }
        if(order.getIsCancelled()==1){
            countDownTimerLayout.setVisibility(View.GONE);
            orderStatus = "CANCELLED";
            orderStatusTV.setTextColor(getResources().getColor(R.color.Red));
            orderCancelButton.setVisibility(View.GONE);
        }

        orderStatusTV.setText(orderStatus);

        orderTypeTV.setText("" + "Normal Delivery");
        recurrencesLeftLayout.setVisibility(View.GONE);

        if(order.getIsScheduleDelivery()==1) {
            orderTypeTV.setText("" + "Scheduled Delivery");
        }
        if(order.getIsRecurringDelivery()==1) {
            orderCancelButton.setVisibility(View.GONE);
            orderTypeTV.setText("" + "Recurring Delivery");
            recurrencesLeftLayout.setVisibility(View.VISIBLE);
            recurrencesLeftTV.setText("" + order.getDeliveryLeft());
            if(ordersDetailsList!=null && !ordersDetailsList.isEmpty()){
                double canPriceAmount = ordersDetailsList.get(0).getCanPrice();
                int numberOfDeliveries = (int)(order.getAmountPaid() - (canPriceAmount * order.getTotalCansOrdered()))/(floorChargeAmount + deliveryChargeAmount);
                floorChargeAmount = floorChargeAmount * numberOfDeliveries;
                deliveryChargeAmount = deliveryChargeAmount * numberOfDeliveries;
            }
        }

        paymentModeTV.setText("" + order.getPaymentMode());

        if(order.getIsNightDelivery()==1){
            offTimeTV.setVisibility(View.VISIBLE);
            deliveryChargeTV.setText(rupeeSymbol + " " + deliveryChargeAmount);
        }
        else {
            offTimeTV.setVisibility(View.GONE);
            deliveryChargeTV.setText(rupeeSymbol + " 0");
        }

        if(order.getHasFloorCharge()==1){

            floorChargeTV.setText(rupeeSymbol + " " + floorChargeAmount);
        }
        else{
            floorChargeTV.setText(rupeeSymbol + " 0");
        }

        discountTV.setText("- " + rupeeSymbol + " " + (int)order.getDiscountedAmount());
        grandTotalTV.setText(rupeeSymbol + " " + order.getAmountPaid());

        if(order.getNumberOfFreeCansAvailed()>1) {

            couponLayout.setVisibility(View.VISIBLE);
            if(order.getIsRecurringDelivery()==1) {
                numFreeCansTV.setText("Last " + order.getNumberOfFreeCansAvailed() + " Neer24 cans availed free in entire recurring order");
            }
            else {
                numFreeCansTV.setText("" + order.getNumberOfFreeCansAvailed() + " Neer24 cans availed free in this order");
            }

        }
        if(HomeScreenActivity.addressList!=null && !HomeScreenActivity.addressList.isEmpty()) {
            for(int i=0; i<HomeScreenActivity.addressList.size();i++){
                CustomerAddress customerAddress = HomeScreenActivity.addressList.get(i);
                if(customerAddress.getCustomerAddressID()==order.getCustomerAddressID()){
                    deliveryAddressTV.setVisibility(View.VISIBLE);
                    deliveryAddressTV.setText(customerAddress.getAddressNickName() + " : " + customerAddress.getFullAddress());
                }
            }
        }

    }

    private void fetchOrderDetails(){


        dialog = ProgressDialog.show(OrderDetailsActivity.this, "",
                "Loading. Please wait...", true);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://18.220.28.118:80/")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        int orderID = order.getOrderID();
        Call<List<CustomerOrder>> call = retroFitNetworkClient.getOrderDetailsForOrderID(orderID);

        call.enqueue(new Callback<List<CustomerOrder>>() {
            @Override
            public void onResponse(Call<List<CustomerOrder>> call, Response<List<CustomerOrder>> response) {

                ordersDetailsList = (ArrayList<CustomerOrder>) response.body();
                setUpViewComponents();
                setUpRecyclerView();
                dialog.cancel();
            }

            @Override
            public void onFailure(Call<List<CustomerOrder>> call, Throwable t) {
                Toast.makeText(OrderDetailsActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }

        });
    }

    private void setUpRatingListeners(){
        eRat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 1;
                eRat1.setVisibility(View.GONE);
                fRat1.setVisibility(View.VISIBLE);

                eRat2.setVisibility(View.VISIBLE);
                fRat2.setVisibility(View.GONE);

                eRat3.setVisibility(View.VISIBLE);
                fRat3.setVisibility(View.GONE);

                eRat4.setVisibility(View.VISIBLE);
                fRat4.setVisibility(View.GONE);

                eRat5.setVisibility(View.VISIBLE);
                fRat5.setVisibility(View.GONE);
            }
        });

        eRat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 2;
                eRat1.setVisibility(View.GONE);
                fRat1.setVisibility(View.VISIBLE);

                eRat2.setVisibility(View.GONE);
                fRat2.setVisibility(View.VISIBLE);

                eRat3.setVisibility(View.VISIBLE);
                fRat3.setVisibility(View.GONE);

                eRat4.setVisibility(View.VISIBLE);
                fRat4.setVisibility(View.GONE);

                eRat5.setVisibility(View.VISIBLE);
                fRat5.setVisibility(View.GONE);
            }
        });

        eRat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 3;
                eRat1.setVisibility(View.GONE);
                fRat1.setVisibility(View.VISIBLE);

                eRat2.setVisibility(View.GONE);
                fRat2.setVisibility(View.VISIBLE);

                eRat3.setVisibility(View.GONE);
                fRat3.setVisibility(View.VISIBLE);

                eRat4.setVisibility(View.VISIBLE);
                fRat4.setVisibility(View.GONE);

                eRat5.setVisibility(View.VISIBLE);
                fRat5.setVisibility(View.GONE);
            }
        });

        eRat4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating=4;
                eRat1.setVisibility(View.GONE);
                fRat1.setVisibility(View.VISIBLE);

                eRat2.setVisibility(View.GONE);
                fRat2.setVisibility(View.VISIBLE);

                eRat3.setVisibility(View.GONE);
                fRat3.setVisibility(View.VISIBLE);

                eRat4.setVisibility(View.GONE);
                fRat4.setVisibility(View.VISIBLE);

                eRat5.setVisibility(View.VISIBLE);
                fRat5.setVisibility(View.GONE);
            }
        });

        eRat5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = 5;
                eRat1.setVisibility(View.GONE);
                fRat1.setVisibility(View.VISIBLE);

                eRat2.setVisibility(View.GONE);
                fRat2.setVisibility(View.VISIBLE);

                eRat3.setVisibility(View.GONE);
                fRat3.setVisibility(View.VISIBLE);

                eRat4.setVisibility(View.GONE);
                fRat4.setVisibility(View.VISIBLE);

                eRat5.setVisibility(View.GONE);
                fRat5.setVisibility(View.VISIBLE);
            }
        });

        fRat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating=1;
                eRat1.setVisibility(View.GONE);
                fRat1.setVisibility(View.VISIBLE);

                eRat2.setVisibility(View.VISIBLE);
                fRat2.setVisibility(View.GONE);

                eRat3.setVisibility(View.VISIBLE);
                fRat3.setVisibility(View.GONE);

                eRat4.setVisibility(View.VISIBLE);
                fRat4.setVisibility(View.GONE);

                eRat5.setVisibility(View.VISIBLE);
                fRat5.setVisibility(View.GONE);
            }
        });

        fRat2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating=2;
                eRat1.setVisibility(View.GONE);
                fRat1.setVisibility(View.VISIBLE);

                eRat2.setVisibility(View.GONE);
                fRat2.setVisibility(View.VISIBLE);

                eRat3.setVisibility(View.VISIBLE);
                fRat3.setVisibility(View.GONE);

                eRat4.setVisibility(View.VISIBLE);
                fRat4.setVisibility(View.GONE);

                eRat5.setVisibility(View.VISIBLE);
                fRat5.setVisibility(View.GONE);
            }
        });

        fRat3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating=3;
                eRat1.setVisibility(View.GONE);
                fRat1.setVisibility(View.VISIBLE);

                eRat2.setVisibility(View.GONE);
                fRat2.setVisibility(View.VISIBLE);

                eRat3.setVisibility(View.GONE);
                fRat3.setVisibility(View.VISIBLE);

                eRat4.setVisibility(View.VISIBLE);
                fRat4.setVisibility(View.GONE);

                eRat5.setVisibility(View.VISIBLE);
                fRat5.setVisibility(View.GONE);
            }
        });

        fRat4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating=4;
                eRat1.setVisibility(View.GONE);
                fRat1.setVisibility(View.VISIBLE);

                eRat2.setVisibility(View.GONE);
                fRat2.setVisibility(View.VISIBLE);

                eRat3.setVisibility(View.GONE);
                fRat3.setVisibility(View.VISIBLE);

                eRat4.setVisibility(View.GONE);
                fRat4.setVisibility(View.VISIBLE);

                eRat5.setVisibility(View.VISIBLE);
                fRat5.setVisibility(View.GONE);
            }
        });

        fRat5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating=5;
                eRat1.setVisibility(View.GONE);
                fRat1.setVisibility(View.VISIBLE);

                eRat2.setVisibility(View.GONE);
                fRat2.setVisibility(View.VISIBLE);

                eRat3.setVisibility(View.GONE);
                fRat3.setVisibility(View.VISIBLE);

                eRat4.setVisibility(View.GONE);
                fRat4.setVisibility(View.VISIBLE);

                eRat5.setVisibility(View.GONE);
                fRat5.setVisibility(View.VISIBLE);
            }
        });

    }

    private void setUpOnClickListeners() {
        callCustomerCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkPermissions()) {
                    requestPermissions();
                } else {
                    makeACall();
                }

            }
        });

        setUpRatingListeners();


        orderCancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailsActivity.this);

                builder.setTitle("Confirm");
                builder.setMessage("Are you sure you want to cancel this order ?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        cancelOrder();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);

                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        //TODO
        ratingSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = ProgressDialog.show(OrderDetailsActivity.this, "",
                        "Sharing your feedback. Please wait...", true);

                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl("http://192.168.0.4:8080/")
                        .addConverterFactory(GsonConverterFactory.create());
                Retrofit retrofit = builder.build();

                RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
                //Call<Integer> call = retroFitNetworkClient.rateDeliveryBoy(order.getDeliveryBoyID(), rating);
                Call<String> call = retroFitNetworkClient.rateDeliveryBoy(4, rating);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if (response.body() != null) {
                            if(response.body().toString().contains("true")){
                                dialog.cancel();
                                Toast.makeText(OrderDetailsActivity.this, " Thank you for your feedback.", Toast.LENGTH_LONG).show();

                            }
                            else{
                                dialog.cancel();
                                Toast.makeText(OrderDetailsActivity.this, " Failed to share feedback. Please try again.", Toast.LENGTH_LONG).show();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        dialog.cancel();
                        Toast.makeText(OrderDetailsActivity.this, " Failed to share feedback. Please try again.", Toast.LENGTH_LONG).show();

                    }
                });
            }
        });
    }

    private void cancelOrder() {

        dialog = ProgressDialog.show(OrderDetailsActivity.this, "",
                "Cancelling your order. Please wait...", true);



        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://18.220.28.118:80/")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);

        Call<String> call = retroFitNetworkClient.cancelOrder(order.getOrderID());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body() != null) {

                    if (response.body().toString().contains("true")) {
                        dialog.cancel();
                        Toast.makeText(OrderDetailsActivity.this, " Order successfully cancelled.", Toast.LENGTH_LONG).show();
                        final Intent intent = new Intent();
                        order.setIsCancelled(1);

                        intent.putExtra("order", order);
                        intent.setClass(OrderDetailsActivity.this, OrderDetailsActivity.class);
                        startActivity(intent);
                    }
                }
                else {
                    dialog.cancel();
                    Toast.makeText(OrderDetailsActivity.this, " Failed to cancel order. Please try again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dialog.cancel();
                Toast.makeText(OrderDetailsActivity.this, " Failed to cancel order. Please try again.", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                makeACall();

            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    private void makeACall() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "8867248261"));
        startActivity(intent);
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CALL_PHONE);

        if (shouldProvideRationale) {

            showSnackbar(R.string.call_permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startPhonePermissionRequest();
                        }
                    });
        } else {

            startPhonePermissionRequest();
        }
    }

    private void startPhonePermissionRequest() {
        ActivityCompat.requestPermissions(OrderDetailsActivity.this,
                new String[]{Manifest.permission.CALL_PHONE},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private void setUpRecyclerView(){

        orderItemsRV.setAdapter(new OrderDetailsRVAdapter(ordersDetailsList, this));
        orderItemsRV.addItemDecoration(new RVItemDecoration(this, LinearLayoutManager.VERTICAL, 500));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final Intent intent = new Intent();

        intent.setClass(OrderDetailsActivity.this, OrdersActivity.class);
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
    protected void onResume() {
        super.onResume();
        if(countDownTimer!=null)
            countDownTimer.cancel();
        startTimer();
        if(HomeScreenActivity.cansList==null || HomeScreenActivity.cansList.isEmpty() || HomeScreenActivity.locationName==null || HomeScreenActivity.locationName.isEmpty()){

            Intent intent  = new Intent();
            intent.setClass(OrderDetailsActivity.this, FirstActivity.class);
            startActivity(intent);
        }
    }

    private void startTimer() {
        setTimerValues();
        startCountDownTimer();

    }

    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

            }

            @Override
            public void onFinish() {

                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
            }

        }.start();
        countDownTimer.start();
    }


    private void setTimerValues() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String dateString = order.getOrderDate().substring(0,16);
        long diffMs = 0;
        try {

            Date date = formatter.parse(dateString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.HOUR_OF_DAY, 2);
            Date deliveryDate = cal.getTime();
            Date currDate=java.util.Calendar.getInstance().getTime();
            if(currDate.compareTo(deliveryDate)>=0 || order.getIsCancelled()==1){
                diffMs = 0;
            }
            else{
                diffMs = deliveryDate.getTime() - currDate.getTime();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeCountInMilliSeconds = diffMs;
    }
}
