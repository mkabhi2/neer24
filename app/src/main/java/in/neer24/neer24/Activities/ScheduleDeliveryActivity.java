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
import android.widget.TextView;

import java.util.ArrayList;

import in.neer24.neer24.Adapters.ScheduleRVAdapter;
import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RVItemClickListener;
import in.neer24.neer24.Utilities.RVItemDecoration;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;

public class ScheduleDeliveryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static RecyclerView recyclerView;
    public static ArrayList<Can> allCans = new ArrayList<Can>();
    boolean isNew = true;
    String scheduleType;
    private View headerView;
    private TextView customerEmailTextViewNavigationHeader;
    private TextView customerNameTextViewNavigationHeader;
    private NavigationView navigationView;

    SharedPreferenceUtility sharedPreferenceUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_delivery);

        sharedPreferenceUtility=new SharedPreferenceUtility(this);

        Intent intent = getIntent();
        scheduleType = intent.getStringExtra("type");

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            allCans = savedInstanceState.getParcelableArrayList("scheduleSavedCansList");
        }

        recyclerView = findViewById(R.id.rv1);

        RecyclerView recyclerView = findViewById(R.id.rv1);
        setUpRecyclerView(recyclerView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));

        if(scheduleType.equals("schedule")) {
            toolbar.setTitle("Schedule Delivery");
        }
        else {
            toolbar.setTitle("Recurring Delivery");
        }
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        customerNameTextViewNavigationHeader = (TextView)headerView.findViewById(R.id.customerNameTextViewNavigationHeader);
        customerEmailTextViewNavigationHeader=(TextView)headerView.findViewById(R.id.customerEmailTextViewNavigationHeader);
        customerNameTextViewNavigationHeader.setText(sharedPreferenceUtility.getCustomerFirstName());
        customerEmailTextViewNavigationHeader.setText(sharedPreferenceUtility.getCustomerEmailID());

        setUpNavigationDrawer(toolbar);
    }

    public void setUpRecyclerView(final RecyclerView recyclerView){

        recyclerView.setAdapter(new ScheduleRVAdapter(allCans, this));
        recyclerView.addItemDecoration(new RVItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.addOnItemTouchListener(
                new RVItemClickListener(this, new RVItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, final int position) {

                        final Intent intent = new Intent();
                        intent.putExtra("item", allCans.get(position));

                        if(scheduleType.equals("recurring")) {
                            intent.setClass(ScheduleDeliveryActivity.this,SetRecurringScheduleActivity.class);
                            startActivity(intent);
                        }
                        else {
                            intent.setClass(ScheduleDeliveryActivity.this,SetOneTimeScheduleActivity.class);
                            startActivity(intent);
                        }
                    }
                })
        );
    }

    public void setUpNavigationDrawer(Toolbar toolbar){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.schedule_delivery_drawer_layout);
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
            intent.setClass(this, ScheduleDeliveryActivity.class);
            intent.putExtra("type", "recurring");
            startActivity(intent);

        }else if (id == R.id.nav_disclaimer) {
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.schedule_delivery_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.schedule_delivery_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putParcelableArrayList("scheduleSavedCansList",allCans);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isNew = false;
    }

}
