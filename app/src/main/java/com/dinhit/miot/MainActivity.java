package com.dinhit.miot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dinhit.miot.adapter.UserRoomAdapter;
import com.dinhit.miot.data.model.ResponseResult;
import com.dinhit.miot.data.model.Room;
import com.dinhit.miot.data.remote.IRequestAPI;
import com.dinhit.miot.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_UID = "com.dinhit.miot.extra.UID";
    public static final String EXTRA_NAME = "com.dinhit.miot.extra.NAME";
    private String uid ="", name="";
    private TextView tvTemp, tvHumi, tvAir, tvTempIndicator, tvHumiIndicator, tvAirIndicator;
    private ArrayList<Room> roomtypes = new ArrayList<>();
    private UserRoomAdapter roomAdapter;
    private ImageView tempIndicator, humiIndicator, airIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        TextView btnAddRoom = findViewById(R.id.btnAddRoomM);
        Intent intent = getIntent();
        name = intent.getStringExtra(LoginActivity.EXTRA_NAME);
        uid = intent.getStringExtra(LoginActivity.EXTRA_UID);
        TextView welcomeName = findViewById(R.id.tvWelcomeName);
        welcomeName.setText(name);
        updateParams();
        RecyclerView recyclerView = findViewById(R.id.rvRooms);
        getUserRoomsList(uid, recyclerView);
        btnAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchAddRoomActivity(uid, name);
            }
        });
    }
    private void getUserRoomsList(String uid, final RecyclerView recyclerView) {
        Retrofit retrofit = RetrofitClient.getClient();
        IRequestAPI requestAPI = retrofit.create(IRequestAPI.class);
        Call<List<Room>> call = requestAPI.getUserRoomsList(uid);
        call.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                roomtypes.clear();
                try {
                    List<Room> roomList = response.body();
                    roomtypes.addAll(roomList);
                    roomAdapter = new UserRoomAdapter(MainActivity.this, roomtypes);
                    recyclerView.setAdapter(roomAdapter);
                    recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));

                } catch (Exception e) {
                    e.printStackTrace();
                }
                roomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {

            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String currentDateTime = java.text.DateFormat.getDateTimeInstance().format(new Date());
            TextView tvDateTime = findViewById(R.id.tvDateTime);
            tvDateTime.setText(currentDateTime);
            tvTemp = findViewById(R.id.tvTempValue);
            tvHumi = findViewById(R.id.tvHumidityValue);
            tvAir = findViewById(R.id.tvAirQualityValue);
            tempIndicator = findViewById(R.id.tempIndicator);
            humiIndicator = findViewById(R.id.humidityIndicator);
            airIndicator = findViewById(R.id.airIndicator);
            tvTempIndicator = findViewById(R.id.tvTempIndicator);
            tvHumiIndicator = findViewById(R.id.tvHumidityIndicator);
            tvAirIndicator = findViewById(R.id.tvAirIndicator);
            Retrofit retrofit = RetrofitClient.getClient();
            IRequestAPI requestAPI = retrofit.create(IRequestAPI.class);
            Call<ResponseResult> callGetTemp = requestAPI.getTemp();
            callGetTemp.enqueue(new retrofit2.Callback<ResponseResult>() {
                @Override
                public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                    tvTemp.setText(response.body().getMessage());
                    int temp = Integer.parseInt(response.body().getMessage());
                    if (temp < 20) {
                        tvTempIndicator.setText("Thấp");
                        tempIndicator.setImageResource(R.drawable.status_yellow_dot);
                    } else
                        if (temp > 30) {
                            tvTempIndicator.setText("Cao");
                            tempIndicator.setImageResource(R.drawable.status_red_dot);
                        } else {
                            tvTempIndicator.setText("Tốt");
                            tempIndicator.setImageResource(R.drawable.status_green_dot);
                        }
                }

                @Override
                public void onFailure(Call<ResponseResult> call, Throwable t) {
                    tvTempIndicator.setText("Read fail");
                    tempIndicator.setImageResource(R.drawable.status_gray_dot);
                }
            });
            Call<ResponseResult> callGetHumi = requestAPI.getHumi();
            callGetHumi.enqueue(new retrofit2.Callback<ResponseResult>() {
                @Override
                public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                    tvHumi.setText(response.body().getMessage());
                    int humi = Integer.parseInt(response.body().getMessage());
                    if (humi < 40) {
                        tvHumiIndicator.setText("Thấp");
                        humiIndicator.setImageResource(R.drawable.status_yellow_dot);
                    } else
                    if (humi > 70) {
                        tvHumiIndicator.setText("Cao");
                        humiIndicator.setImageResource(R.drawable.status_red_dot);
                    } else {
                        tvHumiIndicator.setText("Tốt");
                        humiIndicator.setImageResource(R.drawable.status_green_dot);
                    }
                }
                @Override
                public void onFailure(Call<ResponseResult> call, Throwable t) {
                    tvHumiIndicator.setText("Read fail");
                    humiIndicator.setImageResource(R.drawable.status_gray_dot);
                }
            });
            Call<ResponseResult> callGetAir = requestAPI.getAirQuality();
            callGetAir.enqueue(new retrofit2.Callback<ResponseResult>() {
                @Override
                public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                    tvAir.setText(response.body().getMessage());
                    Float air = Float.parseFloat(response.body().getMessage());
                    if (air < 5) {
                        tvAirIndicator.setText("Tốt");
                        airIndicator.setImageResource(R.drawable.status_green_dot);
                    } else
                    if (air < 10) {
                        tvAirIndicator.setText("Bình thường");
                        airIndicator.setImageResource(R.drawable.status_yellow_dot);
                    } else {
                        tvAirIndicator.setText("Xấu");
                        airIndicator.setImageResource(R.drawable.status_red_dot);
                    }
                }
                @Override
                public void onFailure(Call<ResponseResult> call, Throwable t) {
                    tvAirIndicator.setText("Read fail");
                    airIndicator.setImageResource(R.drawable.status_gray_dot);
                }
            });
        }
    };
    private void updateParams() {
        int initialDelay = 1000; //first update in miliseconds
        int period = 1000;      //nexts updates in miliseconds
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                Message msg = new Message();
                mHandler.sendMessage(msg);
            }
        };
        timer.scheduleAtFixedRate(task, initialDelay, period);
    }

    public void launchAirMonitoringActivity(View view) {
        Toast.makeText(this, "Layout clicked", Toast.LENGTH_SHORT).show();
    }

    public void launchAddRoomActivity(String uid, String name) {
        Intent intent = new Intent(MainActivity.this, AddRoomActivity.class);
        intent.putExtra(EXTRA_UID, uid);
        intent.putExtra(EXTRA_NAME, name);
        startActivityForResult(intent, 1);
    }
}
