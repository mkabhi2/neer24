package in.neer24.neer24.Adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import in.neer24.neer24.Fragments.CheckoutFragment;
import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.Cart;
import in.neer24.neer24.R;

/**
 * Created by abhmishr on 12/8/17.
 */

public class CheckoutActivityLisviewAdapter extends BaseAdapter {

    Context context;
    public CheckoutActivityLisviewAdapter(Context context){
        this.context=context;
    }

    @Override
    public int getCount() {
        return Cart.getCartList().size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View view=convertView;
        if(view==null){
            LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
            view= layoutInflater.inflate(R.layout.lv_checkout_item,null);
        }

        final Can can = Cart.getCanByPosition(position);

        final TextView dishNameCheckoutListView=(TextView)view.findViewById(R.id.dishNameCheckoutListView);
        final Button increaseByOneCheckoutListView=(Button)view.findViewById(R.id.increaseByOneCheckoutListView);
        final Button decreaseByOneCheckoutListView=(Button)view.findViewById(R.id.decreaseByOneCheckoutListView);
        final Button quantityCheckoutListView = (Button)view.findViewById(R.id.quantityCheckoutListView);
        final TextView showAmountCheckoutListView=(TextView)view.findViewById(R.id.showAmountCheckoutListView);
        final ImageView photoIV = (ImageView) view.findViewById(R.id.imageView);

        if(can.getName().equalsIgnoreCase("aquasure")){
            photoIV.setImageResource(R.drawable.aquasure);
        }
        if(can.getName().equalsIgnoreCase("bisleri")) {
            photoIV.setImageResource(R.drawable.bisleri);
        }
        if(can.getName().equalsIgnoreCase("dispencer")) {
            photoIV.setImageResource(R.drawable.dispencer);
        }
        if(can.getName().equalsIgnoreCase("kinley")) {
            photoIV.setImageResource(R.drawable.kinley);
        }
        if(can.getName().equalsIgnoreCase("normal")) {
            photoIV.setImageResource(R.drawable.normal);
        }
        //Picasso.with(mContext).load(can.getPhoto()).into(photoIV);

        showAmountCheckoutListView.setText(String.valueOf(Cart.getTotalDishPriceByPosition(Cart.getCanByPosition(position))));
        dishNameCheckoutListView.setText(Cart.getCanNameByPosition(position));
        Integer count = new Integer(Cart.getQuantityForSelectedItem(can));
        quantityCheckoutListView.setText(count.toString());
        showAmountCheckoutListView.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER);
        dishNameCheckoutListView.setGravity(Gravity.CENTER_VERTICAL);
        increaseByOneCheckoutListView.setGravity(Gravity.CENTER_VERTICAL);
        decreaseByOneCheckoutListView.setGravity(Gravity.CENTER_VERTICAL);

        increaseByOneCheckoutListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cart.addItemsToCarts(can);

                if(Cart.getQuantityForSelectedItem(can) == 1){

                    decreaseByOneCheckoutListView.setVisibility(View.VISIBLE);

                }

                Integer count = new Integer(Cart.getQuantityForSelectedItem(can));
                quantityCheckoutListView.setText(count.toString());
                Double amount = count * can.getPrice();
                showAmountCheckoutListView.setText(amount.toString());
                showAmountCheckoutListView.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER);
                CheckoutFragment.updateTotalValueOfCart();
            }
        });

        decreaseByOneCheckoutListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cart.deleteItemsToCartsFromCheckoutPage(can);

                if(Cart.getQuantityForSelectedItem(can) == 0){

                    decreaseByOneCheckoutListView.setVisibility(View.INVISIBLE);

                }

                Integer count = new Integer(Cart.getQuantityForSelectedItem(can));
                quantityCheckoutListView.setText(count.toString());
                Double amount = count * can.getPrice();
                showAmountCheckoutListView.setText(amount.toString());
                showAmountCheckoutListView.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER);
                CheckoutFragment.updateTotalValueOfCart();
            }
        });

        return view;

    }

}
