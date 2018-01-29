package in.neer24.neer24.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import in.neer24.neer24.Adapters.CheckoutLVAdapter;
import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.NormalCart;
import in.neer24.neer24.CustomObjects.Offer;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CheckoutFragment extends android.app.Fragment {

    ListView chekoutListView;
    TextView noItemsInCart;
    private static TextView totalCheckoutFragmentTextView;
    private EditText applyCouponEditTextCheckoutActivity;
    private Button applyCouponButtonCheckoutActivity;
    private static boolean isOfferApplicable = false;
    private double discountedAmount;
    SharedPreferenceUtility sharedPreferenceUtility;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_checkout, container, false);
        sharedPreferenceUtility = new SharedPreferenceUtility(view.getContext());
        chekoutListView = (ListView) view.findViewById(R.id.checkoutFragmentListViews);
        noItemsInCart = (TextView) view.findViewById(R.id.noItemsInCart);
        applyCouponEditTextCheckoutActivity = (EditText) view.findViewById(R.id.applyCouponEditTextCheckoutActivity);
        applyCouponButtonCheckoutActivity = (Button) view.findViewById(R.id.applyCouponButtonCheckoutActivity);
        CheckoutLVAdapter customAdapter = new CheckoutLVAdapter(getActivity(), noItemsInCart);
        chekoutListView.setAdapter(customAdapter);

        totalCheckoutFragmentTextView = (TextView) view.findViewById(R.id.totalCheckoutFragmentTextView);

        updateTotalValueOfCart();


        applyCouponButtonCheckoutActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOfferApplicable = false;
                String coupon = applyCouponEditTextCheckoutActivity.getText().toString();
                if (coupon.equals("FREE")) {
                    checkIfUserIsEligibleForFreeCanOrNot(sharedPreferenceUtility.getCustomerEmailID());
                } else {
                    verifyIfCouponIsValidFromServer(coupon);
                }
            }
        });


        applyCouponEditTextCheckoutActivity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                isOfferApplicable = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isOfferApplicable = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
                isOfferApplicable = false;
                if (applyCouponEditTextCheckoutActivity.getText().toString().length() > 2) {
                    applyCouponButtonCheckoutActivity.setEnabled(true);
                } else {
                    applyCouponButtonCheckoutActivity.setEnabled(false);
                }
            }
        });

        return view;
    }

    public void checkIfUserIsEligibleForFreeCanOrNot(String emailID) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://192.168.0.2:8080/")  //.baseUrl("http://18.220.28.118:8080/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<String> call = retroFitNetworkClient.checkIfUserIsEligibleForFreeCanOrNot(emailID);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body().contains("true")) {
                    isOfferApplicable = true;
                    setDiscountAmount(35);
                } else {
                    isOfferApplicable = false;
                    String error = "Sorry Not Eligible for discount";
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    public void verifyIfCouponIsValidFromServer(String coupon) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://192.168.0.2:8080/")  //.baseUrl("http://18.220.28.118:8080/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<Offer> call = retroFitNetworkClient.verifyIfCouponIsValidFromServer(coupon);

        call.enqueue(new Callback<Offer>() {
            @Override
            public void onResponse(Call<Offer> call, Response<Offer> response) {
                Offer offer = response.body();
                if (offer.getOutputValue().contains("true")) {
                    calculateDiscountAsPerCouponCode(offer);
                } else {
                    String temp = "error";
                }
            }

            @Override
            public void onFailure(Call<Offer> call, Throwable t) {
                String temp = "error";
            }
        });
    }

    public void setDiscountAmount(double discountAmount) {
        discountedAmount = discountAmount;
    }

    public void calculateDiscountAsPerCouponCode(Offer offer) {
        try {
            double maximumDiscount = offer.getMaximumDiscount();
            double percentageDiscount = offer.getPercentageDiscount();
            String OfferVaildFrom = offer.getOfferVaildFrom();
            String OfferVaildTo = offer.getOfferVaildTo();
            double discountByPercentage;

            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1 = df1.parse(OfferVaildFrom);
            Date d2 = df1.parse(OfferVaildTo);


            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time2 = getCurrentTimeStamp();
            Date d3 = df2.parse(time2);


            if (d3.compareTo(d1) == 0 && d3.compareTo(d2) == 0) {
                isOfferApplicable = true;
                double totalCartValue = getTotalCartValue();
                discountByPercentage = (percentageDiscount * totalCartValue) / 100;
                if (discountByPercentage < maximumDiscount) {
                    setDiscountAmount(discountByPercentage);
                } else {
                    setDiscountAmount(maximumDiscount);
                }
                return;
            } else {
                isOfferApplicable = false;
                return;
            }
        } catch (Exception e) {

        }
    }

    private static boolean getIsOfferApplicable(){
        return isOfferApplicable;
    }

    public double getTotalCartValue() {
        return 0.0;
    }

    public String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

    public static void updateTotalValueOfCart() {
        HashMap<Can, Integer> cart = NormalCart.getCartList();
        double price = 0;
        double totalCost = 0;
        if (cart.size() == 0) {
            totalCheckoutFragmentTextView.setText("\u20B9" + " 0");
        } else {
            for (Can c : cart.keySet()) {
                price = c.getPrice();
                Integer quantity = cart.get(c);
                totalCost = totalCost + (price * quantity);
                if (c.getUserWantsNewCan() == 1) {
                    totalCost = totalCost + (c.getNewCanPrice() * quantity);
                }
            }
            totalCheckoutFragmentTextView.setText("To Pay    :    " + "\u20B9" + " " + String.valueOf(totalCost));

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        chekoutListView.invalidate();
    }
}

