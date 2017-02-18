package com.liuhesan.app.myapplication.http;

import android.content.Context;

import com.liuhesan.app.myapplication.service.BaiduNeworderService;
import com.liuhesan.app.myapplication.service.LoginService;
import com.liuhesan.app.myapplication.service.LoginSuccessService;
import com.liuhesan.app.myapplication.service.NewOrderService;
import com.liuhesan.app.myapplication.service.ReCookieService;
import com.liuhesan.app.myapplication.service.TokenService;
import com.liuhesan.app.myapplication.utility.AddCookiesInterceptor;
import com.liuhesan.app.myapplication.utility.ReceivedCookiesInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Tao on 2016/11/17.
 */

public class HttpMethods_third_data {
    private static final int DEFAULT_TIMEOUT = 5;
    private NewOrderService newOrderService;
    private Retrofit retrofit;
    private static Context mContext;
    private BaiduNeworderService baiduNeworderService;
    private LoginSuccessService loginSuccessService;
    private final OkHttpClient.Builder builder;

    //构造方法私有
    private HttpMethods_third_data() {
        //手动创建一个OkHttpClient并设置超时时间
        builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.addInterceptor(new AddCookiesInterceptor(mContext,"baidu"));
    }
    public void  getRetrofit(String url){
        retrofit = new Retrofit.Builder()
                .client(builder.build())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(url)
                .build();
    }
    //在访问HttpMethods时创建单例
    private static class SingletonHolder{
        private static final HttpMethods_third_data INSTANCE = new HttpMethods_third_data();
    }

    //获取单例
    public static HttpMethods_third_data getInstance(Context context){
        mContext = context;
        return SingletonHolder.INSTANCE;
    }

    //获取新订单通知
    public void getNewOrder(String url,int type,Subscriber<String> subscriber) {
        getRetrofit(url);
        newOrderService = retrofit.create(NewOrderService.class);
         newOrderService.getNewOrder(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        /*Observable observable = newOrderService.getNewOrderData(type);
        observable.interval(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        toSubscribe(observable, subscriber);*/
    }
    //获取新订单详情
    public void getNewOrderdata(String url,String qt, String display,Subscriber<String> subscriber) {
        getRetrofit(url);
        newOrderService = retrofit.create(NewOrderService.class);
        newOrderService.getNewOrderData(qt, display)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
        /*Observable observable = newOrderService.getNewOrderData(type);
        observable.interval(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        toSubscribe(observable, subscriber);*/
    }
    public void cancel(String url,String qt,Map<String, String> params,Subscriber<String> subscriber){
        getRetrofit(url);
        baiduNeworderService = retrofit.create(BaiduNeworderService.class);
       baiduNeworderService.cancel(qt,params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    private <T> void toSubscribe(Observable<T> o, Subscriber<T> s){
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }


}
