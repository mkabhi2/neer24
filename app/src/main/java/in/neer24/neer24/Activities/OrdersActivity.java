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
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.neer24.neer24.Adapters.OrdersRVAdapter;
import in.neer24.neer24.CustomObjects.OrderTable;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RVItemClickListener;
import in.neer24.neer24.Utilities.RVItemDecoration;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrdersActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    private TextView customerEmailTextViewNavigationHeader;
    private TextView customerNameTextViewNavigationHeader;
    private LinearLayout noOrdersFoundTV;
    private NavigationView navigationView;
    SharedPreferenceUtility sharedPreferenceUtility;
    static ArrayList<OrderTable> ordersList = new ArrayList<OrderTable>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        recyclerView = findViewById(R.id.order_rv);

        sharedPreferenceUtility=new SharedPreferenceUtility(this);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        customerNameTextViewNavigationHeader = (TextView)headerview.findViewById(R.id.customerNameTextViewNavigationHeader);
        customerEmailTextViewNavigationHeader=(TextView)headerview.findViewById(R.id.customerEmailTextViewNavigationHeader);
        noOrdersFoundTV = (LinearLayout) findViewById(R.id.noOrdersFound);

        customerNameTextViewNavigationHeader.setText(sharedPreferenceUtility.getCustomerFirstName());
        customerEmailTextViewNavigationHeader.setText(sharedPreferenceUtility.getCustomerEmailID());

        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OrdersActivity.this,UserAccountActivity.class);
                startActivity(intent);
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));
        setSupportActionBar(toolbar);

        fetchCustomerOrders();
        setUpNavigationDrawer(toolbar);


    }

    public void fetchCustomerOrders(){

        final ProgressBar progressBar;
        progressBar = (ProgressBar) findViewById(R.id.ordersActivityProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        Retrofit.Builder builder = new Retrofit.Builder()
                //.baseUrl("http://192.168.43.202:8080/")
                .baseUrl("http://192.168.43.202:8080/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<List<OrderTable>> call = retroFitNetworkClient.getAllCustomerOrders(sharedPreferenceUtility.getCustomerID());

        call.enqueue(new Callback<List<OrderTable>>() {
            @Override
            public void onResponse(Call<List<OrderTable>> call, Response<List<OrderTable>> response) {
                ordersList = (ArrayList<OrderTable>) response.body();
                progressBar.setVisibility(View.INVISIBLE);
                setUpRecyclerView(recyclerView);
            }

            @Override
            public void onFailure(Call<List<OrderTable>> call, Throwable t) {
                Toast.makeText(OrdersActivity.this,"Error occurred",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });



    }

    public void setUpRecyclerView(RecyclerView recyclerView){

        if(ordersList!=null && !ordersList.isEmpty()){
            recyclerView.setAdapter(new OrdersRVAdapter(ordersList, this));
            recyclerView.addItemDecoration(new RVItemDecoration(this, LinearLayoutManager.VERTICAL, 500));
            recyclerView.addOnItemTouchListener(
                    new RVItemClickListener(this, new RVItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, final int position) {

                            final Intent intent = new Intent();
                            intent.putExtra("order", ordersList.get(position));

                            intent.setClass(OrdersActivity.this, OrderDetailsActivity.class);
                            startActivity(intent);
                        }
                    })
            );
        }
        else {
            noOrdersFoundTV.setVisibility(View.VISIBLE);
        }
    }
    public void setUpNavigationDrawer(Toolbar toolbar){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.order_drawer_layout);
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

        if (id == R.id.nav_home) {
            Intent intent = new Intent();
            intent.setClass(this,HomeScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else if (id == R.id.nav_schedule_delivery) {
            Intent intent = new Intent();
            intent.setClass(this,ScheduleDeliveryActivity.class);
            intent.putExtra("type","schedule");
            startActivity(intent);

        } else if (id == R.id.nav_recurring_delivery) {
            Intent intent = new Intent();
            intent.setClass(this,ScheduleDeliveryActivity.class);
            intent.putExtra("type","recurring");
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.order_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.order_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            final Intent intent = new Intent();
            intent.setClass(OrdersActivity.this, HomeScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(HomeScreenActivity.cansList==null || HomeScreenActivity.cansList.isEmpty() || HomeScreenActivity.locationName==null || HomeScreenActivity.locationName.isEmpty()){

            Intent intent  = new Intent();
            intent.setClass(OrdersActivity.this, FirstActivity.class);
            startActivity(intent);
        }
    }

}
