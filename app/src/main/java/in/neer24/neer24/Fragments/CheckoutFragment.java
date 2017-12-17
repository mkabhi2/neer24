package in.neer24.neer24.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;

import in.neer24.neer24.Adapters.CheckoutActivityLisviewAdapter;
import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.CustomObjects.Cart;
import in.neer24.neer24.R;


public class CheckoutFragment extends android.app.Fragment {

    ListView chekoutListView;
    private static TextView totalCheckoutFragmentTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_checkout, container, false);
        chekoutListView = (ListView) view.findViewById(R.id.checkoutFragmentListViews);


        CheckoutActivityLisviewAdapter customAdapter = new CheckoutActivityLisviewAdapter(getActivity());
        chekoutListView.setAdapter(customAdapter);

        totalCheckoutFragmentTextView=(TextView)view.findViewById(R.id.totalCheckoutFragmentTextView);

        updateTotalValueOfCart();

        return view;
    }

    public static void updateTotalValueOfCart() {
        HashMap<Can, Integer> cart = Cart.getCartList();
        double price = 0;
        double totalCost = 0;
        if (cart.size() == 0) {
            totalCheckoutFragmentTextView.setText("Total Amount" + "\t\t\t" + "0");
        } else {
            for (Can c : cart.keySet()) {
                price = c.getPrice();
                Integer qunatity = cart.get(c);
                totalCost = totalCost + (price * qunatity);
                if(c.getUserWantsNewCan() == 1){
                    totalCost = totalCost + c.getNewCanPrice() - c.getPrice();
                }
            }
            totalCheckoutFragmentTextView.setText("Total Amount" + "\t\t\t" + String.valueOf(totalCost));

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        chekoutListView.invalidate();
    }
}

