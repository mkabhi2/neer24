package in.neer24.neer24.CustomObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vijaysingh on 12/23/2017.
 */


public class  OrderTable implements Parcelable{

    private int orderID;
    private int customerID;
    private int warehouseID;
    private int deliveryBoyID;
    private String orderPaymentID;

    private String orderDate;
    private String deliveryTime;

    private double totalAmount;
    private double discountedAmount;
    private double amountPaid;
    private String paymentMode;
    private String couponCode;
    private int numberOfFreeCansAvailed;
    private int customerAddressID;
    private int isNormalDelivery;
    private int isNightDelivery;
    private int isScheduleDelivery;
    private int isRecurringDelivery;


    private String customerUniqueID;

    private int isOrdered;
    private int isDispatched;
    private int isDelivered;
    private int isCancelled;

    private String endDate;
    private int deliveryLeft;
    private int recuringOrderFrequency;
    private int totalCansOrdered;

    private int hasFloorCharge;

    OrderDetails orderContents[];

    public OrderTable() {

    }


    public OrderTable(int customerID, int warehouseID, int deliveryBoyID, String orderPaymentID, String orderDate, String deliveryTime, double totalAmount, double discountedAmount, double amountPaid, String paymentMode, String couponCode, int numberOfFreeCansAvailed, int customerAddressID, int isNormalDelivery, int isNightDelivery, int isScheduleDelivery, int isRecurringDelivery, String customerUniqueID, int isOrdered, int isDispatched, int isDelivered, int isCancelled, String endDate, int deliveryLeft, int recuringOrderFrequency, int totalCansOrdered, int hasFloorCharge) {
        this.customerID = customerID;
        this.warehouseID = warehouseID;
        this.deliveryBoyID = deliveryBoyID;
        this.orderPaymentID = orderPaymentID;
        this.orderDate = orderDate;
        this.deliveryTime = deliveryTime;
        this.totalAmount = totalAmount;
        this.discountedAmount = discountedAmount;
        this.amountPaid = amountPaid;
        this.paymentMode = paymentMode;
        this.couponCode = couponCode;
        this.numberOfFreeCansAvailed = numberOfFreeCansAvailed;
        this.customerAddressID = customerAddressID;
        this.isNormalDelivery = isNormalDelivery;
        this.isNightDelivery = isNightDelivery;
        this.isScheduleDelivery = isScheduleDelivery;
        this.isRecurringDelivery = isRecurringDelivery;
        this.customerUniqueID = customerUniqueID;
        this.isOrdered = isOrdered;
        this.isDispatched = isDispatched;
        this.isDelivered = isDelivered;
        this.isCancelled = isCancelled;
        this.endDate = endDate;
        this.deliveryLeft = deliveryLeft;
        this.recuringOrderFrequency = recuringOrderFrequency;
        this.totalCansOrdered = totalCansOrdered;
        this.hasFloorCharge = hasFloorCharge;
    }

    public static final Parcelable.Creator<OrderTable> CREATOR = new Parcelable.Creator<OrderTable>() {

        @Override
        public OrderTable createFromParcel(Parcel parcel) {
            return new OrderTable(parcel);
        }

        public OrderTable[] newArray(int size){
            return new OrderTable[size];
        }
    };

    private OrderTable(Parcel in){
        orderID = in.readInt();
        customerID = in.readInt();
        warehouseID = in.readInt();
        deliveryBoyID = in.readInt();
        orderPaymentID = in.readString();
        orderDate = in.readString();
        deliveryTime = in.readString();
        totalAmount = in.readDouble();
        discountedAmount = in.readDouble();
        amountPaid = in.readDouble();
        paymentMode = in.readString();
        couponCode = in.readString();
        numberOfFreeCansAvailed = in.readInt();
        customerAddressID = in.readInt();
        isNormalDelivery = in.readInt();
        isNightDelivery = in.readInt();
        isScheduleDelivery = in.readInt();
        isRecurringDelivery = in.readInt();
        customerUniqueID = in.readString();
        isOrdered = in.readInt();
        isDispatched = in.readInt();
        isDelivered = in.readInt();
        isCancelled = in.readInt();
        endDate = in.readString();
        deliveryLeft = in.readInt();
        recuringOrderFrequency = in.readInt();
        totalCansOrdered = in.readInt();
        hasFloorCharge = in.readInt();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(orderID);
        parcel.writeInt(customerID);
        parcel.writeInt(warehouseID);
        parcel.writeInt(deliveryBoyID);
        parcel.writeString(orderPaymentID);
        parcel.writeString(orderDate);
        parcel.writeString(deliveryTime);
        parcel.writeDouble(totalAmount);
        parcel.writeDouble(discountedAmount);
        parcel.writeDouble(amountPaid);
        parcel.writeString(paymentMode);
        parcel.writeString(couponCode);
        parcel.writeInt(numberOfFreeCansAvailed);
        parcel.writeInt(customerAddressID);
        parcel.writeInt(isNormalDelivery);
        parcel.writeInt(isNightDelivery);
        parcel.writeInt(isScheduleDelivery);
        parcel.writeInt(isRecurringDelivery);
        parcel.writeString(customerUniqueID);
        parcel.writeInt(isOrdered);
        parcel.writeInt(isDispatched);
        parcel.writeInt(isDelivered);
        parcel.writeInt(isCancelled);
        parcel.writeString(endDate);
        parcel.writeInt(deliveryLeft);
        parcel.writeInt(recuringOrderFrequency);
        parcel.writeInt(totalCansOrdered);
        parcel.writeInt(hasFloorCharge);
    }

    public int getIsOrdered() {
        return isOrdered;
    }

    public void setIsOrdered(int isOrdered) {
        this.isOrdered = isOrdered;
    }

    public int getIsDispatched() {
        return isDispatched;
    }

    public void setIsDispatched(int isDispatched) {
        this.isDispatched = isDispatched;
    }

    public int getIsDelivered() {
        return isDelivered;
    }

    public void setIsDelivered(int isDelivered) {
        this.isDelivered = isDelivered;
    }

    public int getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(int isCancelled) {
        this.isCancelled = isCancelled;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getDeliveryLeft() {
        return deliveryLeft;
    }

    public void setDeliveryLeft(int deliveryLeft) {
        this.deliveryLeft = deliveryLeft;
    }

    public int getRecuringOrderFrequency() {
        return recuringOrderFrequency;
    }

    public void setRecuringOrderFrequency(int recuringOrderFrequency) {
        this.recuringOrderFrequency = recuringOrderFrequency;
    }

    public int getTotalCansOrdered() {
        return totalCansOrdered;
    }

    public void setTotalCansOrdered(int totalCansOrdered) {
        this.totalCansOrdered = totalCansOrdered;
    }

    public int getNumberOfFreeCansAvailed() {
        return numberOfFreeCansAvailed;
    }

    public void setNumberOfFreeCansAvailed(int numberOfFreeCansAvailed) {
        this.numberOfFreeCansAvailed = numberOfFreeCansAvailed;
    }

    public double getDiscountedAmount() {
        return discountedAmount;
    }

    public void setDiscountedAmount(double discountedAmount) {
        this.discountedAmount = discountedAmount;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public int getCustomerAddressID() {
        return customerAddressID;
    }

    public void setCustomerAddressID(int customerAddressID) {
        this.customerAddressID = customerAddressID;
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

    public String getOrderPaymentID() {
        return orderPaymentID;
    }

    public void setOrderPaymentID(String orderPaymentID) {
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

    public OrderDetails[] getOrderContents() {
        return orderContents;
    }

    public void setOrderContents(OrderDetails[] orderContents) {
        this.orderContents = orderContents;
    }

    public int getHasFloorCharge() {
        return hasFloorCharge;
    }

    public void setHasFloorCharge(int hasFloorCharge) {
        this.hasFloorCharge = hasFloorCharge;
    }
}