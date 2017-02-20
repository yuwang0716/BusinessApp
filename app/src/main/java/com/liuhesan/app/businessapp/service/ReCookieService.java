package com.liuhesan.app.businessapp.service;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Tao on 2016/11/21.
 */

public interface ReCookieService {
    @GET("")
    Observable<String> reCookie();
}
