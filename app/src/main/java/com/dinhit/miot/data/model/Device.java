package com.dinhit.miot.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Device {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("deviceName")
    @Expose
    private String deviceName;
    @SerializedName("devicePort")
    @Expose
    private String devicePort;
    @SerializedName("deviceEspPort")
    @Expose
    private String deviceEspPort;
    @SerializedName("status")
    @Expose
    private Boolean status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Device(String deviceName, String devicePort, String deviceEspPort) {
        this.deviceName = deviceName;
        this.devicePort = devicePort;
        this.deviceEspPort = deviceEspPort;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDevicePort() {
        return devicePort;
    }

    public void setDevicePort(String devicePort) {
        this.devicePort = devicePort;
    }

    public String getDeviceEspPort() {
        return deviceEspPort;
    }

    public void setDeviceEspPort(String deviceEspPort) {
        this.deviceEspPort = deviceEspPort;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

}
