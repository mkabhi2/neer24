package in.neer24.neer24.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.neer24.neer24.CustomObjects.Can;
import in.neer24.neer24.R;

/**
 * Created by abhmishr on 12/5/17.
 */

public class ScheduleRVAdapter extends RecyclerView.Adapter<ScheduleRVAdapter.ViewHolder> {

    private final List<Can> cans;
    private final Context mContext;


    public ScheduleRVAdapter(List<Can> cans, Context context) {
        this.cans = cans;
        mContext = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView description;
        public TextView price;
        public ImageView image;

        public ViewHolder(View itemView) {

            super(itemView);
            name = (TextView) itemView.findViewById(R.id.textView_name);
            description = (TextView) itemView.findViewById(R.id.textView_des);
            price = (TextView) itemView.findViewById(R.id.textView_price);
            image = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View canView = inflater.inflate(R.layout.rv_schedule_item, parent, false);

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

        nameTV.setText(can.getName());
        Double temPrice = new Double(can.getPrice());
        priceTV.setText(temPrice.toString());
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
    }

    @Override
    public int getItemCount() {
        return cans.size();
    }

}
