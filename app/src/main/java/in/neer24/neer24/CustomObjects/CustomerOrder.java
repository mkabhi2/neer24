package in.neer24.neer24.CustomObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vijaysingh on 12/22/2017.
 */

public class CustomerOrder implements Parcelable {

    private int canID;
    private String canName;
    private String canPhoto;
    private double canPrice;
    private String orderDate;
    private String orderDetailsID;
    private int orderID;
    private int orderQuantity;

    public CustomerOrder(){

    }

    public CustomerOrder(int canID, String canName, String canPhoto, double canPrice, String orderDate, String orderDetailsID, int orderID, int orderQuantity) {
        this.canID = canID;
        this.canName = canName;
        this.canPhoto = canPhoto;
        this.canPrice = canPrice;
        this.orderDate = orderDate;
        this.orderDetailsID = orderDetailsID;
        this.orderID = orderID;
        this.orderQuantity = orderQuantity;
    }

    public int getCanID() {
        return canID;
    }

    public void setCanID(int canID) {
        this.canID = canID;
    }

    public String getCanName() {
        return canName;
    }

    public void setCanName(String canName) {
        this.canName = canName;
    }

    public String getCanPhoto() {
        return canPhoto;
    }

    public void setCanPhoto(String canPhoto) {
        this.canPhoto = canPhoto;
    }

    public double getCanPrice() {
        return canPrice;
    }

    public void setCanPrice(double canPrice) {
        this.canPrice = canPrice;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderDetailsID() {
        return orderDetailsID;
    }

    public void setOrderDetailsID(String orderDetailsID) {
        this.orderDetailsID = orderDetailsID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }


    //Functions to implement parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(canID);
        parcel.writeInt(orderID);
        parcel.writeInt(orderQuantity);
        parcel.writeString(canName);
        parcel.writeString(canPhoto);
        parcel.writeString(orderDate);
        parcel.writeString(orderDetailsID);
        parcel.writeDouble(canPrice);
    }

    public static final Parcelable.Creator<CustomerOrder> CREATOR = new Parcelable.Creator<CustomerOrder>() {

        @Override
        public CustomerOrder createFromParcel(Parcel parcel) {
            return new CustomerOrder(parcel);
        }

        public CustomerOrder[] newArray(int size){
            return new CustomerOrder[size];
        }
    };

    private CustomerOrder(Parcel in){
        canID = in.readInt();
        orderID = in.readInt();
        orderQuantity = in.readInt();
        canName = in.readString();
        canPhoto = in.readString();
        orderDate = in.readString();
        orderDetailsID = in.readString();
        canPrice = in.readDouble();
    }

}
