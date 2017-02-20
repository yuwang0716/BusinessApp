package com.liuhesan.app.businessapp.utility;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by Tao on 2016/11/18.
 */

public class ReceivedCookiesInterceptor implements Interceptor {
    private Context context;
    private String headers;
    private String shopName;
    public ReceivedCookiesInterceptor(Context context,String headers,String shopName) {
        super();
        this.context = context;
        this.headers = headers;
        this.shopName = shopName;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {

        okhttp3.Response originalResponse = chain.proceed(chain.request());
        //这里获取请求返回的cookie
        if (!originalResponse.headers(headers).isEmpty()) {
            final StringBuffer cookieBuffer = new StringBuffer();
            Observable.from(originalResponse.headers(headers))
                    .map(new Func1<String, String>() {
                        @Override
                        public String call(String s) {
                            String[] cookieArray = s.split(";");
                            return cookieArray[0];
                        }
                    })
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String cookie) {
                            cookieBuffer.append(cookie).append(";");
                        }
                    });
            SharedPreferences sharedPreferences = context.getSharedPreferences(shopName+"cookie", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("cookie", cookieBuffer.toString());
            editor.commit();
        }

        return originalResponse;

    }
}
