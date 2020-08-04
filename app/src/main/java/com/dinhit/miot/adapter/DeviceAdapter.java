package com.dinhit.miot.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinhit.miot.R;
import com.dinhit.miot.data.model.Device;

import java.util.ArrayList;

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
                Log.d("REMOVE_BUTTON", "Clicked "+holder.getAdapterPosition());
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

}
