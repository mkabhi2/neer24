package in.neer24.neer24.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import in.neer24.neer24.Adapters.ScheduleRVAdapter;
import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RVItemClickListener;
import in.neer24.neer24.Utilities.RVItemDecoration;

public class ScheduleDeliveryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static RecyclerView recyclerView;
    int warehouseID;
    static ArrayList<Can> allCans = new ArrayList<Can>();
    float currentLatitude, currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_delivery);

        recyclerView = findViewById(R.id.rv1);

        RecyclerView recyclerView = findViewById(R.id.rv1);
        setUpRecyclerView(recyclerView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

                        AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleDeliveryActivity.this);
                        builder.setMessage("Please select the type of scheduled delivery you want :")
                                .setCancelable(true)
                                .setPositiveButton("One Time", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        intent.setClass(ScheduleDeliveryActivity.this,SetOneTimeScheduleActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("Recurring", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        intent.setClass(ScheduleDeliveryActivity.this,SetRecurringScheduleActivity.class);
                                        startActivity(intent);

                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.schedule_delivery_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
