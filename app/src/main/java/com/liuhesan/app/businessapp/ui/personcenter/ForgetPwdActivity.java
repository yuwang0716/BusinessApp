package com.liuhesan.app.businessapp.ui.personcenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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

public class ForgetPwdActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "ForgetPwdActivity";
    private EditText et_phone,et_code;
    private Button next;
    private String code;
    private Button  send_code;
    private CountDownTimer countDownTimer;
    private String token;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        AppManager.getAppManager().addActivity(ForgetPwdActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        initView();
    }
    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.forget_pwd_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.mine_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_phone = (EditText) findViewById(R.id.forget_pwd_phone);
        et_code = (EditText) findViewById(R.id.forget_pwd_code);
        send_code = (Button) findViewById(R.id.send_code);
        next = (Button) findViewById(R.id.forget_pwd_next);
        next.setOnClickListener(this);
        countDownTimer = new CountDownTimer(60*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                send_code.setText((millisUntilFinished/1000)+"秒");
            }

            @Override
            public void onFinish() {
                send_code.setText("重新发送");
                send_code.setClickable(true);
                countDownTimer.cancel();
            }
        };
        send_code.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.send_code:
                if (!TextUtils.isEmpty(et_phone.getText())){
                    if (send_code.isClickable()){
                        sendSMS();
                        send_code.setClickable(false);
                        countDownTimer.start();
                    }

                }else {
                    Toast.makeText(ForgetPwdActivity.this,"手机号不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.forget_pwd_next:
                if (!TextUtils.isEmpty(et_phone.getText()) && !TextUtils.isEmpty(et_code.getText())){
                    Log.i(TAG, code+"onClick: ");
                    if (et_code.getText().toString().trim().equals(code)){
                        Intent intent = new Intent(ForgetPwdActivity.this, NewPwdActivity.class);
                        intent.putExtra("phone",et_phone.getText().toString().trim());
                        startActivity(intent);
                    }else {
                        Toast.makeText(ForgetPwdActivity.this,"验证码不正确",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ForgetPwdActivity.this,"手机号或验证码不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    private void sendSMS() {
        RequestQueue requestQueue = NoHttp.newRequestQueue();
        Request<String> request = NoHttp.createStringRequest(API.url_system_SMS, RequestMethod.POST);
        request.add("token",token);
        request.add("mobile",et_phone.getText().toString().trim());
        request.add("do","auth");
        requestQueue.add(0, request, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                super.onSucceed(what, response);
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    int errno = jsonObject.getInt("errno");
                    if (errno == 200){
                        JSONObject data = jsonObject.optJSONObject("data");
                        code = data.optString("authcode");
                        Log.i(TAG, code+"onClick: ");
                    }
                    if (errno == 303){
                        send_code.setText("获取验证码");
                        send_code.setClickable(true);
                        countDownTimer.cancel();
                        Toast.makeText(ForgetPwdActivity.this,jsonObject.optString("errmsg"),Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

