package in.neer24.neer24.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.NormalCart;
import in.neer24.neer24.Fragments.CheckoutFragment;
import in.neer24.neer24.R;

public class CouponActivity extends AppCompatActivity {

    private RelativeLayout freeCanCouponRL;
    private RelativeLayout noCouponsFoundIV;
    private TextView numFreeCansTV, titleTV;
    private int numNeerCans = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        freeCanCouponRL = (RelativeLayout) findViewById(R.id.freeCanCouponRL);

        noCouponsFoundIV = (RelativeLayout) findViewById(R.id.noCouponsFoundIV);
        numFreeCansTV = (TextView) findViewById(R.id.numFreeCansTV);
        titleTV = (TextView) findViewById(R.id.titleTV);

        if(CheckoutFragment.numOfFreeCans < 2) {
            numFreeCansTV.setText("" + CheckoutFragment.numOfFreeCans + " free can left");
        }
        else {
            numFreeCansTV.setText("" + CheckoutFragment.numOfFreeCans + " free cans left");
        }

        for (Can c: NormalCart.getCartList().keySet()) {
            String hello = c.getName();
            if(c.getCanID()==402){
                numNeerCans=NormalCart.getCartList().get(c);
            }
        }

        if(CheckoutFragment.numOfFreeCans < 1 || numNeerCans == 0) {

            freeCanCouponRL.setVisibility(View.GONE);
            noCouponsFoundIV.setVisibility(View.VISIBLE);
            titleTV.setVisibility(View.GONE);
        }

        freeCanCouponRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setClass(CouponActivity.this, CheckoutActivity.class);
                intent.putExtra("isCouponApplied", true);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final Intent intent = new Intent();

        intent.setClass(CouponActivity.this, CheckoutActivity.class);
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
            intent.setClass(CouponActivity.this, FirstActivity.class);
            startActivity(intent);
        }
    }

}
