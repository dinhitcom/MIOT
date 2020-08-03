package com.dinhit.miot.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Room {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("roomTypeId")
    @Expose
    private String roomTypeId;
    @SerializedName("roomName")
    @Expose
    private String roomName;
    @SerializedName("roomDesc")
    @Expose
    private String roomDesc;
    @SerializedName("roomIcon")
    @Expose
    private String roomIcon;

    public Room(String roomTypeId, String roomName, String roomDesc, String roomIcon) {
        this.roomTypeId = roomTypeId;
        this.roomName = roomName;
        this.roomDesc = roomDesc;
        this.roomIcon = roomIcon;
    }

    public String getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(String roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getRoomDesc() {
        return roomDesc;
    }

    public void setRoomDesc(String roomDesc) {
        this.roomDesc = roomDesc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomIcon() {
        return roomIcon;
    }

    public void setRoomIcon(String roomIcon) {
        this.roomIcon = roomIcon;
    }

    public Room(String id, String roomName, String roomIcon) {
        this.id = id;
        this.roomName = roomName;
        this.roomIcon = roomIcon;
    }

    public Room(String roomName, String roomIcon) {
        this.roomName = roomName;
        this.roomIcon = roomIcon;
    }
}
