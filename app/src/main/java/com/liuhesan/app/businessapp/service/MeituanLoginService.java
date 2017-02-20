package com.liuhesan.app.businessapp.service;


import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;


/**
 * Created by Tao on 2016/11/16.
 */

public interface MeituanLoginService {

    @Headers({"User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36","Referer:http://e.waimai.meituan.com/logon"})
    @FormUrlEncoded
    @POST("v2/logon/pass/step1/logon")
    Observable<String> login(@Field("userName") String userName, @Field("password") String password,
                             @Field("imgVerifyValue") String imgVerifyValue, @Field("service") String service);
}
