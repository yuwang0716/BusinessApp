package com.liuhesan.app.myapplication.service;


import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;


/**
 * Created by Tao on 2016/11/16.
 */

public interface IsThirdLoginSuccessService {
    @FormUrlEncoded
    @POST("user/login_wm")
    Observable<String> thirdLoginSuccess(@Field("platform") String takenoutname, @Field("token") String token);

    @FormUrlEncoded
    @POST("user/login_wm")
    Observable<String> commitThirdLogin(@Field("platform") String takenoutname, @Field("token") String token,
                                          @Field("username") String username,@Field("password") String password,
                                        @Field("cookies") String cookies);

}
