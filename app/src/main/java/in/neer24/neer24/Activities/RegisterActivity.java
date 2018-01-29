package in.neer24.neer24.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.AsteriskTransformationMethod;
import in.neer24.neer24.Utilities.BlurImage;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;
import in.neer24.neer24.Utilities.UtilityClass;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText firstNameEditText;
    private EditText mobileEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private RelativeLayout registerActivityRL;
    Button showPasswordButton;
    private Button signUpButton;
    ProgressBar registerProgressBar;
    String emailID;
    String mobileNumber;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    SharedPreferenceUtility sharedPreferenceUtility;

    String flag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_register);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();


        registerActivityRL = (RelativeLayout) findViewById(R.id.register_activity_relative_layout);
        firstNameEditText = (EditText) findViewById(R.id.registerFirstNameEditText);
        mobileEditText = (EditText) findViewById(R.id.registerMobileEditText);
        emailEditText = (EditText) findViewById(R.id.registerEmailEditText);
        passwordEditText = (EditText) findViewById(R.id.registerPassworddEditText);
        showPasswordButton = (Button) findViewById(R.id.registerShowPasswordButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        registerProgressBar = (ProgressBar) findViewById(R.id.registerProgressBar);
        sharedPreferenceUtility = new SharedPreferenceUtility(this);

        registerProgressBar.getIndeterminateDrawable().setColorFilter(Color.BLUE, android.graphics.PorterDuff.Mode.MULTIPLY);
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.signup_img);
        Bitmap blurredBitmap = BlurImage.blur(this, originalBitmap);


        registerActivityRL.setBackground(new BitmapDrawable(getResources(), blurredBitmap));


        registerProgressBar.setVisibility(View.GONE);


        showPasswordButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);

        passwordEditText.setTransformationMethod(new AsteriskTransformationMethod());


        Bundle bundle = getIntent().getExtras();
        flag = bundle.getString("flag");

        if (flag.equals("email")) {
            emailID = bundle.getString("email");
            emailEditText.setText(emailID);
            emailEditText.setEnabled(false);
        } else if (flag.equals("mobilenumber")) {
            mobileNumber = bundle.getString("mobilenumber");
            mobileEditText.setText(mobileNumber);
            mobileEditText.setEnabled(false);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.registerShowPasswordButton:
                if (showPasswordButton.getText().equals("HIDE")) {
                    showPasswordButton.setText("SHOW");
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordEditText.setSelection(passwordEditText.getText().length());
                } else if (showPasswordButton.getText().equals("SHOW")) {
                    showPasswordButton.setText("HIDE");
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordEditText.setSelection(passwordEditText.getText().length());
                }
                break;

            case R.id.signUpButton:
                try {
                    registerProgressBar.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    validateSignUpFields();
                } catch (Exception e) {

                }

        }
    }

    public void validateSignUpFields() throws ExecutionException, InterruptedException {

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String mobileNumber = mobileEditText.getText().toString();

        if (UtilityClass.validate(email) && password != null && password.length() > 5 && firstName != null && mobileNumber != null && mobileNumber.length() == 10 && (mobileNumber.startsWith("9") || mobileNumber.startsWith("8") || mobileNumber.startsWith("7"))) {

            saveEveryThingInSharePreferences(email, password, firstName, mobileNumber);
            checkEmailAndMobileNumberIfALreadyRegistered(email,mobileNumber);
        } else {
            registerProgressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Toast.makeText(RegisterActivity.this,"Error in filled Fields",Toast.LENGTH_SHORT).show();
        }
    }


    public void checkEmailAndMobileNumberIfALreadyRegistered(final String emailID,final String mobileNumber){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://192.168.0.2:8080/")        //.baseUrl("http://18.220.28.118:8080/")      //
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<String> call=null;
        if(flag.equals("mobilenumber")) {
            call = retroFitNetworkClient.checkIfUserIsRegisterdUserOrNotUsingEmail(emailID);

        }else if(flag.equals("email")){
            call = retroFitNetworkClient.checkIfUserIsRegisterdUserOrNotUsingMobileNumber(mobileNumber);
        }


        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String res=response.body();
                if(res.contains("true")){
                    if(flag.equals("mobilenumber")) {
                        Toast.makeText(RegisterActivity.this, "Enail ID already registered", Toast.LENGTH_SHORT);
                    }else {
                        Toast.makeText(RegisterActivity.this, "Mobile Number already registered", Toast.LENGTH_SHORT);
                    }
                    registerProgressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }else {
                    registerProgressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Intent intent = new Intent(RegisterActivity.this, OTPActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                registerProgressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(RegisterActivity.this,"Enail verifying Mobile Number Or EmailID",Toast.LENGTH_SHORT);
            }
        });

    }


    public void saveEveryThingInSharePreferences(String emailID, String password, String firstName, String mobileNumber) {
        sharedPreferenceUtility.setCustomerFirstNameRegisterActivity(firstName);
        sharedPreferenceUtility.setCustomerEmailRegisterActivity(emailID);
        sharedPreferenceUtility.setCustomerMobileNumberRegisterActivity(mobileNumber);
        sharedPreferenceUtility.setCustomerPasswordRegisterActivity(password);

    }


    public void doRestOfTaks(String result) {
        registerProgressBar.setVisibility(View.GONE);
        if (result.contains("true")) {

            editor.putBoolean("isLoggedIn", true);
            editor.commit();
            Intent intent = new Intent(RegisterActivity.this, HomeScreenActivity.class);
            startActivity(intent);

            Toast.makeText(RegisterActivity.this, "Succesfully registerd", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RegisterActivity.this, "Please Fill mandatory fields", Toast.LENGTH_SHORT).show();
        }
    }
}