package in.neer24.neer24.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import in.neer24.neer24.Adapters.OrderDetailsRVAdapter;
import in.neer24.neer24.CustomObjects.OrderDetails;
import in.neer24.neer24.CustomObjects.OrderTable;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderDetailsActivity extends AppCompatActivity {

    TextView orderIDTV, orderDateTV, orderStatusTV, orderTypeTV, grandTotalTV, deliveryAddressTV, recurrencesLeftTV, paymentModeTV,
            offTimeTV, deliveryChargeTV, discountTV, numFreeCansTV;
    RecyclerView orderItemsRV;
    OrderTable order;
    OrderDetails orderDetails;
    LinearLayout ratingLayout, deliveryBoyContact, recurrencesLeftLayout;
    String rupeeSymbol;
    RelativeLayout couponLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Bundle bundle = getIntent().getExtras();
        order = bundle.getParcelable("order");

        rupeeSymbol = getResources().getString(R.string.Rs);

        initialiseViewComponents();
        setUpViewComponents();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initialiseViewComponents(){

        orderItemsRV = (RecyclerView) findViewById(R.id.orderItemsRV);
        setUpOrderItemsRV();

        ratingLayout = (LinearLayout) findViewById(R.id.ratings_layout);
        deliveryBoyContact = (LinearLayout) findViewById(R.id.delivery_boy_contact);
        recurrencesLeftLayout = (LinearLayout) findViewById(R.id.recurrencesLeftLayout);
        recurrencesLeftTV = (TextView) findViewById(R.id.recurrencesLeft);
        paymentModeTV = (TextView) findViewById(R.id.paymentMode);
        offTimeTV = (TextView) findViewById(R.id.offTimeTV);
        deliveryChargeTV = (TextView) findViewById(R.id.deliveryCharge);
        discountTV = (TextView) findViewById(R.id.discount);
        couponLayout = (RelativeLayout) findViewById(R.id.couponLayout);
        numFreeCansTV = (TextView) findViewById(R.id.numFreeCansTV);

        orderIDTV = (TextView) findViewById(R.id.orderID);


        orderDateTV = (TextView) findViewById(R.id.orderDate);


        orderStatusTV = (TextView) findViewById(R.id.orderStatus);

        orderTypeTV = (TextView) findViewById(R.id.orderType);

        grandTotalTV = (TextView) findViewById(R.id.grandTotalTV);
        grandTotalTV.setText("" + order.getAmountPaid());

        deliveryAddressTV = (TextView) findViewById(R.id.deliveryAddress);

    }

    public void setUpViewComponents() {

        couponLayout.setVisibility(View.GONE);

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
        String orderStatus = "Order Placed";

        if(order.getIsDispatched()==1){
            orderStatus = "Dispatched";
        }
        if(order.getIsDelivered()==1){
            orderStatus = "Delivered";
        }
        if(order.getIsCancelled()==1){
            orderStatus = "Cancelled";
            orderStatusTV.setTextColor(getResources().getColor(R.color.Red));
        }

        orderStatusTV.setText(orderStatus);

        orderTypeTV.setText("" + "Normal Delivery");
        recurrencesLeftLayout.setVisibility(View.GONE);

        if(order.getIsScheduleDelivery()==1) {
            orderTypeTV.setText("" + "Scheduled Delivery");
        }
        if(order.getIsRecurringDelivery()==1) {
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
                numFreeCansTV.setText("" + order.getNumberOfFreeCansAvailed() + " Neer24 cans free in this order");
            }

        }


    }

    public void setUpOrderItemsRV(){


        final ProgressBar progressBar;
        progressBar = (ProgressBar) findViewById(R.id.orderDetailsActivityProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        Retrofit.Builder builder = new Retrofit.Builder()
                //.baseUrl("http://192.168.0.2:8080")
                .baseUrl("http://18.220.28.118:80")       //
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<OrderDetails> call = retroFitNetworkClient.getOrderDetailsForOrderID(order.getOrderID());

        call.enqueue(new Callback<OrderDetails>() {
            @Override
            public void onResponse(Call<OrderDetails> call, Response<OrderDetails> response) {

                orderDetails = (OrderDetails) response.body();
                orderItemsRV.setAdapter(new OrderDetailsRVAdapter(orderDetails.getCansList(), orderDetails.getCanQuantity(), OrderDetailsActivity.this));
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<OrderDetails> call, Throwable t) {
                Toast.makeText(OrderDetailsActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }

        });
    }

}
