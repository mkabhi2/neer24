package in.neer24.neer24.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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

    private GoogleApiClient mGoogleApiClient;
    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;

    ProgressBar progressBarFacebookLoginSupportActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login_support);

        sharedPreferenceUtility = new SharedPreferenceUtility(this);
        mobileNumberEditTextFacebookLoginSupportActivity = (EditText) findViewById(R.id.mobileNumberEditTextFacebookLoginSupportActivity);
        verifyMobileButtonFacebookLoginSupportActivity = (Button) findViewById(R.id.verifyMobileButtonFacebookLoginSupportActivity);


        progressBarFacebookLoginSupportActivity = (ProgressBar) findViewById(R.id.progressBarFacebookLoginSupportActivity);
        progressBarFacebookLoginSupportActivity.setVisibility(View.GONE);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        verifyMobileButtonFacebookLoginSupportActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                progressBarFacebookLoginSupportActivity.setVisibility(View.VISIBLE);

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                MobileNumber = mobileNumberEditTextFacebookLoginSupportActivity.getText().toString();
                if (MobileNumber == null || !UtilityClass.validateMobile(MobileNumber)) {
                    progressBarFacebookLoginSupportActivity.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                    progressBarFacebookLoginSupportActivity.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(FacebookLoginSupportActivity.this, "Mobile Number Already Registered with other account", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarFacebookLoginSupportActivity.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    sharedPreferenceUtility.setCustomerMobileNumberRegisterActivity(mobileNumber);
                    Intent intent = new Intent(FacebookLoginSupportActivity.this, OTPActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressBarFacebookLoginSupportActivity.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(FacebookLoginSupportActivity.this, "Please try after sometime", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        String loggedInVia = sharedPreferenceUtility.getLoggedInViaTemporary();
        if (loggedInVia != null) {
            if (loggedInVia.equals("gmail")) {
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                            }
                        });
            } else if (loggedInVia.equals("facebook")) {
                LoginManager.getInstance().logOut();
            }
        }
    }

}
