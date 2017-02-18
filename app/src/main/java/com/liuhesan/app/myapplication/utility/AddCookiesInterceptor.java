package com.liuhesan.app.myapplication.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by Tao on 2016/11/18.
 */

public class AddCookiesInterceptor implements Interceptor {
    private Context context;
    private String shopName;
    public AddCookiesInterceptor(Context context,String shopName) {
        super();
        this.context = context;
        this.shopName = shopName;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {

      final Request.Builder builder = chain.request().newBuilder();
        SharedPreferences sharedPreferences = context.getSharedPreferences(shopName+"cookie", Context.MODE_PRIVATE);
        Log.i("TAG1",sharedPreferences.getString("cookie", "")+ "intercept: "+"  "+shopName+"cookie");

        Observable.just(sharedPreferences.getString("cookie", ""))
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String cookie) {
                        //添加cookie
                        Log.i("TAG0", cookie+"call: ");
                        builder.addHeader("Cookie", cookie);

                    }
                });
        return chain.proceed(builder.build());
    }
}