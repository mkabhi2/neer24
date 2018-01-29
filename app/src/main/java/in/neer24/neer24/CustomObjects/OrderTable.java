package in.neer24.neer24.CustomObjects;

/**
 * Created by vijaysingh on 12/23/2017.
 */


public class OrderTable {

    private int orderID;
    private int customerID;
    private int warehouseID;
    private int deliveryBoyID;
    private double totalAmount;
    private String orderDate;
    private String deliveryTime;
    private int orderPaymentID;
    private int isNormalDelivery;
    private int isNightDelivery;
    private int isScheduleDelivery;
    private int isRecurringDelivery;
    private String customerUniqueID;
    private int isCouponCodeUsed;
    private int couponCodeID;


    public OrderTable(){

    }

    public OrderTable(int customerID, int warehouseID, int deliveryBoyID, double totalAmount, String orderDate, String deliveryTime, int orderPaymentID, int isNormalDelivery, int isNightDelivery, int isScheduleDelivery, int isRecurringDelivery,String customerUniqueID) {
        this.customerID = customerID;
        this.warehouseID = warehouseID;
        this.deliveryBoyID = deliveryBoyID;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.deliveryTime = deliveryTime;
        this.orderPaymentID = orderPaymentID;
        this.isNormalDelivery = isNormalDelivery;
        this.isNightDelivery = isNightDelivery;
        this.isScheduleDelivery = isScheduleDelivery;
        this.isRecurringDelivery = isRecurringDelivery;
        this.customerUniqueID=customerUniqueID;
    }

    public String getCustomerUniqueID() {
        return customerUniqueID;
    }

    public void setCustomerUniqueID(String customerUniqueID) {
        this.customerUniqueID = customerUniqueID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getWarehouseID() {
        return warehouseID;
    }

    public void setWarehouseID(int warehouseID) {
        this.warehouseID = warehouseID;
    }

    public int getDeliveryBoyID() {
        return deliveryBoyID;
    }

    public void setDeliveryBoyID(int deliveryBoyID) {
        this.deliveryBoyID = deliveryBoyID;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public int getOrderPaymentID() {
        return orderPaymentID;
    }

    public void setOrderPaymentID(int orderPaymentID) {
        this.orderPaymentID = orderPaymentID;
    }

    public int getIsNormalDelivery() {
        return isNormalDelivery;
    }

    public void setIsNormalDelivery(int isNormalDelivery) {
        this.isNormalDelivery = isNormalDelivery;
    }

    public int getIsNightDelivery() {
        return isNightDelivery;
    }

    public void setIsNightDelivery(int isNightDelivery) {
        this.isNightDelivery = isNightDelivery;
    }

    public int getIsScheduleDelivery() {
        return isScheduleDelivery;
    }

    public void setIsScheduleDelivery(int isScheduleDelivery) {
        this.isScheduleDelivery = isScheduleDelivery;
    }

    public int getIsRecurringDelivery() {
        return isRecurringDelivery;
    }

    public void setIsRecurringDelivery(int isRecurringDelivery) {
        this.isRecurringDelivery = isRecurringDelivery;
    }

}
