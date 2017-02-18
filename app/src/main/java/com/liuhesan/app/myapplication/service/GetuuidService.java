package com.liuhesan.app.myapplication.service;


import org.json.JSONObject;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;


/**
 * Created by Tao on 2016/11/16.
 */

public interface GetuuidService {
    //获取uuid
    @GET("napos.picto.melody/login")
    Observable<String> getUuid();
}
