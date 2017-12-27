package in.neer24.neer24.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.neer24.neer24.Activities.OrderDetailsActivity;
import in.neer24.neer24.R;

/**
 * Created by kumarpallav on 23/12/17.
 */

public class HelpRVAdapter extends RecyclerView.Adapter<HelpRVAdapter.ViewHolder> {


    private final ArrayList<String> helpContentList;
    private final Context mContext;


    public HelpRVAdapter(ArrayList<String> helpCOntentList, Context context) {
        this.helpContentList = helpCOntentList;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewHelpActivity;

        public ViewHolder(View itemView) {

            super(itemView);
            textViewHelpActivity = (TextView) itemView.findViewById(R.id.textViewHelpActivity);

        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View helpView = inflater.inflate(R.layout.rv_help_items, parent, false);

        ViewHolder viewHolder = new ViewHolder(helpView);

        return viewHolder;
    }

    public void onBindViewHolder(HelpRVAdapter.ViewHolder holder, final int position) {
        final String helpContent = helpContentList.get(position);

        TextView nameTV = holder.textViewHelpActivity;

        nameTV.setText(helpContent);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(mContext, OrderDetailsActivity.class);
//                ContextCompat.startActivity(mContext,intent,null);
                Toast.makeText(mContext, "Recycle Click" + position, Toast.LENGTH_SHORT).show();


            }
        });


    }
    @Override
    public int getItemCount() {
        return helpContentList.size();
    }


}
