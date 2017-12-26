package in.neer24.neer24.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import in.neer24.neer24.Activities.HomeScreenActivity;
import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.NormalCart;
import in.neer24.neer24.R;

/**
 * Created by abhmishr on 12/5/17.
 */

public class HomeRVAdapter extends RecyclerView.Adapter<HomeRVAdapter.ViewHolder> {

    private final List<Can> cans;
    private final Context mContext;


    public HomeRVAdapter(List<Can> cans, Context context) {
        this.cans = cans;
        mContext = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView description;
        public TextView price;
        public ImageView image;
        public Button increaseByOne;
        public Button decreaseByOne;
        public Button displayItemCount;

        public ViewHolder(View itemView) {

            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_productName);
            description = (TextView) itemView.findViewById(R.id.tv_productQuality);
            price = (TextView) itemView.findViewById(R.id.tv_productPrice);
            image = (ImageView) itemView.findViewById(R.id.iv_productImage);
            increaseByOne = (Button) itemView.findViewById(R.id.btn_qty_increase);
            decreaseByOne = (Button) itemView.findViewById(R.id.btn_qty_decrease);
            displayItemCount = (Button) itemView.findViewById(R.id.btn_order_or_qty);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View canView = inflater.inflate(R.layout.rv_home_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(canView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Can can = cans.get(position);

        TextView nameTV = holder.name;
        TextView priceTV = holder.price;
        TextView descriptionTV = holder.description;
        ImageView photoIV = holder.image;
        final Button increaseByOneBT = holder.increaseByOne;
        final Button decreaseByOneBT = holder.decreaseByOne;
        final Button displayItemCountBT = holder.displayItemCount;

        String rupeeSymbol = mContext.getResources().getString(R.string.Rs);

        nameTV.setText(can.getName() + " 20 L");
        Double temPrice = new Double(can.getPrice());
        priceTV.setText(rupeeSymbol + " " + temPrice.toString());

        String canName = can.getName().toLowerCase();
        switch (canName) {
            case "aquasure":
                photoIV.setImageResource(R.drawable.aquasure);
                break;
            case "bisleri":
                photoIV.setImageResource(R.drawable.bisleri);
                break;
            case "despenser":
                photoIV.setImageResource(R.drawable.dispencer);
                descriptionTV.setVisibility(View.GONE);
                break;
            case "kinley":
                photoIV.setImageResource(R.drawable.kinley);
                break;
            default:
                photoIV.setImageResource(R.drawable.normal);
        }
        //Picasso.with(mContext).load(can.getPhoto()).into(photoIV);


        HashMap<Can, Integer> ch = NormalCart.getCartList();

        if (NormalCart.getQuantityForSelectedItem(can) == 0) {

            displayItemCountBT.setText("ADD");
            decreaseByOneBT.setVisibility(View.INVISIBLE);
            increaseByOneBT.setVisibility(View.INVISIBLE);
        } else {
            Integer count = new Integer(NormalCart.getQuantityForSelectedItem(can));
            displayItemCountBT.setText(count.toString());
            HomeScreenActivity.showCartDetailsSummary();
        }

        displayItemCountBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NormalCart.getQuantityForSelectedItem(can) == 0) {
                    NormalCart.addItemsToCarts(can);
                    displayItemCountBT.setText("1");
                    displayItemCountBT.requestLayout();
                    decreaseByOneBT.setVisibility(View.VISIBLE);
                    increaseByOneBT.setVisibility(View.VISIBLE);
                    HomeScreenActivity.showCartDetailsSummary();
                }
            }
        });

        increaseByOneBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NormalCart.addItemsToCarts(can);
                Integer count = new Integer(NormalCart.getQuantityForSelectedItem(can));
                displayItemCountBT.setText(count.toString());
                HomeScreenActivity.showCartDetailsSummary();
            }
        });

        decreaseByOneBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NormalCart.getQuantityForSelectedItem(can) == 1 && can.getUserWantsNewCan() == 1) {
                    can.setUserWantsNewCan(0);
                }

                NormalCart.deleteItemsFromCarts(can);

                if (NormalCart.getQuantityForSelectedItem(can) == 0) {

                    displayItemCountBT.setText("ADD");
                    increaseByOneBT.setVisibility(View.INVISIBLE);
                    decreaseByOneBT.setVisibility(View.INVISIBLE);
                } else {
                    Integer count = new Integer(NormalCart.getQuantityForSelectedItem(can));
                    displayItemCountBT.setText(count.toString());
                }
                HomeScreenActivity.showCartDetailsSummary();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cans.size();
    }
}