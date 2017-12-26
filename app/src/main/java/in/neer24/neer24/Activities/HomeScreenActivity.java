package in.neer24.neer24.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import in.neer24.neer24.Adapters.CustomPagerAdapter;
import in.neer24.neer24.Adapters.HomeRVAdapter;
import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.NormalCart;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RVItemDecoration;

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
        checkoutButton.setBackgroundColor(getResources().getColor(R.color.app_color));
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreenActivity.this,CheckoutActivity.class);
                startActivity(intent);
            }
        });

        cartSummary=(TextView)findViewById(R.id.dishActivityCartSummaryTextView);
        cartSummary.setBackgroundColor(getResources().getColor(R.color.app_color));
        cartSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreenActivity.this,CheckoutActivity.class);
                startActivity(intent);
            }
        });

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            cansList = savedInstanceState.getParcelableArrayList("savedCansList");
        }

        recyclerView = findViewById(R.id.rv);

        RecyclerView recyclerView = findViewById(R.id.rv);
        setUpRecyclerView(recyclerView);

        setUpViewPager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));
        setSupportActionBar(toolbar);

        setUpNavigationDrawer(toolbar);

    }

    public void setUpViewPager(){
        viewPager=(ViewPager)findViewById(R.id.viewPager);
        pagerAdapter=new CustomPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        Timer timer;
        final long DELAY_MS = 2000;//delay in milliseconds before task is to be executed
        final long PERIOD_MS = 4000;

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            int currentPage = 0;
            public void run() {
                if (currentPage == pagerAdapter.getCount()-1) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer .schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

    public void setUpRecyclerView(RecyclerView recyclerView){

        recyclerView.setAdapter(new HomeRVAdapter(cansList, this));
        recyclerView.addItemDecoration(new RVItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
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


        HashMap<Can,Integer> cart= NormalCart.getCartList();
        int totalQuantity=0;
        double price=0;
        double totalCost=0;
        if(cart.size()==0){
            cartSummary.setVisibility(View.GONE);
            checkoutButton.setVisibility(View.GONE);
        }else {
            for (Can c : cart.keySet()) {
                price = c.getPrice();
                Integer quantity = cart.get(c);
                totalCost = totalCost + (price * quantity);
                if(c.getUserWantsNewCan() == 1){
                    totalCost = totalCost + (c.getNewCanPrice() * quantity);
                }
                totalQuantity += quantity;
            }

            String message = totalQuantity + "items in your cart" + "\n" + "Rs " + totalCost;
            cartSummary.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cart_icon, 0, 0, 0);
            cartSummary.setText(message);
            cartSummary.setGravity(Gravity.CENTER_VERTICAL);

            cartSummary.setVisibility(View.VISIBLE);
            checkoutButton.setVisibility(View.VISIBLE);


        }
    }

    public static double  calculateTotalCostOfCart(){
        HashMap<Can,Integer> cart= NormalCart.getCartList();
        int totalQuantity=0;
        double price=0;
        double totalCost=0;
        if(cart.size()==0){
            return 0.0;
        }else {
            for (Can c : cart.keySet()) {
                price = c.getPrice();
                Integer value = cart.get(c);
                totalCost = totalCost + (price * value);
            }
            return totalCost;
        }
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putParcelableArrayList("savedCansList",cansList);
        super.onSaveInstanceState(savedInstanceState);
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
