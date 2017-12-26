package in.neer24.neer24.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import in.neer24.neer24.R;

/**
 * Created by abhmishr on 12/5/17.
 */

public class CustomPagerAdapter extends PagerAdapter {
    private int[] slide_images={R.drawable.slide7, R.drawable.slide10, R.drawable.slide11, R.drawable.slide12};
    private Context ctx;
    private LayoutInflater layoutInflater;

    public CustomPagerAdapter(Context ctx){
        this.ctx=ctx;
    }

    @Override
    public int getCount() {
        return slide_images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==(LinearLayout)object);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position){

        layoutInflater=(LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view=layoutInflater.inflate(R.layout.swipe_layout,container,false);
        ImageView imageView=(ImageView)item_view.findViewById(R.id.iv_productImage);
        imageView.setImageResource(slide_images[position]);
        container.addView(item_view);
        return item_view;

    }
    public void destroyItem(ViewGroup container, int position, Object object){

        container.removeView((LinearLayout)object);
    }
}
