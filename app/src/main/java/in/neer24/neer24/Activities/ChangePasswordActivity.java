package in.neer24.neer24.Activities;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        oldPasswordChangePasswordActivity=(EditText)findViewById(R.id.oldPasswordChangePasswordActivity);
        newPasswordChangePasswordActivity=(EditText)findViewById(R.id.newPasswordChangePasswordActivity);
        updatePasswordChangePasswordActivity=(Button)findViewById(R.id.updatePasswordChangePasswordActivity);
        sharedPreferenceUtility=new SharedPreferenceUtility(this);

        updatePasswordChangePasswordActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updatePasswordOnServer(oldPasswordChangePasswordActivity.getText().toString(),newPasswordChangePasswordActivity.getText().toString());
            }
        });

    }

    public void updatePasswordOnServer(String oldPassword, String newPassword) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                //.baseUrl("http://192.168.0.2:8080/")
                .baseUrl("http://18.220.28.118:8080/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        RetroFitNetworkClient retroFitNetworkClient = retrofit.create(RetroFitNetworkClient.class);

        Call<String> call=retroFitNetworkClient.updatePasswordOnServer(sharedPreferenceUtility.getCustomerEmailID(),oldPassword,newPassword);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result=response.body();
                if(result.contains("true")){

                }else {
                    Toast.makeText(ChangePasswordActivity.this,"Error in changing password",Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this,"Error in Server",Toast.LENGTH_SHORT);
            }
        });
    }
}
