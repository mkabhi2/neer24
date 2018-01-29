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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;

import in.neer24.neer24.Adapters.UserAccountAddressRVAdapter;
import in.neer24.neer24.CustomObjects.Customer;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RVItemDecoration;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;

public class UserAccountActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public static ArrayList<Customer> addressList = new ArrayList<Customer>();
    private RecyclerView recyclerView;
    private ImageView editUserPersonalInformation;
    private TextView changePasswordUserAcountActivity;
    private TextView addNewAddressUserAccountActivity;
    private TextView nameUserAccountActivity;
    private TextView emailUserAccountActivity;
    private TextView mobileNumberUserAccountActivity;
    SharedPreferenceUtility sharedPreferenceUtility;
    private ProgressBar progressBarUserAccountActivity;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        editUserPersonalInformation = (ImageView) findViewById(R.id.editUserPersonalInformation);
        changePasswordUserAcountActivity = (TextView) findViewById(R.id.changePasswordUserAcountActivity);
        addNewAddressUserAccountActivity = (TextView) findViewById(R.id.addNewAddressUserAccountActivity);
        nameUserAccountActivity = (TextView) findViewById(R.id.nameUserAccountActivity);
        emailUserAccountActivity = (TextView) findViewById(R.id.emailUserAccountActivity);
        mobileNumberUserAccountActivity = (TextView) findViewById(R.id.mobileNumberUserAccountActivity);
        progressBarUserAccountActivity = (ProgressBar) findViewById(R.id.progressBarUserAccountActivity);

        sharedPreferenceUtility = new SharedPreferenceUtility(this);
        recyclerView = (RecyclerView) findViewById(R.id.address_rv);

        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));


        nameUserAccountActivity.setText(sharedPreferenceUtility.getCustomerFirstName());
        mobileNumberUserAccountActivity.setText(sharedPreferenceUtility.getCustomerMobileNumber());
        emailUserAccountActivity.setText(sharedPreferenceUtility.getCustomerEmailID());

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
                Intent intent = new Intent(UserAccountActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                break;

            case R.id.addNewAddressUserAccountActivity:

                break;
        }
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
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
            if (loggedInVia.equals("facebook")) {
                sharedPreferenceUtility.setLoggedIn(false);
                LoginManager.getInstance().logOut();
                progressBarUserAccountActivity.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Intent intent = new Intent(UserAccountActivity.this, LoginActivity.class);
                startActivity(intent);
            } else if (loggedInVia.equals("gmail")) {
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                sharedPreferenceUtility.setLoggedIn(false);
                                progressBarUserAccountActivity.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Intent i = new Intent(UserAccountActivity.this, LoginActivity.class);
                                startActivity(i);
                            }
                        });
            } else if (loggedInVia.equals("normal")) {
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
}
