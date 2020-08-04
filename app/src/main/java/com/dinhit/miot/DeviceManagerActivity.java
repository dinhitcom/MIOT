package com.dinhit.miot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dinhit.miot.adapter.DeviceAdapter;
import com.dinhit.miot.adapter.UserRoomAdapter;
import com.dinhit.miot.data.model.Device;
import com.dinhit.miot.data.model.Port;
import com.dinhit.miot.data.model.Room;
import com.dinhit.miot.data.remote.IRequestAPI;
import com.dinhit.miot.data.remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DeviceManagerActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView backButton;
    private Button addDeviceBtn;
    private String roomId, roomName;
    private RecyclerView recyclerView;
    private ArrayList<Device> devices = new ArrayList<>();
    private DeviceAdapter deviceAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_device_manager);
        backButton = findViewById(R.id.imgBtnBack);
        backButton.setOnClickListener(this);
        Intent intent = getIntent();
        roomId = intent.getStringExtra("EXTRA_ROOM_ID");
        roomName = intent.getStringExtra("EXTRA_ROOM_NAME");
        TextView textView = findViewById(R.id.tvRoomName);
        textView.setText(roomName);
        addDeviceBtn = findViewById(R.id.btnAddDevice);
        addDeviceBtn.setOnClickListener(this);
        recyclerView = findViewById(R.id.rvDevicesList);
        Log.d("ROOM_ID", "RID"+roomId);
        getDevicesList(roomId, recyclerView);

    }
    private void getDevicesList(String roomId, final RecyclerView recyclerView) {
        Retrofit retrofit = RetrofitClient.getClient();
        IRequestAPI requestAPI = retrofit.create(IRequestAPI.class);
        Call<List<Device>> call = requestAPI.getDevices(roomId);
        call.enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                devices.clear();
                try {
                    List<Device> deviceList = response.body();
                    devices.addAll(deviceList);
                    deviceAdapter = new DeviceAdapter(DeviceManagerActivity.this, devices);
                    recyclerView.setAdapter(deviceAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                deviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {

            }
        });
    }
    @Override
    public void onClick(View v) {
        if (v == backButton) {
            finish();

        }
        if (v == addDeviceBtn) {
            Intent intent = new Intent(DeviceManagerActivity.this, AddDeviceActivity.class);
            intent.putExtra("EXTRA_ROOM_ID", roomId);
            intent.putExtra("EXTRA_ROOM_NAME", roomName);
            startActivity(intent);
        }
    }
}
