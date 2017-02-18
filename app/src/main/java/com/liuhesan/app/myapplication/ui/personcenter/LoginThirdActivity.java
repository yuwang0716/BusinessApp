package com.liuhesan.app.myapplication.ui.personcenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.liuhesan.app.myapplication.R;
import com.liuhesan.app.myapplication.http.HttpMethods_eleme;
import com.liuhesan.app.myapplication.http.HttpMethods_meituan;
import com.liuhesan.app.myapplication.http.HttpMethods_meituan_login;
import com.liuhesan.app.myapplication.utility.API;
import com.liuhesan.app.myapplication.utility.AppManager;
import com.liuhesan.app.myapplication.utility.L;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.SimpleResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.RequestBody;
import rx.Subscriber;

public class LoginThirdActivity extends AppCompatActivity {
    private static final  String TAG = "LoginThird";
    @BindView(R.id.username)
    EditText et_username;
    @BindView(R.id.pwd)
    EditText et_pwd;
    @BindView(R.id.login)
    Button login;
    String username, pwd;
    @BindView(R.id.title)
    TextView tv_title;
    private String title;
    private RequestQueue requestQueue;
    private List<HttpCookie> httpCookies_meit;
    private String uuid_elem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_third);
        AppManager.getAppManager().addActivity(LoginThirdActivity.this);
        ButterKnife.bind(this);
        requestQueue = NoHttp.newRequestQueue();
        //设置标题
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        tv_title.setText(title);
    }

    @OnClick(R.id.login)
    public void onClick() {
        if (!TextUtils.isEmpty(et_username.getText()) && !TextUtils.isEmpty(et_pwd.getText())) {
            username = et_username.getText().toString().trim();
            pwd = et_pwd.getText().toString().trim();
            successLogin(username,pwd);
        }else {
            Toast.makeText(LoginThirdActivity.this,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
        }
    }

    private void successLogin(String username,String pwd) {
        //美团登录
        CookieStore cookieStore = NoHttp.getCookieManager().getCookieStore();
        if (title.equals("登录美团外卖")) {

            //获取uuid
            try {
                httpCookies_meit = cookieStore.get(new URI(API.url_meituan_uuid));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            //获取登录凭证cookie
            Request<String> request_logon = NoHttp.createStringRequest(API.url_meituan_logon, RequestMethod.POST);
            request_logon.addHeader(httpCookies_meit.get(0));
            request_logon.add("userName",username);
            request_logon.add("password",pwd);
            request_logon.add("imgVerifyValue","");
            request_logon.add("service","");
            requestQueue.add(10, request_logon, new SimpleResponseListener<String>() {
                @Override
                public void onSucceed(int what, Response<String> response) {
                    super.onSucceed(what, response);
                    Log.e(TAG,10+"\n"+ response.getHeaders().getCookies().toString().replace("[","").replace("]","").replace(",",";"));
                    try {
                        JSONObject jsonObject = new JSONObject(response.get());
                        int code = jsonObject.optInt("code");
                        if (code == 1002){
                            Toast.makeText(LoginThirdActivity.this,"用户名或密码不正确，请重新输入",Toast.LENGTH_SHORT).show();
                        }
                        if (code == 1){
                            Toast.makeText(LoginThirdActivity.this,"登录出错,请重新登录",Toast.LENGTH_SHORT).show();
                        }
                        if (code == 0){
                            Toast.makeText(LoginThirdActivity.this,"登录成功",Toast.LENGTH_SHORT).show();

                            SharedPreferences sharedPreferences = getSharedPreferences("meituan"+"cookie", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("cookie", response.getHeaders().getCookies().toString().replace("[","").replace("]","").replace(",",";"));
                            editor.commit();
                            Intent intent = new Intent();
                            intent.putExtra("isSuccsess_meituan",true);
                            LoginThirdActivity.this.setResult(21,intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        //饿了么登录
        if (title.equals("登录饿了么外卖")) {

            //获取uuid
            Request<String> request_uuid = NoHttp.createStringRequest(API.url_uuid_eleme_uuid, RequestMethod.GET);
            requestQueue.add(20, request_uuid, new SimpleResponseListener<String>() {
                @Override
                public void onSucceed(int what, Response<String> response) {
                    super.onSucceed(what, response);

                    //获取登录凭证cookie
                    uuid_elem = response.getHeaders().getValue("X-NWS-LOG-UUID",0);
                    String json = "{\"id\":\""+uuid_elem+"\",\"method\":\"loginByUsername\",\"service\":\"LoginService\"," +
                            "\"params\":{\"username\":\""+username+"\",\"password\":\""+pwd+"\",\"captchaCode\":\"\"," +
                            "\"loginedSessionIds\":[]},\"metas\":{\"appName\":\"melody\",\"appVersion\":\"4.4.0\"},\"ncp\":\"2.0.0\"}";
                    Request<String> request_login = NoHttp.createStringRequest(API.url_eleme_logon, RequestMethod.POST);
                    request_login.setDefineRequestBodyForJson(json);
                    requestQueue.add(21, request_login, new SimpleResponseListener<String>() {
                        @Override
                        public void onSucceed(int what, Response<String> response) {
                            super.onSucceed(what, response);
                            String uuid,ksid,shopId = null;
                            try {
                                JSONObject jsonObject = new JSONObject(response.get());
                                uuid = jsonObject.optString("id");
                                JSONObject result = jsonObject.optJSONObject("result");
                                boolean succeed = result.optBoolean("succeed");
                                if (succeed){
                                    JSONObject successData = result.optJSONObject("successData");
                                    ksid = successData.optString("ksid");
                                    JSONArray shops = successData.getJSONArray("shops");
                                    for (int i = 0; i < shops.length(); i++) {
                                        JSONObject shopObject = shops.optJSONObject(i);
                                        shopId = shopObject.optString("id");
                                    }
                                    SharedPreferences sharedPreferences = LoginThirdActivity.this.getSharedPreferences("elemecookie", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor edit = sharedPreferences.edit();
                                    edit.putString("uuid",uuid);
                                    edit.putString("ksid",ksid);
                                    edit.putString("shopId",shopId);
                                    edit.commit();
                                    Intent intent = new Intent();
                                    intent.putExtra("isSuccsess_elem",true);
                                    LoginThirdActivity.this.setResult(31,intent);
                                    finish();
                                }else {
                                    Toast.makeText(LoginThirdActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        }
    }
}