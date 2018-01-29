package in.neer24.neer24.CustomObjects;

/**
 * Created by kumarpallav on 25/12/17.
 */

public class CustomerAddress {

    private int customerID, warehouseID;
    private int customerAddressID;
    private String latitude, longitude, addressNickName;
    String address;

    public CustomerAddress(int customerID, int customerAddressID, String latitude, String longitude, String address) {
        this.customerID = customerID;
        this.customerAddressID = customerAddressID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
}
