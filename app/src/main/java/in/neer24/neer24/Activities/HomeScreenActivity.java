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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import in.neer24.neer24.Adapters.CustomPagerAdapter;
import in.neer24.neer24.Adapters.HomeRVAdapter;
import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.CustomerAddress;
import in.neer24.neer24.CustomObjects.NormalCart;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RVItemDecoration;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ArrayList<CustomerAddress> addressList=new ArrayList<CustomerAddress>();
    static ArrayList<Can> cansList = new ArrayList<Can>();
    boolean isLoggedIn;
    int warehouseID;
    double currentLatitude, currentLongitude;
    static RecyclerView recyclerView;
    ViewPager viewPager;
    CustomPagerAdapter pagerAdapter;
    private static Button checkoutButton;
    private static LinearLayout checkoutButtonLinearLayout;
    private static TextView cartSummary;
    boolean isNew = true;

    private TextView customerEmailTextViewNavigationHeader;
    private TextView customerNameTextViewNavigationHeader;
    private NavigationView navigationView;
    SharedPreferenceUtility sharedPreferenceUtility;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        sharedPreferenceUtility=new SharedPreferenceUtility(this);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        customerNameTextViewNavigationHeader = (TextView)headerview.findViewById(R.id.customerNameTextViewNavigationHeader);
        customerEmailTextViewNavigationHeader=(TextView)headerview.findViewById(R.id.customerEmailTextViewNavigationHeader);
        checkoutButtonLinearLayout=(LinearLayout)findViewById(R.id.checkoutButtonHomeScreenLinearLayout);
        customerNameTextViewNavigationHeader.setText(sharedPreferenceUtility.getCustomerFirstName()+" "+sharedPreferenceUtility.getCustomerLastName());
        customerEmailTextViewNavigationHeader.setText(sharedPreferenceUtility.getCustomerEmailID());

        checkoutButtonLinearLayout.setVisibility(View.GONE);
        getCustomerAddress();

        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreenActivity.this,UserAccountActivity.class);
                startActivity(intent);
            }
        });


        checkoutButton=(Button)findViewById(R.id.dishActivityCheckoutButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreenActivity.this,CheckoutActivity.class);
                startActivity(intent);
            }
        });

        cartSummary=(TextView)findViewById(R.id.dishActivityCartSummaryTextView);
//        cartSummary.setBackgroundColor(getResources().getColor(R.color.app_color));
//        cartSummary.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(HomeScreenActivity.this,CheckoutActivity.class);
//                startActivity(intent);
//            }
//        });

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

    public void getCustomerAddress(){
        int customerid=sharedPreferenceUtility.getCustomerID();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://192.168.0.2:8080/").client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        String  unique=sharedPreferenceUtility.getCustomerUniqueID();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<List<CustomerAddress>> call=retroFitNetworkClient.getAllCustomerAddress(customerid,unique);

        call.enqueue(new Callback<List<CustomerAddress>>() {
            @Override
            public void onResponse(Call<List<CustomerAddress>> call, Response<List<CustomerAddress>> response) {
                addressList=(ArrayList<CustomerAddress>)response.body();
            }

            @Override
            public void onFailure(Call<List<CustomerAddress>> call, Throwable t) {
                Toast.makeText(HomeScreenActivity.this,"Error loading address",Toast.LENGTH_SHORT).show();
            }
        });


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
            checkoutButtonLinearLayout.setVisibility(View.GONE);
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

            String message = "(" + totalQuantity+")" +"\n" + "Rs " + totalCost;
            cartSummary.setText(message);
            cartSummary.setGravity(Gravity.CENTER_VERTICAL);
            checkoutButtonLinearLayout.setVisibility(View.VISIBLE);
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
    public static ArrayList<CustomerAddress> getaddressList(){
        return addressList;
    }
}
