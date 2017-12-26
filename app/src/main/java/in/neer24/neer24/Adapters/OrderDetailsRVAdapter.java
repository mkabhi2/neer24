package in.neer24.neer24.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.R;

/**
 * Created by abhmishr on 12/25/17.
 */

public class OrderDetailsRVAdapter extends RecyclerView.Adapter<OrderDetailsRVAdapter.ViewHolder> {

    private Can cansList[];
    private int canQuantities[];
    private final Context mContext;

    public OrderDetailsRVAdapter(Can[] cansList, int[] canQuantities, Context mContext) {
        this.cansList = cansList;
        this.canQuantities = canQuantities;
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

        Can can = cansList[position];
        int canQuantity = canQuantities[position];

        holder.itemNameTV.setText(can.getName());
        holder.priceDetailsTV.setText(rupeeSymbol + " " + can.getPrice() + " x " + canQuantity + " (cans)");
        holder.totalCostTV.setText(rupeeSymbol + (can.getPrice() * canQuantity));

    }

    @Override
    public int getItemCount() {
        return cansList.length;
    }
}
