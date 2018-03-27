package in.neer24.neer24.Activities;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

public class PaymentModeActivity extends AppCompatActivity implements PaymentResultListener {

    SharedPreferenceUtility sharedPreferenceUtility;
    Button proceedButton;
    OrderTable orderTable = null;
    String orderType = "";
    String modeOfPayment="";

    RadioGroup parentRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_mode);
        sharedPreferenceUtility = new SharedPreferenceUtility(this);

        parentRadioButton = (RadioGroup) findViewById(R.id.radioButtonParent);

        parentRadioButton.clearCheck();


        parentRadioButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    if (rb.getText() == "Cash On Delivery") {
                        modeOfPayment="COD";
                    } else if (rb.getText() == "PAYTM on Delivery") {
                        modeOfPayment="POD";
                    } else if (rb.getText() == "Online Payment") {
                        modeOfPayment="ONLINEPAYMENT";
                    }

                }

            }
        });


        proceedButton = (Button) findViewById(R.id.proccedButton);
        Bundle bundle = getIntent().getExtras();
        orderType = bundle.getString("ORDERTYPE");

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modeOfPayment.equals("COD")){
                    createObjectForOrderTable("COD");
                    insertDataIntoOrderTable(orderTable);
                }else if(modeOfPayment.equals("POD")) {
                    createObjectForOrderTable("POD");
                    insertDataIntoOrderTable(orderTable);
                } else if (modeOfPayment.equals("ONLINEPAYMENT")) {
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
                        //insertIntoOrderDetailsTable(returnedOrderID);
                        //updateWarehouseCansTable(c.getCanID(),c.getWarehouseID());
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(PaymentModeActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setImage(R.drawable.logo);
        String toPay = Double.valueOf(CheckoutFragment.getToPay()).toString();
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
        try {
            orderTable.setAmountPaid(10);
            orderTable.setOrderPaymentID("hello");
            orderTable.setPaymentMode("Credit Card");
            insertDataIntoOrderTable(orderTable);
            Toast.makeText(PaymentModeActivity.this, "Payment is Successful", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //take to some error page
            Toast.makeText(PaymentModeActivity.this, "Payment is Successful", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.d("error", s);
        Toast.makeText(PaymentModeActivity.this, "Error", Toast.LENGTH_SHORT).show();
    }


    public void createObjectForOrderTable(String paymentModes) {
        String paymentID = null;
        double amountPaid = 0;

        Date oDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
        Date dTime = cal.getTime();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String deliveryTime = sdf1.format(dTime);

        int customerID = sharedPreferenceUtility.getCustomerID();
        int warehouseID = sharedPreferenceUtility.getWareHouseID();
        int deliveryBoyID = 0;
        double totalAmount = CheckoutFragment.getTotalAmount();
        String orderDate = sdf.format(oDate);

        int isNormalDelivery = (orderType.equals("NORMAL")?1:0);
        int isNightDelivery = (UtilityClass.checkIfOrderIsNightDeliveryOrNot(deliveryTime) == true ? 1 : 0);
        int isScheduleDelivery = (orderType.equals("SCHEDULED")?1:0);;
        int isRecurringDelivery = (orderType.equals("RECURRING")?1:0);
        int isOrdered = 1;
        int isDispatched = 0;
        int isDelivered = 0;
        int recurringOrderFrequency = 0;
        String recurringOrderEndDate = null;
        int freeCansInOrder = (int) CheckoutFragment.getDiscountedAmount() / 35;
        int customerAddressID = 1;
        double discountedAmount = CheckoutFragment.getDiscountedAmount();
        String couponCode = (CheckoutFragment.getDiscountedAmount() > 0 ? "NEER@24" : null);
        int deliveryLeft = 0;
        int isCancelled = 0;
        String refundID = null;
        int totalCansOrdered = 10;

        orderTable = new OrderTable(customerID, warehouseID, deliveryBoyID, paymentID, orderDate, deliveryTime, totalAmount, discountedAmount, amountPaid, paymentModes, couponCode, freeCansInOrder, customerAddressID, isNormalDelivery, isNightDelivery, isScheduleDelivery, isRecurringDelivery, sharedPreferenceUtility.getCustomerUniqueID(), isOrdered, isDispatched, isDelivered, isCancelled, recurringOrderEndDate, deliveryLeft, recurringOrderFrequency, totalCansOrdered);

    }

}
