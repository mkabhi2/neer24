package in.neer24.neer24.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import in.neer24.neer24.Adapters.HomeRVAdapter;
import in.neer24.neer24.BuildConfig;
import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FirstActivity extends AppCompatActivity {

    private static String currentLongitude;
    private static String currentLatitude;

    private static int TIME_OUT = 5000;

    private ProgressBar progressBar;
    private static final String TAG = FirstActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;

    private String mLatitudeLabel;
    private String mLongitudeLabel;


    Snackbar snackbar, retroCallFailSnackbar;
    private RelativeLayout relativeLayout;
    private BroadcastReceiver mNetworkReceiver;


    SharedPreferenceUtility sharedPreferenceUtility;

    int warehouseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        sharedPreferenceUtility = new SharedPreferenceUtility(this);
        relativeLayout = (RelativeLayout) findViewById(R.id.main_activity_container);
        progressBar = (ProgressBar) findViewById(R.id.firstActivityProgressBar);


        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void doTheOperations() {


        if (isNetworkAvailable()) {
            warehouseID = sharedPreferenceUtility.getWareHouseID();
            if (warehouseID == 0) {
                if (!checkPermissions()) {
                    requestPermissions();
                } else {
                    getLastLocation();
                }
                getWarehouseForLocation(Float.parseFloat("2.1"), Float.parseFloat("2.1"));
            } else {
                getCansListForWarehouse(warehouseID);
            }
        } else {
            snackbar = Snackbar
                    .make(relativeLayout, "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            doTheOperations();
                        }
                    });
            snackbar.show();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        doTheOperations();
    }

    public void pauseActivityForTwoSeconds() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                launchNextActivity();
            }
        }, 2500);
    }

    private void launchNextActivity() {

        progressBar.setVisibility(View.GONE);

        if (sharedPreferenceUtility.getFirstTimeLaunch()) {
            sharedPreferenceUtility.setFirstTimeLaunch(false);
            Intent intent = new Intent(FirstActivity.this, WelcomeActivity.class);
            startActivity(intent);

        } else {
            if (sharedPreferenceUtility.loggedIn()) {
                //take to home page
                Intent intent = new Intent(FirstActivity.this, HomeScreenActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(FirstActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    private void delayActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, TIME_OUT);
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Toast.makeText(FirstActivity.this, "exception 1", Toast.LENGTH_SHORT);
                            progressBar.setVisibility(View.VISIBLE);
                            mLastLocation = task.getResult();

                            currentLatitude = String.format(Locale.ENGLISH, "%f",
                                    mLastLocation.getLatitude());
                            currentLongitude = String.format(Locale.ENGLISH, "%f",
                                    mLastLocation.getLongitude());
                            if (currentLongitude != null && currentLatitude != null)
                                getWarehouseForLocation(Float.parseFloat(currentLatitude), Float.parseFloat(currentLongitude));
                        } else {
                            Toast.makeText(FirstActivity.this, "exception 2", Toast.LENGTH_SHORT);
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            showSnackbar(getString(R.string.no_location_detected));
                        }

                    }
                });

        //pauseActivityForTwoSeconds();


    }

    private void showSnackbar(final String text) {
        View container = findViewById(R.id.main_activity_container);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(FirstActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                progressBar.setVisibility(View.INVISIBLE);
                Log.i(TAG, "User interaction was cancelled.");
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

    public void getWarehouseForLocation(double latitude, double longitude) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://18.220.28.118:8080")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<Integer> call = retroFitNetworkClient.getWarehouseForLocation("12.948568", "77.704373");

        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                Context context = FirstActivity.this;

                if (response.body() != null) {
                    warehouseID = Integer.parseInt(response.body().toString());
                    System.out.print(warehouseID);
                }

                if (warehouseID != 0)
                    getCansListForWarehouse(warehouseID);
                else {
                    //TODO  CORRECT IT
                    Intent intent = new Intent();
                    intent.setClass(context, TempActivity.class);
                    //startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                retroCallFailSnackbar = Snackbar
                        .make(relativeLayout, "Failed to load data. No Internet Connection.", Snackbar.LENGTH_INDEFINITE)
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

    public void getCansListForWarehouse(int wid) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://18.220.28.118:8080")
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
                        .make(relativeLayout, "Failed to load data. No Internet Connection.", Snackbar.LENGTH_INDEFINITE)
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


}
