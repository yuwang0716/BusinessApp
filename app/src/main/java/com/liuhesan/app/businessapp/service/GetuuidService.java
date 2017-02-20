package com.liuhesan.app.businessapp.service;


import retrofit2.http.GET;
import rx.Observable;


/**
 * Created by Tao on 2016/11/16.
 */

public interface GetuuidService {
    //获取uuid
    @GET("napos.picto.melody/login")
    Observable<String> getUuid();
}
