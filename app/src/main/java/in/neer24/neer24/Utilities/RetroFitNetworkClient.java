package in.neer24.neer24.Utilities;

import java.util.List;

import in.neer24.neer24.CustomObjects.Can;
import retrofit2.Call;
import retrofit2.http.GET;
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
}
