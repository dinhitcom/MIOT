package com.dinhit.miot;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.dinhit.miot.data.model.ResponseResult;
import com.dinhit.miot.data.remote.IRequestAPI;
import com.dinhit.miot.data.remote.RetrofitClient;
import com.google.android.material.snackbar.Snackbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    public static final String EXTRA_NAME = "com.dinhit.miot.extra.NAME";
    public static final String EXTRA_UID = "com.dinhit.miot.extra.UID";
    private Button loginButton;
    private EditText username, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Intent intent = getIntent();
        if(getIntent()!=null && getIntent().getExtras()!=null){
            String savedUsername = intent.getStringExtra(RegisterActivity.EXTRA_USERNAME);
            username.setText(savedUsername);
        }
        loginButton = (Button) findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                View loginView = findViewById(R.id.layoutLogin);
                String uname = username.getText().toString();
                String pwd = password.getText().toString();
                if (validLoginData(uname, pwd).equals("valid")) {
                    sendLoginRequest(uname, pwd, loginView);

                } else showSnackbar(loginView, validLoginData(uname, pwd),0 );
            }
        });
    }

    public void launchRegisterActivity(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
    private void sendLoginRequest(String username, String password, final View view) {
        Retrofit retrofit = RetrofitClient.getClient();
        IRequestAPI requestAPI = retrofit.create(IRequestAPI.class);
        Call<ResponseResult> call = requestAPI.signIn(username, password);
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
                    final String name = response.body().getName().toString();
                    final String uid = response.body().getUid().toString();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra(EXTRA_NAME, name);
                            intent.putExtra(EXTRA_UID, uid);
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
    private String validLoginData(String username, String password) {
        if (username.isEmpty())
            return "Tên đăng nhập không được trống";
        else
            if (username.length()<5)
                return "Tên đăng nhập phải chứa hơn 5 ký tự";
        if (password.isEmpty())
            return "Mật khẩu không được trống";
        else
            if (password.length()<6)
                return "Mật khẩu phải chứa hơn 6 ký tự";
        return "valid";
    }
    public void showSnackbar(View view, String message, int duration)
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
    /*public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        View focusedView = activity.getCurrentFocus();
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }*/
}
