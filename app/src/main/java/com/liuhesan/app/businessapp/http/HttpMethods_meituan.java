package com.liuhesan.app.businessapp.http;

import android.content.Context;
import android.util.Log;


import com.liuhesan.app.businessapp.service.GetMeituanCookieService;
import com.liuhesan.app.businessapp.utility.AddCookiesInterceptor;
import com.liuhesan.app.businessapp.utility.ReceivedCookiesInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Tao on 2016/11/17.
 */

public class HttpMethods_meituan {
    private static final int DEFAULT_TIMEOUT = 5;
    private static Context mContext;
    private Retrofit retrofit;
    private final OkHttpClient.Builder builder;
    private GetMeituanCookieService getMeituanCookieService;
    //构造方法私有
    private HttpMethods_meituan() {
        //手动创建一个OkHttpClient并设置超时时间
        builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i("Tao","OkHttp====message     "+message);
            }
        });
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.addInterceptor
                (interceptor.setLevel(HttpLoggingInterceptor.Level.BODY));
        builder.addInterceptor(new ReceivedCookiesInterceptor(mContext,"Set-Cookie","meituanuuid"));
        builder.addInterceptor(new AddCookiesInterceptor(mContext,"meituanuuid"));
    }
    private void  getRetrofit(String url){
        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(url)
                .build();
    }
    //在访问HttpMethods时创建单例
    private static class SingletonHolder{
        private static final HttpMethods_meituan INSTANCE = new HttpMethods_meituan();
    }

    //获取单例
    public static HttpMethods_meituan getInstance(Context context){
        mContext = context;
        return SingletonHolder.INSTANCE;
    }
    public void getCookie(String url,Subscriber<String> subscriber){

        getRetrofit(url);
        getMeituanCookieService = retrofit.create(GetMeituanCookieService.class);

        getMeituanCookieService.getCookie()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}
