package in.neer24.neer24.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
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


    Pinview pinViewOTPActivity;
    Button resendOTPButton;
    Button verifyOTPButton;
    ProgressBar progressBarOTPActivity;
    SharedPreferenceUtility sharedPreferenceUtility;
    SmsVerifyCatcher smsVerifyCatcher;
    String enteredOTP;
    String generatedOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);


        pinViewOTPActivity = (Pinview) findViewById(R.id.pinViewOTPActivty);
        resendOTPButton = (Button) findViewById(R.id.resendOTPButton);
        verifyOTPButton = (Button) findViewById(R.id.verifyOTPButton);
        progressBarOTPActivity = (ProgressBar) findViewById(R.id.progressBarOTPActivity);
        sharedPreferenceUtility = new SharedPreferenceUtility(this);

        resendOTPButton.setEnabled(false);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                OTPActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        resendOTPButton.setEnabled(true);

                    }
                });
            }
        }).start();


        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                resendOTPButton.setText(Long.valueOf(millisUntilFinished / 1000).toString());
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                resendOTPButton.setText("Resend OTP!");
            }

        }.start();

        requestOTPFromServer(sharedPreferenceUtility.getCustomerMobileNumberRegisterActivity(), sharedPreferenceUtility.getCustomerEmailRegisterActivity());


        verifyOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarOTPActivity.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                verifyWithSentOTP(enteredOTP);
            }
        });

        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String otpMessage = message.substring(message.indexOf("is") + 3, message.length());
                pinViewOTPActivity.setValue(otpMessage);
                verifyOTPButton.performClick();
            }
        });


        resendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOTPButton.setEnabled(false);

                new CountDownTimer(10000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        resendOTPButton.setText(Long.valueOf(millisUntilFinished / 1000).toString());
                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
                        resendOTPButton.setText("Resend OTP!");
                    }

                }.start();
                progressBarOTPActivity.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                requestOTPFromServer(sharedPreferenceUtility.getCustomerMobileNumberRegisterActivity(), sharedPreferenceUtility.getCustomerEmailRegisterActivity());
                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                resendOTPButton.setEnabled(true);
                            }
                        });
                    }
                }, 10000);

            }
        });


        pinViewOTPActivity.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean b) {
                Toast.makeText(OTPActivity.this, pinview.getValue(), Toast.LENGTH_SHORT).show();
                enteredOTP = pinview.getValue();
            }
        });
    }


    public void verifyWithSentOTP(String otp) {
        if (generatedOTP.equals(otp)) {
            registerUserSuccesFully();
        } else {
            progressBarOTPActivity.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBarOTPActivity.setVisibility(View.GONE);
            Toast.makeText(OTPActivity.this, "Invalid OTP,Try Again", Toast.LENGTH_SHORT).show();
        }
    }


    public void registerUserSuccesFully() {

        final Customer customer = createCustomerObject();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://192.168.0.2:8080/")  //.baseUrl("http://18.220.28.118:8080/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<Customer> call = retroFitNetworkClient.registerUser(customer);

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                Customer returnedCustomerObject = (Customer) response.body();
                saveAlltheInfromationTosharedPreferences(returnedCustomerObject);
                takeUserToHomeScreen();
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                progressBarOTPActivity.setVisibility(View.GONE);
                Toast.makeText(OTPActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void takeUserToHomeScreen() {
        progressBarOTPActivity.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Intent intent = new Intent(OTPActivity.this, HomeScreenActivity.class);
        startActivity(intent);
    }

    public void saveAlltheInfromationTosharedPreferences(Customer customer) {
        sharedPreferenceUtility.setLoggedIn(true);
        sharedPreferenceUtility.setCustomerID(customer.getCustomerID());
        sharedPreferenceUtility.setCustomerEmailID(customer.getCustomerEmail());
        sharedPreferenceUtility.setCustomerFirstName(customer.getCustomerFirstName());
        sharedPreferenceUtility.setCustomerUniqueID(customer.getCustomerUniqueID());
        sharedPreferenceUtility.setCustomerMobileNumber(customer.getCustomerMobileNumber());

    }

    private Customer createCustomerObject() {
        String firstName = sharedPreferenceUtility.getCustomerFirstNameRegisterActivity();
        String email = sharedPreferenceUtility.getCustomerEmailRegisterActivity();
        String password = (sharedPreferenceUtility.getCustomerPasswordRegisterActivity()==null?" ":sharedPreferenceUtility.getCustomerPasswordRegisterActivity());
        String mobileNumber = sharedPreferenceUtility.getCustomerMobileNumberRegisterActivity();
        return new Customer(email, firstName, mobileNumber, password);
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

    public void requestOTPFromServer(final String mobileNumber, final String emailID) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://192.168.0.2:8080/")        //.baseUrl("http://18.220.28.118:8080/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<String> call = retroFitNetworkClient.requestOTPFromServer(mobileNumber);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                generatedOTP = response.body();
                progressBarOTPActivity.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(OTPActivity.this, "Error in generating OTP", Toast.LENGTH_SHORT).show();
                progressBarOTPActivity.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });

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