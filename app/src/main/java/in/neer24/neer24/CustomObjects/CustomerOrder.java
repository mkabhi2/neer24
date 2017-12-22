package in.neer24.neer24.CustomObjects;

/**
 * Created by vijaysingh on 12/22/2017.
 */

public class CustomerOrder {

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
}
