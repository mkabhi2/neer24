package in.neer24.neer24.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by vijaysingh on 12/17/2017.
 */

public class SharedPreferenceUtility {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    public SharedPreferenceUtility(Context context) {
        this.context = context;
        //context.get
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public void setLoggedIn(boolean loggedIn) {
        editor.putBoolean("loggedinmode", loggedIn);
        editor.commit();
    }

    public void setLoggediInVia(String logInMode) {
        editor.putString("loginmode", logInMode);
        editor.commit();
    }

    public String getLoggediInVia(){
        return sharedPreferences.getString("loginmode",null);
    }

    public boolean loggedIn() {
        return sharedPreferences.getBoolean("loggedinmode", false);

    }

    public void setFirstTimeLaunch(boolean firstTimeLaunch) {
        editor.putBoolean("firstTimeLaunch", firstTimeLaunch);
        editor.commit();
    }

    public boolean getFirstTimeLaunch() {
        return sharedPreferences.getBoolean("firstTimeLaunch", true);
    }

    public void setCustomerID(int customerID) {
        editor.putInt("CUSTOMERID", customerID);
        editor.commit();
    }

    public int getCustomerID() {
        return sharedPreferences.getInt("CUSTOMERID", 0);
    }


    public void setLocationLatitude(String latitude) {
        editor.putString("currentLatitude", latitude);
        editor.commit();
    }

    public String getLocationLatitude() {
        return sharedPreferences.getString("currentLatitude", "0");
    }


    public void setLocationLongitude(String longitude) {
        editor.putString("currentLongitude", longitude);
        editor.commit();
    }


    public String getLocationLongitude() {
        return sharedPreferences.getString("currentLongitude", "0");
    }

    public void setWareHouseID(int wareHouseID) {
        editor.putInt("warehouseID", wareHouseID);
        editor.commit();
    }

    public int getWareHouseID() {
        return sharedPreferences.getInt("warehouseID", 0);
    }

    public void setCustomerEmailID(String emailID) {
        editor.putString("EMAILID", emailID);
        editor.commit();
    }

    public String getCustomerEmailID() {
        return sharedPreferences.getString("EMAILID", null);
    }

    public void setCustomerFirstName(String customerFirstName) {
        editor.putString("FIRSTNAME", customerFirstName);
        editor.commit();
    }


    public String getCustomerFirstName() {
        return sharedPreferences.getString("FIRSTNAME", null);
    }

    public void setCustomerUniqueID(String customerUniqueID) {
        editor.putString("UNIQUEID", customerUniqueID);
        editor.commit();
    }

    public String getCustomerUniqueID() {
        return sharedPreferences.getString("UNIQUEID", null);
    }

    public void setCustomerFirstNameRegisterActivity(String name) {
        editor.putString("CustomerFirstNameRegisterActivity", name);
        editor.commit();
    }

    public String getCustomerFirstNameRegisterActivity() {
        return sharedPreferences.getString("CustomerFirstNameRegisterActivity", null);
    }

    public void setCustomerEmailRegisterActivity(String email) {
        editor.putString("CustomerEmailRegisterActivity", email);
        editor.commit();
    }

    public String getCustomerEmailRegisterActivity() {
        return sharedPreferences.getString("CustomerEmailRegisterActivity", null);
    }

    public void setCustomerMobileNumberRegisterActivity(String mobileNumber) {
        editor.putString("CustomerMobileNumberRegisterActivity", mobileNumber);
        editor.commit();
    }

    public String getCustomerMobileNumberRegisterActivity() {
        return sharedPreferences.getString("CustomerMobileNumberRegisterActivity", null);
    }

    public void setCustomerPasswordRegisterActivity(String password) {
        editor.putString("CustomerPasswordRegisterActivity", password);
        editor.commit();
    }

    public String getCustomerPasswordRegisterActivity() {
        return sharedPreferences.getString("CustomerPasswordRegisterActivity", null);
    }

    public void setCustomerOTPRegisterActivity(String otp) {
        editor.putString("CustomerOTPRegisterActivity", otp);
        editor.commit();
    }

    public String getCustomerOTPRegisterActivity() {
        return sharedPreferences.getString("CustomerOTPRegisterActivity", null);
    }

    public void setOTPStopwatch(long time) {
        editor.putLong("OTPWATCH", time);
        editor.commit();
    }

    public long getOTPStopwatch() {
        return sharedPreferences.getLong("OTPWATCH", 0);
    }

    public void setCustomerMobileNumber(String customerMobileNumber) {
        editor.putString("MOBILENUMBER", customerMobileNumber);
        editor.commit();
    }

    public String getCustomerMobileNumber() {
        return sharedPreferences.getString("MOBILENUMBER", null);
    }
}
