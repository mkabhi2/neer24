package in.neer24.neer24.CustomObjects;

/**
 * Created by kumarpallav on 24/12/17.
 */

public class Customer {

    private int customerID;
    private String address;
    private String customerEmail;
    private String customerFirstName;
    private String customerLastName;
    private String customerMobileNumber;
    private String customerPhotoPath;
    private String customerUniqueID;
    private String password;
    private String outputValue;



    public Customer(String email, String customerMobileNumber, String password){
        if(customerMobileNumber==null){
            this.customerEmail=email;
        }else if(email==null){
            this.customerMobileNumber=customerMobileNumber;
        }
        this.password=password;
    }

    public Customer(String customerEmail, String customerFirstName, String customerLastName, String customerMobileNumber, String password) {
        this.customerEmail = customerEmail;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.customerMobileNumber = customerMobileNumber;
        this.password = password;
    }

    public Customer(int customerID, String address, String customerEmail, String customerFirstName, String customerLastName, String customerMobileNumber, String customerPhotoPath, String customerUniqueID) {
        this.customerID = customerID;
        this.address = address;
        this.customerEmail = customerEmail;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.customerMobileNumber = customerMobileNumber;
        this.customerPhotoPath = customerPhotoPath;
        this.customerUniqueID = customerUniqueID;
    }

    public String getOutputValue() {
        return outputValue;
    }

    public void setOutputValue(String outputValue) {
        this.outputValue = outputValue;
    }

    public String getPassword() {
        return password;
    }

    public void setCustomerPassword(String Password) {
        this.password = Password;
    }


    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getCustomerMobileNumber() {
        return customerMobileNumber;
    }

    public void setCustomerMobileNumber(String customerMobileNumber) {
        this.customerMobileNumber = customerMobileNumber;
    }

    public String getCustomerPhotoPath() {
        return customerPhotoPath;
    }

    public void setCustomerPhotoPath(String customerPhotoPath) {
        this.customerPhotoPath = customerPhotoPath;
    }

    public String getCustomerUniqueID() {
        return customerUniqueID;
    }

    public void setCustomerUniqueID(String customerUniqueID) {
        this.customerUniqueID = customerUniqueID;
    }
}
