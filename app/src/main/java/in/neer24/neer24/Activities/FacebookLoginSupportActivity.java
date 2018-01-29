package in.neer24.neer24.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;

public class FacebookLoginSupportActivity extends AppCompatActivity {

    private String MobileNumber;
    SharedPreferenceUtility sharedPreferenceUtility;
    EditText mobileNumberEditTextFacebookLoginSupportActivity;
    Button verifyMobileButtonFacebookLoginSupportActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login_support);

        sharedPreferenceUtility = new SharedPreferenceUtility(this);
        mobileNumberEditTextFacebookLoginSupportActivity = (EditText) findViewById(R.id.mobileNumberEditTextFacebookLoginSupportActivity);
        verifyMobileButtonFacebookLoginSupportActivity = (Button) findViewById(R.id.verifyMobileButtonFacebookLoginSupportActivity);


        verifyMobileButtonFacebookLoginSupportActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobileNumber = mobileNumberEditTextFacebookLoginSupportActivity.getText().toString();
                sharedPreferenceUtility.setCustomerMobileNumberRegisterActivity(MobileNumber);
                Intent intent = new Intent(FacebookLoginSupportActivity.this, OTPActivity.class);
                startActivity(intent);

            }
        });
    }
}
