package in.neer24.neer24.Utilities;

import java.util.ArrayList;
import java.util.List;

import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.CustomerOrder;
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

    @GET("/messenger/webapi/restraunt/specificrest")
    Call<List<Can>> getCansListForLocation(@Query("latitude") String latitude, @Query("longitude") String longitude);

    //http://192.168.0.7:8034/messenger/webapi/warehouse/specificwarehouse?latitude=12.948568&longitude=77.704373
    @GET("/neer/webapi/warehouse/specificwarehouse")
    Call<Integer> getWarehouseForLocation(@Query("latitude") String latitude, @Query("longitude") String longitude);

    @GET("/neer/webapi/warehousecan/warehousecanbywarehouseid")
    Call<List<Can>> getCansListForWarehouse(@Query("warehouseid") int warehouseID);

    @GET("/messenger/webapi/restraunt/specificrest")
    Call<List<Can>> getAllCansListForWarehouse(@Query("warehouseID") int warehouseID);

    @POST("/neer/webapi/ordertable/insertordertable")
    Call<String> insertIntoOrderTable(@Body OrderTable orderTable);

    @POST("/neer/webapi/orderdetailstable/insertorderdetailstable")
    Call<String> insertIntoOrderDetailsTable(@Body ArrayList<OrderDetails> orderDetails);

    @GET("/neer/webapi/customerorder/customerorderbycustomerid")
    Call<List<CustomerOrder>> getAllCustomerOrders(@Query("customerid") int customerid);

    @GET("/neer/webapi/warehousecan/updatecanquantity")
    Call<String> updateWarehouseCanTable(@Query("canid") int canID, @Query("warehouseid") int warehouseID);

    @GET("/neer/webapi/warehousecan/warehousecanbywarehouseid")
    Call<OrderDetails> getOrderDetailsForOrderID(@Query("orderID") int orderID);
}
