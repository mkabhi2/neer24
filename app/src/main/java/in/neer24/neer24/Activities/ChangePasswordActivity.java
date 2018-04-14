package in.neer24.neer24.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import in.neer24.neer24.R;
import in.neer24.neer24.Utilities.RetroFitNetworkClient;
import in.neer24.neer24.Utilities.SharedPreferenceUtility;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePasswordActivity extends AppCompatActivity {


    EditText oldPasswordChangePasswordActivity;
    EditText newPasswordChangePasswordActivity;
    Button updatePasswordChangePasswordActivity;
    SharedPreferenceUtility sharedPreferenceUtility;
    private Toolbar toolbar;
    ProgressDialog dialog;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        oldPasswordChangePasswordActivity = (EditText) findViewById(R.id.oldPasswordChangePasswordActivity);
        newPasswordChangePasswordActivity = (EditText) findViewById(R.id.newPasswordChangePasswordActivity);
        updatePasswordChangePasswordActivity = (Button) findViewById(R.id.updatePasswordChangePasswordActivity);
        sharedPreferenceUtility = new SharedPreferenceUtility(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        oldPasswordChangePasswordActivity.setFocusable(false);
        newPasswordChangePasswordActivity.setFocusable(false);

        oldPasswordChangePasswordActivity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                oldPasswordChangePasswordActivity.setFocusableInTouchMode(true);

                return false;
            }
        });

        newPasswordChangePasswordActivity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                newPasswordChangePasswordActivity.setFocusableInTouchMode(true);

                return false;
            }
        });

        updatePasswordChangePasswordActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                oldPasswordChangePasswordActivity.setFocusable(false);
                newPasswordChangePasswordActivity.setFocusable(false);

                if (oldPasswordChangePasswordActivity.getText().toString().trim().length() == 0) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(ChangePasswordActivity.this, "Please enter your old password", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                if (newPasswordChangePasswordActivity.getText().toString().trim().length() == 0) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(ChangePasswordActivity.this, "Please enter your new password", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                dialog = ProgressDialog.show(ChangePasswordActivity.this, "",
                        "Updating. Please wait...", true);
                dialog.show();
                updatePasswordOnServer(oldPasswordChangePasswordActivity.getText().toString(), newPasswordChangePasswordActivity.getText().toString());

            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void updatePasswordOnServer(String oldPassword, String newPassword) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                //.baseUrl("http://192.168.0.2:8080/")
                .baseUrl("http://18.220.28.118/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);

        Call<String> call = retroFitNetworkClient.updatePasswordOnServer(sharedPreferenceUtility.getCustomerEmailID(), oldPassword, newPassword);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = response.body();
                if (result.contains("true")) {
                    Intent intent = new Intent();
                    intent.putExtra("UPDATE_PASSWORD", "TRUE");
                    setResult(1, intent);
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(ChangePasswordActivity.this, "Password Updated", Toast.LENGTH_SHORT);
                    toast.show();
                    finish();
                } else {
                    dialog.cancel();
                    Intent intent = new Intent();
                    intent.putExtra("UPDATE_PASSWORD", "FALSE");
                    setResult(1, intent);
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(ChangePasswordActivity.this, "Old password IS WRONG. Please enter a correct old password", Toast.LENGTH_LONG);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                dialog.cancel();

                Snackbar.make(findViewById(R.id.changePasswordLL), "Failed to update password. No internet connection or error on server", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.show();
                                updatePasswordOnServer(oldPasswordChangePasswordActivity.getText().toString(), newPasswordChangePasswordActivity.getText().toString());
                            }
                        }).show();
            }
        });
    }
}
