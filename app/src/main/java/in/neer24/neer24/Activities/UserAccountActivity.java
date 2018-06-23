package in.neer24.neer24.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import in.neer24.neer24.Adapters.UserAccountAddressRVAdapter;
import in.neer24.neer24.CustomObjects.Customer;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RVItemDecoration;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;

public class UserAccountActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public static ArrayList<Customer> addressList = new ArrayList<Customer>();
    public static RecyclerView recyclerView;
    private TextView changePasswordUserAcountActivity;
    private TextView addNewAddressUserAccountActivity;
    private TextView nameUserAccountActivity;
    private TextView emailUserAccountActivity;
    private TextView mobileNumberUserAccountActivity;
    SharedPreferenceUtility sharedPreferenceUtility;
    private ProgressBar progressBarUserAccountActivity;
    private GoogleApiClient mGoogleApiClient;

    private View headerView;
    private TextView customerEmailTextViewNavigationHeader;
    private TextView customerNameTextViewNavigationHeader;
    private NavigationView navigationView;

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        changePasswordUserAcountActivity = (TextView) findViewById(R.id.changePasswordUserAcountActivity);
        addNewAddressUserAccountActivity = (TextView) findViewById(R.id.addNewAddressUserAccountActivity);
        nameUserAccountActivity = (TextView) findViewById(R.id.nameUserAccountActivity);
        emailUserAccountActivity = (TextView) findViewById(R.id.emailUserAccountActivity);
        mobileNumberUserAccountActivity = (TextView) findViewById(R.id.mobileNumberUserAccountActivity);
        progressBarUserAccountActivity = (ProgressBar) findViewById(R.id.progressBarUserAccountActivity);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        customerNameTextViewNavigationHeader = (TextView) headerView.findViewById(R.id.customerNameTextViewNavigationHeader);
        customerEmailTextViewNavigationHeader = (TextView) headerView.findViewById(R.id.customerEmailTextViewNavigationHeader);

        sharedPreferenceUtility = new SharedPreferenceUtility(this);
        recyclerView = (RecyclerView) findViewById(R.id.address_rv);

        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));

        customerNameTextViewNavigationHeader.setText(sharedPreferenceUtility.getCustomerFirstName());
        customerEmailTextViewNavigationHeader.setText(sharedPreferenceUtility.getCustomerEmailID());


        nameUserAccountActivity.setText(sharedPreferenceUtility.getCustomerFirstName());
        mobileNumberUserAccountActivity.setText(sharedPreferenceUtility.getCustomerMobileNumber());
        emailUserAccountActivity.setText(sharedPreferenceUtility.getCustomerEmailID());

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
        recyclerView.setAdapter(new UserAccountAddressRVAdapter(HomeScreenActivity.getAddressList(), this));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent();
            intent.setClass(this, HomeScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } else if (id == R.id.nav_orders) {
            Intent intent = new Intent();
            intent.setClass(this, OrdersActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_schedule_delivery) {
            Intent intent = new Intent();
            intent.setClass(this, ScheduleDeliveryActivity.class);
            intent.putExtra("type", "schedule");
            startActivity(intent);

        } else if (id == R.id.nav_recurring_delivery) {
            Intent intent = new Intent();
            intent.setClass(this, ScheduleDeliveryActivity.class);
            intent.putExtra("type", "recurring");
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
        Intent intent;

        int id = v.getId();
        switch (id) {

            case R.id.changePasswordUserAcountActivity:
                intent = new Intent(UserAccountActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                break;

            case R.id.addNewAddressUserAccountActivity:
                intent = new Intent();
                intent.putExtra("className", "UserAccountActivity");
                intent.setClass(UserAccountActivity.this, AddAddressActivity.class);
                startActivity(intent);
                break;
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            progressBarUserAccountActivity.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Toast.makeText(UserAccountActivity.this, "Logout Pressed", Toast.LENGTH_SHORT).show();
            String loggedInVia = sharedPreferenceUtility.getLoggediInVia();
            if (loggedInVia != null && loggedInVia.equals("facebook")) {
                sharedPreferenceUtility.clearAllData();
                sharedPreferenceUtility.setLoggedIn(false);

                LoginManager.getInstance().logOut();
                progressBarUserAccountActivity.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Intent intent = new Intent(UserAccountActivity.this, LoginActivity.class);
                startActivity(intent);
            } else if (loggedInVia != null && loggedInVia.equals("gmail")) {
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                                sharedPreferenceUtility.clearAllData();
                                sharedPreferenceUtility.setLoggedIn(false);
                                progressBarUserAccountActivity.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                Intent i = new Intent(UserAccountActivity.this, LoginActivity.class);
                                startActivity(i);
                            }
                        });
            } else if (loggedInVia != null && loggedInVia.equals("normal")) {
                sharedPreferenceUtility.clearAllData();
                sharedPreferenceUtility.setLoggedIn(false);
                progressBarUserAccountActivity.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Intent intent = new Intent(UserAccountActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.user_account_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            final Intent intent = new Intent();
            intent.setClass(UserAccountActivity.this, HomeScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (HomeScreenActivity.cansList == null || HomeScreenActivity.cansList.isEmpty() || HomeScreenActivity.locationName == null || HomeScreenActivity.locationName.isEmpty()) {

            Intent intent = new Intent();
            intent.setClass(UserAccountActivity.this, FirstActivity.class);
            startActivity(intent);
        }
    }
}
