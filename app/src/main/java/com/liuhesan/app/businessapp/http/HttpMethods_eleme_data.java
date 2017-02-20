package com.liuhesan.app.businessapp.http;

import android.content.Context;

import com.liuhesan.app.businessapp.service.IsElemeLoginSuccessService;
import com.liuhesan.app.businessapp.utility.AddCookiesInterceptor;

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

public class HttpMethods_eleme_data {
    private static final int DEFAULT_TIMEOUT = 5;
    private Retrofit retrofit;
    private static Context mContext;
    private final OkHttpClient.Builder builder;
    private IsElemeLoginSuccessService isElemeLoginSuccessService;
    //构造方法私有
    private HttpMethods_eleme_data() {
        //手动创建一个OkHttpClient并设置超时时间
        builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
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
        private static final HttpMethods_eleme_data INSTANCE = new HttpMethods_eleme_data();
    }

    //获取单例
    public static HttpMethods_eleme_data getInstance(Context context){
        mContext = context;
        return SingletonHolder.INSTANCE;
    }
    //获取新订单
    public void getData(String url,String method, RequestBody body, Subscriber<String> subscriber){
        getRetrofit(url);
        isElemeLoginSuccessService = retrofit.create(IsElemeLoginSuccessService.class);
        isElemeLoginSuccessService.getData(method,body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
  /* private <T> void toSubscribe(Observable<T> o, Subscriber<T> s){
        o.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }*/



}
