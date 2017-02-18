package com.liuhesan.app.myapplication.ui.personcenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.liuhesan.app.myapplication.R;
import com.liuhesan.app.myapplication.http.HttpMethods;
import com.liuhesan.app.myapplication.http.HttpMethods_third;
import com.liuhesan.app.myapplication.utility.API;
import com.liuhesan.app.myapplication.utility.AppManager;
import com.liuhesan.app.myapplication.utility.HookViewClickUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;
import rx.Subscriber;

/**
 * Created by Tao on 2016/11/28.
 */

public class LoginActivity extends AppCompatActivity {
    private final static String TAG = "LoginActivity";
    @BindView(R.id.login_username)
    EditText loginUsername;
    @BindView(R.id.login_userpwd)
    EditText loginUserpwd;
    @BindView(R.id.login_bt)
    Button loginBt;
    private String name;
    private String pwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppManager.getAppManager().addActivity(LoginActivity.this);
        ButterKnife.bind(this);
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                HookViewClickUtil.hookView(loginBt);
            }
        });
    }

    @OnClick(R.id.login_bt)
    public void onClick() {
        if (TextUtils.isEmpty(loginUsername.getText())&&TextUtils.isEmpty(loginUserpwd.getText())) {
            Toast.makeText(LoginActivity.this,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
        }else {
            name = loginUsername.getText().toString().trim();
            pwd = loginUserpwd.getText().toString().trim();
        }
        OkGo.post(API.url_login+"user/login")
                .tag(this)
                .params("username",name)
                .params("password",pwd)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e(TAG, s+"onSuccess: " );
                        try {
                            JSONObject object = new JSONObject(s);
                            int errno = object.optInt("errno");
                            if (errno == 200){
                                Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
                                JSONObject data = object.optJSONObject("data");
                                String token = data.optString("token");
                                String cdate = data.optString("cdate");
                                SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences("login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("loginjson", s);
                                editor.putString("token",token);
                                editor.putString("date",cdate);
                                editor.commit();
                                //获取第三方Cookie
                                getCookie(token);
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            if (errno == 201){
                                Toast.makeText(LoginActivity.this,"密码不正确",Toast.LENGTH_SHORT).show();
                            }
                            if (errno == 300){
                                Toast.makeText(LoginActivity.this,"用户不存在",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getCookie(String token) {
        getLogin("baidu",token);
        getLogin("meit",token);
        getLogin("elem",token);
    }
    private void getLogin(String name,String token) {
       /* HttpMethods.getInstance(LoginActivity.this).thirdLoginSuccess(API.url_login, name, token, new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    int errno = jsonObject.optInt("errno");
                    if (errno == 200){
                        JSONObject data = jsonObject.optJSONObject("data");
                        JSONObject info = data.optJSONObject("info");
                        String cookies = info.optString("cookies");
                        SharedPreferences sharedPreferences = getSharedPreferences(name+"cookie", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("cookie",cookies);
                        editor.commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });*/
        OkGo.post(API.url_login+"user/login_wm")
                .tag(this)
                .params("platform",name)
                .params("token",token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int errno = jsonObject.optInt("errno");
                            if (errno == 200){
                                JSONObject data = jsonObject.optJSONObject("data");
                                JSONObject info = data.optJSONObject("info");
                                String cookies = info.optString("cookies");
                                SharedPreferences sharedPreferences = getSharedPreferences(name+"cookie", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("cookie",cookies);
                                editor.commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
