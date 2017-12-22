package in.neer24.neer24.Activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.Cart;
import in.neer24.neer24.CustomObjects.OrderDetails;
import in.neer24.neer24.CustomObjects.OrderTable;
import in.neer24.neer24.Fragments.CheckoutFragment;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckoutActivity extends AppCompatActivity {

    private TextView cartSummaryTextView;
    private Button proceedToPayButton;

    String returnedOrderID="";
    String isOrderDetailsInsertionSuccesFull;
    // private TextView checkoutActivityTotalCartValueTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        cartSummaryTextView = (TextView) findViewById(R.id.checkoutActivityCartSummaryTextView);
        //checkoutActivityTotalCartValueTextView=(TextView)findViewById(R.id.checkoutActivityTotalCartValueTextView);
        cartSummaryTextView.setGravity(Gravity.CENTER_VERTICAL);

        proceedToPayButton = (Button) findViewById(R.id.proceedToPayCheckoutActivity);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CheckoutFragment checkoutFragment = new CheckoutFragment();
        fragmentTransaction.add(R.id.cartFragmentContainer, checkoutFragment, "checkoutFragment");
        fragmentTransaction.commit();

        //updateTotalValueOfCart();


        proceedToPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap.Entry<Can, Integer> entry = Cart.getCartList().entrySet().iterator().next();
                Can can = entry.getKey();
                insertDataIntoOrderTable(createObjectForOrderTable(can));
            }
        });
    }


    public ArrayList<OrderDetails> createObjectForOrderDetails(String orderID){
        ArrayList<OrderDetails> al=new ArrayList<OrderDetails>();
        HashMap<Can,Integer> cans=Cart.getCartList();
        for (Can can:cans.keySet()) {
            int canID=can.getCanID();
            int isNew=0;
            int isNewDespenser=0;
            int orderIDs=Integer.parseInt(orderID);
            int quantity= cans.get(can);
            OrderDetails orderDetails=new OrderDetails(canID,isNew,isNewDespenser,orderIDs,quantity);
            al.add(orderDetails);
        }
        return al;
    }
    public OrderTable createObjectForOrderTable(Can can){
        SharedPreferenceUtility sharedPreferenceUtility=sharedPreferenceUtility=new SharedPreferenceUtility(this);;

        Date oDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
        Date dTime=cal.getTime();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int customerID=sharedPreferenceUtility.getCustomerID();
        int warehouseID=can.getWarehouseID();
        int deliveryBoyID=1;
        double totalAmount=HomeScreenActivity.calculateTotalCostOfCart();

        String orderDate= sdf.format(oDate);
        String deliveryTime=sdf1.format(dTime);

        int orderPaymentID=1;
        int isNormalDelivery=1;
        int isNightDelivery=0;
        int isScheduleDelivery=0;
        int isRecurringDelivery=0;
        OrderTable orderTable=new OrderTable(customerID,warehouseID,deliveryBoyID,totalAmount,orderDate,deliveryTime,orderPaymentID,isNormalDelivery,isNightDelivery,isScheduleDelivery,isRecurringDelivery);
        return orderTable;
    }

    public void insertDataIntoOrderTable(OrderTable orderTable){

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://18.220.28.118:8080/")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);

        Call<String> call = retroFitNetworkClient.insertIntoOrderTable(orderTable);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(response.body()!=null){
                    returnedOrderID = response.body().toString();
                    if(!returnedOrderID.equals("0")){
                        insertIntoOrderDetailsTable(returnedOrderID);
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

    public void insertIntoOrderDetailsTable(String returnedOrderID){
        ArrayList<OrderDetails> orderDetails=createObjectForOrderDetails(returnedOrderID);
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://192.168.0.4:8034")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);

        Call<String> call = retroFitNetworkClient.insertIntoOrderDetailsTable(orderDetails);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    isOrderDetailsInsertionSuccesFull = response.body().toString();
                    if(isOrderDetailsInsertionSuccesFull.contains("true")){
                        Toast.makeText(CheckoutActivity.this,"Insertion Succesfull Order Details",Toast.LENGTH_SHORT);
                    }else {
                        Toast.makeText(CheckoutActivity.this,"Data Completly not inserter into Order Details",Toast.LENGTH_SHORT);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this,"Insertion Failed Due to Server Error",Toast.LENGTH_SHORT);
            }
        });
    }


    public void updateWarehouseCansTable(int canID, int warehouseID){
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://192.168.0.4:8034")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);

        Call<String> call = retroFitNetworkClient.updateWarehouseCanTable(canID,warehouseID);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body()!=null){
                    returnedOrderID = response.body().toString();
                    if(response.body().toString().contains("true")){
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



//    public void updateTotalValueOfCart() {
//        HashMap<Dish, Integer> cart = Cart.getCartList();
//        double price = 0;
//        double totalCost = 0;
//        if (cart.size() == 0) {
//            checkoutActivityTotalCartValueTextView.setText("Total Amount" + "\t" + "0");
//        } else {
//            for (Dish d : cart.keySet()) {
//                price = Double.parseDouble(d.getDishPrice());
//                Integer value = cart.get(d);
//                totalCost = totalCost + (price * value);
//            }
//            checkoutActivityTotalCartValueTextView.setText("Total Amount" + "\t" + String.valueOf(totalCost));
//
//        }
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HomeScreenActivity.showCartDetailsSummary();
    }
}

