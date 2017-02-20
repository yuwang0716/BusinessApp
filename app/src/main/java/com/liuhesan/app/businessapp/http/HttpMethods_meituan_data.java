package com.liuhesan.app.businessapp.http;

import android.content.Context;
import android.util.Log;

import com.liuhesan.app.businessapp.service.GetMeituanNewOrderService;
import com.liuhesan.app.businessapp.utility.AddCookiesInterceptor;


import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.CertificatePinner;
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

public class HttpMethods_meituan_data {
    private static final int DEFAULT_TIMEOUT = 5;
    private static Context mContext;
    private Retrofit retrofit;
    private final OkHttpClient okHttpClient;
    private GetMeituanNewOrderService getMeituanNewOrderService;
    //构造方法私有
    private HttpMethods_meituan_data() {
        //手动创建一个OkHttpClient并设置超时时间
        /*builder = new OkHttpClient.Builder();

        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i("Tao","OkHttp====message     "+message);
            }
        });
        builder.addInterceptor(interceptor.setLevel(HttpLoggingInterceptor.Level.BODY));
     //   builder.addInterceptor(new AddCookiesInterceptor(mContext,"meituan"));
        builder.certificatePinner(new CertificatePinner.Builder()
                .add("sbbic.com", "sha1/C8xoaOSEzPC6BgGmxAt/EAcsajw=")
                .add("closedevice.com", "sha1/T5x9IXmcrQ7YuQxXnxoCmeeQ84c=")
                .build());*/
         okHttpClient = getUnsafeOkHttpClient();
    }
    private void  getRetrofit(String url){
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(url)
                .build();
    }
    //在访问HttpMethods时创建单例
    private static class SingletonHolder{
        private static final HttpMethods_meituan_data INSTANCE = new HttpMethods_meituan_data();
    }

    //获取单例
    public static HttpMethods_meituan_data getInstance(Context context){
        mContext = context;
        return SingletonHolder.INSTANCE;
    }

    public void getCount(String url,Subscriber<String> subscriber){
        getRetrofit(url);
        getMeituanNewOrderService = retrofit.create(GetMeituanNewOrderService.class);
        getMeituanNewOrderService.getCount()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    public void getData(String url,String time,String isQuery,String getNewVo,Subscriber<String> subscriber){
        getRetrofit(url);
        getMeituanNewOrderService = retrofit.create(GetMeituanNewOrderService.class);
        getMeituanNewOrderService.getData(time,isQuery,getNewVo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
    public static OkHttpClient getUnsafeOkHttpClient() {

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] chain,
                        String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            } };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts,
                    new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext
                    .getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
             builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            builder.sslSocketFactory(sslSocketFactory).hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.i("Tao","OkHttp====message     "+message);
                }
            });
            builder.addInterceptor(interceptor.setLevel(HttpLoggingInterceptor.Level.BODY));
            builder.addInterceptor(new AddCookiesInterceptor(mContext,"meituan"));
            builder.certificatePinner(new CertificatePinner.Builder()
                    .add("sbbic.com", "sha1/C8xoaOSEzPC6BgGmxAt/EAcsajw=")
                    .add("closedevice.com", "sha1/T5x9IXmcrQ7YuQxXnxoCmeeQ84c=")
                    .build());
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
