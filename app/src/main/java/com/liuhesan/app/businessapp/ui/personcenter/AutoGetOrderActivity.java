package com.liuhesan.app.businessapp.ui.personcenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

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

public class AutoGetOrderActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AutoGetOrderActivity";
    private Toolbar mToolbar;
    private RadioButton getOrder,autoOrder;
    private TextView open_getOrder,open_autoOrder;
    private RequestQueue requestQueue;
    private Request<String> request;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_get_order);
        AppManager.getAppManager().addActivity(AutoGetOrderActivity.this);
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        requestQueue = NoHttp.newRequestQueue();
        request = NoHttp.createStringRequest(API.url_system_auto, RequestMethod.POST);
        request.add("token",token);
        initView();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.autogetorder_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.mine_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getOrder = (RadioButton) findViewById(R.id.getorder);
        autoOrder = (RadioButton) findViewById(R.id.autogetorder);
        open_getOrder = (TextView) findViewById(R.id.open_getorder);
        open_autoOrder = (TextView) findViewById(R.id.open_autoorder);
        getOrder.setOnClickListener(this);
        autoOrder.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.getorder:
                getOrder.setChecked(true);
                autoOrder.setChecked(false);
                open_autoOrder.setVisibility(View.GONE);
                open_getOrder.setVisibility(View.VISIBLE);
                request.add("autoconfirm","no");
                requestQueue.add(0, request, new SimpleResponseListener<String>() {
                    @Override
                    public void onSucceed(int what, Response<String> response) {
                        super.onSucceed(what, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response.get());
                            int errno = jsonObject.optInt("errno");
                            if (errno == 200){
                                open_autoOrder.setVisibility(View.GONE);
                                open_getOrder.setVisibility(View.VISIBLE);
                            }else {
                                open_autoOrder.setVisibility(View.VISIBLE);
                                open_getOrder.setVisibility(View.GONE);
                                getOrder.setChecked(false);
                                autoOrder.setChecked(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.autogetorder:
                getOrder.setChecked(false);
                autoOrder.setChecked(true);
                request.add("autoconfirm","yes");
                requestQueue.add(0, request, new SimpleResponseListener<String>() {
                    @Override
                    public void onSucceed(int what, Response<String> response) {
                        super.onSucceed(what, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response.get());
                            int errno = jsonObject.optInt("errno");
                            if (errno == 200){
                                open_autoOrder.setVisibility(View.VISIBLE);
                                open_getOrder.setVisibility(View.GONE);
                            }else {
                                open_autoOrder.setVisibility(View.GONE);
                                open_getOrder.setVisibility(View.VISIBLE);
                                getOrder.setChecked(true);
                                autoOrder.setChecked(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
        }
    }
}
