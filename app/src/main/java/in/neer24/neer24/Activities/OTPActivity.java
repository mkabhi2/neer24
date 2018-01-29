package in.neer24.neer24.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import in.neer24.neer24.CustomObjects.Customer;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OTPActivity extends AppCompatActivity {


    Button resendOTPButton;
    Button verifyOTPButton;
    EditText enterOTPEditText;
    ProgressBar progressBarOTPActivity;
    SharedPreferenceUtility sharedPreferenceUtility;
    SmsVerifyCatcher smsVerifyCatcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        resendOTPButton = (Button) findViewById(R.id.resendOTPButton);
        verifyOTPButton = (Button) findViewById(R.id.verifyOTPButton);
        enterOTPEditText = (EditText) findViewById(R.id.enterOTPEditText);
        progressBarOTPActivity = (ProgressBar) findViewById(R.id.progressBarOTPActivity);
        resendOTPButton.setEnabled(false);
        sharedPreferenceUtility = new SharedPreferenceUtility(this);

        long timeElapsed = sharedPreferenceUtility.getOTPStopwatch();
        long currentTime = new Date().getTime();
        if (currentTime - timeElapsed > 30 * 1000) {
            //enable the button
            resendOTPButton.setEnabled(true);
        } else {
            resendOTPButton.setEnabled(false);
            new CountDownTimer(currentTime - timeElapsed, 1000) {
                public void onTick(long millisUntilFinished) {
                    resendOTPButton.setText("fdfjhsn" + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    resendOTPButton.setEnabled(true);
                }
            }.start();


            verifyOTPButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBarOTPActivity.setVisibility(View.VISIBLE);
                    String otp = enterOTPEditText.getText().toString();
                    verifyWithSentOTP(otp);
                }
            });

            smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
                @Override
                public void onSmsCatch(String message) {
                    String otpMessage = message.substring(message.indexOf("is") + 3, message.length());
                    enterOTPEditText.setText(otpMessage);
                    verifyOTPButton.performClick();
                }
            });


            resendOTPButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterOTPEditText.setText("vijay");
                    sharedPreferenceUtility.setOTPStopwatch(new Date().getTime());
                }
            });

        }
    }


    public void verifyWithSentOTP(String otp){
        String orignalOTP=sharedPreferenceUtility.getCustomerOTPRegisterActivity();
        if(orignalOTP.equals(otp)){
            registerUserSuccesFully();
        }else {
            progressBarOTPActivity.setVisibility(View.GONE);
            Toast.makeText(OTPActivity.this,"Invalid OTP,Try Again",Toast.LENGTH_SHORT).show();
        }
    }


    public void registerUserSuccesFully(){

        final Customer customer= createCustomerObject();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://18.220.28.118:8080/").client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<Customer> call=retroFitNetworkClient.registerUser(customer);

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                Customer returnedCustomerObject=(Customer)response.body();
                saveAlltheInfromationTosharedPreferences(returnedCustomerObject);
                takeUserToHomeScreen();
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                progressBarOTPActivity.setVisibility(View.GONE);
                Toast.makeText(OTPActivity.this,"Registration Failed",Toast.LENGTH_SHORT).show();
            }
        });




    }
    public void takeUserToHomeScreen(){
        progressBarOTPActivity.setVisibility(View.GONE);

        if (HomeScreenActivity.cansList.isEmpty()) {
            Intent intent = new Intent(OTPActivity.this, ChangeLocationActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent=new Intent(OTPActivity.this,HomeScreenActivity.class);
            startActivity(intent);
        }
    }

    public void saveAlltheInfromationTosharedPreferences(Customer customer){
        sharedPreferenceUtility.setCustomerID(customer.getCustomerID());
        sharedPreferenceUtility.setCustomerEmailID(customer.getCustomerEmail());
        sharedPreferenceUtility.setCustomerFirstName(customer.getCustomerFirstName());
        sharedPreferenceUtility.setCustomerLastName(customer.getCustomerLastName());
        sharedPreferenceUtility.setCustomerUniqueID(customer.getCustomerUniqueID());

    }

    private Customer createCustomerObject(){
        String firstName=sharedPreferenceUtility.getCustomerFirstNameRegisterActivity();
        String lastName=sharedPreferenceUtility.getCustomerLastNameRegisterActivity();
        String email=sharedPreferenceUtility.getCustomerEmailRegisterActivity();
        String password=sharedPreferenceUtility.getCustomerPasswordRegisterActivity();
        String mobileNumber=sharedPreferenceUtility.getCustomerMobileNumberRegisterActivity();
        return new Customer(email,firstName,lastName,mobileNumber,password);
    }

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        long timeElapsed = sharedPreferenceUtility.getOTPStopwatch();
        long currentTime = new Date().getTime();
        if (currentTime - timeElapsed > 30 * 1000) {
            resendOTPButton.setEnabled(true);
        } else {
            resendOTPButton.setEnabled(false);
            new CountDownTimer(currentTime - timeElapsed, 1000) {
                public void onTick(long millisUntilFinished) {
                    resendOTPButton.setText("fdfjhsn" + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    resendOTPButton.setEnabled(true);
                }
            }.start();


            verifyOTPButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressBarOTPActivity.setVisibility(View.VISIBLE);
                    String otp = enterOTPEditText.getText().toString();
                    verifyWithSentOTP(otp);
                }
            });


        }
    }

    /**
     * need for Android 6 real time permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}