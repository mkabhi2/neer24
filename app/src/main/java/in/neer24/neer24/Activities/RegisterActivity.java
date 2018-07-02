package in.neer24.neer24.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.AsteriskTransformationMethod;
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

    private TextView firstNameErrorMessageTextView;
    private TextView mobileNumberErrorMessageTextView;
    private TextView emailErrorMessageTextView;
    private TextView passwordErrorMessageTextView;
    private TextView referralCodeErrorMessageTextView;


    private EditText firstNameEditText;
    private EditText mobileEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private CheckBox referralCodeCheckBox;
    private EditText referralCodeEditText;
    private TextInputLayout referralCodeInputLayout;
    private Button showPasswordButton;
    private Button signUpButton;
    private ProgressBar registerProgressBar;
    private String emailID;
    private String mobileNumber;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private SharedPreferenceUtility sharedPreferenceUtility;

    private String flag = "";

    private boolean isEmailValid = false;
    private boolean isFirstNameValid = false;
    private boolean isMobileNumberValid = false;
    private boolean isPasswordValid = false;
    private boolean isCouponCodeValid = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerProgressBar = (ProgressBar) findViewById(R.id.registerProgressBar);
        registerProgressBar.getIndeterminateDrawable().setColorFilter(Color.BLUE, android.graphics.PorterDuff.Mode.MULTIPLY);

        registerProgressBar.setVisibility(View.VISIBLE);
       // dialog = ProgressDialog.show(RegisterActivity.this, "",
         //       "Saving your details. Please wait...", true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();


        firstNameErrorMessageTextView = (TextView) findViewById(R.id.firstNameErrorMessageTextView);
        mobileNumberErrorMessageTextView = (TextView) findViewById(R.id.mobileNumberErrorMessageTextView);
        emailErrorMessageTextView = (TextView) findViewById(R.id.emailErrorMessageTextView);
        passwordErrorMessageTextView = (TextView) findViewById(R.id.passwordErrorMessageTextView);
        referralCodeErrorMessageTextView = (TextView) findViewById(R.id.referralCodeErrorMessageTextView);


        firstNameEditText = (EditText) findViewById(R.id.registerFirstNameEditText);
        mobileEditText = (EditText) findViewById(R.id.registerMobileEditText);
        emailEditText = (EditText) findViewById(R.id.registerEmailEditText);
        passwordEditText = (EditText) findViewById(R.id.registerPassworddEditText);
        showPasswordButton = (Button) findViewById(R.id.registerShowPasswordButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        referralCodeCheckBox = (CheckBox) findViewById(R.id.referalCodeCheckBox);
        referralCodeEditText = (EditText) findViewById(R.id.referalCodeEditText);
        referralCodeInputLayout = (TextInputLayout) findViewById(R.id.referralCodeInputLayout);
        sharedPreferenceUtility = new SharedPreferenceUtility(this);


        firstNameEditText.addTextChangedListener(new GenericTextWatcher(firstNameEditText));
        mobileEditText.addTextChangedListener(new GenericTextWatcher(mobileEditText));
        emailEditText.addTextChangedListener(new GenericTextWatcher(emailEditText));
        passwordEditText.addTextChangedListener(new GenericTextWatcher(passwordEditText));
        referralCodeEditText.addTextChangedListener(new GenericTextWatcher(referralCodeEditText));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        showPasswordButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        referralCodeCheckBox.setOnClickListener(this);

        passwordEditText.setTransformationMethod(new AsteriskTransformationMethod());

        passwordErrorMessageTextView.setVisibility(View.GONE);
        Bundle bundle = getIntent().getExtras();
        flag = bundle.getString("flag");
        flag = bundle.getString("flag");

        if (flag.equals("email")) {
            emailID = bundle.getString("email");
            emailEditText.setText(emailID);
            emailEditText.setEnabled(false);
            emailEditText.setTextColor(Color.GRAY);
        } else if (flag.equals("mobilenumber")) {
            mobileNumber = bundle.getString("mobilenumber");
            mobileEditText.setText(mobileNumber);
            mobileEditText.setEnabled(false);
            mobileEditText.setTextColor(Color.GRAY);
        }


        referralCodeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                           @Override
                                                           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                               referralCodeEditText.getText().clear();
                                                               if (isChecked) {
                                                                   isCouponCodeValid = false;
                                                               } else {
                                                                   isCouponCodeValid = true;
                                                                   referralCodeErrorMessageTextView.setVisibility(View.GONE);
                                                               }
                                                           }
                                                       }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerProgressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
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
//                    dialog = ProgressDialog.show(RegisterActivity.this, "",
//                            "Sending your details. Please wait...", true);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    validateSignUpFields();
                } catch (Exception e) {

                }
                break;

            case R.id.referalCodeCheckBox:
                if (referralCodeCheckBox.isChecked()) {
                    referralCodeInputLayout.setVisibility(View.VISIBLE);
                } else {
                    referralCodeInputLayout.setVisibility(View.GONE);
                }
                break;
        }

        if(HomeScreenActivity.cansList==null || HomeScreenActivity.cansList.isEmpty() || HomeScreenActivity.locationName==null || HomeScreenActivity.locationName.isEmpty()){

            Intent intent  = new Intent();
            intent.setClass(RegisterActivity.this, FirstActivity.class);
            startActivity(intent);
        }
    }

    private void validateSignUpFields() throws ExecutionException, InterruptedException {

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String firstName = firstNameEditText.getText().toString();
        String mobileNumber = mobileEditText.getText().toString();
        String referralCode = "";

        if (referralCodeInputLayout.getVisibility() == View.VISIBLE)
            referralCode = referralCodeEditText.getText().toString();

        if (isEmailValid && isMobileNumberValid && isCouponCodeValid && isFirstNameValid && isPasswordValid && (mobileNumber.startsWith("9") || mobileNumber.startsWith("8") || mobileNumber.startsWith("7"))) {
            saveEveryThingInSharePreferences(email, password, firstName, mobileNumber, referralCode);
            checkEmailAndMobileNumberIfALreadyRegistered(email, mobileNumber);
        } else {
          //  dialog.cancel();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            if (!isEmailValid)
                emailErrorMessageTextView.setVisibility(View.VISIBLE);

            if (!isPasswordValid)
                passwordErrorMessageTextView.setVisibility(View.VISIBLE);

            if (!isMobileNumberValid)
                mobileNumberErrorMessageTextView.setVisibility(View.VISIBLE);

            if (!isFirstNameValid)
                firstNameErrorMessageTextView.setVisibility(View.VISIBLE);

            if (!isCouponCodeValid)
                referralCodeErrorMessageTextView.setVisibility(View.VISIBLE);


        }
    }


    private void checkEmailAndMobileNumberIfALreadyRegistered(final String emailID, final String mobileNumber) {
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
        if (flag.equals("mobilenumber")) {
            call = retroFitNetworkClient.checkIfUserIsRegisterdUserOrNotUsingEmail(emailID);

        } else if (flag.equals("email")) {
            call = retroFitNetworkClient.checkIfUserIsRegisterdUserOrNotUsingMobileNumber(mobileNumber);
        }


        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                registerProgressBar.setVisibility(View.GONE);
                String res = response.body();
                if (res.contains("true")) {
                    if (flag.equals("mobilenumber")) {
                      //  dialog.cancel();
                        Toast.makeText(RegisterActivity.this, "Email ID already registered", Toast.LENGTH_SHORT).show();
                    } else {
                       // dialog.cancel();
                        Toast.makeText(RegisterActivity.this, "Mobile Number already registered", Toast.LENGTH_SHORT).show();
                    }
                    //dialog.cancel();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                } else {
                   // dialog.cancel();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Intent intent = new Intent(RegisterActivity.this, OTPActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
              //  dialog.cancel();
                registerProgressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(RegisterActivity.this, "Email verifying Mobile Number Or EmailID", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void saveEveryThingInSharePreferences(String emailID, String password, String firstName, String mobileNumber, String referralCode) {
        sharedPreferenceUtility.setCustomerFirstNameRegisterActivity(firstName);
        sharedPreferenceUtility.setCustomerEmailRegisterActivity(emailID);
        sharedPreferenceUtility.setCustomerMobileNumberRegisterActivity(mobileNumber);
        sharedPreferenceUtility.setCustomerPasswordRegisterActivity(password);
        sharedPreferenceUtility.setCustomerReferralCodeRegisterActivity(referralCode);

    }


    public class GenericTextWatcher implements TextWatcher {
        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch (view.getId()) {
                case R.id.registerEmailEditText:
                    if (!UtilityClass.validateEmail(emailEditText.getText().toString())) {
                        emailErrorMessageTextView.setVisibility(View.VISIBLE);
                        isEmailValid = false;
                    } else {
                        emailErrorMessageTextView.setVisibility(View.GONE);
                        isEmailValid = true;
                    }
                    break;

                case R.id.registerFirstNameEditText:
                    if (!UtilityClass.validateFirstName(firstNameEditText.getText().toString())) {
                        firstNameErrorMessageTextView.setVisibility(View.VISIBLE);
                        isFirstNameValid = false;
                    } else {
                        firstNameErrorMessageTextView.setVisibility(View.GONE);
                        isFirstNameValid = true;
                    }
                    break;

                case R.id.registerMobileEditText:
                    if (!UtilityClass.validateMobile(mobileEditText.getText().toString())) {
                        mobileNumberErrorMessageTextView.setVisibility(View.VISIBLE);
                        isMobileNumberValid = false;
                    } else {
                        mobileNumberErrorMessageTextView.setVisibility(View.GONE);
                        isMobileNumberValid = true;
                    }
                    break;

                case R.id.registerPassworddEditText:
                    if (!UtilityClass.validatePassword(passwordEditText.getText().toString())) {
                        passwordErrorMessageTextView.setVisibility(View.VISIBLE);
                        isPasswordValid = false;
                    } else {
                        passwordErrorMessageTextView.setVisibility(View.GONE);
                        isPasswordValid = true;
                    }
                    break;

                case R.id.referalCodeEditText:
                    if (referralCodeCheckBox.isChecked() && !UtilityClass.validateEmail(referralCodeEditText.getText().toString())) {
                        referralCodeErrorMessageTextView.setVisibility(View.VISIBLE);
                        isCouponCodeValid = false;
                    } else {
                        referralCodeErrorMessageTextView.setVisibility(View.GONE);
                        isCouponCodeValid = true;
                    }
                    break;
            }
        }
    }



}