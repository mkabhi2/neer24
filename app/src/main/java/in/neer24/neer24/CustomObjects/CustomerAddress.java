package in.neer24.neer24.CustomObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kumarpallav on 25/12/17.
 */

public class CustomerAddress implements Parcelable {

    private int customerID, warehouseID;
    private int customerAddressID;
    private String latitude, longitude, addressNickName;
    private String societyName, buildingNumber, floorNumber, flatNumber ;
    int hasLift;
    String fullAddress;
    String mapAddress;
    String landmark;
    String houseAddress;

    public CustomerAddress(int customerID, int warehouseID, String latitude, String longitude,
                           String addressNickName, String societyName, String buildingNum, String floorNum, String flatNum,
                           int hasLift, String mapAddress, String landmark) {
        this.customerID = customerID;
        this.warehouseID = warehouseID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressNickName = addressNickName;
        this.societyName = societyName;
        this.buildingNumber = buildingNum;
        this.floorNumber = floorNum;
        this.flatNumber = flatNum;
        this.hasLift = hasLift;
        this.mapAddress = mapAddress;
        this.landmark = landmark;
        this.houseAddress = societyName + ", " + buildingNumber + ", Floor : " + floorNumber + ", " + flatNumber;
        this.fullAddress = houseAddress + mapAddress;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getCustomerAddressID() {
        return customerAddressID;
    }

    public void setCustomerAddressID(int customerAddressID) {
        this.customerAddressID = customerAddressID;
    }

    public String getFullAddress() {
        return houseAddress + mapAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getWarehouseID() {
        return warehouseID;
    }

    public void setWarehouseID(int warehouseID) {
        this.warehouseID = warehouseID;
    }

    public String getAddressNickName() {
        return addressNickName;
    }

    public void setAddressNickName(String addressNickName) {
        this.addressNickName = addressNickName;
    }

    public String getHouseAddress() {
        return houseAddress;
    }

    public void setHouseAddress(String houseAddress) {
        this.houseAddress = houseAddress;
    }

    public String getMapAddress() {
        return mapAddress;
    }

    public void setMapAddress(String mapAddress) {
        this.mapAddress = mapAddress;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }


    public String getSocietyName() {
        return societyName;
    }

    public void setSocietyName(String societyName) {
        this.societyName = societyName;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(String floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getFlatNumber() {
        return flatNumber;
    }

    public void setFlatNumber(String flatNumber) {
        this.flatNumber = flatNumber;
    }

    public int getHasLift() {
        return hasLift;
    }

    public void setHasLift(int hasLift) {
        this.hasLift = hasLift;
    }

    //Functions to implement parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(customerID);
        parcel.writeInt(warehouseID);
        parcel.writeInt(customerAddressID);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
        parcel.writeString(addressNickName);
        parcel.writeString(societyName);
        parcel.writeString(buildingNumber);
        parcel.writeString(floorNumber);
        parcel.writeString(flatNumber);
        parcel.writeInt(hasLift);
        parcel.writeString(fullAddress);
        parcel.writeString(houseAddress);
        parcel.writeString(mapAddress);
        parcel.writeString(landmark);
    }

    public static final Parcelable.Creator<CustomerAddress> CREATOR = new Parcelable.Creator<CustomerAddress>() {

        @Override
        public CustomerAddress createFromParcel(Parcel parcel) {
            return new CustomerAddress(parcel);
        }

        public CustomerAddress[] newArray(int size){
            return new CustomerAddress[size];
        }
    };

    private CustomerAddress(Parcel in){
        customerID = in.readInt();
        warehouseID = in.readInt();
        customerAddressID = in.readInt();
        latitude = in.readString();
        longitude = in.readString();
        addressNickName = in.readString();
        societyName = in.readString();
        buildingNumber = in.readString();
        floorNumber = in.readString();
        flatNumber = in.readString();
        hasLift = in.readInt();
        fullAddress = in.readString();
        houseAddress = in.readString();
        mapAddress = in.readString();
        landmark = in.readString();
    }


}
