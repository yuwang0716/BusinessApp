package com.liuhesan.app.businessapp.ui.personcenter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.liuhesan.app.businessapp.utility.API;
import com.liuhesan.app.businessapp.utility.AppManager;
import com.liuhesan.app.businessapp.utility.SharedPreferencesUtil;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.SimpleResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 457;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 判断是否是第一次开启应用
        boolean isFirstOpen = SharedPreferencesUtil.getBoolean(this, SharedPreferencesUtil.FIRST_OPEN, true);

        //获取权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED &&ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ) {

        } else {
            // 没有赋予权限，那就去申请权限
            getPermission();
        }
        // 如果是第一次启动，则先进入功能引导页isFirstOpen
        if (isFirstOpen) {
            Intent intent = new Intent(this, WelcomeGuideActivity.class);
            startActivity(intent);
            finish();
            return;
        }

            // setContentView(R.layout.activity_welcome);
        AppManager.getAppManager().addActivity(WelcomeActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        if (TextUtils.isEmpty(token)){
            startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
            finish();
        }else {
            RequestQueue requestQueue = NoHttp.newRequestQueue();
            Request<String> request_shopdetails = NoHttp.createStringRequest(API.url_system_shopdetails, RequestMethod.POST);
            request_shopdetails.add("token",token);
            requestQueue.add(0, request_shopdetails, new SimpleResponseListener<String>() {
                @Override
                public void onSucceed(int what, Response<String> response) {
                    super.onSucceed(what, response);
                    Log.e(TAG, response.get()+"onSucceed: ");
                    try {
                        JSONObject jsonObject = new JSONObject(response.get());
                        int errno = jsonObject.optInt("errno");
                        if (errno == 200){
                            JSONObject data = jsonObject.optJSONObject("data");
                            int is_online = data.optInt("is_online");//店铺开关1:营业 0:关店
                            int certificationID = data.optInt("isvalid_user");//身份证认证：0：未审核 1：审核
                            int qualification = data.optInt("isvalid_shop");//门店资质 0：未审核 1：审核
                            String phone = data.optString("phone");//联系电话
                            String shop_name = data.optString("name");//门店名
                            String address = data.optString("address");//门店地址
                            String username = data.optString("username");//用户名
                            String headportrait = "http://crm.weidab.cn/"+data.optString("pic_url");
                            SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = sharedPreferences.edit();
                            edit.putInt("is_online",is_online);
                            edit.putInt("certificationID",certificationID);
                            edit.putInt("qualification",qualification);
                            edit.putString("phone",phone);
                            edit.putString("shop_name",shop_name);
                            edit.putString("address",address);
                            edit.putString("username",username);
                            edit.putString("headportrait",headportrait);
                            edit.commit();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
            finish();
        }
    }
    private void getPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.CALL_PHONE,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_WRITE_EXTERNAL_STORAGE);
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    // 权限请求成功

                } else {
                    // 用户拒绝了
                    showTipDialog();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null)
        dialog.dismiss();
    }

    private void showTipDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                .setMessage("该程序需要该权限，否则无法正常运行")
                .setPositiveButton(android.R.string.ok, null)
                .create();
        dialog = builder.show();
    }
}
