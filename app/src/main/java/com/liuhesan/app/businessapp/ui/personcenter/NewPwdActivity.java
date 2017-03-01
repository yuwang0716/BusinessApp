package com.liuhesan.app.businessapp.ui.personcenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.utility.API;
import com.liuhesan.app.businessapp.utility.AppManager;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.SimpleResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

public class NewPwdActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "NewPwdActivity";
    private EditText pwd_first,pwd_second;
    private Button finish;
    private String password_first,password_second;
    private String token;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pwd);
        AppManager.getAppManager().addActivity(NewPwdActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token","");
        initView();
    }
    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.newpwd_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.mine_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pwd_first = (EditText) findViewById(R.id.newpwd_pwd_first);
        pwd_second = (EditText) findViewById(R.id.newpwd_pwd_second);
        finish = (Button) findViewById(R.id.newpwd_finish);
        finish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newpwd_finish:
                Intent intent = getIntent();
                String phone = intent.getStringExtra("phone");
                if (!TextUtils.isEmpty(pwd_first.getText()) && !TextUtils.isEmpty(pwd_second.getText())) {
                    password_first = pwd_first.getText().toString().trim();
                    password_second = pwd_second.getText().toString().trim();
                    if (password_first.equals(password_second)) {
                        RequestQueue requestQueue = NoHttp.newRequestQueue();
                        Request<String> request = NoHttp.createStringRequest(API.url_system_findPwd, RequestMethod.GET);
                        request.add("token",token);
                        request.add("mobile", phone);
                        request.add("password", password_first);
                        requestQueue.add(0, request, new SimpleResponseListener<String>() {
                            @Override
                            public void onSucceed(int what, Response<String> response) {
                                super.onSucceed(what, response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response.get());
                                    int errno = jsonObject.optInt("errno");
                                    if (errno == 200) {
                                        Toast.makeText(NewPwdActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(NewPwdActivity.this, LoginActivity.class));
                                        AppManager.getAppManager().finishActivity(ForgetPwdActivity.class);
                                        finish();
                                    } else {
                                        Toast.makeText(NewPwdActivity.this, "密码修改失败", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(NewPwdActivity.this, "两次输入不一样", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NewPwdActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

