package in.neer24.neer24.CustomObjects;

/**
 * Created by kumarpallav on 25/12/17.
 */

public class CustomerAddress {

    private int customerID;
    private int customerAddressID;
    String address;

    public CustomerAddress(int customerID, int customerAddressID, String address) {
        this.customerID = customerID;
        this.customerAddressID = customerAddressID;
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
}
