package com.dinhit.miot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dinhit.miot.adapter.UserRoomAdapter;
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
    private TextView tvDateTime;
    private RecyclerView recyclerView;
    private ArrayList<Room> roomtypes = new ArrayList<>();
    private UserRoomAdapter roomAdapter;
    private TextView welcomeName, btnAddRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_main);
        btnAddRoom = findViewById(R.id.btnAddRoomM);
        Intent intent = getIntent();
        name = intent.getStringExtra(LoginActivity.EXTRA_NAME);
        uid = intent.getStringExtra(LoginActivity.EXTRA_UID);
        welcomeName = findViewById(R.id.tvWelcomeName);
        welcomeName.setText(name);
        updateTime();
        recyclerView = findViewById(R.id.rvRooms);
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String currentDateTime = java.text.DateFormat.getDateTimeInstance().format(new Date());
            tvDateTime = findViewById(R.id.tvDateTime);
            tvDateTime.setText(currentDateTime);
        }
    };
    private void updateTime() {
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
