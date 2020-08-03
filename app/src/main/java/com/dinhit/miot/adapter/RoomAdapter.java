package com.dinhit.miot.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dinhit.miot.R;
import com.dinhit.miot.data.model.Room;

import java.util.ArrayList;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Room> rooms = null;
    private int selectedPos = RecyclerView.NO_POSITION;

    public RoomAdapter(Context context, ArrayList<Room> rooms) {
        this.context = context;
        this.rooms = rooms;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView roomIcon;
        private TextView roomName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            roomIcon = itemView.findViewById(R.id.imgRoomIcon);
            roomName = itemView.findViewById(R.id.tvRoomName);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Room room = rooms.get(position);
        //
        Glide.with(context)
                .load(room.getRoomIcon())
                .placeholder(R.drawable.room)
                .error(R.drawable.error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .animate(R.anim.fade_in)
                .fitCenter()
                .into(holder.roomIcon);
        holder.roomName.setText(room.getRoomName());
        if (selectedPos ==  position) {
            holder.itemView.setBackgroundResource(R.drawable.border);
            String roomName = room.getRoomName();
            String roomIcon = room.getRoomIcon();
            String roomId = room.getId();
            Boolean isSelected = true;
            Intent intent = new Intent("selected_room_info");
            intent.putExtra("roomName", roomName);
            intent.putExtra("roomIcon", roomIcon);
            intent.putExtra("roomId", roomId);
            intent.putExtra("isSelected", isSelected);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
        else holder.itemView.setBackgroundResource(R.drawable.border_shadow);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPos = position;
                notifyDataSetChanged();
            }
        });
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View roomView = layoutInflater.inflate(R.layout.room_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(roomView);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }


}
