package in.neer24.neer24.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;
import java.util.List;

import in.neer24.neer24.Adapters.ChangeLocationAddressRVAdapter;
import in.neer24.neer24.Adapters.UserAccountAddressRVAdapter;
import in.neer24.neer24.CustomObjects.CustomerAddress;
import in.neer24.neer24.Fragments.CheckoutFragment;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddAddressActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Button selectLocationBtn, saveButton;
    TextView locationNameTextView;
    View locationNameLineView;
    int PLACE_PICKER_REQUEST = 1, floor;
    SharedPreferenceUtility sharedPreferenceUtility;
    private EditText addressNickName, landmark, flatNumberET, societyNameET, houseNumberET;
    Toast toast;
    Toolbar toolbar;
    ProgressDialog dialog;
    Spinner floorSpinner;
    int hasLift;
    SwitchCompat hasLiftSwitch;
    CustomerAddress customerAddress;

    private int warehouseID, currentWarehouseID;
    private int customerAddressID;
    private double latitude, longitude;
    String mapAddress, callingClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        Intent intent = getIntent();
        callingClass = intent.getStringExtra("className");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.app_color));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferenceUtility=new SharedPreferenceUtility(this);
        currentWarehouseID = sharedPreferenceUtility.getWareHouseID();


        initialiseViewObjects();
        setUpClickListeners();
        setUpSpinner();
    }

    public void setUpSpinner(){

        List<String> floorList = new ArrayList<String>();
        floorList.add("None Selected");
        floorList.add("Ground Floor");
        floorList.add("1st Floor");
        floorList.add("2nd Floor");
        floorList.add("3rd Floor");

        for(int i=4;i<21;i++){
            floorList.add(i + "th Floor");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,floorList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floorSpinner.setAdapter(adapter);
        floorSpinner.setOnItemSelectedListener(this);
        floorSpinner.setSelection(0);
        floor = -1;
    }



    private void initialiseViewObjects() {

        selectLocationBtn = (Button) findViewById(R.id.selectLocationBtn);
        locationNameTextView = (TextView) findViewById(R.id.locationNameTV);
        locationNameLineView = (View) findViewById(R.id.locationNameLine);
        saveButton = (Button) findViewById(R.id.saveButton);
        addressNickName = (EditText) findViewById(R.id.labelET);
        landmark = (EditText) findViewById(R.id.landmarkET);
        flatNumberET = (EditText) findViewById(R.id.flatNoET);
        societyNameET = (EditText) findViewById(R.id.societyNameET);
        houseNumberET = (EditText) findViewById(R.id.houseNumberET);
        flatNumberET = (EditText) findViewById(R.id.flatNoET);
        floorSpinner = (Spinner) findViewById(R.id.spinner_floor);
        hasLiftSwitch = (SwitchCompat) findViewById(R.id.switch_has_lift);


    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        floor = position - 1;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

        hasLiftSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasLiftSwitch.isChecked()) {

                    hasLift = 1;

                } else {
                    hasLift = 0;
                }
            }
        });

    }

    private boolean checkFields() {


        if (toast != null) {
            toast.cancel();
        }

        if(locationNameTextView.getText()=="") {
            toast = Toast.makeText(this, "Please select a location on map", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if(houseNumberET.getText().toString().contentEquals("") && societyNameET.getText().toString().contentEquals("")) {
            toast = Toast.makeText(this, "Please enter your building name, or building number or guiding instructions to reach your building", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if(floor==-1){
            toast = Toast.makeText(this, "Please select your floor number", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        if(flatNumberET.getText().toString().contentEquals("")) {
            toast = Toast.makeText(this, "Please enter your flat number, or guiding instructions to reach the required flat", Toast.LENGTH_SHORT);
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
                    saveAddressToServer();
                }
            }



            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                saveButton.requestFocus();
                addressNickName.setEnabled(false);
                flatNumberET.setEnabled(false);
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
                .baseUrl("http://18.220.28.118:80/")

                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<String> call = retroFitNetworkClient.addCustomerAddressToServer(createObjectForCustomerAddress());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(response.body().contains("0")) {
                    onFailure(call,new Throwable());
                }
                else {
                    if(warehouseID == currentWarehouseID) {
                        customerAddressID = Integer.parseInt(response.body());
                    }
                    fetchCustomerAddress();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                saveButton.requestFocus();
                addressNickName.setEnabled(false);
                flatNumberET.setEnabled(false);
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
        if(Integer.parseInt(newAddress.getFloorNumber())>2 && newAddress.getHasLift()==0){
            CheckoutFragment.hasFloorCharge = 1;
        }
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
        if(callingClass.equals("CheckoutActivity")) {
            if(warehouseID == currentWarehouseID) {
                CheckoutActivity.selectedAddressID = customerAddressID;
                if(customerAddress.getHasLift()==1 && Integer.parseInt(customerAddress.getFloorNumber())>2){
                    CheckoutFragment.hasFloorCharge = 1;
                }
                else{
                    CheckoutFragment.hasFloorCharge = 0;
                }
            }
            intent.setClass(AddAddressActivity.this,CheckoutActivity.class);
        }

        if(callingClass.equals("RecurringActivity")) {
            if(warehouseID == currentWarehouseID) {
                SetRecurringScheduleActivity.selectedAddressID = customerAddressID;
                if(customerAddress.getHasLift()==1 && Integer.parseInt(customerAddress.getFloorNumber())>2){
                    SetRecurringScheduleActivity.hasFloorCharge = 1;
                }
                else{
                    SetRecurringScheduleActivity.hasFloorCharge = 0;
                }
            }

            intent.setClass(AddAddressActivity.this,SetRecurringScheduleActivity.class);
        }

        if(callingClass.equals("OneTimeScheduleActivity")) {
            if(warehouseID == currentWarehouseID) {
                SetOneTimeScheduleActivity.selectedAddressID = customerAddressID;
                if(customerAddress.getHasLift()==1 && Integer.parseInt(customerAddress.getFloorNumber())>2){
                    SetOneTimeScheduleActivity.hasFloorCharge = 1;
                }
                else{
                    SetOneTimeScheduleActivity.hasFloorCharge = 0;
                }
            }

            intent.setClass(AddAddressActivity.this,SetOneTimeScheduleActivity.class);
        }

        if(callingClass.equals("UserAccountActivity")) {

            intent.setClass(AddAddressActivity.this, UserAccountActivity.class);
        }

        startActivity(intent);

    }

    public CustomerAddress createObjectForCustomerAddress() {
        customerAddress =  new CustomerAddress(sharedPreferenceUtility.getCustomerID(), warehouseID, String.valueOf(latitude), String.valueOf(longitude), addressNickName.getText().toString(), societyNameET.getText().toString(), houseNumberET.getText().toString(), Integer.toString(floor), flatNumberET.getText().toString(), hasLift, mapAddress, landmark.getText().toString());
        return  customerAddress;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final Intent intent = new Intent();

        if(callingClass.equals("UserAccountActivity")){
            intent.setClass(AddAddressActivity.this, UserAccountActivity.class);
        }
        else{
            if(callingClass.equals("CheckoutActivity")){
                intent.setClass(AddAddressActivity.this, CheckoutActivity.class);
            }
            else {
                if(callingClass.equals("SetOneTimeScheduleActivity")){
                    intent.setClass(AddAddressActivity.this, SetOneTimeScheduleActivity.class);
                }
                else{
                    intent.setClass(AddAddressActivity.this, SetRecurringScheduleActivity.class);
                }
            }
        }
        startActivity(intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(HomeScreenActivity.cansList==null || HomeScreenActivity.cansList.isEmpty() || HomeScreenActivity.locationName==null || HomeScreenActivity.locationName.isEmpty()){

            Intent intent  = new Intent();
            intent.setClass(AddAddressActivity.this, FirstActivity.class);
            startActivity(intent);
        }
    }
}
