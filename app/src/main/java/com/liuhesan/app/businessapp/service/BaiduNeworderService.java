package com.liuhesan.app.businessapp.service;


import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;


/**
 * Created by Tao on 2016/11/16.
 */

public interface BaiduNeworderService {
    @FormUrlEncoded
    @POST("crm?")
    Observable<String> cancel(@Field("qt") String qt,@FieldMap Map<String, String> params);
}
