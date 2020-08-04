package com.dinhit.miot.data.remote;

import com.dinhit.miot.data.model.Device;
import com.dinhit.miot.data.model.Port;
import com.dinhit.miot.data.model.ResponseResult;
import com.dinhit.miot.data.model.Room;
import com.dinhit.miot.data.model.User;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IRequestAPI {
    @GET("/users/list")
    Call<List<User>> getUserList();

    @POST("/users/create")
    Call<ResponseResult> signUp(@Body User user);

    @FormUrlEncoded
    @POST("/users/login")
    Call<ResponseResult> signIn(@Field("username") String username, @Field("password") String password);

    @GET("/rooms/list")
    Call<List<Room>> getRoomList();

    @GET("/users/{uid}/rooms")
    Call<List<Room>> getUserRoomsList(@Path("uid") String uid);

    @POST("/users/{uid}/room")
    Call<ResponseResult> addRoom(@Path("uid") String uid, @Body Room room);

    @GET("/rooms/ports")
    Call<List<Port>> getPortList();
    @POST("/rooms/{rid}/devices/add")
    Call<ResponseResult> addDevice(@Path("rid") String rid, @Body Device device);
    @GET("/rooms/{rid}/devices")
    Call<List<Device>> getDevices(@Path("rid") String rid);
    @DELETE("/rooms/devices/{did}")
    Call<ResponseResult> removeDevice(@Path("did") String did);

    @PUT("/rooms/ports/update/{pid}")
    Call<ResponseResult> updatePort(@Path("pid") String pid);
    @PUT("/rooms/devices/{did}/turn")
    Call<ResponseResult> turnDevice(@Path("did") String did, @Body Device device);
    @GET("/rooms/temperature")
    Call<ResponseResult> getTemp();

    @GET("/rooms/humidity")
    Call<ResponseResult> getHumi();

    @GET("rooms/airquality")
    Call<ResponseResult> getAirQuality();
}
