package com.dinhit.miot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dinhit.miot.adapter.RoomAdapter;
import com.dinhit.miot.data.model.ResponseResult;
import com.dinhit.miot.data.model.Room;
import com.dinhit.miot.data.remote.IRequestAPI;
import com.dinhit.miot.data.remote.RetrofitClient;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddRoomActivity extends AppCompatActivity{
    public static final String EXTRA_UID = "com.dinhit.miot.extra.UID";
    public static final String EXTRA_NAME = "com.dinhit.miot.extra.NAME";
    private RecyclerView recyclerView;
    private ArrayList<Room> roomtypes = new ArrayList<>();
    private RoomAdapter roomAdapter;
    private String roomName ="", roomIcon, roomId="", uid, name;
    private Boolean roomSelected, msgRecieved = false;
    private EditText tvRoomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);
        recyclerView = findViewById(R.id.roomTypesList);
        tvRoomName = findViewById(R.id.roomName);
        final EditText roomDesc = findViewById(R.id.roomDesc);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("selected_room_info"));
        getRoomList();
        Intent intent = getIntent();
        uid = intent.getStringExtra(MainActivity.EXTRA_UID);
        name = intent.getStringExtra(MainActivity.EXTRA_NAME);
        Button btnAddRoom = findViewById(R.id.btnAddRoom);
        final View view = findViewById(R.id.layoutAddRoom);

        btnAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (msgRecieved) {
                    final String name = tvToString(tvRoomName);
                    String desc = tvToString(roomDesc);
                    if (name.equals("") || name.equals(null)) {
                        showSnackbar(view, "Tên phòng không được trống", 0);
                    } else {
                        Room room = new Room(roomId, name, desc , roomIcon);
                        sendAddRoomRequest(uid, room, view);
                    }
                } else {
                    showSnackbar(view, "Vui lòng chọn loại phòng", 0);
                }
            }
        });
    }
    private void sendAddRoomRequest(final String uid, Room room, final View view) {
        Retrofit retrofit = RetrofitClient.getClient();
        IRequestAPI requestAPI = retrofit.create(IRequestAPI.class);
        Call<ResponseResult> call = requestAPI.addRoom(uid, room);
        call.enqueue(new Callback<ResponseResult>() {
            @Override
            public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                String status = response.body().getStatus().toString();
                final String message = response.body().getMessage().toString();
                String id = response.body().getId().toString();
                if (status.equals("error")) {
                    showSnackbar(view, message, 0);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showSnackbar(view, message, 0);
                            Intent intent = new Intent(AddRoomActivity.this, MainActivity.class);
                            intent.putExtra(EXTRA_UID, uid);
                            intent.putExtra(EXTRA_NAME, name);
                            startActivity(intent);
                        }
                    },1000);
                }
            }
            @Override
            public void onFailure(Call<ResponseResult> call, Throwable t) {
                t.printStackTrace();
                showSnackbar(view, "Lỗi kết nối", 0);
            }
        });
    }
    BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            roomName = intent.getStringExtra("roomName");
            roomIcon = intent.getStringExtra("roomIcon");
            roomId = intent.getStringExtra("roomId");
            roomSelected = intent.getBooleanExtra("isSelected", false);
            msgRecieved = true;
            tvRoomName.setText(roomName);
        }
    };
    private void getRoomList() {
        Retrofit retrofit = RetrofitClient.getClient();
        IRequestAPI requestAPI = retrofit.create(IRequestAPI.class);
        Call<List<Room>> call = requestAPI.getRoomList();
        call.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                roomtypes.clear();
                try {
                    List<Room> roomList = response.body();
                    roomtypes.addAll(roomList);
                    roomAdapter = new RoomAdapter(AddRoomActivity.this, roomtypes);
                    recyclerView.setAdapter(roomAdapter);
                    recyclerView.setLayoutManager(new GridLayoutManager(AddRoomActivity.this, 2));

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
    private String tvToString(TextView tv) {
        return tv.getText().toString();
    }
}
