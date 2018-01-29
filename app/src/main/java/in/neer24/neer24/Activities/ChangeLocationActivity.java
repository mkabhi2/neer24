package in.neer24.neer24.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import in.neer24.neer24.Adapters.ChangeLocationAddressRVAdapter;
import in.neer24.neer24.R;

public class ChangeLocationActivity extends AppCompatActivity {

    public static RecyclerView addressRV;
    Button placePickerBtn;
    LinearLayout comingSoonTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_location);

        Bundle bundle = getIntent().getExtras();
        boolean isFromHomeScreen = bundle.getBoolean("isFromHomeScreen");

        comingSoonTV = (LinearLayout) findViewById(R.id.tv_comingSoon);
        addressRV = (RecyclerView) findViewById(R.id.address_rv);
        addressRV.setAdapter(new ChangeLocationAddressRVAdapter(HomeScreenActivity.addressList, this));

        if(isFromHomeScreen) {
            comingSoonTV.setVisibility(View.GONE);
        }

        placePickerBtn = (Button) findViewById(R.id.btn_selectLocationOnMap);
        placePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }
}
