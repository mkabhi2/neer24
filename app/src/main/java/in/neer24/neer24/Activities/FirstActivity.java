package in.neer24.neer24.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import in.neer24.neer24.Adapters.ChangeLocationAddressRVAdapter;
import in.neer24.neer24.Adapters.HomeRVAdapter;
import in.neer24.neer24.Adapters.UserAccountAddressRVAdapter;
import in.neer24.neer24.BuildConfig;
import in.neer24.neer24.CustomObjects.ApplicationVersion;
import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.CustomerAddress;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.FetchLocationNameService;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FirstActivity extends AppCompatActivity {

    private final String FIRST_ACTIVITY_TAG = FirstActivity.class.getSimpleName();
    private final int FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 34;

    private SharedPreferenceUtility sharedPreferenceUtility;
    private String currentLongitude;
    private String currentLatitude;
    private int warehouseID;

    private ProgressBar progressBar;
    private Snackbar snackbar, retroCallFailSnackbar;
    private RelativeLayout totalLayout;

    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        initialiseViewObjects();
        setUpViewObjects();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getApplicationVersion();

        if (sharedPreferenceUtility.loggedIn()) {
            getCustomerAddress();
        }
    }

    private void initialiseViewObjects(){
        sharedPreferenceUtility = new SharedPreferenceUtility(this);
        totalLayout = (RelativeLayout) findViewById(R.id.main_activity_container);
        progressBar = (ProgressBar) findViewById(R.id.firstActivityProgressBar);
    }

    private void setUpViewObjects(){
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.Black), PorterDuff.Mode.MULTIPLY);
    }

    private void getApplicationVersion(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://192.168.0.4:8080/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<ApplicationVersion> call = retroFitNetworkClient.getLatestVersion();

        call.enqueue(new Callback<ApplicationVersion>() {
            @Override
            public void onResponse(Call<ApplicationVersion> call, Response<ApplicationVersion> response) {
                ApplicationVersion applicationVersion = response.body();
                if(applicationVersion!=null){
                    HomeScreenActivity.applicationVersion = applicationVersion;
                }
            }

            @Override
            public void onFailure(Call<ApplicationVersion> call, Throwable t) {
            }
        });
    }

    private void getCustomerAddress() {

        int customerID = sharedPreferenceUtility.getCustomerID();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://18.220.28.118:80/").client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        String unique = sharedPreferenceUtility.getCustomerUniqueID();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<List<CustomerAddress>> call = retroFitNetworkClient.getAllCustomerAddress(customerID, unique);

        call.enqueue(new Callback<List<CustomerAddress>>() {

            @Override
            public void onResponse(Call<List<CustomerAddress>> call, Response<List<CustomerAddress>> response) {

                HomeScreenActivity.addressList = (ArrayList<CustomerAddress>) response.body();
                if (UserAccountActivity.recyclerView != null) {
                    UserAccountActivity.recyclerView.setAdapter(new UserAccountAddressRVAdapter(HomeScreenActivity.addressList,FirstActivity.this));
                    UserAccountActivity.recyclerView.invalidate();
                }

                if(ChangeLocationActivity.addressRV != null) {
                    ChangeLocationActivity.addressRV.setAdapter(new ChangeLocationAddressRVAdapter(HomeScreenActivity.addressList,FirstActivity.this));
                    ChangeLocationActivity.addressRV.invalidate();
                }
            }

            @Override
            public void onFailure(Call<List<CustomerAddress>> call, Throwable t) {
                Toast.makeText(FirstActivity.this, "Error loading address", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        doTheOperations();
    }

    private void doTheOperations() {

        if (isNetworkAvailable()) {

            if (!checkPermissions()) {
                requestPermissions();
            } else {

                getLastLocation();
            }

        } else {
            snackbar = Snackbar
                    .make(totalLayout, "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            doTheOperations();
                        }
                    });
            snackbar.show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {

        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });
        } else {

            startLocationPermissionRequest();
        }
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(FirstActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                FINE_LOCATION_PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(FIRST_ACTIVITY_TAG, "onRequestPermissionResult");
        if (requestCode == FINE_LOCATION_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                progressBar.setVisibility(View.INVISIBLE);
                Log.i(FIRST_ACTIVITY_TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();

            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                progressBar.setVisibility(View.INVISIBLE);
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {

        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {

                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        if (task.isSuccessful() && task.getResult() != null) {

                            progressBar.setVisibility(View.VISIBLE);
                            mLastLocation = task.getResult();

                            currentLatitude = String.format(Locale.ENGLISH, "%f", mLastLocation.getLatitude());
                            currentLongitude = String.format(Locale.ENGLISH, "%f", mLastLocation.getLongitude());

                            sharedPreferenceUtility.setLocationLatitude(currentLatitude);
                            sharedPreferenceUtility.setLocationLongitude(currentLongitude);

                            if (currentLongitude != null && currentLatitude != null) {

                                //Fetch the name of the location to show on the app bar
                                FetchLocationNameService.getAddressFromLocation(Float.parseFloat(currentLatitude), Float.parseFloat(currentLongitude),
                                        getApplicationContext(), new GeocoderHandler());

                                getWarehouseForLocation(Float.parseFloat(currentLatitude), Float.parseFloat(currentLongitude));
                            }

                        } else {

                            progressBar.setVisibility(View.GONE);
                            Log.w(FIRST_ACTIVITY_TAG, "getLastLocation:exception", task.getException());
                            Snackbar.make(totalLayout, "Failed to detect location.", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("RETRY", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            getLastLocation();
                                        }
                                    }).show();
                        }

                    }
                });
    }

    private void getWarehouseForLocation(double latitude, double longitude) {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://18.220.28.118:80/")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        //Call<Integer> call = retroFitNetworkClient.getWarehouseForLocation("12.948568", "77.704373");
        Call<Integer> call = retroFitNetworkClient.getWarehouseForLocation(Double.toString(latitude), Double.toString(longitude));

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if (response.body() != null) {
                    warehouseID = Integer.parseInt(response.body().toString());
                }

                //Warehouse ID 0 means no warehouse found for current location
                if (warehouseID > 0) {
                    getCansListForWarehouse(warehouseID);
                    sharedPreferenceUtility.setWareHouseID(warehouseID);
                }
                else {
                    launchNextActivity();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

                progressBar.setVisibility(View.GONE);
                retroCallFailSnackbar = Snackbar
                        .make(totalLayout, "Failed to load data. No Internet Connection.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                doTheOperations();
                            }
                        });
                retroCallFailSnackbar.show();
            }
        });
    }

    private void getCansListForWarehouse(int wid) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://18.220.28.118:80/")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<List<Can>> call = retroFitNetworkClient.getCansListForWarehouse(wid);


        call.enqueue(new Callback<List<Can>>() {
            @Override
            public void onResponse(Call<List<Can>> call, Response<List<Can>> response) {

                HomeScreenActivity.cansList = (ArrayList<Can>) response.body();
                ScheduleDeliveryActivity.allCans = new ArrayList<Can>(response.body());

                if (HomeScreenActivity.recyclerView != null) {
                    HomeScreenActivity.recyclerView.setAdapter(new HomeRVAdapter(HomeScreenActivity.cansList, FirstActivity.this));
                    HomeScreenActivity.recyclerView.invalidate();
                }
                if (ScheduleDeliveryActivity.recyclerView != null) {
                    ScheduleDeliveryActivity.recyclerView.setAdapter(new HomeRVAdapter(ScheduleDeliveryActivity.allCans, FirstActivity.this));
                    ScheduleDeliveryActivity.recyclerView.invalidate();
                }
                launchNextActivity();
            }

            @Override
            public void onFailure(Call<List<Can>> call, Throwable t) {
                retroCallFailSnackbar = Snackbar
                        .make(totalLayout, "Failed to load data. No Internet Connection.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                doTheOperations();
                            }
                        });
                retroCallFailSnackbar.show();
            }
        });
    }

    private void launchNextActivity() {

        progressBar.setVisibility(View.GONE);

        if (sharedPreferenceUtility.getFirstTimeLaunch()) {

            sharedPreferenceUtility.setFirstTimeLaunch(false);
            Intent intent = new Intent(FirstActivity.this, WelcomeActivity.class);
            startActivity(intent);

        } else {
            if (sharedPreferenceUtility.loggedIn()) {

                if (warehouseID==0) {
                    Intent intent = new Intent(FirstActivity.this, ChangeLocationActivity.class);
                    startActivity(intent);
                }
                if (warehouseID>0){
                    //take to home page
                    Intent intent = new Intent(FirstActivity.this, HomeScreenActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                if ( warehouseID== -1 ){
                    Intent intent = new Intent(FirstActivity.this, BadWeatherActivity.class);
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(FirstActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }


    public static class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            HomeScreenActivity.locationName = locationAddress;
        }
    }





}
