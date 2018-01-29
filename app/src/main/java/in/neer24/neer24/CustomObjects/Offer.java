package in.neer24.neer24.CustomObjects;

/**
 * Created by kumarpallav on 27/01/18.
 */

public class Offer {

    private int offerID;
    private String offerName;
    private String offerDescription;
    private String offerVaildFrom;
    private String offerVaildTo;
    private double maximumDiscount;
    private double percentageDiscount;
    private String couponCode;
    private String outputValue;
    private String isOneCanFree;

    public Offer() {

    }

    public Offer(String outputValue, String couponCode) {
        this.outputValue = outputValue;
        this.couponCode = couponCode;
    }

    public Offer(int offerID, String offerName, String offerDescription, String offerVaildFrom, String offerVaildTo,
                 double maximumDiscount, double percentageDiscount, String couponCode, String outputValue, String isOneCanFree) {
        this.offerID = offerID;
        this.offerName = offerName;
        this.offerDescription = offerDescription;
        this.offerVaildFrom = offerVaildFrom;
        this.offerVaildTo = offerVaildTo;
        this.maximumDiscount = maximumDiscount;
        this.percentageDiscount = percentageDiscount;
        this.couponCode = couponCode;
        this.outputValue = outputValue;
        this.isOneCanFree = isOneCanFree;
    }

    public String getOutputValue() {
        return outputValue;
    }

    public void setOutputValue(String outputValue) {
        this.outputValue = outputValue;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public int getOfferID() {
        return offerID;
    }

    public void setOfferID(int offerID) {
        this.offerID = offerID;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public String getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(String offerDescription) {
        this.offerDescription = offerDescription;
    }

    public String getOfferVaildFrom() {
        return offerVaildFrom;
    }

    public void setOfferVaildFrom(String offerVaildFrom) {
        this.offerVaildFrom = offerVaildFrom;
    }

    public String getOfferVaildTo() {
        return offerVaildTo;
    }

    public void setOfferVaildTo(String offerVaildTo) {
        this.offerVaildTo = offerVaildTo;
    }

    public double getMaximumDiscount() {
        return maximumDiscount;
    }

    public void setMaximumDiscount(double maximumDiscount) {
        this.maximumDiscount = maximumDiscount;
    }

    public double getPercentageDiscount() {
        return percentageDiscount;
    }

    public void setPercentageDiscount(double percentageDiscount) {
        this.percentageDiscount = percentageDiscount;
    }

    public void setIsOneCanFree(String isOneCanFree) {
        this.isOneCanFree = isOneCanFree;
    }

    public String getIsOneCanFree() {
        return isOneCanFree;
    }
}
