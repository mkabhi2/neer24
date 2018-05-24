package in.neer24.neer24.Utilities;

import java.util.ArrayList;
import java.util.List;

import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.Customer;
import in.neer24.neer24.CustomObjects.CustomerAddress;
import in.neer24.neer24.CustomObjects.Offer;
import in.neer24.neer24.CustomObjects.OrderDetails;
import in.neer24.neer24.CustomObjects.OrderTable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by abhmishr on 12/5/17.
 */

public interface RetroFitNetworkClient {

    @GET("/neer/webapi/customers/customeremail")
    Call<String> checkIfUserIsRegisterdUserOrNotUsingEmail(@Query("emailid") String email);



    @GET("/neer/webapi/customers/customermobile")
    Call<String> checkIfUserIsRegisterdUserOrNotUsingMobileNumber(@Query("mobilenumber") String mobileNumber);



    @GET("/neer/webapi/otpauth/requestotp")
    Call<String> requestOTPFromServer(@Query("mobilenumber") String mobileNumber);



    @POST("/neer/webapi/customers/logincustomerusingemail")
    Call<Customer> authenticateUserAndLoginUsingEmail(@Body Customer customer);


    @GET("/neer/webapi/customers/socialaccountlogin")
    Call<Customer> checkIfUserHasSignedInUsingSocialAccount(@Query("emailid") String emailID);


    @POST("/neer/webapi/customers/logincustomerusingmobile")
    Call<Customer> authenticateUserAndLoginUsingMobileNumber(@Body Customer customer);


    @POST("/neer/webapi/customers/registercustomer")
    Call<Customer> registerUser(@Body Customer customer);


    @GET("/neer/webapi/restraunt/specificrest")
    Call<List<Can>> getCansListForLocation(@Query("latitude") String latitude, @Query("longitude") String longitude);


    //http://18.220.28.118//messenger/webapi/warehouse/specificwarehouse?latitude=12.948568&longitude=77.704373
    @GET("/neer/webapi/warehouse/specificwarehouse")
    Call<Integer> getWarehouseForLocation(@Query("latitude") String latitude, @Query("longitude") String longitude);


    @GET("/neer/webapi/warehousecan/warehousecanbywarehouseid")
    Call<List<Can>> getCansListForWarehouse(@Query("warehouseid") int warehouseID);


    @GET("/neer/webapi/restraunt/specificrest")
    Call<List<Can>> getAllCansListForWarehouse(@Query("warehouseID") int warehouseID);

    @POST("/neer/webapi/ordertable/insertordertable")
    Call<String> insertIntoOrderTable(@Body OrderTable orderTable);

    @POST("/neer/webapi/orderdetailstable/insertorderdetailstable")
    Call<String> insertIntoOrderDetailsTable(@Body ArrayList<OrderDetails> orderDetails);

    @GET("/neer/webapi/ordertable/orderbycustomerid")
    Call<List<OrderTable>> getAllCustomerOrders(@Query("customerid") int customerid);

    @GET("/neer/webapi/warehousecan/updatecanquantity")
    Call<String> updateWarehouseCanTable(@Query("canid") int canID, @Query("warehouseid") int warehouseID);


    @GET("/neer/webapi/customeraddress/customeraddressbycustomerid")
    Call<List<CustomerAddress>> getAllCustomerAddress(@Query("customerid") int customerid, @Query("customeruniqueid") String customerUniqueId);

    @GET("/neer/webapi/orderdetailstable/ordercompletedetails")
    Call<List<OrderDetails>> getOrderDetailsForOrderID(@Query("orderid") int orderID);


    @GET("/neer/webapi/customers/updatepassword")
    Call<String> updatePasswordOnServer(@Query("emailid")String emailID,@Query("oldpassword")String oldPassword, @Query("newpassword") String newPassword);

    @GET("/neer/webapi/offers/verifycoupon")
    Call<Offer> verifyIfCouponIsValidFromServer(@Query("coupon")String coupon);

    @GET("/neer/webapi/offers/validateforfreecan")
    Call<String> checkIfUserIsEligibleForFreeCanOrNot(@Query("emailID")String emailID);

    @GET("/neer/webapi/customers/sendemailforpassword")
    Call<String> sendEmailWithNewPassword(@Query("emailid")String email,@Query("mobilenumber")String mobileNumber);

    @POST("/neer/webapi/customeraddress/addcustomeraddress")
    Call<String> addCustomerAddressToServer(@Body CustomerAddress customerAddress);

    @GET("/neer/webapi/customers/freecans")
    Call<Integer> getFreeCanNumForUser(@Query("emailid")String email);

    @GET("/neer/webapi/offers/referralcode")
    Call<String> increaseNumberOfFreeCansReferral(@Query("referralcode")String referralCode);

    @GET("/neer/webapi/deliveryboyapp/cancelorder/")
    Call<String> cancelOrder(@Query("orderid")int orderID);

}
