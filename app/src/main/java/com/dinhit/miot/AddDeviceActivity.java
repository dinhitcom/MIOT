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
import android.widget.CheckBox;
import android.widget.EditText;

import com.dinhit.miot.data.model.Device;
import com.dinhit.miot.data.model.Port;
import com.dinhit.miot.data.model.ResponseResult;
import com.dinhit.miot.data.remote.IRequestAPI;
import com.dinhit.miot.data.remote.RetrofitClient;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddDeviceActivity extends AppCompatActivity {
    private EditText tvDevice, tvPort;
    private ArrayList<Port> ports = new ArrayList<>();
    private CheckBox cbLight;
    private String deviceName="", devicePort="", roomId="", roomName="", pid="";
    private Button btnAdddDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        getPortList();
        Intent intent = getIntent();
        roomId = intent.getStringExtra("EXTRA_ROOM_ID");
        roomName = intent.getStringExtra("EXTRA_ROOM_NAME");
        final View view = findViewById(R.id.layoutAddDevice);
        tvDevice = findViewById(R.id.tvDeviceName);
        tvPort = findViewById(R.id.tvPort);
        cbLight = findViewById(R.id.cbLight);
        btnAdddDevice = findViewById(R.id.btnAddDeviceSubmit);
        btnAdddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                deviceName = tvDevice.getText().toString();
                devicePort = tvPort.getText().toString();
                String msg = validData(deviceName, devicePort);
                String espPort = "";
                if (msg.equals("valid")) {
                    for (Port port : ports) {
                        if (port.getPort().toString().equals(devicePort)) {
                            espPort = port.getEspPort().toString();
                            pid = port.getId();
                        }
                    }
                    Device device = new Device(deviceName, devicePort, espPort);
                    sendAddDeviceRequest(roomId, device, view);
                } else showSnackbar(view, msg, 0);
            }
        });

        //validData
    }
    private void sendAddDeviceRequest(final String roomId, Device device, final View view) {
        Retrofit retrofit = RetrofitClient.getClient();
        final IRequestAPI requestAPI = retrofit.create(IRequestAPI.class);
        Call<ResponseResult> call = requestAPI.addDevice(roomId, device);
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
                            Call<ResponseResult> call2 = requestAPI.updatePort(pid);
                            call2.enqueue(new Callback<ResponseResult>() {
                                @Override
                                public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {

                                }

                                @Override
                                public void onFailure(Call<ResponseResult> call, Throwable t) {

                                }
                            });
                            showSnackbar(view, message, 0);
                            Intent intent = new Intent(AddDeviceActivity.this, DeviceManagerActivity.class);
                            intent.putExtra("EXTRA_ROOM_ID", roomId);
                            intent.putExtra("EXTRA_ROOM_NAME", roomName);
                            startActivity(intent);
                        }
                    },1000);
                }
            }

            @Override
            public void onFailure(Call<ResponseResult> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    private String validData(String deviceName, String devicePort) {
        if (deviceName.isEmpty()) return "Tên thiết bị không được trống";

        if (devicePort.isEmpty()) return "Port không được trống";
        else {
            int portNumber = Integer.parseInt(devicePort);
            if (portNumber<0 || portNumber > 15) return "Port phải trong khoảng 0-15";
            else
                for (Port port : ports) {
                    if (port.getPort().toString().equals(devicePort))
                        if (port.getIsAvailable() == false) return "Port không khả dụng";
                }
        }
        return "valid";
    }
    private void getPortList() {
        Retrofit retrofit = RetrofitClient.getClient();
        IRequestAPI requestAPI = retrofit.create(IRequestAPI.class);
        Call<List<Port>> call = requestAPI.getPortList();
        call.enqueue(new Callback<List<Port>>() {
            @Override
            public void onResponse(Call<List<Port>> call, Response<List<Port>> response) {
                ports.clear();
                try {
                    List<Port> portList = response.body();
                    ports.addAll(portList);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<List<Port>> call, Throwable t) {

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
}
