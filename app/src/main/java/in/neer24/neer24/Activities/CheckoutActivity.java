package in.neer24.neer24.Activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.TextView;

import in.neer24.neer24.Fragments.CheckoutFragment;
import in.neer24.neer24.R;

public class CheckoutActivity extends AppCompatActivity {

    private Toolbar checkoutActivityToolbar;
    private TextView cartSummaryTextView;
    // private TextView checkoutActivityTotalCartValueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        checkoutActivityToolbar=(Toolbar)findViewById(R.id.checkoutActivityToolbar);
        cartSummaryTextView=(TextView)findViewById(R.id.checkoutActivityCartSummaryTextView);
        //checkoutActivityTotalCartValueTextView=(TextView)findViewById(R.id.checkoutActivityTotalCartValueTextView);
        cartSummaryTextView.setGravity(Gravity.CENTER_VERTICAL);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CheckoutFragment checkoutFragment = new CheckoutFragment();
        fragmentTransaction.add(R.id.cartFragmentContainer, checkoutFragment, "checkoutFragment");
        fragmentTransaction.commit();

        //updateTotalValueOfCart();

    }

//    public void updateTotalValueOfCart() {
//        HashMap<Dish, Integer> cart = Cart.getCartList();
//        double price = 0;
//        double totalCost = 0;
//        if (cart.size() == 0) {
//            checkoutActivityTotalCartValueTextView.setText("Total Amount" + "\t" + "0");
//        } else {
//            for (Dish d : cart.keySet()) {
//                price = Double.parseDouble(d.getDishPrice());
//                Integer value = cart.get(d);
//                totalCost = totalCost + (price * value);
//            }
//            checkoutActivityTotalCartValueTextView.setText("Total Amount" + "\t" + String.valueOf(totalCost));
//
//        }
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HomeScreenActivity.showCartDetailsSummary();
    }
}

