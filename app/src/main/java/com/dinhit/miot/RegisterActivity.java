package com.dinhit.miot;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dinhit.miot.data.model.ResponseResult;
import com.dinhit.miot.data.model.User;
import com.dinhit.miot.data.remote.IRequestAPI;
import com.dinhit.miot.data.remote.RetrofitClient;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    public static final String EXTRA_USERNAME = "com.dinhit.miot.extra.USERNAME";
    private String savedUsername;
    private Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_register);
        registerButton = findViewById(R.id.btnRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(RegisterActivity.this);
                EditText username, password, rePassword, name;
                username = findViewById(R.id.registerUsername);
                password = findViewById(R.id.registerPassword);
                rePassword = findViewById(R.id.reRegisterPassword);
                name = findViewById(R.id.registerName);
                savedUsername = tvToString(username);
                View registerView = findViewById(R.id.layoutRegister);
                String validDataMsg = validRegisterData(tvToString(username), tvToString(password),
                        tvToString(rePassword), tvToString(name));
                if (validDataMsg.equals("valid")) {
                    sendRegisterRequest(tvToString(username), tvToString(password),
                            tvToString(rePassword), tvToString(rePassword),
                            createUserModel(tvToString(username),tvToString(password),tvToString(name)),
                            registerView);
                } else showSnackbar(registerView, validDataMsg,0 );

            }
        });
    }

    public void launchLoginActivity(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }

    private void sendRegisterRequest(String username, String password, String rePassword, String name, User user, final View view) {
        Retrofit retrofit = RetrofitClient.getClient();
        IRequestAPI requestAPI = retrofit.create(IRequestAPI.class);
        Call<ResponseResult> call = requestAPI.signUp(user);
        call.enqueue(new Callback<ResponseResult>() {
            @Override
            public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                String status = response.body().getStatus().toString();
                String message = response.body().getMessage().toString();
                String id = response.body().getId().toString();
                if (status.equals("error")) {
                    showSnackbar(view, message, 0);
                } else {
                    showSnackbar(view, message, 0);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.putExtra(EXTRA_USERNAME, savedUsername);
                            startActivity(intent);
                            finish();
                        }
                    }, 300);
                }
            }

            @Override
            public void onFailure(Call<ResponseResult> call, Throwable t) {
                t.printStackTrace();
                showSnackbar(view, "Lỗi kết nối", 0);
            }
        });
    }
    private User createUserModel(String username, String password, String name) {
        User user = new User(username, password, name);
        return user;
    }
    private String validRegisterData(String username, String password, String rePassword, String name) {
        if (username.isEmpty()) {
            return "Tên đăng nhập không được trống";
        } else
            if (username.length() < 5) {
                return "Tên đăng nhập phải chứa hơn 5 ký  tự";
            }
        if (password.isEmpty()) {
            return "Mật khẩu không được trống";
        } else
            if (password.length() < 6) {
                return "Mật khẩu phải chứa hơn 6 ký  tự";
            }
        if (rePassword.isEmpty()) {
            return "Nhập lại mật khẩu không được trống";
        } if (!password.toString().equals(rePassword.toString())) {
            return "Mật khẩu nhập lại không khớp";
        }
        if (name.isEmpty()) {
            return "Tên không được trống";
        }
        return "valid";
    }
    private void showSnackbar(View view, String message, int duration)
    {
        final Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.setAction("DISMISS", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
    private String tvToString(TextView tv) {
        return tv.getText().toString();
    }
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
