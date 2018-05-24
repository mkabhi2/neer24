package in.neer24.neer24.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import in.neer24.neer24.Adapters.HelpRVAdapter;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;

public class HelpActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {



    private TextView customerEmailTextViewNavigationHeader;
    private TextView customerNameTextViewNavigationHeader;
    private NavigationView navigationView;
    SharedPreferenceUtility sharedPreferenceUtility;



    private TextView AboutAnIssueTextViewHelpActivity;
    private TextView WaterCansTextViewHelpActivity;
    private TextView OrdersTextViewHelpActivity;
    private TextView PaymentsTextViewHelpActivity;
    private TextView DeliveryTextViewHelpActivity;
    private TextView RefundsTextViewHelpActivity;
    private TextView ContactUsextViewHelpActivity;
    private TextView PrivacyPolicyTextViewHelpActivity;
    private TextView TermsAndConditionTextViewHelpActivity;

    private ArrayList<String> helpTextViewContentsList;

    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));

        recyclerView = findViewById(R.id.help_rv);


        helpTextViewContentsList=new ArrayList<String>();
        helpTextViewContentsList.add("About an issue");
        helpTextViewContentsList.add("Water Can");
        helpTextViewContentsList.add("Orders");
        helpTextViewContentsList.add("Payments");
        helpTextViewContentsList.add("Delivery");
        helpTextViewContentsList.add("Refunds");
        helpTextViewContentsList.add("Contact Us");
        helpTextViewContentsList.add("Privacy Policy");
        helpTextViewContentsList.add("Terms and Conditions");
        helpTextViewContentsList.add("Feedback or Suggestion");






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
                Intent intent=new Intent(HelpActivity.this,UserAccountActivity.class);
                startActivity(intent);
            }
        });
        setSupportActionBar(toolbar);
        setUpNavigationDrawer(toolbar);

        setUpRecyclerView(recyclerView);
    }

    public void setUpRecyclerView(RecyclerView recyclerView){

        recyclerView.setAdapter(new HelpRVAdapter(helpTextViewContentsList, this));
        //recyclerView.addItemDecoration(new RVItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
    }
    public void setUpNavigationDrawer(Toolbar toolbar){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.help_drawer_layout);
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

        } else if (id == R.id.nav_disclaimer) {
            Intent intent = new Intent();
            intent.setClass(this,DisclaimerActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.help_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.help_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            final Intent intent = new Intent();
            intent.setClass(HelpActivity.this, HomeScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(HomeScreenActivity.cansList==null || HomeScreenActivity.cansList.isEmpty() || HomeScreenActivity.locationName==null || HomeScreenActivity.locationName.isEmpty()){

            Intent intent  = new Intent();
            intent.setClass(HelpActivity.this, FirstActivity.class);
            startActivity(intent);
        }
    }


}
