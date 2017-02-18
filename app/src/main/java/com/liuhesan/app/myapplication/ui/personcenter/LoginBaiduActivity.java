package com.liuhesan.app.myapplication.ui.personcenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.liuhesan.app.myapplication.R;
import com.liuhesan.app.myapplication.utility.API;
import com.liuhesan.app.myapplication.http.HttpMethods_third;
import com.liuhesan.app.myapplication.utility.AppManager;
import com.liuhesan.app.myapplication.utility.L;
import com.liuhesan.app.myapplication.utility.StringToList;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.SimpleResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;
import rx.Subscriber;

/**
 * Created by Tao on 2016/11/28.
 */

public class LoginBaiduActivity extends Activity {
    private final static String TAG = "LoginBaidu";
    @BindView(R.id.login_third_webview)
    WebView mWebView;
    private WebAppInterface appInterface;
    private String name, pwd, imgtext;
    private String token, wmstoken, cookie;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_baidu);
        AppManager.getAppManager().addActivity(LoginBaiduActivity.this);
        ButterKnife.bind(this);
        requestQueue = NoHttp.newRequestQueue();
        initView();
    }

    private void initView() {
        //允许JavaScript执行
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("file:///android_asset/index.html");
        appInterface = new WebAppInterface(LoginBaiduActivity.this);
        mWebView.addJavascriptInterface(appInterface, "app");
        SharedPreferences sharedPreferences = LoginBaiduActivity.this.getSharedPreferences("baiducookie", Context.MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        initImg();
    }

    private void initImg() {

        //获取token
        SharedPreferences sharedPreferences = LoginBaiduActivity.this.getSharedPreferences("baiducookie", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Request<String> request_toke = NoHttp.createStringRequest(API.url_baidu_toke, RequestMethod.GET);
        requestQueue.add(0, request_toke, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                super.onSucceed(what, response);
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    JSONObject data = jsonObject.optJSONObject("data");
                    String token = data.optString("token");
                    editor.putString("cookie",token);
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        //获取验证码
        Request<String> request_img = NoHttp.createStringRequest(API.url_baidu_img, RequestMethod.POST);
        requestQueue.add(1, request_img, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                super.onSucceed(what, response);
                JSONObject object = null;
                try {
                    object = new JSONObject(response.get());
                    JSONObject data = object.getJSONObject("data");
                    token = data.get("token").toString().trim();
                    Intent intent = getIntent();
                    Bundle bundle = intent.getBundleExtra("title");
                    String  title = bundle.getString("title","");
                    appInterface.initTitle("https://wmpass.baidu.com/wmpass/openservice/imgcaptcha?token=" + token,title);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class WebAppInterface {


        private Context context;

        public WebAppInterface(Context ct) {

            this.context = ct;
        }

        @JavascriptInterface
        public void getValues(String n, String p, String i) {
            name = n;
            pwd = p;
            imgtext = i;
            successLogin();
        }
        @JavascriptInterface
        public void getImg(String n){
           initImg();
        }

        public void initTitle(final String img,final String title) {


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWebView.loadUrl("javascript:initTitle('" + img + "','" + title + "')");
                }
            });

        }

    }
    //登录， 获取最后的cookie
    private void successLogin() {
        Request<String> request_login = NoHttp.createStringRequest(API.url_baidu_login, RequestMethod.POST);
        request_login.add("redirect_url","https://wmcrm.baidu.com/");
        request_login.add("return_url","https://wmcrm.baidu.com/crm/setwmstoken");
        request_login.add("type",1);
        request_login.add("channel","pc");
        request_login.add("account",name);
        request_login.add("upass",pwd);
        request_login.add("captcha",imgtext);
        request_login.add("token",token);
        requestQueue.add(2, request_login, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                super.onSucceed(what, response);
                JSONObject object = null;
                try {
                    object = new JSONObject(response.get());
                    int errno = object.optInt("errno");
                    if (errno == 0) {
                        Intent intent = new Intent();
                        intent.putExtra("isSuccsess", true);
                        intent.putExtra("name", name);
                        intent.putExtra("pwd", pwd);
                        LoginBaiduActivity.this.setResult(11, intent);
                        JSONObject data = object.getJSONObject("data");
                        wmstoken = data.getString("WMSTOKEN");
                        API.setREURL(data.getString("return_url"));


                        //获取所用的cookie
                        List<HttpCookie> httpCookies = response.getHeaders().getCookies();
                        cookie = httpCookies.get(0).getName()+ "="+ httpCookies.get(0).getValue()+";";
                        SharedPreferences sharedPreferences = LoginBaiduActivity.this.getSharedPreferences("baiducookie", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("cookie",cookie+ "WMSTOKEN="+wmstoken);
                        editor.commit();
                        // reCookie();
                        finish();
                    } else {
                        Toast.makeText(LoginBaiduActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }
    private void reCookie() {
        HttpMethods_third.getInstance(LoginBaiduActivity.this).reCookie(API.getREURL(),new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                // TODO: 2016/11/21
                //刷新，防止后期数据访问不到
                Log.i("TAGORDER1", s + "onNext: ");
            }
        });
    }
}
