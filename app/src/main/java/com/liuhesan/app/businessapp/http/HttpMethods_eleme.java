package com.liuhesan.app.businessapp.http;

import android.content.Context;

import com.liuhesan.app.businessapp.service.GetuuidService;
import com.liuhesan.app.businessapp.service.IsElemeLoginSuccessService;
import com.liuhesan.app.businessapp.utility.AddCookiesInterceptor;
import com.liuhesan.app.businessapp.utility.ReceivedCookiesInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Tao on 2016/11/17.
 */

public class HttpMethods_eleme {
    private static final int DEFAULT_TIMEOUT = 5;
    private GetuuidService getuuidService;
    private IsElemeLoginSuccessService isElemeLoginSuccessService;
    private Retrofit retrofit;
    private static Context mContext;
    private final OkHttpClient.Builder builder;

    //构造方法私有
    private HttpMethods_eleme() {
        //手动创建一个OkHttpClient并设置超时时间
        builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.addInterceptor(new ReceivedCookiesInterceptor(mContext,"X-NWS-LOG-UUID","eleme"));
        builder.addInterceptor(new AddCookiesInterceptor(mContext,"eleme"));
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
        private static final HttpMethods_eleme INSTANCE = new HttpMethods_eleme();
    }

    //获取单例
    public static HttpMethods_eleme getInstance(Context context){
        mContext = context;
        return SingletonHolder.INSTANCE;
    }

    //获取uuid
    public void getUuid(String url, Subscriber<String> subscriber){
        getRetrofit(url);
        getuuidService = retrofit.create(GetuuidService.class);
        getuuidService.getUuid()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    //登录
    public void getLogin(String url, RequestBody body, Subscriber<String> subscriber){
        getRetrofit(url);
        isElemeLoginSuccessService = retrofit.create(IsElemeLoginSuccessService.class);
        isElemeLoginSuccessService.login(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
  /* private <T> void toSubscribe(Observable<T> o, Subscriber<T> s){
        o.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }*/



}
