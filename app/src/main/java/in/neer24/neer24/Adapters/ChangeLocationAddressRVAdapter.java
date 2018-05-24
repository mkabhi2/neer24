package in.neer24.neer24.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.neer24.neer24.Activities.ChangeLocationActivity;
import in.neer24.neer24.Activities.HomeScreenActivity;
import in.neer24.neer24.Activities.ScheduleDeliveryActivity;
import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.CustomerAddress;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.FetchLocationNameService;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kumarpallav on 24/12/17.
 */

public class ChangeLocationAddressRVAdapter extends RecyclerView.Adapter<ChangeLocationAddressRVAdapter.ViewHolder> {


    private final List<CustomerAddress> addressList;
    private final Context mContext;
    int warehouseID;
    SharedPreferenceUtility sharedPreferenceUtility;
    Snackbar snackbar, retroCallFailSnackbar;
    View addressView;


    public ChangeLocationAddressRVAdapter(List<CustomerAddress> addressList, Context context) {
        this.addressList = addressList;
        mContext = context;
        sharedPreferenceUtility = new SharedPreferenceUtility(mContext);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView nickName;
        public ImageView image;

        public ViewHolder(View itemView) {

            super(itemView);

            name = (TextView) itemView.findViewById(R.id.addressTV);
            nickName = (TextView) itemView.findViewById(R.id.addressNickNameTV);
            image = (ImageView) itemView.findViewById(R.id.locIcon);

        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        addressView = inflater.inflate(R.layout.rv_change_location, parent, false);

        ViewHolder viewHolder = new ViewHolder(addressView);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CustomerAddress customerAddress = addressList.get(position);

        TextView nameTV = holder.name;
        ImageView imageView = holder.image;
        TextView nickNameTV = holder.nickName;

        nickNameTV.setText(customerAddress.getAddressNickName());
        nameTV.setText(customerAddress.getFullAddress());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferenceUtility.setLocationLatitude(customerAddress.getLatitude());
                sharedPreferenceUtility.setLocationLongitude(customerAddress.getLongitude());

                ChangeLocationActivity.dialog.setMessage("Loading. Please wait...");
                ChangeLocationActivity.dialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
                ChangeLocationActivity.dialog.show();

                Retrofit.Builder builder1 = new Retrofit.Builder()
                        .baseUrl("http://18.220.28.118/")
                        .addConverterFactory(GsonConverterFactory.create());
                Retrofit retrofit1 = builder1.build();

                RetroFitNetworkClient retroFitNetworkClient1 = retrofit1.create(RetroFitNetworkClient.class);
                Call<Integer> call1 = retroFitNetworkClient1.getWarehouseForLocation(customerAddress.getLatitude(), customerAddress.getLongitude());

                call1.enqueue(new Callback<Integer>() {
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {

                        if (response.body() != null) {
                            warehouseID = Integer.parseInt(response.body().toString());
                        }

                        if (warehouseID != 0) {

                            sharedPreferenceUtility.setWareHouseID(warehouseID);

                            FetchLocationNameService locationAddress = new FetchLocationNameService();
                            locationAddress.getAddressFromLocation(Float.parseFloat(customerAddress.getLatitude()), Float.parseFloat(customerAddress.getLongitude()),
                                    mContext, new GeocoderHandler());

                            Retrofit.Builder builder = new Retrofit.Builder()
                                    .baseUrl("http://18.220.28.118/")
                                    .addConverterFactory(GsonConverterFactory.create());
                            Retrofit retrofit = builder.build();

                            RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
                            Call<List<Can>> call2 = retroFitNetworkClient.getCansListForWarehouse(warehouseID);


                            call2.enqueue(new Callback<List<Can>>() {
                                @Override
                                public void onResponse(Call<List<Can>> call, Response<List<Can>> response) {

                                    HomeScreenActivity.cansList = (ArrayList<Can>) response.body();
                                    ScheduleDeliveryActivity.allCans = new ArrayList<Can>(response.body());

                                    if (HomeScreenActivity.recyclerView != null) {
                                        HomeScreenActivity.recyclerView.setAdapter(new HomeRVAdapter(HomeScreenActivity.cansList, mContext));
                                        HomeScreenActivity.recyclerView.invalidate();
                                    }
                                    if (ScheduleDeliveryActivity.recyclerView != null) {
                                        ScheduleDeliveryActivity.recyclerView.setAdapter(new HomeRVAdapter(ScheduleDeliveryActivity.allCans, mContext));
                                        ScheduleDeliveryActivity.recyclerView.invalidate();
                                    }
                                    ChangeLocationActivity.dialog.cancel();
                                    Intent intent = new Intent(mContext, HomeScreenActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    mContext.startActivity(intent);

                                }

                                @Override
                                public void onFailure(Call<List<Can>> call, Throwable t) {
                                    retroCallFailSnackbar = Snackbar
                                            .make(addressView, "Failed to load data. No Internet Connection.", Snackbar.LENGTH_SHORT);
                                    retroCallFailSnackbar.show();
                                }
                            });

                        }
                        else {
                            ChangeLocationActivity.dialog.cancel();
                            Intent intent = new Intent(mContext, ChangeLocationActivity.class);
                            mContext.startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {
                        retroCallFailSnackbar = Snackbar
                                .make(addressView, "Failed to load data. No Internet Connection.", Snackbar.LENGTH_SHORT);
                        retroCallFailSnackbar.show();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {

        if(addressList!=null)
            return addressList.size();
        else
            return 0;
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            HomeScreenActivity.locationName = locationAddress;
        }
    }
}
