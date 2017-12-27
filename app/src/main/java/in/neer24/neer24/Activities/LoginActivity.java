package in.neer24.neer24.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import in.neer24.neer24.CustomObjects.Customer;
import in.neer24.neer24.R;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private RelativeLayout loginActivityRL;
    private RelativeLayout innerRelativeLayoutForPassword;
    private Button nextButton;

    private EditText passwordEditText;
    private EditText emailEditText;
    private EditText welcomeMessageEditText;
    private Button showPasswordButton;

    private CallbackManager callbackManager;
    private Button facebookButton;
    private LoginButton facebookLoginButton;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private boolean checkUserExistanceFlag = false;
    private ProgressBar progressBar;
    private Button gmailButton;
    private SignInButton signInButton;



    String email = "";
    String mobileNumber = "";

    SharedPreferenceUtility sharedPreferenceUtility;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    //Signing Options
    private GoogleSignInOptions gso;

    //google api client
    private GoogleApiClient mGoogleApiClient;

    //Signin constant to check the activity result
    private int RC_SIGN_IN = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        loginActivityRL = (RelativeLayout) findViewById(R.id.loginActivityRL);
        nextButton = (Button) findViewById(R.id.nextButton);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        showPasswordButton = (Button) findViewById(R.id.loginShowPasswordButton);

        welcomeMessageEditText = (EditText) findViewById(R.id.welocmeMessageEditText);
        facebookLoginButton = (LoginButton) findViewById(R.id.facebookLoginButton);
        gmailButton = (Button) findViewById(R.id.gmailButton);
        facebookButton = (Button) findViewById(R.id.facebookButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        innerRelativeLayoutForPassword = (RelativeLayout) findViewById(R.id.innerRelativeLayout);

        innerRelativeLayoutForPassword.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        sharedPreferenceUtility = new SharedPreferenceUtility(this);


        nextButton.setOnClickListener(this);
        facebookButton.setOnClickListener(this);
        facebookLoginButton.setOnClickListener(this);
        gmailButton.setOnClickListener(this);
        showPasswordButton.setOnClickListener(this);


        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.firstactivityimage);
        Bitmap blurredBitmap = BlurImage.blur(this, originalBitmap);


        loginActivityRL.setBackground(new BitmapDrawable(getResources(), blurredBitmap));


        facebookLoginButton.setReadPermissions("email");
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getUserDetails(loginResult);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        signInButton.setScopes(gso.getScopeArray());

        //Initializing google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            case R.id.gmailButton:
                signIn();
                break;


            case R.id.facebookButton:
                facebookLoginButton.performClick();
                break;


            case R.id.nextButton:
                String emailOrMobileNumber = emailEditText.getText().toString();
                if (innerRelativeLayoutForPassword.getVisibility() == View.GONE) {

                    if (isEmailValid(emailOrMobileNumber)) {
                        email = emailOrMobileNumber;
                        progressBar.setVisibility(View.VISIBLE);
                        takeUserToLoginOrRegisterPage(email,null,"email");
                    } else {
                        if (emailOrMobileNumber.length() == 10) {
                            mobileNumber = emailOrMobileNumber;
                            progressBar.setVisibility(View.VISIBLE);
                            takeUserToLoginOrRegisterPage(null,mobileNumber,"mobilenumber");
                        } else if (emailOrMobileNumber.contains("@")) {
                            Toast.makeText(LoginActivity.this, "Email Address Invalid", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Mobile Number Invalid", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    String password = passwordEditText.getText().toString();
                    if (password == null || password.length() < 3)
                        Toast.makeText(LoginActivity.this, "please enter a valid password", Toast.LENGTH_SHORT).show();
                    else {
                        if(email.length()>5) {
                            authenticateUserAndLogin(email, password,mobileNumber,"email");
                        }else {
                            authenticateUserAndLogin(email,password,mobileNumber,"mobilenumber");
                        }
                    }
                }


                break;
            case R.id.loginShowPasswordButton:
                if (showPasswordButton.getText().equals("HIDE")) {
                    showPasswordButton.setText("SHOW");
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordEditText.setSelection(passwordEditText.getText().length());
                } else if (showPasswordButton.getText().equals("SHOW")) {
                    showPasswordButton.setText("HIDE");
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordEditText.setSelection(passwordEditText.getText().length());
                    break;
                }

        }

    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void authenticateUserAndLogin(String email, String password, String mobileNumber, String flag) {
        progressBar.setVisibility(View.VISIBLE);



        Customer customer =null;

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://192.168.0.2:8080/").client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<Customer> call=null;

        if(flag.contains("email")){
            customer= createCustomerObject(email,null,password);
            call = retroFitNetworkClient.authenticateUserAndLoginUsingEmail(customer);
        }else if(flag.contains("mobilenumber")){
            customer= createCustomerObject(null,mobileNumber ,password);
            call = retroFitNetworkClient.authenticateUserAndLoginUsingMobileNumber(customer);
        }


        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                progressBar.setVisibility(View.GONE);
                Customer customer = (Customer) response.body();
                if (customer.getOutputValue().equals("false")) {
                    Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                } else {

                    sharedPreferenceUtility.setLoggedIn(true);

                    sharedPreferenceUtility.setCustomerID(customer.getCustomerID());
                    sharedPreferenceUtility.setCustomerEmailID(customer.getCustomerEmail());
                    sharedPreferenceUtility.setCustomerFirstName(customer.getCustomerFirstName());
                    sharedPreferenceUtility.setCustomerLastName(customer.getCustomerLastName());
                    sharedPreferenceUtility.setCustomerUniqueID(customer.getCustomerUniqueID());


                    String lastName = sharedPreferenceUtility.getCustomerLastName();
                    String firsName = sharedPreferenceUtility.getCustomerFirstName();

                    Toast.makeText(LoginActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                }

            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public Customer createCustomerObject(String email,String mobileNumber,String password) {
        return new Customer(email,mobileNumber, password);
    }

    public void takeUserToLoginOrRegisterPage(final String email,final String mobileNumber, final String flag) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://192.168.0.2:8080/").client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());


        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
        Call<String> call=null;

        if(flag.equals("email")){
            call= retroFitNetworkClient.checkIfUserIsRegisterdUserOrNotUsingEmail(email);

        }else if(flag.equals("mobilenumber")){
            call= retroFitNetworkClient.checkIfUserIsRegisterdUserOrNotUsingMobileNumber(mobileNumber);
        }

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressBar.setVisibility(View.GONE);
                String result = response.body();
                if (response.body().toString().contains("true")) {
                    innerRelativeLayoutForPassword.setVisibility(View.VISIBLE);
                } else {
                    showToastANdTakeUSerToRegisterPage(email,mobileNumber);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void showToastANdTakeUSerToRegisterPage(String email,String mobileNumber) {
        Toast.makeText(LoginActivity.this, "Email id not registerd", Toast.LENGTH_SHORT).show();
        Intent registerActivityIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        if(email!=null) {
            registerActivityIntent.putExtra("email", email.toString());
            registerActivityIntent.putExtra("flag", "email");
        }else if(mobileNumber!=null){
            registerActivityIntent.putExtra("mobilenumber", mobileNumber.toString());
            registerActivityIntent.putExtra("flag", "mobilenumber");
        }

        startActivity(registerActivityIntent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {

        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();
            Toast.makeText(this, "Login Succesfull", Toast.LENGTH_SHORT).show();

        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
        }
    }

    protected void getUserDetails(LoginResult loginResult) {
        GraphRequest data_request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject json_object,
                            GraphResponse response) {
                        Intent intent = new Intent(LoginActivity.this, UserProfileActivity.class);
                        intent.putExtra("userProfile", json_object.toString());
                        startActivity(intent);
                    }

                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }

    @Override
    protected void onResume() {
        super.onResume();
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                callMethod();
            }
        });


    }

    private void callMethod() {

        innerRelativeLayoutForPassword.setVisibility(View.GONE);
    }


    private void signIn() {
        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);

        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
