package in.neer24.neer24.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        public TextView name;
        public TextView orderDate;
        public TextView price;
        public ImageView image;
        public TextView itemQuantity;

        public ViewHolder(View itemView) {

            super(itemView);
            itemQuantity = (TextView) itemView.findViewById(R.id.tv_productTotalItemRVOrder);
            price=(TextView) itemView.findViewById(R.id.tv_productTotalCostRVOrder);
            name = (TextView) itemView.findViewById(R.id.tv_productNameRVOrder);
            orderDate = (TextView) itemView.findViewById(R.id.tv_productOrderDateRVOrder);
            image = (ImageView) itemView.findViewById(R.id.iv_productImageRVOrder);

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

        TextView nameTV = holder.name;
        TextView orderDateTV = holder.orderDate;
        TextView itemQuantity = holder.itemQuantity;
        TextView price=holder.price;
        String rupeeSymbol = mContext.getResources().getString(R.string.Rs);

        nameTV.setText(customerOrder.getCanName().toString().replaceAll(",$",""));
        itemQuantity.setText(Integer.valueOf(customerOrder.getOrderQuantity()).toString());
        price.setText(rupeeSymbol+" "+ Double.valueOf(customerOrder.getCanPrice()).toString());
        orderDateTV.setText(customerOrder.getOrderDate().toString().substring(0,16));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, OrderDetailsActivity.class);
                ContextCompat.startActivity(mContext,intent,null);
                Toast.makeText(mContext, "Recycle Click" + position, Toast.LENGTH_SHORT).show();
            }
        });


    }
    @Override
    public int getItemCount() {
        return orders.size();
    }
}