package in.neer24.neer24.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;
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

    public static ArrayList<CustomerAddress> addressList = new ArrayList<CustomerAddress>();
    public static ArrayList<Can> cansList = new ArrayList<Can>();

    AutoScrollViewPager viewPager;
    CustomPagerAdapter pagerAdapter;
    SharedPreferenceUtility sharedPreferenceUtility;

    public static RecyclerView recyclerView;
    private static Button checkoutButton;
    private static LinearLayout checkoutButtonLinearLayout;
    private static TextView cartSummary;
    public static String locationName;

    private View headerView;
    private Toolbar toolbar;
    private TextView customerEmailTextViewNavigationHeader;
    private TextView customerNameTextViewNavigationHeader;
    private NavigationView navigationView;

    boolean isNew = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        sharedPreferenceUtility=new SharedPreferenceUtility(this);

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            cansList = savedInstanceState.getParcelableArrayList("savedCansList");
            addressList = savedInstanceState.getParcelableArrayList("savedAddressList");
        }

        if(addressList==null || addressList.isEmpty() && (sharedPreferenceUtility.loggedIn())) {

            getCustomerAddress();
        }

        initialiseViewObjects();
        setViewObjects();
        setClickListeners();
        setUpRecyclerView(recyclerView);
        setUpViewPager();
        //getCustomerAddress();

    }



    private void initialiseViewObjects() {

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        customerNameTextViewNavigationHeader = (TextView)headerView.findViewById(R.id.customerNameTextViewNavigationHeader);
        customerEmailTextViewNavigationHeader=(TextView)headerView.findViewById(R.id.customerEmailTextViewNavigationHeader);
        checkoutButtonLinearLayout=(LinearLayout)findViewById(R.id.checkoutButtonHomeScreenLinearLayout);
        customerNameTextViewNavigationHeader.setText(sharedPreferenceUtility.getCustomerFirstName());
        customerEmailTextViewNavigationHeader.setText(sharedPreferenceUtility.getCustomerEmailID());
        checkoutButton=(Button)findViewById(R.id.dishActivityCheckoutButton);
        cartSummary=(TextView)findViewById(R.id.dishActivityCartSummaryTextView);
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void setViewObjects() {

        checkoutButtonLinearLayout.setVisibility(View.GONE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));
        toolbar.setTitle("");
        toolbar.setSubtitle(locationName);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.White));
        setSupportActionBar(toolbar);
        setUpNavigationDrawer(toolbar);
    }

    private void setClickListeners() {

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreenActivity.this,UserAccountActivity.class);
                startActivity(intent);
            }
        });

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(HomeScreenActivity.this,CheckoutActivity.class);
                startActivity(intent);
            }
        });
    }


    public void getCustomerAddress(){
        int customerid=sharedPreferenceUtility.getCustomerID();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                //.baseUrl("http://192.168.0.2:8080/")
                .baseUrl("http://18.220.28.118:8080/")  //
                .client(okHttpClient)
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
        viewPager=(AutoScrollViewPager)findViewById(R.id.viewPager);
        pagerAdapter=new CustomPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.startAutoScroll();
        viewPager.setInterval(5000);
        viewPager.setStopScrollWhenTouch(true);
        viewPager.setBorderAnimation(false);
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
        if (id == R.id.action_change_location) {
            Intent intent = new Intent(HomeScreenActivity.this, ChangeLocationActivity.class);
            intent.putExtra("isFromHomeScreen",true);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
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
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putParcelableArrayList("savedCansList",cansList);
        savedInstanceState.putParcelableArrayList("savedAddressList", addressList);
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

            String rupeeSymbol = Character.toString((char)'\u20B9');

            String message = "(" + totalQuantity+")" +"\n" + rupeeSymbol + " " + totalCost;
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

    public static ArrayList<CustomerAddress> getAddressList(){
        return addressList;
    }
}
