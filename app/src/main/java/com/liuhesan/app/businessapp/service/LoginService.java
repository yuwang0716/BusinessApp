package com.liuhesan.app.businessapp.service;


import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;


/**
 * Created by Tao on 2016/11/16.
 */

public interface LoginService {
    @FormUrlEncoded
    @POST("api/login")
    Observable<String> updateUser(@Field("redirect_url") String redirect_url, @Field("return_url") String return_url,
                                  @Field("type") String type, @Field("channel") String channel,
                                  @Field("account") String account, @Field("upass") String upass,
                                  @Field("captcha") String captcha, @Field("token") String token);
}
