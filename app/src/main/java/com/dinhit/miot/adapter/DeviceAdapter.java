package com.dinhit.miot.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinhit.miot.R;
import com.dinhit.miot.data.model.Device;
import com.dinhit.miot.data.model.ResponseResult;
import com.dinhit.miot.data.remote.IRequestAPI;
import com.dinhit.miot.data.remote.RetrofitClient;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Device> devices = null;
    private int selectedPos = RecyclerView.NO_POSITION;

    public DeviceAdapter(Context context, ArrayList<Device> devices) {
        this.context = context;
        this.devices = devices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.device_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Device device = devices.get(position);
        holder.deviceName.setText(device.getDeviceName());
        holder.sw.setChecked(device.getStatus());
        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int curPos = holder.getAdapterPosition();
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Remove device");
                builder.setMessage("Bạn có muốn xóa thiết bị này không?");
                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Log.d("REMOVE_YES", ""+holder.getAdapterPosition());
                        Retrofit retrofit = RetrofitClient.getClient();
                        IRequestAPI requestAPI = retrofit.create(IRequestAPI.class);
                        Device selectedDevice = devices.get(holder.getAdapterPosition());
                        Call<ResponseResult> call = requestAPI.removeDevice(selectedDevice.getId());
                        call.enqueue(new Callback<ResponseResult>() {
                            @Override
                            public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {
                                String status = response.body().getStatus().toString();
                                String message = response.body().getMessage().toString();
                                String id = response.body().getId().toString();
                                if (status.equals("error")) {
                                } else {
                                    devices.remove(curPos);
                                    notifyItemRemoved(curPos);
                                    notifyItemRangeChanged(curPos, devices.size());
                                    Log.d("REMOVE_SUCCESS", ""+message);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseResult> call, Throwable t) {

                            }
                        });
                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Log.d("REMOVE_NO", ""+holder.getAdapterPosition());
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        holder.sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Retrofit retrofit = RetrofitClient.getClient();
                IRequestAPI requestAPI = retrofit.create(IRequestAPI.class);
                Device selectedDevice = devices.get(holder.getAdapterPosition());
                selectedDevice.setStatus(holder.sw.isChecked());
                Call<ResponseResult> call = requestAPI.turnDevice(selectedDevice.getId(), selectedDevice);
                call.enqueue(new Callback<ResponseResult>() {
                    @Override
                    public void onResponse(Call<ResponseResult> call, Response<ResponseResult> response) {

                    }

                    @Override
                    public void onFailure(Call<ResponseResult> call, Throwable t) {

                    }
                });
            }
        });
        if (selectedPos ==  position) {

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPos = holder.getAdapterPosition();
                Log.d("SelectedPos", ""+selectedPos);

            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView deviceName;
        private Switch sw;
        private ImageView btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.tvDeviceNameR);
            sw = itemView.findViewById(R.id.swDevice);
            btnRemove = itemView.findViewById(R.id.btnRemoveDevice);

        }
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
