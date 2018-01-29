package in.neer24.neer24.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import in.neer24.neer24.Adapters.OrderDetailsRVAdapter;
import in.neer24.neer24.CustomObjects.CustomerOrder;
import in.neer24.neer24.CustomObjects.OrderDetails;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderDetailsActivity extends AppCompatActivity {

    TextView orderIDTV, orderDateTV, orderStatusTV, orderTypeTV, grandTotalTV, deliveryAddressTV;
    RecyclerView orderItemsRV;
    CustomerOrder order;
    OrderDetails orderDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Bundle bundle = getIntent().getExtras();
        order = bundle.getParcelable("order");

        initialiseViewComponents();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initialiseViewComponents(){

        orderItemsRV = (RecyclerView) findViewById(R.id.orderItemsRV);
        setUpOrderItemsRV();

        orderIDTV = (TextView) findViewById(R.id.orderID);
        orderIDTV.setText("" + order.getOrderID());

        orderDateTV = (TextView) findViewById(R.id.orderDate);
        orderDateTV.setText("" + order.getOrderDate().toString().substring(0,16));

        orderStatusTV = (TextView) findViewById(R.id.orderStatus);

        orderTypeTV = (TextView) findViewById(R.id.orderType);

        grandTotalTV = (TextView) findViewById(R.id.grandTotalTV);
        grandTotalTV.setText("" + order.getCanPrice());

        deliveryAddressTV = (TextView) findViewById(R.id.deliveryAddress);

    }

    public void setUpOrderItemsRV(){


        final ProgressBar progressBar;
        progressBar = (ProgressBar) findViewById(R.id.orderDetailsActivityProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://192.168.0.2:8080")         //.baseUrl("http://18.220.28.118:8080")       //
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
