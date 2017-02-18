package com.liuhesan.app.myapplication.service;


import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;


/**
 * Created by Tao on 2016/11/16.
 */

public interface LoginSuccessService {
    @FormUrlEncoded
    @POST("user/login")
    Observable<String> loginSuccess(@Field("username") String username, @Field("password") String password);
    //上报新订单
    @FormUrlEncoded
    @POST("order/newOrder")
    Observable<String> commit(@Field("token") String token,@Field("platform") String thirdName,@Field("data") String data);
    //新订单操作
    @FormUrlEncoded
    @POST("order/{operation}")
    Observable<String> operation(@Path("operation") String operation, @Field("token") String token,@FieldMap Map<String, String> params);
}
