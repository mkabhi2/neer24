package in.neer24.neer24.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.neer24.neer24.Adapters.OrderDetailsRVAdapter;
import in.neer24.neer24.BuildConfig;
import in.neer24.neer24.CustomObjects.Customer;
import in.neer24.neer24.CustomObjects.CustomerAddress;
import in.neer24.neer24.CustomObjects.CustomerOrder;
import in.neer24.neer24.CustomObjects.OrderDetails;
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

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 35;

    TextView orderIDTV, orderDateTV, orderStatusTV, orderTypeTV, grandTotalTV, deliveryAddressTV, recurrencesLeftTV, paymentModeTV,
            offTimeTV, deliveryChargeTV, discountTV, numFreeCansTV;
    RecyclerView orderItemsRV;
    OrderTable order;
    ArrayList<CustomerOrder> ordersDetailsList = new ArrayList<CustomerOrder>();
    LinearLayout ratingLayout, deliveryBoyContact, recurrencesLeftLayout, callCustomerCare;
    String rupeeSymbol;
    LinearLayout couponLayout;
    ProgressDialog dialog;
    Button orderCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Bundle bundle = getIntent().getExtras();
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

    public void initialiseViewComponents(){

        orderItemsRV = (RecyclerView) findViewById(R.id.orderItemsRV);
        fetchOrderDetails();

        ratingLayout = (LinearLayout) findViewById(R.id.ratings_layout);
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

    }

    public void setUpViewComponents() {

        couponLayout.setVisibility(View.GONE);
        deliveryAddressTV.setVisibility(View.GONE);

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
        }
        if(order.getIsCancelled()==1){
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
        }

        paymentModeTV.setText("" + order.getPaymentMode());

        if(order.getIsNightDelivery()==1){
            offTimeTV.setVisibility(View.VISIBLE);
            deliveryChargeTV.setText(rupeeSymbol + " 20");
        }
        else {
            offTimeTV.setVisibility(View.GONE);
            deliveryChargeTV.setText(rupeeSymbol + " 0");
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

    public void fetchOrderDetails(){


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

    public void setUpOnClickListeners() {
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

        orderCancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                cancelOrder();

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
                            stratPhonePermissionRequest();
                        }
                    });
        } else {

            stratPhonePermissionRequest();
        }
    }

    private void stratPhonePermissionRequest() {
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

    public void setUpRecyclerView(){

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
        if(HomeScreenActivity.cansList==null || HomeScreenActivity.cansList.isEmpty() || HomeScreenActivity.locationName==null || HomeScreenActivity.locationName.isEmpty()){

            Intent intent  = new Intent();
            intent.setClass(OrderDetailsActivity.this, FirstActivity.class);
            startActivity(intent);
        }
    }
}
