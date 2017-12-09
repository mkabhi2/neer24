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
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.AsteriskTransformationMethod;
import in.neer24.neer24.Utilities.BlurImage;
import in.neer24.neer24.Utilities.UtilityClass;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    private RelativeLayout loginActivityRL;
    private Button nextButton;
    private int showPasswordButtonID = -1;
    private int passwordEditTextID=-1;
    private EditText passwordEditText=null;
    private EditText emailEditText;
    private EditText welcomeMessageEditText;
    private Button showPasswordButton;
    private boolean flag = true;
    private boolean isEmailBoxModified=false;
    private boolean nextButtonFlag = true;

    private CallbackManager callbackManager;
    private Button facebookButton;
    private LoginButton facebookLoginButton;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private boolean checkUserExistanceFlag=false;
    private ProgressBar progressBar;
    private Button gmailButton;
    private SignInButton signInButton;

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

        welcomeMessageEditText = (EditText) findViewById(R.id.welocmeMessageEditText);
        facebookLoginButton = (LoginButton) findViewById(R.id.facebookLoginButton);
        gmailButton=(Button)findViewById(R.id.gmailButton);
        facebookButton = (Button) findViewById(R.id.facebookButton);
        progressBar=(ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setVisibility(View.GONE);


        nextButton.setOnClickListener(this);
        facebookButton.setOnClickListener(this);
        facebookLoginButton.setOnClickListener(this);
        gmailButton.setOnClickListener(this);




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
                if(passwordEditText==null || isEmailBoxModified) {
                    String email = emailEditText.getText().toString();
                    if (UtilityClass.validate(email)) {
                        progressBar.setVisibility(View.VISIBLE);
                        takeUserToLoginOrRegisterPage(email);
                    } else {
                        //Context context = getApplicationContext();
                        Toast.makeText(LoginActivity.this, "Email Address Invalid", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    String password=passwordEditText.getText().toString();
                    if(password==null || password.length()<3)
                        Toast.makeText(LoginActivity.this, "please enter a valid password", Toast.LENGTH_SHORT).show();
                    else{
                        String email=emailEditText.getText().toString();
                        authenticateUserAndLogin(email,password);
                    }
                }
                break;
        }
    }

    public void authenticateUserAndLogin(String email, String password){
        progressBar.setVisibility(View.VISIBLE);
        new ExecuteTask().execute(email,password);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class ExecuteTask extends AsyncTask<String, Integer, String>
    {

        @Override
        protected String doInBackground(String... params) {

            String type="application/json";
            String s="";
            try
            {

                HttpURLConnection httpURLConnection = null;
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL("http://18.220.28.118:8080/neer/webapi/customers/logincustomer");
//                    URL url = new URL("http://192.168.0.7:8034/messenger/webapi/customers");
                    //URL url = new URL("http://18.220.28.118:8080/messenger/webapi/customers");//vs02277@gmail.com");
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", type);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.connect();

                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("customerEmail", params[0]);
                    jsonObject.put("password",params[1]);

                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr.writeBytes(jsonObject.toString());
                    wr.flush();
                    wr.close();

                    int responseCode=httpURLConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in=new BufferedReader(
                                new InputStreamReader(httpURLConnection.getInputStream()));
                        StringBuffer sb = new StringBuffer("");
                        String line="";
                        while((line = in.readLine()) != null) {
                            sb.append(line);
                            //break;
                        }

                        in.close();
                        return sb.toString();
                    }
                }catch(Exception e){

                }
            }
            catch(Exception exception)  {}
            return s;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            if(result.contains("false")) {
                Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();

            }else {
                editor.putBoolean("isLoggedIn",true);
                editor.commit();
                String tempString[]=result.split(",");
                editor.putString("EMAIL", tempString[0]);
                editor.commit();
                editor.putString("ID", tempString[1]);
                editor.commit();
//                editor.putString("NAME", tempString[2]);
//                editor.commit();

//                String fir=sharedPref.getString("EMAIL"," ");
//                String sec=sharedPref.getString("NAME","");
//                //insert values in shared preferences
                Toast.makeText(LoginActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(LoginActivity.this,HomeScreenActivity.class);
                startActivity(intent);
            }
        }

    }

    public String readResponse(HttpResponse res) {
        InputStream is=null;
        String return_text="";
        try {
            is=res.getEntity().getContent();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is));
            String line="";
            StringBuffer sb=new StringBuffer();
            while ((line=bufferedReader.readLine())!=null)
            {
                sb.append(line);
            }
            return_text=sb.toString();
        } catch (Exception e)
        {

        }
        return return_text;

    }

    public void takeUserToLoginOrRegisterPage(String email) {
        FetchData fetchData = new FetchData(email);
        fetchData.execute();
    }

    private class FetchData extends AsyncTask<String,Void,String> {
        private String email;
        private String JsonData;

        FetchData(String email){
            this.email=email;
        }
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;
            try {
                URL url=new URL("http://18.220.28.118:8080/neer/webapi/customers/"+email);
                //URL url = new URL("http://18.220.28.118:8080/messenger/webapi/customers/"+email);//vs02277@gmail.com");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();
                if (inputStream == null) {
                    Log.e("ERR", "Input stream empty");
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }
                if (stringBuffer.length() == 0) {
                    Log.e("ERR", "No data returned");
                    return null;
                }
                JsonData = stringBuffer.toString();
            } catch (IOException e) {
                Log.e("ERR ", e.toString());
                return null;

            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.e("HEELO", e.getMessage(), e);
                        e.printStackTrace();
                    }
                }
            }
            return JsonData;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            if(JsonData.contains("true")) {
                if (passwordEditText == null) {
                    flag = true;
                    passwordEditTextID = createPasswordEditText();
                    showPasswordButtonID = createShowPasswordButton(passwordEditTextID);
                    changeNextButtonPosition(true);
                    showPasswordButton = (Button) findViewById(showPasswordButtonID);
                    passwordEditText = (EditText) findViewById(passwordEditTextID);
                    passwordEditText.setTransformationMethod(new AsteriskTransformationMethod());
                }else if(isEmailBoxModified){
                    passwordEditText.setVisibility(View.VISIBLE);
                    showPasswordButton.setVisibility(View.VISIBLE);
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) passwordEditText.getLayoutParams();
                    lp.addRule(RelativeLayout.BELOW, R.id.emailEditText);
                    lp.setMargins(0, 20, 0, 0);
                    passwordEditText.setLayoutParams(lp);
                    changeNextButtonPosition(true);
                    isEmailBoxModified=false;
                }
            }else {
                Toast.makeText(LoginActivity.this,"Email id not registerd",Toast.LENGTH_SHORT).show();
                Intent registerActivityIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                registerActivityIntent.putExtra("email", emailEditText.getText().toString());
                startActivity(registerActivityIntent);
            }
        }
    }


    private void changeNextButtonPosition(boolean flag) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) nextButton.getLayoutParams();
        if (flag) {
            lp.addRule(RelativeLayout.BELOW, R.id.emailEditText);
            lp.setMargins(0, 250, 0, 0);
        } else {
            lp.addRule(RelativeLayout.BELOW, R.id.emailEditText);
            lp.setMargins(0, 85, 0, 0);
        }
        nextButton.setLayoutParams(lp);
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

        if (passwordEditText != null && showPasswordButton != null) {
            passwordEditText.setVisibility(View.INVISIBLE);
            passwordEditText.setText("");
            showPasswordButton.setVisibility(View.INVISIBLE);
            changeNextButtonPosition(false);
            isEmailBoxModified = true;
        }
    }

    private int createShowPasswordButton(int id) {

        final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final Button showPasswordButton = new Button(this);
        showPasswordButton.setId(View.generateViewId());
        lparams.addRule(RelativeLayout.ALIGN_TOP, id);
        lparams.addRule(RelativeLayout.ALIGN_BOTTOM, id);
        lparams.addRule(RelativeLayout.ALIGN_RIGHT, id);
        showPasswordButton.setLayoutParams(lparams);
        showPasswordButton.setText("SHOW");
        showPasswordButton.setBackground(getResources().getDrawable(R.drawable.show_password_button));
        loginActivityRL.addView(showPasswordButton);

        showPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (showPasswordButton.getText().equals("HIDE")) {
                    showPasswordButton.setText("SHOW");
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    passwordEditText.setSelection(passwordEditText.getText().length());
                } else if (showPasswordButton.getText().equals("SHOW")) {
                    showPasswordButton.setText("HIDE");
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    passwordEditText.setSelection(passwordEditText.getText().length());
                }
            }
        });
        return showPasswordButton.getId();
    }

    private int createPasswordEditText() {
        final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);

        EditText passwordEditText = new EditText(this);
        passwordEditText.setId(View.generateViewId());
        lparams.height = 128;

        lparams.addRule(RelativeLayout.BELOW, R.id.emailEditText);
        lparams.setMargins(0, 20, 0, 0);


        passwordEditText.setLayoutParams(lparams);
        passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        passwordEditText.setHint("Password");
        passwordEditText.setBackground(getResources().getDrawable(R.drawable.rounded_corners));
        passwordEditText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        passwordEditText.setTextColor(Color.WHITE);
        passwordEditText.setHintTextColor(Color.WHITE);
        loginActivityRL.addView(passwordEditText);

        return passwordEditText.getId();
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
}
