package com.dinhit.miot.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Port {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("port")
    @Expose
    private Integer port;
    @SerializedName("espPort")
    @Expose
    private Integer espPort;
    @SerializedName("isAvailable")
    @Expose
    private Boolean isAvailable;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getEspPort() {
        return espPort;
    }

    public void setEspPort(Integer espPort) {
        this.espPort = espPort;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

}
