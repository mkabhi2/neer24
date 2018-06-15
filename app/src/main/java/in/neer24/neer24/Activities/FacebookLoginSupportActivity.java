package in.neer24.neer24.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;
import in.neer24.neer24.Utilities.UtilityClass;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
                if (MobileNumber == null || !UtilityClass.validateMobile(MobileNumber)) {
                    Toast.makeText(FacebookLoginSupportActivity.this, "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                } else {
                    checkIfmobileNumberIsAlreadyRegisteredWithOtherAccount(MobileNumber);
                }
            }
        });
    }

    public void checkIfmobileNumberIsAlreadyRegisteredWithOtherAccount(final String mobileNumber) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://18.220.28.118:80/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<String> call = retroFitNetworkClient.checkIfUserIsRegisterdUserOrNotUsingMobileNumber(mobileNumber);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body().contains("true")) {
                    Toast.makeText(FacebookLoginSupportActivity.this, "Mobile Number Already Registered with other account", Toast.LENGTH_SHORT).show();
                } else {
                    sharedPreferenceUtility.setCustomerMobileNumberRegisterActivity(mobileNumber);
                    Intent intent = new Intent(FacebookLoginSupportActivity.this, OTPActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(FacebookLoginSupportActivity.this, "Please try after sometime", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
