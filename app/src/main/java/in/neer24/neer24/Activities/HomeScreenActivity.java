package in.neer24.neer24.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import in.neer24.neer24.Adapters.CustomPagerAdapter;
import in.neer24.neer24.Adapters.HomeRVAdapter;
import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.Cart;
import in.neer24.neer24.R;

public class HomeScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static ArrayList<Can> cansList = new ArrayList<Can>();
    boolean isLoggedIn;
    int warehouseID;
    double currentLatitude, currentLongitude;
    static RecyclerView recyclerView;
    ViewPager viewPager;
    CustomPagerAdapter pagerAdapter;
    private static Button checkoutButton;
    private static TextView cartSummary;
    boolean isNew = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        checkoutButton=(Button)findViewById(R.id.dishActivityCheckoutButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreenActivity.this,CheckoutActivity.class);
                startActivity(intent);
            }
        });

        cartSummary=(TextView)findViewById(R.id.dishActivityCartSummaryTextView);
        cartSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreenActivity.this,CheckoutActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.rv);

        RecyclerView recyclerView = findViewById(R.id.rv);
        setUpRecyclerView(recyclerView);

        setUpViewPager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#ff0099cc"));
        setSupportActionBar(toolbar);

        setUpNavigationDrawer(toolbar);

    }

    public void setUpViewPager(){
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        pagerAdapter=new CustomPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
    }

    public void setUpRecyclerView(RecyclerView recyclerView){

        recyclerView.setAdapter(new HomeRVAdapter(cansList, this));
    }

    public void setUpNavigationDrawer(Toolbar toolbar){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_orders) {
            Intent intent = new Intent();
            intent.setClass(this,OrdersActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_schedule_delivery) {
            Intent intent = new Intent();
            intent.setClass(this,ScheduleDeliveryActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_disclaimer) {
            Intent intent = new Intent();
            intent.setClass(this,DisclaimerActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_share) {
            Intent intent = new Intent();
            intent.setClass(this,ShareActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_help) {
            Intent intent = new Intent();
            intent.setClass(this,HelpActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void showCartDetailsSummary(){


        HashMap<Can,Integer> cart= Cart.getCartList();
        int totalQuantity=0;
        double price=0;
        double totalCost=0;
        if(cart.size()==0){
            cartSummary.setVisibility(View.INVISIBLE);
            checkoutButton.setVisibility(View.INVISIBLE);
        }else {
            for (Can c : cart.keySet()) {
                price = c.getPrice();
                Integer value = cart.get(c);
                totalCost = totalCost + (price * value);
                totalQuantity += value;
            }

            String message = totalQuantity + "items in your cart" + "\n" + "Rs " + totalCost;
            cartSummary.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cart_icon, 0, 0, 0);
            cartSummary.setText(message);
            cartSummary.setGravity(Gravity.CENTER_VERTICAL);

            cartSummary.setVisibility(View.VISIBLE);
            checkoutButton.setVisibility(View.VISIBLE);


        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(!isNew) {
            recyclerView.swapAdapter(new HomeRVAdapter(cansList, this),false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isNew = false;

    }
}
