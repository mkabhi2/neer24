package in.neer24.neer24.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;

public class DisclaimerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private TextView customerEmailTextViewNavigationHeader;
    private TextView customerNameTextViewNavigationHeader;
    private NavigationView navigationView;
    private SharedPreferenceUtility sharedPreferenceUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));

        sharedPreferenceUtility=new SharedPreferenceUtility(this);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        customerNameTextViewNavigationHeader = (TextView)headerview.findViewById(R.id.customerNameTextViewNavigationHeader);
        customerEmailTextViewNavigationHeader=(TextView)headerview.findViewById(R.id.customerEmailTextViewNavigationHeader);

        customerNameTextViewNavigationHeader.setText(sharedPreferenceUtility.getCustomerFirstName());
        customerEmailTextViewNavigationHeader.setText(sharedPreferenceUtility.getCustomerEmailID());

        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DisclaimerActivity.this,UserAccountActivity.class);
                startActivity(intent);
            }
        });

        setSupportActionBar(toolbar);

        setUpNavigationDrawer(toolbar);
    }

    private void setUpNavigationDrawer(Toolbar toolbar){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.disclaimer_drawer_layout);
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

        } else if (id == R.id.nav_orders) {
            Intent intent = new Intent();
            intent.setClass(this,OrdersActivity.class);
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

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent();
            intent.setClass(this,ShareActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_help) {
            Intent intent = new Intent();
            intent.setClass(this,HelpActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.disclaimer_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.disclaimer_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            final Intent intent = new Intent();
            intent.setClass(DisclaimerActivity.this, HomeScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(HomeScreenActivity.cansList==null || HomeScreenActivity.cansList.isEmpty() || HomeScreenActivity.locationName==null || HomeScreenActivity.locationName.isEmpty()){

            Intent intent  = new Intent();
            intent.setClass(DisclaimerActivity.this, FirstActivity.class);
            startActivity(intent);
        }
    }


}
