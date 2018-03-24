package in.neer24.neer24.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import in.neer24.neer24.CustomObjects.CustomerAddress;
import in.neer24.neer24.R;

/**
 * Created by abhmishr on 2/20/18.
 */

public class SetAddressSelectorDialogAdapter extends ArrayAdapter<CustomerAddress> {

    ArrayList<CustomerAddress> addressList = new ArrayList<>();

    public SetAddressSelectorDialogAdapter(Context context, int listViewResourceId, ArrayList<CustomerAddress> objects) {
        super(context, listViewResourceId, objects);
        addressList = objects;
    }

    @Override
    public int getCount() {
        int a = super.getCount();
        return super.getCount() + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.select_address_custom_layout, null);
        TextView nickName = (TextView) v.findViewById(R.id.nickName);
        TextView addressDesc = (TextView) v.findViewById(R.id.addressDetail);
        ImageView icon = (ImageView) v.findViewById(R.id.addressIcon);

        if(position == super.getCount()) {
            View seperatorView = v.findViewById(R.id.seperatorView);
            seperatorView.setVisibility(View.GONE);
            nickName.setText("ADD NEW ADDRESS");
            nickName.setTextColor(getContext().getResources().getColor(R.color.Red));
            addressDesc.setVisibility(View.GONE);
            icon.setImageResource(R.drawable.add);
            return v;
        }

        nickName.setText(addressList.get(position).getAddressNickName());
        addressDesc.setText(addressList.get(position).getFullAddress());
        return v;

    }

}