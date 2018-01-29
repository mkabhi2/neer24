package in.neer24.neer24.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import in.neer24.neer24.R;

public class UserProfileActivity extends AppCompatActivity {


    private String FirstName;
    private String Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Intent intent = getIntent();
        String jsondata = intent.getStringExtra("userProfile");
        try {
            JSONObject response = new JSONObject(jsondata);
            Email = response.get("email").toString();
            FirstName = response.get("name").toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
