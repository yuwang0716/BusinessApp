package com.liuhesan.app.businessapp.ui.personcenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.utility.API;
import com.liuhesan.app.businessapp.utility.AppManager;
import com.liuhesan.app.businessapp.utility.HookViewClickUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.SimpleResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

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
    @BindView(R.id.remember_pwd)
    CheckBox rememberPwd;
    @BindView(R.id.forget_pwd)
    TextView forgetPwd;
    private String name;
    private String pwd;
    private RequestQueue requestQueue;
    private Request<String> request_wm;
    private Request<String> request_login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppManager.getAppManager().addActivity(LoginActivity.this);
        requestQueue = NoHttp.newRequestQueue();
        ButterKnife.bind(this);
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                HookViewClickUtil.hookView(loginBt);
            }
        });
    }

    @OnClick({R.id.login_bt,R.id.remember_pwd, R.id.forget_pwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_bt:
                if (TextUtils.isEmpty(loginUsername.getText()) && TextUtils.isEmpty(loginUserpwd.getText())) {
                    Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    name = loginUsername.getText().toString().trim();
                    pwd = loginUserpwd.getText().toString().trim();
                }
                request_login = NoHttp.createStringRequest(API.url_login,RequestMethod.POST);
                request_login.add("username", name);
                request_login.add("password", pwd);
                requestQueue.add(0, request_login, new SimpleResponseListener<String>() {
                    @Override
                    public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                        super.onSucceed(what, response);
                        Log.e(TAG, response.get() + "onSuccess: ");
                        try {
                            JSONObject object = new JSONObject(response.get());
                            int errno = object.optInt("errno");
                            if (errno == 200) {
                                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                JSONObject data = object.optJSONObject("data");
                                String token = data.optString("token");
                                String cdate = data.optString("cdate");
                                SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences("login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("loginjson", response.get());
                                editor.putString("token", token);
                                editor.putString("date", cdate);
                                editor.commit();
                                //获取第三方Cookie
                                getCookie(token);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            if (errno == 201) {
                                Toast.makeText(LoginActivity.this, "密码不正确", Toast.LENGTH_SHORT).show();
                            }
                            if (errno == 300) {
                                Toast.makeText(LoginActivity.this, "用户不存在", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                break;
            case R.id.remember_pwd:

                break;
            case R.id.forget_pwd:
                startActivity(new Intent(LoginActivity.this,ForgetPwdActivity.class));
                break;
        }
    }

    private void getCookie(String token) {
        getLogin("baidu", token);
        getLogin("meit", token);
        getLogin("elem", token);
    }

    private void getLogin(String name, String token) {
        request_wm = NoHttp.createStringRequest(API.url_login_wm, RequestMethod.POST);
        request_wm.add("token", token);
        request_wm.add("platform", name);
        requestQueue.add(1, request_wm, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                super.onSucceed(what, response);
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    int errno = jsonObject.optInt("errno");
                    if (errno == 200) {
                        Log.e(TAG, response.get()+"onSuccess:\n "+name );
                        JSONObject data = jsonObject.optJSONObject("data");
                        JSONObject info = data.optJSONObject("info");
                        String cookies = info.optString("cookies");
                        SharedPreferences sharedPreferences = getSharedPreferences(name + "cookie", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("cookie", cookies);
                        editor.commit();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
