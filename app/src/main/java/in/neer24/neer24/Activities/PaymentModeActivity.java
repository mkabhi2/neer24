package in.neer24.neer24.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

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

    SharedPreferenceUtility sharedPreferenceUtility;
    Button proceedButton;
    OrderTable orderTable = null;
    //String orderType = "";
    OrderTable order;
    String modeOfPayment="";
    LinearLayout payOnDeliveryLL, onlinePaymentLL;
    ImageView selectCirclePOD, selectCheckBoxPOD, selectCircleOP, selectCheckBoxOP;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_mode);
        sharedPreferenceUtility = new SharedPreferenceUtility(this);

        initialiseViewObjects();
        setUpOnClickListeners();


        Bundle bundle = getIntent().getExtras();
        order = bundle.getParcelable("order");


    }

    public void initialiseViewObjects() {
        proceedButton = (Button) findViewById(R.id.proccedButton);
        payOnDeliveryLL = (LinearLayout) findViewById(R.id.payOnDeliveryLL);
        onlinePaymentLL = (LinearLayout) findViewById(R.id.onlinePaymentLL);
        selectCheckBoxOP = (ImageView) findViewById(R.id.selectCheckBoxOP);
        selectCheckBoxPOD = (ImageView) findViewById(R.id.selectCheckBoxPOD);
        selectCircleOP = (ImageView) findViewById(R.id.selectCircleOP);
        selectCirclePOD = (ImageView) findViewById(R.id.selectCirclePOD);

    }

    public void setUpOnClickListeners() {

        payOnDeliveryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modeOfPayment = "POD";
                selectCirclePOD.setVisibility(View.GONE);
                selectCheckBoxPOD.setVisibility(View.VISIBLE);
                selectCircleOP.setVisibility(View.VISIBLE);
                selectCheckBoxOP.setVisibility(View.GONE);
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
            }
        });

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modeOfPayment.equals("POD")){

                    order.setPaymentMode("COD");
                    dialog = ProgressDialog.show(PaymentModeActivity.this, "",
                            "Placing order. Please wait...", true);
                    insertDataIntoOrderTable(order);
                }
                else if (modeOfPayment.equals("ONLINEPAYMENT")) {
                    startPayment();
                }
            }
        });
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
                    String returnedOrderID = response.body().toString();
                    if (!returnedOrderID.equals("0")) {
                        Toast.makeText(PaymentModeActivity.this, "Order Succesfull", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
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
        String toPay = Double.valueOf(order.getAmountPaid()*100).toString();
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
        insertDataIntoOrderTable(order);
    }

    @Override
    public void onPaymentError(int i, String s) {

        Log.d("error", s);
        Toast.makeText(PaymentModeActivity.this, "Error : "+s, Toast.LENGTH_LONG).show();
    }

}
