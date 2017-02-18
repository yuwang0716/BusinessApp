package com.liuhesan.app.myapplication.http;

import android.content.Context;

import com.liuhesan.app.myapplication.service.BaiduNeworderService;
import com.liuhesan.app.myapplication.subscribers.MySubscriber;
import com.liuhesan.app.myapplication.utility.AddCookiesInterceptor;
import com.liuhesan.app.myapplication.utility.ReceivedCookiesInterceptor;
import com.liuhesan.app.myapplication.service.LoginService;
import com.liuhesan.app.myapplication.service.LoginSuccessService;
import com.liuhesan.app.myapplication.service.NewOrderService;
import com.liuhesan.app.myapplication.service.ReCookieService;
import com.liuhesan.app.myapplication.service.TokenService;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Tao on 2016/11/17.
 */

public class HttpMethods_third {
    private static final int DEFAULT_TIMEOUT = 5;

    private Retrofit retrofit;
    private TokenService tokenService;
    private LoginService loginService;
    private static Context mContext;
    private ReCookieService reCookieService;
    private NewOrderService newOrderService;
    private BaiduNeworderService baiduNeworderService;
    private LoginSuccessService loginSuccessService;
    private final OkHttpClient.Builder builder;

    //构造方法私有
    private HttpMethods_third() {
        //手动创建一个OkHttpClient并设置超时时间
        builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.addInterceptor(new ReceivedCookiesInterceptor(mContext,"Set-Cookie","baidu"));
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
        private static final HttpMethods_third INSTANCE = new HttpMethods_third();
    }

    //获取单例
    public static HttpMethods_third getInstance(Context context){
        mContext = context;
        return SingletonHolder.INSTANCE;
    }
    public void getResult(String url,String img,Map<String, String> options,Subscriber<String> subscriber) {
        getRetrofit(url);
        tokenService = retrofit.create(TokenService.class);
        tokenService.getResult(img,options)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

    }
    //获取全部cookie
    public void  updateUser(String url,String redirect_url,String return_url,String type,String channel,
                            String account,String upass,String captcha,String token,
                            Subscriber<String> subscriber){
        getRetrofit(url);
        loginService = retrofit.create(LoginService.class);
        loginService.updateUser(redirect_url,return_url,type,channel,account,upass,captcha,token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    //重新获取全部cookie
    public void  reCookie(String url,Subscriber<String> subscriber){
        getRetrofit(url);
        reCookieService = retrofit.create(ReCookieService.class);
        reCookieService.reCookie()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
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
