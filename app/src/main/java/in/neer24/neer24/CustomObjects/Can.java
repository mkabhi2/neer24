package in.neer24.neer24.CustomObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by abhmishr on 12/5/17.
 */

public class Can implements Parcelable {
    int canID;
    int warehouseID;
    String canName;
    String canPhoto;
    double canPrice;
    String canQuantity;

    public Can(){}

    public Can(int canID, int warehouseID, String name, String photo, double price, String quantity) {
        this.canID = canID;
        this.warehouseID = warehouseID;
        this.canName = name;
        this.canPhoto = photo;
        this.canPrice = price;
        this.canQuantity = quantity;
    }

    //Functions to implement parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(canID);
        parcel.writeInt(warehouseID);
        parcel.writeString(canName);
        parcel.writeString(canPhoto);
        parcel.writeDouble(canPrice);
        parcel.writeString(canQuantity);
    }

    public static final Parcelable.Creator<Can> CREATOR = new Parcelable.Creator<Can>() {

        @Override
        public Can createFromParcel(Parcel parcel) {
            return new Can(parcel);
        }

        public Can[] newArray(int size){
            return new Can[size];
        }
    };

    private Can(Parcel in){
        canID = in.readInt();
        warehouseID = in.readInt();
        canName = in.readString();
        canPhoto = in.readString();
        canPrice = in.readDouble();
        canQuantity = in.readString();
    }

    public int getCanID() {
        return canID;
    }

    public void setCanID(int canID) {
        this.canID = canID;
    }

    public int getWarehouseID() {
        return warehouseID;
    }

    public void setWarehouseID(int warehouseID) {
        this.warehouseID = warehouseID;
    }

    public String getName() {
        return canName;
    }

    public void setName(String name) {
        this.canName = name;
    }

    public String getPhoto() {
        return canPhoto;
    }

    public void setPhoto(String photo) {
        this.canPhoto = photo;
    }

    public double getPrice() {
        return canPrice;
    }

    public void setPrice(double price) {
        this.canPrice = price;
    }

    public String getQuantity() {
        return canQuantity;
    }

    public void setQuantity(String quantity) {
        this.canQuantity = quantity;
    }
}
