package com.liuhesan.app.businessapp.service;


import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;


/**
 * Created by Tao on 2016/11/16.
 */

public interface TokenService {
    @GET("wmpass/openservice/{img}")
    Observable<String> getResult(@Path("img") String img, @QueryMap Map<String, String> options);
}