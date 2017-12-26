package in.neer24.neer24.CustomObjects;

/**
 * Created by vijaysingh on 12/23/2017.
 */

public class OrderDetails {

    private int canID;
    private int isNew;
    private int isNewDespenser;
    private int orderDetailsID;
    private int orderID;
    private int orderQuantity;

    Can cansList[];
    int canQuantity[];


    public OrderDetails(int canID, int isNew, int isNewDespenser, int orderID, int orderQuantity) {
        this.canID = canID;
        this.isNew = isNew;
        this.isNewDespenser = isNewDespenser;
        this.orderID = orderID;
        this.orderQuantity = orderQuantity;
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

    public int getIsNewDespenser() {
        return isNewDespenser;
    }

    public void setIsNewDespenser(int isNewDespenser) {
        this.isNewDespenser = isNewDespenser;
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

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public Can[] getCansList() {
        return cansList;
    }

    public void setCansList(Can[] cansList) {
        this.cansList = cansList;
    }

    public int[] getCanQuantity() {
        return canQuantity;
    }

    public void setCanQuantity(int[] canQuantity) {
        this.canQuantity = canQuantity;
    }
}
