package in.neer24.neer24.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import in.neer24.neer24.Activities.OrderDetailsActivity;
import in.neer24.neer24.CustomObjects.CustomerOrder;
import in.neer24.neer24.R;

/**
 * Created by vijaysingh on 12/22/2017.
 */

public class OrdersRVAdapter extends RecyclerView.Adapter<OrdersRVAdapter.ViewHolder>{

    private final List<CustomerOrder> orders;
    private final Context mContext;


    public OrdersRVAdapter(List<CustomerOrder> orders, Context context) {
        this.orders = orders;
        mContext = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView orderIDTV, totalAmountTV, orderDateTV, paymentModeTV, orderedTV, dispatchedTV, deliveredTV;
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

            cancelOrderBtn = (Button) itemView.findViewById(R.id.cancelOrderBtn);
            viewOrderBtn = (Button) itemView.findViewById(R.id.viewOrderBtn);

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

        final CustomerOrder customerOrder = orders.get(position);
        String rupeeSymbol = mContext.getResources().getString(R.string.Rs);

        holder.orderIDTV.setText("ORDER ID : " + customerOrder.getOrderID());
        holder.totalAmountTV.setText("Total Amount : " + rupeeSymbol + " " + customerOrder.getCanPrice());
        holder.orderDateTV.setText(customerOrder.getOrderDate().toString().substring(0,16));


        holder.viewOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, OrderDetailsActivity.class);
                intent.putExtra("order", customerOrder);
                ContextCompat.startActivity(mContext,intent,null);
            }
        });

        holder.cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, OrderDetailsActivity.class);
                ContextCompat.startActivity(mContext,intent,null);
            }
        });




    }
    @Override
    public int getItemCount() {
        return orders.size();
    }
}