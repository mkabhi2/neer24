package in.neer24.neer24.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;

public class ShareActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button shareButtonShareActivity;

    private TextView customerEmailTextViewNavigationHeader;
    private TextView customerNameTextViewNavigationHeader;
    private NavigationView navigationView;
    SharedPreferenceUtility sharedPreferenceUtility;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        shareButtonShareActivity=(Button) findViewById(R.id.shareButtonShareActivity);

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
                Intent intent=new Intent(ShareActivity.this,UserProfileActivity.class);
                startActivity(intent);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));
        setSupportActionBar(toolbar);

        setUpNavigationDrawer(toolbar);


        shareButtonShareActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_SUBJECT,"Subject test");
                i.putExtra(android.content.Intent.EXTRA_TEXT, "extra text that you want to put");
                startActivity(Intent.createChooser(i,"Share via"));
            }
        });
    }

    public void setUpNavigationDrawer(Toolbar toolbar){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.share_drawer_layout);
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

        } else if (id == R.id.nav_schedule_delivery) {
            Intent intent = new Intent();
            intent.setClass(this,ScheduleDeliveryActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_disclaimer) {
            Intent intent = new Intent();
            intent.setClass(this,DisclaimerActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_orders) {
            Intent intent = new Intent();
            intent.setClass(this,OrdersActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_help) {
            Intent intent = new Intent();
            intent.setClass(this,HelpActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.share_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
