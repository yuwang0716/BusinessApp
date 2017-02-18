package com.liuhesan.app.myapplication.service;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;


/**
 * Created by Tao on 2016/11/16.
 */

public interface NewOrderService {

    @POST("order/notice")
    Observable<String> getNewOrder(@Query("type") int type);

    @POST("crm")
    Observable<String> getNewOrderData(@Query("qt") String qt,@Query("display") String display);
}
