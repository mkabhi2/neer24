package in.neer24.neer24.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.NormalCart;
import in.neer24.neer24.CustomObjects.OrderDetails;
import in.neer24.neer24.CustomObjects.OrderTable;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentModeActivity extends AppCompatActivity implements PaymentResultListener {

    private SharedPreferenceUtility sharedPreferenceUtility;
    private Button proceedButton;
    private OrderTable order;
    private OrderDetails orderDetails[];
    private String modeOfPayment = "";
    private LinearLayout payOnDeliveryLL, onlinePaymentLL;
    private ImageView selectCirclePOD, selectCheckBoxPOD, selectCircleOP, selectCheckBoxOP;
    private ProgressDialog dialog;
    private Toolbar toolbar;
    private String parentClassName;
    private HorizontalScrollView codHSV, onlineHSV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_mode);
        sharedPreferenceUtility = new SharedPreferenceUtility(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialiseViewObjects();
        setUpOnClickListeners();


        Bundle bundle = getIntent().getExtras();

        order = bundle.getParcelable("order");
        parentClassName = bundle.getString("parentClassName");

        ArrayList<OrderDetails> orderDetail = bundle.getParcelableArrayList("orderContents");
        orderDetails = orderDetail.toArray(new OrderDetails[orderDetail.size()]);
        order.setOrderContents(orderDetails);


    }

    private void initialiseViewObjects() {
        proceedButton = (Button) findViewById(R.id.proccedButton);
        payOnDeliveryLL = (LinearLayout) findViewById(R.id.payOnDeliveryLL);
        onlinePaymentLL = (LinearLayout) findViewById(R.id.onlinePaymentLL);
        selectCheckBoxOP = (ImageView) findViewById(R.id.selectCheckBoxOP);
        selectCheckBoxPOD = (ImageView) findViewById(R.id.selectCheckBoxPOD);
        selectCircleOP = (ImageView) findViewById(R.id.selectCircleOP);
        selectCirclePOD = (ImageView) findViewById(R.id.selectCirclePOD);
        codHSV = (HorizontalScrollView) findViewById(R.id.codHSV);
        onlineHSV = (HorizontalScrollView) findViewById(R.id.onlineHSV);

    }

    private void setUpOnClickListeners() {

        payOnDeliveryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modeOfPayment = "POD";
                selectCirclePOD.setVisibility(View.GONE);
                selectCheckBoxPOD.setVisibility(View.VISIBLE);
                selectCircleOP.setVisibility(View.VISIBLE);
                selectCheckBoxOP.setVisibility(View.GONE);
                payOnDeliveryLL.setBackgroundColor(getResources().getColor(R.color.Gold));
                onlinePaymentLL.setBackground(getResources().getDrawable(R.drawable.home_screen_icons_shadow));
            }
        });

        onlinePaymentLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modeOfPayment = "ONLINEPAYMENT";
                selectCirclePOD.setVisibility(View.VISIBLE);
                selectCheckBoxPOD.setVisibility(View.GONE);
                selectCircleOP.setVisibility(View.GONE);
                selectCheckBoxOP.setVisibility(View.VISIBLE);
                onlinePaymentLL.setBackgroundColor(getResources().getColor(R.color.Gold));
                payOnDeliveryLL.setBackground(getResources().getDrawable(R.drawable.home_screen_icons_shadow));
            }
        });

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modeOfPayment.equals("POD")) {
                    order.setPaymentMode("COD");
                    dialog = ProgressDialog.show(PaymentModeActivity.this, "",
                            "Placing order. Please wait...", true);
                    insertDataIntoOrderTable(order);
                } else if (modeOfPayment.equals("ONLINEPAYMENT")) {
                    order.setPaymentMode("ONLINEPAYMENT");
                    startPayment();
                }
            }
        });

        codHSV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                payOnDeliveryLL.performClick();
                return false;
            }
        });

        onlineHSV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onlinePaymentLL.performClick();
                return false;
            }
        });
    }

    int count = 1;

    private void insertDataIntoOrderTable(final OrderTable orderTable) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://18.220.28.118:80/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);

        Call<String> call = retroFitNetworkClient.insertIntoOrderTable(orderTable);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (response.body() != null) {
                    String returnedOrderID = response.body().toString();
                    if (!returnedOrderID.equals("0")) {
                        Toast.makeText(PaymentModeActivity.this, "Order Successful", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                        NormalCart.getCartList().clear();
                        final Intent intent = new Intent();
                        orderTable.setOrderID(Integer.parseInt(returnedOrderID));
                        intent.putExtra("order", orderTable);

                        intent.setClass(PaymentModeActivity.this, OrderDetailsActivity.class);
                        startActivity(intent);
                    } else {
                        if (count > 20) {
                            count=1;
                            Toast.makeText(PaymentModeActivity.this, "Unable to Place order at this time", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        } else {
                            count += 1;
                            insertDataIntoOrderTable(orderTable);
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(PaymentModeActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
    }

    private void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setImage(R.drawable.logo);
        String toPay = Double.valueOf(order.getAmountPaid() * 100).toString();
        final Activity activity = this;
        try {
            JSONObject options = new JSONObject();
            options.put("key", "Neer24");
            options.put("amount", toPay);
            options.put("name", "Neer24");
            options.put("description", "Order #123456");
            options.put("currency", "INR");

            JSONObject preFill = new JSONObject();
            preFill.put("email", (sharedPreferenceUtility.getCustomerEmailID() != null ? sharedPreferenceUtility.getCustomerEmailID() : " "));
            preFill.put("contact", (sharedPreferenceUtility.getCustomerMobileNumber() != null ? sharedPreferenceUtility.getCustomerMobileNumber() : " "));
            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("error", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {

        //TODO UPDATE ORDER OBJECT
        dialog = ProgressDialog.show(PaymentModeActivity.this, "",
                "Placing order. Please wait...", true);
        order.setOrderPaymentID(s);
        insertDataIntoOrderTable(order);
    }

    @Override
    public void onPaymentError(int i, String s) {

        Log.d("error", s);
        Toast.makeText(PaymentModeActivity.this, "Error : " + s, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final Intent intent = new Intent();

        if (parentClassName.equals("CheckoutActivity")) {
            intent.setClass(PaymentModeActivity.this, CheckoutActivity.class);
        } else {
            for (Can can : HomeScreenActivity.cansList) {
                if (can.getCanID() == orderDetails[0].getCanID()) {
                    intent.putExtra("item", can);
                    break;
                }
            }
            if (parentClassName.equals("SetOneTimeScheduleActivity")) {
                intent.setClass(PaymentModeActivity.this, SetOneTimeScheduleActivity.class);
            } else {
                intent.setClass(PaymentModeActivity.this, SetRecurringScheduleActivity.class);
            }
        }

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
        if (HomeScreenActivity.cansList == null || HomeScreenActivity.cansList.isEmpty() || HomeScreenActivity.locationName == null || HomeScreenActivity.locationName.isEmpty()) {

            Intent intent = new Intent();
            intent.setClass(PaymentModeActivity.this, FirstActivity.class);
            startActivity(intent);
        }
    }

}
