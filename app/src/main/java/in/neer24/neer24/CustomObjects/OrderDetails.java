package in.neer24.neer24.CustomObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vijaysingh on 12/23/2017.
 */

public class OrderDetails implements Parcelable{

    private int canID;
    private int isNew;
    private int orderDetailsID;
    private int orderID;
    private int canQuantity;

    private String canName;
    private double canPrice;



    public OrderDetails(int canID, int isNew, int orderDetailsID, int orderID, int canQuantity) {
        this.canID = canID;
        this.isNew = isNew;
        this.orderDetailsID = orderDetailsID;
        this.orderID = orderID;
        this.canQuantity = canQuantity;
    }


    public static final Parcelable.Creator<OrderDetails> CREATOR = new Parcelable.Creator<OrderDetails>() {

        @Override
        public OrderDetails createFromParcel(Parcel parcel) {
            return new OrderDetails(parcel);
        }

        public OrderDetails[] newArray(int size){
            return new OrderDetails[size];
        }
    };

    private OrderDetails(Parcel in){
        canID = in.readInt();
        isNew = in.readInt();
        orderDetailsID = in.readInt();
        orderID = in.readInt();
        canQuantity = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(canID);
        parcel.writeInt(isNew);
        parcel.writeInt(orderDetailsID);
        parcel.writeInt(orderID);
        parcel.writeInt(canQuantity);
    }

    public int getCanID() {
        return canID;
    }

    public void setCanID(int canID) {
        this.canID = canID;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }

    public int getOrderDetailsID() {
        return orderDetailsID;
    }

    public void setOrderDetailsID(int orderDetailsID) {
        this.orderDetailsID = orderDetailsID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getCanQuantity() {
        return canQuantity;
    }

    public void setCanQuantity(int canQuantity) {
        this.canQuantity = canQuantity;
    }

    public String getCanName() {
        return canName;
    }

    public void setCanName(String canName) {
        this.canName = canName;
    }

    public double getCanPrice() {
        return canPrice;
    }

    public void setCanPrice(double canPrice) {
        this.canPrice = canPrice;
    }
}
