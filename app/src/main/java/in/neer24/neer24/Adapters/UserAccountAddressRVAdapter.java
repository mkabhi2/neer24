package in.neer24.neer24.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.neer24.neer24.CustomObjects.CustomerAddress;
import in.neer24.neer24.R;

/**
 * Created by kumarpallav on 24/12/17.
 */

public class UserAccountAddressRVAdapter extends RecyclerView.Adapter<UserAccountAddressRVAdapter.ViewHolder> {


    private final List<CustomerAddress> addressList;
    private final Context mContext;


    public UserAccountAddressRVAdapter(List<CustomerAddress> addressList, Context context) {
        this.addressList = addressList;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView nickName;
        public ImageView image;

        public ViewHolder(View itemView) {

            super(itemView);

            name = (TextView) itemView.findViewById(R.id.rvAddressTextView);
            nickName = (TextView) itemView.findViewById(R.id.addressNickName);

        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View addressView = inflater.inflate(R.layout.rv_address_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(addressView);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CustomerAddress customerAddress = addressList.get(position);

        TextView nameTV = holder.name;
        TextView nickNameTV = holder.nickName;
        nameTV.setText(customerAddress.getFullAddress());
        nickNameTV.setText(customerAddress.getAddressNickName());

    }

    @Override
    public int getItemCount() {
        if(addressList!=null)
            return addressList.size();
        else
            return 0;
    }
}
