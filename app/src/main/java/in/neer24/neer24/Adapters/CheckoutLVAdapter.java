package in.neer24.neer24.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import in.neer24.neer24.Activities.CheckoutActivity;
import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.NormalCart;
import in.neer24.neer24.Fragments.CheckoutFragment;
import in.neer24.neer24.R;

/**
 * Created by abhmishr on 12/8/17.
 */

public class CheckoutLVAdapter extends BaseAdapter {

    private Context context;
    private TextView noItemsInCart;

    public CheckoutLVAdapter(Context context, TextView noItemsInCart) {
        this.context = context;
        this.noItemsInCart = noItemsInCart;
    }

    @Override
    public int getCount() {
        return NormalCart.getCartList().size();
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
        View view = convertView;
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            view = layoutInflater.inflate(R.layout.lv_checkout_item, null);
        }

        if(NormalCart.getCartList().size() == 0) {
            CheckoutActivity.proceedToPayButton.setVisibility(View.VISIBLE);
        }

        final Can can = NormalCart.getCanByPosition(position);

        final TextView dishNameCheckoutListView = (TextView) view.findViewById(R.id.tv_checkout_product_name);
        final Button increaseByOneCheckoutListView = (Button) view.findViewById(R.id.increaseByOneCheckoutListView);
        final Button decreaseByOneCheckoutListView = (Button) view.findViewById(R.id.decreaseByOneCheckoutListView);
        final Button quantityCheckoutListView = (Button) view.findViewById(R.id.quantityCheckoutListView);
        final TextView showAmountCheckoutListView = (TextView) view.findViewById(R.id.showAmountCheckoutListView);
        final ImageView photoIV = (ImageView) view.findViewById(R.id.iv_productImage);
        final TextView priceTV = (TextView) view.findViewById(R.id.tv_productPrice);
        final SwitchCompat newCanSwitch = (SwitchCompat) view.findViewById(R.id.switch_has_lift);
        final TextView switchTV = (TextView) view.findViewById(R.id.switchTV);


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
                break;
            case "kinley":
                photoIV.setImageResource(R.drawable.kinley);
                break;
            default:
                photoIV.setImageResource(R.drawable.normal);
        }
        //Picasso.with(mContext).load(can.getPhoto()).into(photoIV);

        final String rupeeSymbol = context.getResources().getString(R.string.Rs);

        switchTV.setText("I don't have a can to replace ( " + rupeeSymbol + " " + can.getNewCanPrice() + " / new can )  ");

        showAmountCheckoutListView.setText("Total    :    " + rupeeSymbol + " " + String.valueOf(NormalCart.getTotalCanPriceByPosition(NormalCart.getCanByPosition(position))));
        dishNameCheckoutListView.setText(NormalCart.getCanNameByPosition(position));

        Double price = new Double(NormalCart.getCanPriceByPosition(position));
        priceTV.setText(rupeeSymbol + " " + price.toString() + " / unit");

        Integer count = new Integer(NormalCart.getQuantityForSelectedItem(can));
        quantityCheckoutListView.setText(count.toString());

        showAmountCheckoutListView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
        dishNameCheckoutListView.setGravity(Gravity.CENTER_VERTICAL);
        increaseByOneCheckoutListView.setGravity(Gravity.CENTER_VERTICAL);
        decreaseByOneCheckoutListView.setGravity(Gravity.CENTER_VERTICAL);
        final int position1 = position;

        if(can.getUserWantsNewCan()==1) {
            newCanSwitch.setChecked(true);
        }
        else {
            newCanSwitch.setChecked(false);
        }

        increaseByOneCheckoutListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NormalCart.getQuantityForSelectedItem(can) == 0) {
                    newCanSwitch.setVisibility(View.VISIBLE);
                    switchTV.setText("I don't have a can to replace ( " + rupeeSymbol + " " + can.getNewCanPrice() + " / new can )  ");
                }
                else {
                    switchTV.setText("I don't have a can to replace ( " + rupeeSymbol + " " + can.getNewCanPrice() + " / new can )  ");
                }

                NormalCart.addItemsToCarts(can);

                if (NormalCart.getQuantityForSelectedItem(can) == 1) {

                    decreaseByOneCheckoutListView.setVisibility(View.VISIBLE);

                }

                Integer count = new Integer(NormalCart.getQuantityForSelectedItem(can));
                quantityCheckoutListView.setText(count.toString());
                showAmountCheckoutListView.setText("Total    :    " + context.getResources().getString(R.string.Rs) + " " + String.valueOf(NormalCart.getTotalCanPriceByPosition(can)));

                showAmountCheckoutListView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                CheckoutFragment.updateBill();
            }
        });

        decreaseByOneCheckoutListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NormalCart.getQuantityForSelectedItem(can) == 1) {
                    newCanSwitch.setChecked(false);
                    can.setUserWantsNewCan(0);
                }

                NormalCart.deleteItemsToCartsFromCheckoutPage(can);
                notifyDataSetChanged();
                if(NormalCart.getCartList().size() == 0) {
                    noItemsInCart.setVisibility(View.VISIBLE);
                    CheckoutActivity.proceedToPayButton.setVisibility(View.GONE);
                    CheckoutActivity.addressView.setVisibility(View.GONE);
                }

                Integer count = new Integer(NormalCart.getQuantityForSelectedItem(can));
                quantityCheckoutListView.setText(count.toString());
                showAmountCheckoutListView.setText("Total    :    " + context.getResources().getString(R.string.Rs) + " " + String.valueOf(NormalCart.getTotalCanPriceByPosition(can)));
                showAmountCheckoutListView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                CheckoutFragment.updateBill();
            }
        });

        newCanSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newCanSwitch.isChecked()) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setMessage("This option lets you order new can, if you don't already have a can to replace with the can we deliver. You will be charged a refundable security deposit for the new can. We assure you the security deposit will be refunded back to you once you return the can. Are you sure you want a new can ?");
                    alertDialogBuilder.setPositiveButton("yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    can.setUserWantsNewCan(1);
                                    showAmountCheckoutListView.setText("Total    :    " + context.getResources().getString(R.string.Rs) + " " + String.valueOf(NormalCart.getTotalCanPriceByPosition(NormalCart.getCanByPosition(position1))));
                                    showAmountCheckoutListView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                                    CheckoutFragment.updateBill();
                                }
                            });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newCanSwitch.setChecked(false);
                            can.setUserWantsNewCan(0);
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();

                } else {
                    can.setUserWantsNewCan(0);
                    showAmountCheckoutListView.setText("Total    :    " + context.getResources().getString(R.string.Rs) + " " + String.valueOf(NormalCart.getTotalCanPriceByPosition(NormalCart.getCanByPosition(position1))));
                    showAmountCheckoutListView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER);
                    CheckoutFragment.updateBill();
                }
            }
        });

        if(can.getIsReplacable()==0) {
            switchTV.setVisibility(View.GONE);
            newCanSwitch.setVisibility(View.GONE);
        }
        else {
            switchTV.setVisibility(View.VISIBLE);
            newCanSwitch.setVisibility(View.VISIBLE);
        }


        return view;

    }

}
