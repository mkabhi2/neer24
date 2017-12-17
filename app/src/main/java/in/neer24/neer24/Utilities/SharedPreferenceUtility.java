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

    public SharedPreferenceUtility(Context context){
        this.context=context;
        //context.get
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
        editor=sharedPreferences.edit();
    }

    public void setLoggedIn(boolean loggedIn){
        editor.putBoolean("loggedinmode",loggedIn);
        editor.commit();
    }

    public boolean loggedIn(){
        return sharedPreferences.getBoolean("loggedinmode",false);

    }

    public void setFirstTimeLaunch(boolean firstTimeLaunch){
        editor.putBoolean("firstTimeLaunch",firstTimeLaunch);
        editor.commit();
    }

    public boolean getFirstTimeLaunch(){
        return sharedPreferences.getBoolean("firstTimeLaunch",true);
    }
    public void setCustomerID(int customerID){
        editor.putInt("CUSTOMERID",customerID);
        editor.commit();
    }

    public int getCustomerID(){
        return sharedPreferences.getInt("CUSTOMERID",0);
    }


    public void setLocationLatitude(String latitude){
        editor.putString("currentLatitude",latitude);
        editor.commit();
    }

    public String getLocationLatitude(){
        return sharedPreferences.getString("currentLatitude","0");
    }



    public void setLocationLongitude(String longitude){
        editor.putString("currentLongitude",longitude);
        editor.commit();
    }


    public String getLocationLongitude(){
        return sharedPreferences.getString("currentLongitude","0");
    }

    public void setWareHouseID(int wareHouseID){
        editor.putInt("warehouseID",wareHouseID);
    }

    public int getWareHouseID(){
        return sharedPreferences.getInt("warehouseID",0);
    }


}
