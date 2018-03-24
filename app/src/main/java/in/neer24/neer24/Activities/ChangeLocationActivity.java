package in.neer24.neer24.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.neer24.neer24.Adapters.ChangeLocationAddressRVAdapter;
import in.neer24.neer24.Adapters.HomeRVAdapter;
import in.neer24.neer24.BuildConfig;
import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.FetchLocationNameService;
import in.neer24.neer24.Utilities.RVItemDecoration;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangeLocationActivity extends AppCompatActivity {

    private static final String TAG = ChangeLocationActivity.class.getSimpleName();

    public static RecyclerView addressRV;
    private static String currentLongitude;
    private static String currentLatitude;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    SharedPreferenceUtility sharedPreferenceUtility;
    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;
    LatLng latLng;

    RelativeLayout placePickerRL, currentLocationRL;
    LinearLayout comingSoonTV;
    private Toolbar toolbar;
    private LinearLayout linearLayout;
    public static ProgressDialog dialog;

    int PLACE_PICKER_REQUEST = 1;
    int warehouseID = 0;
    private Boolean exit = false;

    public ArrayList<Can> cansList = new ArrayList<Can>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_location);

        sharedPreferenceUtility = new SharedPreferenceUtility(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initialiseViewObjects();
        setViewObjects();
        setClickListeners();
        setUpRecyclerView(addressRV);

        Bundle bundle = getIntent().getExtras();
        boolean isFromHomeScreen = bundle.getBoolean("isFromHomeScreen");

        if(isFromHomeScreen) {
            comingSoonTV.setVisibility(View.GONE);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    private void initialiseViewObjects() {

        dialog = new ProgressDialog(this);
        linearLayout = (LinearLayout) findViewById(R.id.changeLocation_container);
        comingSoonTV = (LinearLayout) findViewById(R.id.tv_comingSoon);
        addressRV = (RecyclerView) findViewById(R.id.address_rv);
        placePickerRL = (RelativeLayout) findViewById(R.id.placePicker);
        currentLocationRL = (RelativeLayout) findViewById(R.id.currentLocation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void setViewObjects() {

        addressRV.setAdapter(new ChangeLocationAddressRVAdapter(HomeScreenActivity.addressList, this));
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));
        toolbar.setTitle("");
        toolbar.setSubtitle(HomeScreenActivity.locationName);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.White));
        setSupportActionBar(toolbar);
    }

    private void setClickListeners() {
        placePickerRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try{
                    startActivityForResult(builder.build(ChangeLocationActivity.this), PLACE_PICKER_REQUEST);
                }
                catch (Exception e) {
                    Toast.makeText(ChangeLocationActivity.this, "Please install google play services ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        currentLocationRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = ProgressDialog.show(ChangeLocationActivity.this, "",
                        "Loading. Please wait...", true);
                doTheOperations();
            }
        });
    }


    public void setUpRecyclerView(RecyclerView recyclerView){

        recyclerView.setAdapter(new ChangeLocationAddressRVAdapter(HomeScreenActivity.addressList, this));
        recyclerView.addItemDecoration(new RVItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                dialog = ProgressDialog.show(ChangeLocationActivity.this, "",
                        "Loading. Please wait...", true);
                Place place = PlacePicker.getPlace(this, data);
                latLng = place.getLatLng();
                FetchLocationNameService.getAddressFromLocation(latLng.latitude, latLng.longitude,
                        getApplicationContext(), new FirstActivity.GeocoderHandler());
                getWarehouseForLocation(latLng.latitude, latLng.longitude);
            }
        }
    }

    public void getWarehouseForLocation(double latitude, double longitude) {

        Retrofit.Builder builder = new Retrofit.Builder()
                //.baseUrl("http://192.168.0.2:8080")
                .baseUrl("http://18.220.28.118:")       //
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
                if (warehouseID != 0) {
                    getCansListForWarehouse(warehouseID);
                    sharedPreferenceUtility.setWareHouseID(warehouseID);
                }
                else {
                    launchNextActivity();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

                dialog.cancel();
                Snackbar.make(linearLayout, "Failed to load data. No Internet Connection.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getWarehouseForLocation(latLng.latitude, latLng.longitude);
                            }
                        }).show();
            }
        });
    }

    public void getCansListForWarehouse(int wid) {
        Retrofit.Builder builder = new Retrofit.Builder()
                //.baseUrl("http://192.168.0.2:8080/")
                .baseUrl("http://18.220.28.118")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<List<Can>> call = retroFitNetworkClient.getCansListForWarehouse(wid);


        call.enqueue(new Callback<List<Can>>() {
            @Override
            public void onResponse(Call<List<Can>> call, Response<List<Can>> response) {

                cansList = (ArrayList<Can>) response.body();
                HomeScreenActivity.cansList = (ArrayList<Can>) response.body();
                ScheduleDeliveryActivity.allCans = new ArrayList<Can>(response.body());

                if (HomeScreenActivity.recyclerView != null) {
                    HomeScreenActivity.recyclerView.setAdapter(new HomeRVAdapter(HomeScreenActivity.cansList, ChangeLocationActivity.this));
                    HomeScreenActivity.recyclerView.invalidate();
                }
                if (ScheduleDeliveryActivity.recyclerView != null) {
                    ScheduleDeliveryActivity.recyclerView.setAdapter(new HomeRVAdapter(ScheduleDeliveryActivity.allCans, ChangeLocationActivity.this));
                    ScheduleDeliveryActivity.recyclerView.invalidate();
                }
                launchNextActivity();
            }

            @Override
            public void onFailure(Call<List<Can>> call, Throwable t) {
                Snackbar
                        .make(linearLayout, "Failed to load data. No Internet Connection.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getWarehouseForLocation(latLng.latitude, latLng.longitude);
                            }
                        }).show();
            }
        });
    }

    private void launchNextActivity() {

        dialog.cancel();

        if (sharedPreferenceUtility.loggedIn()) {
            if (cansList==null || cansList.isEmpty()) {
                //Toast.makeText(this, "Sorry we dont deliver in this area", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ChangeLocationActivity.this, ChangeLocationActivity.class);
                    startActivity(intent);
            }
            else {
                //take to home page
                Intent intent = new Intent(ChangeLocationActivity.this, HomeScreenActivity.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(ChangeLocationActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    public void doTheOperations() {

        if (isNetworkAvailable()) {

            if (!checkPermissions()) {
                requestPermissions();
            } else {
                getLastLocation();
            }

        } else {
            Snackbar
                    .make(linearLayout, "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            doTheOperations();
                        }
                    }).show();
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

            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.permission_rationale),
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(getString(android.R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    }).show();
        } else {

            startLocationPermissionRequest();
        }
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(ChangeLocationActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {

                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                dialog.cancel();
                Log.i(TAG, "User interaction was cancelled.");

            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();

            } else {
                // Permission denied.

                dialog.cancel();

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

                            mLastLocation = task.getResult();

                            currentLatitude = String.format(Locale.ENGLISH, "%f", mLastLocation.getLatitude());
                            currentLongitude = String.format(Locale.ENGLISH, "%f", mLastLocation.getLongitude());

                            sharedPreferenceUtility.setLocationLatitude(currentLatitude);
                            sharedPreferenceUtility.setLocationLongitude(currentLongitude);

                            if (currentLongitude != null && currentLatitude != null) {

                                //Fetch the name of the location to show on the app bar
                                FetchLocationNameService.getAddressFromLocation(Float.parseFloat(currentLatitude), Float.parseFloat(currentLongitude),
                                        getApplicationContext(), new FirstActivity.GeocoderHandler());

                                getWarehouseForLocation(Float.parseFloat(currentLatitude), Float.parseFloat(currentLongitude));
                            }

                        } else {

                            dialog.cancel();
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            Snackbar.make(linearLayout, "Failed to detect location.", Snackbar.LENGTH_INDEFINITE)
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

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /*@Override
    public void onBackPressed() {

        if(comingSoonTV.getVisibility() == View.VISIBLE && (cansList==null || cansList.isEmpty())){

            if (exit) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);*Change Here***
                startActivity(intent);
                finish();
                System.exit(0);

            } else {
                Toast.makeText(this, "Press Back again to Exit.",
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);

            }


            if (exit) {
                finish(); // finish activity
            }
            else {
                Toast.makeText(this, "Press Back again to Exit.",
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }
                }, 1000);

        }}
        else {

            super.onBackPressed();
        }
    }*/

}
