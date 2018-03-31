package in.neer24.neer24.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;

import java.util.HashMap;

import in.neer24.neer24.Activities.CheckoutActivity;
import in.neer24.neer24.Activities.CouponActivity;
import in.neer24.neer24.Adapters.CheckoutLVAdapter;
import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.NormalCart;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CheckoutFragment extends android.app.Fragment {

    ListView checkoutListView;
    TextView noItemsInCart;
    private static TextView totalCheckoutFragmentTextView;
    static View view, footerView;
    static TextView applyCouponTV, applyCouponSubTextTV;
    static ImageView couponApplyArrowIV, couponCancelIconIV;
    static TextView  billOffersTV, billGrandTotalTV;
    static LinearLayout billOffersLL, couponLayout;
    static LinearLayout staticCouponLayout;
    static TextView billItemTotalTV;
    static TextView billDeliveryChargesDetailTV, billDeliveryChargesTV;
    public static double discountedAmount = 0, deliveryCharge = 0, toPay = 0, totalAmount=0;
    public static int freeCansAvailed = 0, isNightDelivery = 0;
    SharedPreferenceUtility sharedPreferenceUtility;

    public static int numOfFreeCans;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_checkout, container, false);
        footerView = inflater.inflate(R.layout.cart_bill_layout, container, false);
        sharedPreferenceUtility = new SharedPreferenceUtility(view.getContext());

        getFreeCansNum();
        instantiateViewObjects();
        setViewObjects();
        setUpOnClickListeners();
        updateBill();
        updateCouponLayout();

        return view;
    }


    private static void updateCouponLayout(){

        if(CheckoutActivity.isCouponApplied) {

            couponApplyArrowIV.setVisibility(View.GONE);
            couponCancelIconIV.setVisibility(View.VISIBLE);
            applyCouponTV.setText("NEERFREE");
            applyCouponSubTextTV.setText("Offer applied on the bill");
            applyCouponSubTextTV.setVisibility(View.VISIBLE);
        }

        else {
            couponApplyArrowIV.setVisibility(View.VISIBLE);
            couponCancelIconIV.setVisibility(View.GONE);
            applyCouponTV.setText("APPLY COUPON");
            applyCouponSubTextTV.setVisibility(View.GONE);
        }
    }

    private void getFreeCansNum() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://18.220.28.118:80")       //
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<Integer> call = retroFitNetworkClient.getFreeCanNumForUser(sharedPreferenceUtility.getCustomerEmailID());

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if (response.body() != null) {
                    numOfFreeCans = Integer.parseInt(response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {}
        });
    }

    private void instantiateViewObjects() {


        checkoutListView = (ListView) view.findViewById(R.id.checkoutFragmentListViews);
        noItemsInCart = (TextView) view.findViewById(R.id.noItemsInCart);
        totalCheckoutFragmentTextView = (TextView) view.findViewById(R.id.totalCheckoutFragmentTextView);
        couponLayout = (LinearLayout) view.findViewById(R.id.ll1);
        staticCouponLayout = couponLayout;
        couponApplyArrowIV = (ImageView) view.findViewById(R.id.couponApplyArrowIV);
        couponCancelIconIV = (ImageView) view.findViewById(R.id.couponCancelIconIV);
        applyCouponTV = (TextView) view.findViewById(R.id.applyCouponTV);
        applyCouponSubTextTV = (TextView) view.findViewById(R.id.applyCouponSubTextTV);
        billOffersLL = (LinearLayout) footerView.findViewById(R.id.billOffersLL);
        billItemTotalTV = (TextView) footerView.findViewById(R.id.bill_item_total_tv);
        billDeliveryChargesDetailTV = (TextView) footerView.findViewById(R.id.bill_delivery_charges_details_tv);
        billDeliveryChargesTV = (TextView) footerView.findViewById(R.id.bill_delivery_charges_tv);
        billOffersTV = (TextView) footerView.findViewById(R.id.bill_discount_tv);
        billGrandTotalTV = (TextView) footerView.findViewById(R.id.grand_total_tv);
    }

    private void setViewObjects() {

        CheckoutLVAdapter customAdapter = new CheckoutLVAdapter(getActivity(), noItemsInCart);
        checkoutListView.setAdapter(customAdapter);
        checkoutListView.addFooterView(footerView);
    }

    private void setUpOnClickListeners() {

            couponLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!CheckoutActivity.isCouponApplied) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity().getApplicationContext(), CouponActivity.class);
                    startActivity(intent);
                }
                else {
                    CheckoutActivity.isCouponApplied = false;
                    updateBill();
                    updateCouponLayout();
                }
            }
        });
    }

    public static void updateBill() {

        HashMap<Can, Integer> cart = NormalCart.getCartList();
        double price;
        double totalCost = 0;
        if (cart.size() == 0) {

            footerView.setVisibility(View.GONE);
            staticCouponLayout.setVisibility(View.GONE);
            totalCheckoutFragmentTextView.setText("To Pay    :    " + "\u20B9" + " " + 0);
            toPay = 0;

        } else {

            for (Can c : cart.keySet()) {

                price = c.getPrice();
                Integer quantity = cart.get(c);
                totalCost = totalCost + (price * quantity);
                if (c.getUserWantsNewCan() == 1) {
                    totalCost = totalCost + (c.getNewCanPrice() * quantity);
                }
            }
            footerView.setVisibility(View.VISIBLE);
            staticCouponLayout.setVisibility(View.VISIBLE);
            totalAmount=totalCost;
            billItemTotalTV.setText("\u20B9" + " " + String.valueOf(totalCost));

            if(getTime()){

                billDeliveryChargesDetailTV.setText("Off Time Delivery Charge \n(11 PM - 6 AM)");
                billDeliveryChargesTV.setText("\u20B9" + " " + "20");
                deliveryCharge = 20;
                isNightDelivery = 1;
            }
            else {
                billDeliveryChargesDetailTV.setText("Delivery Charge");
                billDeliveryChargesTV.setText("\u20B9" + " " + "0");
                deliveryCharge = 0;
            }

            if(CheckoutActivity.isCouponApplied) {

                billOffersLL.setVisibility(View.VISIBLE);

                int numNeerCans = 0;
                double neerCanPrice = 0;

                for (Can c: NormalCart.getCartList().keySet()) {
                    if(c.getCanID() == 402){
                        numNeerCans=NormalCart.getCartList().get(c);
                        neerCanPrice = c.getPrice();
                    }
                }

                if(numNeerCans!=0 && numOfFreeCans!=0) {

                    freeCansAvailed = (numNeerCans > numOfFreeCans) ? numOfFreeCans : numNeerCans;
                    discountedAmount = freeCansAvailed * neerCanPrice;

                }


            }

            else {
                billOffersLL.setVisibility(View.GONE);
                discountedAmount = 0;
            }

            billOffersTV.setText("- " + "\u20B9" + " " + String.valueOf(discountedAmount));
            toPay = totalCost - discountedAmount + deliveryCharge;
            billGrandTotalTV.setText("\u20B9" + " " + String.valueOf(toPay));
            totalCheckoutFragmentTextView.setText("To Pay    :    " + "\u20B9" + " " + String.valueOf(toPay));

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkoutListView.invalidate();
        updateBill();
        updateCouponLayout();
    }

    static boolean getTime() {

        DateTimeZone timeZone = DateTimeZone.forID( "Asia/Kolkata" );
        LocalTime now = LocalTime.now( timeZone ); // Adjust computer/JVM time zone's current time to desired time zone's current time.
        //LocalTime now = new LocalTime( "00:00:00" );

        LocalTime six = new LocalTime( "06:00:01" );
        LocalTime twelve = new LocalTime( "00:00:00" );
        LocalTime midnight = new LocalTime("23:59:59");
        LocalTime elevenPM = new LocalTime("22:59:59");

        boolean isBeforeSix = now.isBefore(six);
        boolean isAfterTwelve = now.isAfter(twelve);
        boolean isBeforeTwelve = now.isBefore(midnight);
        boolean isAfter11PM = now.isAfter(elevenPM);
        boolean is12AM = now.isEqual(twelve);

        return (isBeforeSix && isAfterTwelve) || (isBeforeTwelve && isAfter11PM) || is12AM;
    }

    public static double getToPay() {
        return toPay;
    }

    public static void setToPay(double toPay) {
        CheckoutFragment.toPay = toPay;
    }

    public static double getTotalAmount() {
        return totalAmount;
    }

    public static void setTotalAmount(double totalAmount) {
        CheckoutFragment.totalAmount = totalAmount;
    }

    public static double getDiscountedAmount() {
        return discountedAmount;
    }

    public static void setDiscountedAmount(double discountedAmount) {
        CheckoutFragment.discountedAmount = discountedAmount;
    }
}

