package in.neer24.neer24.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;

public class HelpActivitySecondLevel extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView customerEmailTextViewNavigationHeader;
    private TextView customerNameTextViewNavigationHeader;
    private NavigationView navigationView;
    SharedPreferenceUtility sharedPreferenceUtility;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 35;

    ListView listView;
    TextView textView;
    String[] listItem;
    WebView myWebView;

    ArrayAdapter<String> adapter = null;
    String topicName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_second_level);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));


        sharedPreferenceUtility = new SharedPreferenceUtility(this);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        customerNameTextViewNavigationHeader = (TextView) headerview.findViewById(R.id.customerNameTextViewNavigationHeader);
        customerEmailTextViewNavigationHeader = (TextView) headerview.findViewById(R.id.customerEmailTextViewNavigationHeader);

        customerNameTextViewNavigationHeader.setText(sharedPreferenceUtility.getCustomerFirstName());
        customerEmailTextViewNavigationHeader.setText(sharedPreferenceUtility.getCustomerEmailID());


        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HelpActivitySecondLevel.this, UserAccountActivity.class);
                startActivity(intent);
            }
        });
        setSupportActionBar(toolbar);
        setUpNavigationDrawer(toolbar);


        Bundle bundle = getIntent().getExtras();
        topicName = bundle.getString("NAME");

        listView = (ListView) findViewById(R.id.listViewSecondLevel);
        textView = (TextView) findViewById(R.id.secondLevelTextView);


        if (topicName.equals("Regarding an Issue")) {
            listItem = new String[]{"Email Us", "Call Us"};
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
        } else if (topicName.equals("Water Quality")) {
            listItem = new String[]{"Water is Pure and Healthy!!!", "How can I check purity of Water", "Know our Partners"};
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
        } else if (topicName.equals("Orders")) {
            listItem = new String[]{"How to place an Order", "Bulk Order", "Schedule Order", "Recuring Order", "Cancel an Order", "Reschedule an Order", "Order at Night"};
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
        } else if (topicName.equals("Payments")) {
            listItem = new String[]{"Mode Of Payments", "How Safe is Online Payment?"};
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
        } else if (topicName.equals("Delivery")) {
            listItem = new String[]{"Where do we Deliver?", "Delivery Charge", "Delivery Not Received", "Delivery Timings", "Who is Delivering"};
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
        } else if (topicName.equals("Refunds")) {
            listItem = new String[]{"How does Refunds Works??", "When will I get my Refund"};
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
        } else if (topicName.equals("Policy")) {
            listItem = new String[]{"jkjkjk", "azazaz", "sasasasa", "lalalalla"};
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
        } else if (topicName.equals("Terms and Conditions")) {
            listItem = new String[]{"jkjkjk", "azazaz", "sasasasa", "lalalalla"};
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
        } else if (topicName.equals("Feedback & Suggestions")) {
            listItem = new String[]{"jkjkjk", "azazaz", "sasasasa", "lalalalla"};
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
        } else if (topicName.equals("Contact Us")) {
            listItem = new String[]{"jkjkjk", "azazaz", "sasasasa", "lalalalla"};
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
        } else {
            listItem = new String[]{"jkjkjk", "azazaz", "sasasasa", "lalalalla"};
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
        }


        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String value = adapter.getItem(position);
                Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
                if (value != null) {
                    if (value.equals("Email Us")) {
                        openEmailApplicationClient();
                    } else if (value.equals("Call Us")) {
                        makeCallToCustomerCare();
                    } else if (value.equals("Water is Pure and Healthy!!!")){
                        Intent intent=new Intent(HelpActivitySecondLevel.this,HelpActivityThirdLevel.class);
                        startActivity(intent);
                    }else if(value.equals("")){

                    }else if(value.equals("")){

                    }else if(value.equals("")){

                    }else if(value.equals("")){

                    }
//                Intent intent = new Intent(HelpActivitySecondLevel.this, HelpActivityThirdLevel.class);
//                intent.putExtra("NAME", value);
//                startActivity(intent);
                }else {
                    Toast.makeText(HelpActivitySecondLevel.this,"Error Occured",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void openWebView(String url){
        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(url);
    }
    public void makeCallToCustomerCare(){
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            makeACall();
        }
    }

    private void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CALL_PHONE);

        if (shouldProvideRationale) {

            showSnackbar(R.string.call_permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            stratPhonePermissionRequest();
                        }
                    });
        } else {

            stratPhonePermissionRequest();
        }
    }


    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    @SuppressWarnings("MissingPermission")
    private void makeACall() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "8867248261"));
        startActivity(intent);
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private void stratPhonePermissionRequest() {
        ActivityCompat.requestPermissions(HelpActivitySecondLevel.this,
                new String[]{Manifest.permission.CALL_PHONE},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    public void setUpNavigationDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.help_second_level_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    public void openEmailApplicationClient() {
        String subject="neer24";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:recipient@example.com?subject=" + subject + "&body=" + subject);
        intent.setData(data);
        startActivity(intent);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.help_second_level_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
