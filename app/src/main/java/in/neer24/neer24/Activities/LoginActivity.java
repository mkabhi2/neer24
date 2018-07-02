package in.neer24.neer24.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.neer24.neer24.Adapters.ChangeLocationAddressRVAdapter;
import in.neer24.neer24.Adapters.UserAccountAddressRVAdapter;
import in.neer24.neer24.CustomObjects.Customer;
import in.neer24.neer24.CustomObjects.CustomerAddress;
import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.BlurImage;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {


    private final String TAG = LoginActivity.class.getSimpleName();
    private final int RC_SIGN_IN = 007;

    private GoogleApiClient mGoogleApiClient;
    private SignInButton btnSignIn;

    private RelativeLayout loginActivityRL;
    private RelativeLayout innerRelativeLayoutForPassword;
    private Button nextButton;

    private TextView forgotPasswordTextViewLoginActivity;

    private EditText passwordEditText;
    private EditText emailEditText;
    private Button showPasswordButton;

    private CallbackManager callbackManager;
    private Button facebookButton;
    private LoginButton facebookLoginButton;

    private ProgressBar progressBar;
    private LinearLayout gmailButton;

    private String email = "";
    private String mobileNumber = "";

    private SharedPreferenceUtility sharedPreferenceUtility;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login);

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        progressBar.setVisibility(View.VISIBLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        loginActivityRL = (RelativeLayout) findViewById(R.id.loginActivityRL);
        nextButton = (Button) findViewById(R.id.nextButton);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        showPasswordButton = (Button) findViewById(R.id.loginShowPasswordButton);
        forgotPasswordTextViewLoginActivity = (TextView) findViewById(R.id.forgotPasswordTextViewLoginActivity);

        facebookLoginButton = (LoginButton) findViewById(R.id.facebookLoginButton);
        facebookLoginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));


        btnSignIn = (SignInButton) findViewById(R.id.sign_in_button);
        btnSignIn.setOnClickListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        gmailButton = (LinearLayout) findViewById(R.id.gmailButton);
        facebookButton = (Button) findViewById(R.id.facebookButton);
        innerRelativeLayoutForPassword = (RelativeLayout) findViewById(R.id.innerRelativeLayout);

        innerRelativeLayoutForPassword.setVisibility(View.GONE);


        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sharedPreferenceUtility = new SharedPreferenceUtility(this);

        forgotPasswordTextViewLoginActivity.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        facebookButton.setOnClickListener(this);
        gmailButton.setOnClickListener(this);
        showPasswordButton.setOnClickListener(this);


        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nav_bg);
        Bitmap blurredBitmap = BlurImage.blur(this, originalBitmap);


        loginActivityRL.setBackground(new BitmapDrawable(getResources(), blurredBitmap));

        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
                getUserDetails(loginResult);
            }

            @Override
            public void onCancel() {
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                String text = "vijay";
            }

            @Override
            public void onError(FacebookException exception) {
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Log.d("facebook", exception.toString());
            }
        });
    }


    private void sendEmailWithNewPassword() {
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
        Call<String> call = null;
        if (email != null)
            call = retroFitNetworkClient.sendEmailWithNewPassword(email, null);
        else if (mobileNumber != null)
            call = retroFitNetworkClient.sendEmailWithNewPassword(null, mobileNumber);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body() != null && response.body().length() > 0) {
                    Toast.makeText(LoginActivity.this, "Password is Sent to you mail id", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Error in generating password", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(LoginActivity.this, "Error in generating password", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            case R.id.forgotPasswordTextViewLoginActivity:
                if (emailEditText.getText().toString() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("An email with new password will be sent to you Email ID")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    sendEmailWithNewPassword();
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    Toast.makeText(LoginActivity.this, "Please Enter an Email Address", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.gmailButton:
                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                signIn();
                break;


            case R.id.facebookButton:
                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                facebookLoginButton.performClick();
                break;


            case R.id.nextButton:
                hideKeyboard(this);
                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                String emailOrMobileNumber = emailEditText.getText().toString();
                if (innerRelativeLayoutForPassword.getVisibility() == View.GONE) {

                    if (isEmailValid(emailOrMobileNumber)) {
                        email = emailOrMobileNumber;
                        takeUserToLoginOrRegisterPage(email, null, "email");
                    } else {
                        if (emailOrMobileNumber.length() == 10) {
                            mobileNumber = emailOrMobileNumber;
                            takeUserToLoginOrRegisterPage(null, mobileNumber, "mobilenumber");
                        } else if (emailOrMobileNumber.contains("@")) {
                            progressBar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(LoginActivity.this, "Email Address Invalid", Toast.LENGTH_SHORT).show();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(LoginActivity.this, "Mobile Number Invalid", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    String password = passwordEditText.getText().toString();
                    if (password == null || password.length() < 3) {
                        progressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(LoginActivity.this, "please enter a valid password", Toast.LENGTH_SHORT).show();
                    } else {
                        if (email.length() > 5) {
                            authenticateUserAndLogin(email, password, mobileNumber, "email");
                        } else {
                            authenticateUserAndLogin(email, password, mobileNumber, "mobilenumber");
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void authenticateUserAndLogin(String email, String password, String mobileNumber, String flag) {
        Customer customer = null;

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
        Call<Customer> call = null;

        if (flag.contains("email")) {
            customer = createCustomerObject(email, null, password);
            call = retroFitNetworkClient.authenticateUserAndLoginUsingEmail(customer);
        } else if (flag.contains("mobilenumber")) {
            customer = createCustomerObject(null, mobileNumber, password);
            call = retroFitNetworkClient.authenticateUserAndLoginUsingMobileNumber(customer);
        }


        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                Customer customer = (Customer) response.body();
                if (customer !=null && customer.getOutputValue().equals("false")) {
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();

                } else {

                    saveUserInformationInSharedPreferences(customer, "normal");
                    getCustomerAddress();
                }

            }

            void getCustomerAddress() {
                int customerid = sharedPreferenceUtility.getCustomerID();

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .connectTimeout(1, TimeUnit.MINUTES)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .build();

                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl("http://18.220.28.118:80/").client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create());

                Retrofit retrofit = builder.build();
                String unique = sharedPreferenceUtility.getCustomerUniqueID();

                RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);
                Call<List<CustomerAddress>> call = retroFitNetworkClient.getAllCustomerAddress(customerid, unique);

                call.enqueue(new Callback<List<CustomerAddress>>() {
                    @Override
                    public void onResponse(Call<List<CustomerAddress>> call, Response<List<CustomerAddress>> response) {
                        HomeScreenActivity.addressList = (ArrayList<CustomerAddress>) response.body();
                        if (UserAccountActivity.recyclerView != null) {
                            UserAccountActivity.recyclerView.setAdapter(new UserAccountAddressRVAdapter(HomeScreenActivity.addressList, LoginActivity.this));
                            UserAccountActivity.recyclerView.invalidate();
                        }

                        if (ChangeLocationActivity.addressRV != null) {
                            ChangeLocationActivity.addressRV.setAdapter(new ChangeLocationAddressRVAdapter(HomeScreenActivity.addressList, LoginActivity.this));
                            ChangeLocationActivity.addressRV.invalidate();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CustomerAddress>> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Error loading address", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(LoginActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserInformationInSharedPreferences(Customer customer, String loggedInVia) {
        sharedPreferenceUtility.setLoggedIn(true);
        sharedPreferenceUtility.setLoggediInVia(loggedInVia);
        sharedPreferenceUtility.setCustomerID(customer.getCustomerID());
        sharedPreferenceUtility.setCustomerEmailID(customer.getCustomerEmail());
        sharedPreferenceUtility.setCustomerFirstName(customer.getCustomerFirstName());
        sharedPreferenceUtility.setCustomerUniqueID(customer.getCustomerUniqueID());
        sharedPreferenceUtility.setCustomerMobileNumber(customer.getCustomerMobileNumber());

        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

        if (HomeScreenActivity.cansList == null || HomeScreenActivity.cansList.isEmpty()) {

            Intent intent = new Intent(LoginActivity.this, ChangeLocationActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private Customer createCustomerObject(String email, String mobileNumber, String password) {
        return new Customer(email, mobileNumber, password);
    }

    private void takeUserToLoginOrRegisterPage(final String email, final String mobileNumber, final String flag) {
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
        Call<String> call = null;

        if (flag.equals("email")) {
            call = retroFitNetworkClient.checkIfUserIsRegisterdUserOrNotUsingEmail(email);

        } else if (flag.equals("mobilenumber")) {
            call = retroFitNetworkClient.checkIfUserIsRegisterdUserOrNotUsingMobileNumber(mobileNumber);
        }

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                String result = response.body();
                if (response.body().toString().contains("true")) {
                    innerRelativeLayoutForPassword.setVisibility(View.VISIBLE);
                } else {
                    showToastANdTakeUSerToRegisterPage(email, mobileNumber);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(LoginActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showToastANdTakeUSerToRegisterPage(String email, String mobileNumber) {
        sharedPreferenceUtility.setLoggedInViaTemporary("normal");
        Toast.makeText(LoginActivity.this, "Email id not registerd", Toast.LENGTH_SHORT).show();
        Intent registerActivityIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        if (email != null) {
            registerActivityIntent.putExtra("email", email.toString());
            registerActivityIntent.putExtra("flag", "email");
        } else if (mobileNumber != null) {
            registerActivityIntent.putExtra("mobilenumber", mobileNumber.toString());
            registerActivityIntent.putExtra("flag", "mobilenumber");
        }

        startActivity(registerActivityIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String email = acct.getEmail();
            hasUserLoggedInUsingSocailNetworking(email, acct, null);
        } else {
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private boolean hasUserLoggedInUsingSocailNetworking(String email, final GoogleSignInAccount account, final JSONObject object) {
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
        Call<Customer> call = retroFitNetworkClient.checkIfUserHasSignedInUsingSocialAccount(email);

        call.enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(Call<Customer> call, Response<Customer> response) {
                Customer customer = response.body();
                if (customer.getOutputValue().equals("true")) {
                    if (object != null) {
                        saveUserInformationInSharedPreferences(customer, "facebook");
                    } else if (account != null) {
                        saveUserInformationInSharedPreferences(customer, "gmail");
                    }
                } else {
                    if (account != null) {
                        sharedPreferenceUtility.setCustomerEmailRegisterActivity(account.getEmail());
                        sharedPreferenceUtility.setCustomerFirstNameRegisterActivity(account.getDisplayName());
                        sharedPreferenceUtility.setLoggedInViaTemporary("gmail");
                        Log.e(TAG, "display name: " + account.getDisplayName());
                    } else if (object != null) {
                        try {
                            sharedPreferenceUtility.setCustomerEmailRegisterActivity(object.getString("email"));
                            sharedPreferenceUtility.setCustomerFirstNameRegisterActivity(object.getString("name"));
                            sharedPreferenceUtility.setLoggedInViaTemporary("facebook");
                        } catch (Exception e) {

                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Intent intent = new Intent(LoginActivity.this, FacebookLoginSupportActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Customer> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });

        return true;

    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(LoginActivity.this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(LoginActivity.this);
            mGoogleApiClient.disconnect();
        }
    }

    protected void getUserDetails(LoginResult loginResult) {

        try {
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.v("LoginActivity", response.toString());
                            try {
                                // Application code
                                String email = object.getString("email");
                                hasUserLoggedInUsingSocailNetworking(email, null, object);
                            } catch (Exception e) {
                                Log.d("excep", e.toString());
                            }
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,birthday");
            request.setParameters(parameters);
            request.executeAsync();
        } catch (Exception e) {
            Log.d("Exce", e.toString());
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

//
//        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
//        if (opr.isDone()) {
//            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
//            // and the GoogleSignInResult will be available instantly.
//            Log.d(TAG, "Got cached sign-in");
//            GoogleSignInResult result = opr.get();
//            handleSignInResult(result);
//        } else {
//            // If the user has not previously signed in on this device or the sign-in has expired,
//            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
//            // single sign-on will occur in this branch.
//            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//                @Override
//                public void onResult(GoogleSignInResult googleSignInResult) {
//                    handleSignInResult(googleSignInResult);
//                }
//            });
//        }
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
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
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
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
