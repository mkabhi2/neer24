package in.neer24.neer24.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import in.neer24.neer24.CustomObjects.OrderTable;
import in.neer24.neer24.R;

/**
 * Created by vijaysingh on 12/22/2017.
 */

public class OrdersRVAdapter extends RecyclerView.Adapter<OrdersRVAdapter.ViewHolder>{

    private final List<OrderTable> orders;
    private final Context mContext;


    public OrdersRVAdapter(List<OrderTable> orders, Context context) {
        this.orders = orders;
        mContext = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView orderIDTV, totalAmountTV, orderDateTV, paymentModeTV, orderedTV, dispatchedTV, deliveredTV, cancelledTV ;
        Button cancelOrderBtn, viewOrderBtn;

        public ViewHolder(View itemView) {

            super(itemView);
            orderIDTV = (TextView) itemView.findViewById(R.id.orderID);
            totalAmountTV=(TextView) itemView.findViewById(R.id.total_tv);
            orderDateTV = (TextView) itemView.findViewById(R.id.orderDateAndTime);
            paymentModeTV = (TextView) itemView.findViewById(R.id.paymentMode);
            orderedTV = (TextView) itemView.findViewById(R.id.tv_ordered);
            dispatchedTV = (TextView) itemView.findViewById(R.id.tv_dispatched);
            deliveredTV = (TextView) itemView.findViewById(R.id.tv_delivered);
            cancelledTV = (TextView) itemView.findViewById(R.id.tv_cancelled);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View orderView = inflater.inflate(R.layout.rv_orders_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(orderView);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {

        final OrderTable customerOrder = orders.get(position);
        String rupeeSymbol = mContext.getResources().getString(R.string.Rs);

        if(customerOrder.getIsNormalDelivery()==1)
            holder.orderIDTV.setText("ORDER ID : " + customerOrder.getOrderID());
        if(customerOrder.getIsRecurringDelivery()==1)
            holder.orderIDTV.setText("RECURRING ORDER ID : " + customerOrder.getOrderID());
        if(customerOrder.getIsScheduleDelivery()==1)
            holder.orderIDTV.setText("SCHEDULED ORDER ID : " + customerOrder.getOrderID());

        holder.totalAmountTV.setText("Total Amount Paid : " + rupeeSymbol + " " + customerOrder.getAmountPaid());
        holder.orderDateTV.setText(customerOrder.getOrderDate().toString().substring(0,16));

        if(customerOrder.getIsDispatched()==1){
            holder.dispatchedTV.setBackgroundColor(mContext.getResources().getColor(R.color.Green));
        }
        if(customerOrder.getIsDelivered()==1) {
            holder.dispatchedTV.setVisibility(View.GONE);
            holder.orderedTV.setVisibility(View.GONE);
            holder.deliveredTV.setVisibility(View.VISIBLE);
            holder.deliveredTV.setBackgroundColor(mContext.getResources().getColor(R.color.Green));
        }

        if(customerOrder.getIsCancelled()==1){
            holder.dispatchedTV.setVisibility(View.GONE);
            holder.orderedTV.setVisibility(View.GONE);
            holder.cancelledTV.setVisibility(View.VISIBLE);
            holder.cancelledTV.setBackgroundColor(mContext.getResources().getColor(R.color.Red));
        }

        holder.paymentModeTV.setText(customerOrder.getPaymentMode());

    }
    @Override
    public int getItemCount() {
        if(orders!=null)
            return orders.size();
        else
            return 0;
    }
}