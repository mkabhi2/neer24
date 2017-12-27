package in.neer24.neer24.Activities;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.neer24.neer24.Adapters.OrdersRVAdapter;
import in.neer24.neer24.Adapters.UserAccountAddressRVAdapter;
import in.neer24.neer24.CustomObjects.Customer;
import in.neer24.neer24.CustomObjects.CustomerOrder;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RVItemDecoration;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserAccountActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public static ArrayList<Customer> addressList = new ArrayList<Customer>();
    private RecyclerView recyclerView;
    private ImageView editUserPersonalInformation;
    private TextView changePasswordUserAcountActivity;
    private TextView addNewAddressUserAccountActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        editUserPersonalInformation = (ImageView) findViewById(R.id.editUserPersonalInformation);
        changePasswordUserAcountActivity = (TextView) findViewById(R.id.changePasswordUserAcountActivity);
        addNewAddressUserAccountActivity = (TextView) findViewById(R.id.addNewAddressUserAccountActivity);
        recyclerView = (RecyclerView) findViewById(R.id.address_rv);


        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));

        editUserPersonalInformation.setOnClickListener(this);
        changePasswordUserAcountActivity.setOnClickListener(this);
        addNewAddressUserAccountActivity.setOnClickListener(this);

        setSupportActionBar(toolbar);
        setUpNavigationDrawer(toolbar);

        fetchAllAddressForUser();
    }

    public void fetchAllAddressForUser() {
        setUpRecyclerView(recyclerView);
    }

    public void setUpRecyclerView(RecyclerView recyclerView) {
        recyclerView.setAdapter(new UserAccountAddressRVAdapter(HomeScreenActivity.getaddressList(), this));
        recyclerView.addItemDecoration(new RVItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
    }

    public void setUpNavigationDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.user_account_drawer_layout);
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
            intent.setClass(this, HomeScreenActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_orders) {
            Intent intent = new Intent();
            intent.setClass(this, OrdersActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_schedule_delivery) {
            Intent intent = new Intent();
            intent.setClass(this, ScheduleDeliveryActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent();
            intent.setClass(this, ShareActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_disclaimer) {
            Intent intent = new Intent();
            intent.setClass(this, DisclaimerActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.user_account_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {

            case R.id.editUserPersonalInformation:

                break;

            case R.id.changePasswordUserAcountActivity:

                break;

            case R.id.addNewAddressUserAccountActivity:

                break;
        }
    }
}
