package in.neer24.neer24.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;

import in.neer24.neer24.Adapters.ChangeLocationAddressRVAdapter;
import in.neer24.neer24.Adapters.UserAccountAddressRVAdapter;
import in.neer24.neer24.CustomObjects.CustomerAddress;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddAddressActivity extends AppCompatActivity {

    Button selectLocationBtn, saveButton;
    TextView locationNameTextView;
    View locationNameLineView;
    int PLACE_PICKER_REQUEST = 1;
    SharedPreferenceUtility sharedPreferenceUtility;
    private EditText addressNickName, landmark, houseAddress;
    Toast toast;
    ProgressDialog dialog;

    private int customerID, warehouseID, currentWarehouseID;
    private int customerAddressID;
    private double latitude, longitude;
    String mapAddress, callingClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        Intent intent = getIntent();
        callingClass = intent.getStringExtra("className");

        sharedPreferenceUtility=new SharedPreferenceUtility(this);
        currentWarehouseID = sharedPreferenceUtility.getWareHouseID();


        initialiseViewObjects();
        setUpClickListeners();
    }

    private void initialiseViewObjects() {
        selectLocationBtn = (Button) findViewById(R.id.selectLocationBtn);
        locationNameTextView = (TextView) findViewById(R.id.locationNameTV);
        locationNameLineView = (View) findViewById(R.id.locationNameLine);
        saveButton = (Button) findViewById(R.id.saveButton);
        addressNickName = (EditText) findViewById(R.id.labelET);
        landmark = (EditText) findViewById(R.id.landmarkET);
        houseAddress = (EditText) findViewById(R.id.houseNoET);


    }

    private void setUpClickListeners() {
        selectLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try{
                    startActivityForResult(builder.build(AddAddressActivity.this), PLACE_PICKER_REQUEST);
                }
                catch (Exception e) {
                    Toast.makeText(AddAddressActivity.this, "Please install google play services ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkFields()) {
                    getWarehouseIDForNewAddress();
                }
            }
        });

    }

    private boolean checkFields() {

        String a  = houseAddress.getText().toString();
        String b  = houseAddress.getText().toString();

        if (toast != null) {
            toast.cancel();
        }

        if(locationNameTextView.getText()=="") {
            toast = Toast.makeText(this, "Please select a location on map", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if(houseAddress.getText().toString().contentEquals("")) {
            toast = Toast.makeText(this, "Please enter your house No. / Flat No & Floor", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if(addressNickName.getText().toString().contentEquals("")) {
            toast = Toast.makeText(this, "Please enter a label or nickname for this address", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        return true;
    }

    private void getWarehouseIDForNewAddress() {

        dialog = ProgressDialog.show(AddAddressActivity.this, "",
                "Saving. Please wait...", true);
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
                    saveAddressToServer();
                }
            }



            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                saveButton.requestFocus();
                addressNickName.setEnabled(false);
                houseAddress.setEnabled(false);
                landmark.setEnabled(false);
                selectLocationBtn.setEnabled(false);
                saveButton.setEnabled(false);
                Snackbar.make(findViewById(R.id.locationNameTV), "Failed to save address. No Internet Connection.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.show();
                                getWarehouseIDForNewAddress();
                            }
                        }).show();
                dialog.cancel();
            }
        });





    }

    public void saveAddressToServer(){

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://18.220.28.118:80")       //
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        //Call<Integer> call = retroFitNetworkClient.getWarehouseForLocation("12.948568", "77.704373");
        Call<String> call = retroFitNetworkClient.addCustomerAddressToServer(createObjectForCustomerAddress());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(response.body().contains("0")) {
                    onFailure(call,new Throwable());
                }
                else {
                    if(warehouseID == currentWarehouseID) {
                        CheckoutActivity.selectedAddressID = Integer.parseInt(response.body());
                        customerAddressID = Integer.parseInt(response.body());
                    }
                    fetchCustomerAddress();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                saveButton.requestFocus();
                addressNickName.setEnabled(false);
                houseAddress.setEnabled(false);
                landmark.setEnabled(false);
                selectLocationBtn.setEnabled(false);
                saveButton.setEnabled(false);
                Snackbar.make(findViewById(R.id.locationNameTV), "Failed to save address. No Internet Connection.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.show();
                                saveAddressToServer();
                            }
                        }).show();
                dialog.cancel();
            }
        });

    }

    public void fetchCustomerAddress() {

        CustomerAddress newAddress = createObjectForCustomerAddress();
        newAddress.setCustomerAddressID(customerAddressID);
        if(HomeScreenActivity.addressList!=null)
            HomeScreenActivity.addressList.add(newAddress);
        else {
            HomeScreenActivity.addressList = new ArrayList<CustomerAddress>();
            HomeScreenActivity.addressList.add(newAddress);
        }
        if (UserAccountActivity.recyclerView != null) {
            UserAccountActivity.recyclerView.setAdapter(new UserAccountAddressRVAdapter(HomeScreenActivity.addressList,AddAddressActivity.this));
            UserAccountActivity.recyclerView.invalidate();
        }

        if(ChangeLocationActivity.addressRV != null) {
            ChangeLocationActivity.addressRV.setAdapter(new ChangeLocationAddressRVAdapter(HomeScreenActivity.addressList,AddAddressActivity.this));
            ChangeLocationActivity.addressRV.invalidate();
        }
        dialog.cancel();
        Intent intent = new Intent();
        if(callingClass.equals("CheckoutActivity"))
            intent.setClass(AddAddressActivity.this,CheckoutActivity.class);
        if(callingClass.equals("RecurringActivity"))
            intent.setClass(AddAddressActivity.this,SetRecurringScheduleActivity.class);
        if(callingClass.equals("UserAccountActivity"))
            intent.setClass(AddAddressActivity.this, UserAccountActivity.class);
        startActivity(intent);

    }

    public CustomerAddress createObjectForCustomerAddress() {
        return new CustomerAddress(sharedPreferenceUtility.getCustomerID(), warehouseID, String.valueOf(latitude), String.valueOf(longitude), addressNickName.getText().toString(), mapAddress, landmark.getText().toString(), houseAddress.getText().toString());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                mapAddress = place.getAddress().toString();
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                locationNameTextView.setText(place.getAddress());
                locationNameTextView.setVisibility(View.VISIBLE);
                locationNameLineView.setVisibility(View.VISIBLE);
                selectLocationBtn.setText("CHANGE LOCATION");
            }
        }
    }
}
