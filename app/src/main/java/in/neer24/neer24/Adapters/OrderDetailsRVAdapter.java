package in.neer24.neer24.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.neer24.neer24.CustomObjects.CustomerOrder;
import in.neer24.neer24.R;

/**
 * Created by abhmishr on 12/25/17.
 */

public class OrderDetailsRVAdapter extends RecyclerView.Adapter<OrderDetailsRVAdapter.ViewHolder> {

    private final List<CustomerOrder> orderDetailsList;
    private final Context mContext;

    public OrderDetailsRVAdapter(List<CustomerOrder> orderDetails, Context mContext) {
       this.orderDetailsList = orderDetails;
        this.mContext = mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView itemNameTV, priceDetailsTV, totalCostTV;

        public ViewHolder(View itemView) {

            super(itemView);
            itemNameTV = (TextView) itemView.findViewById(R.id.itemNameTV);
            priceDetailsTV = (TextView) itemView.findViewById(R.id.priceDetialsTV);
            totalCostTV = (TextView) itemView.findViewById(R.id.totalCostTV);
        }
    }

    @Override
    public OrderDetailsRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View itemView = inflater.inflate(R.layout.rv_order_details_item, parent, false);

        OrderDetailsRVAdapter.ViewHolder viewHolder = new OrderDetailsRVAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(OrderDetailsRVAdapter.ViewHolder holder, int position) {

        String rupeeSymbol = mContext.getResources().getString(R.string.Rs);

        CustomerOrder orderDetails = orderDetailsList.get(position);

        holder.itemNameTV.setText(orderDetails.getCanName());

        String priceDetailsString = rupeeSymbol + " " + orderDetails.getCanPrice() + " x " + orderDetails.getCanQuantity() + " can(s)";
        int price = (int)(orderDetails.getCanPrice() * orderDetails.getCanQuantity());

        if(orderDetails.getIsNew()==1){
            priceDetailsString = priceDetailsString + " +\n" + rupeeSymbol + " 150 x " + orderDetails.getCanQuantity() + " new can(s)";
            price = price + (150 * orderDetails.getCanQuantity());
        }

        holder.priceDetailsTV.setText(priceDetailsString);
        holder.totalCostTV.setText(rupeeSymbol + price);

    }

    @Override
    public int getItemCount() {
        return orderDetailsList.size();
    }
}
