package com.liuhesan.app.myapplication.fragment;


import android.content.Context;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.liuhesan.app.myapplication.R;
import com.liuhesan.app.myapplication.adapter.NewOrderAdapter;
import com.liuhesan.app.myapplication.adapter.UrgingOrderAdapter;
import com.liuhesan.app.myapplication.bean.User;
import com.liuhesan.app.myapplication.ui.personcenter.LoginThirdActivity;
import com.liuhesan.app.myapplication.utility.API;
import com.liuhesan.app.myapplication.utility.L;
import com.liuhesan.app.myapplication.utility.NewOrderData_baidu;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.cookie.store.CookieStore;
import com.squareup.okhttp.Interceptor;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.SimpleResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;


/**
 * Created by Tao on 2016/11/11.
 */

public class UrgeFragment extends Fragment {
    private final static String TAG = "UrgeFragment";
    private View view;
    private Context mContext;
    private RequestQueue requestQueue;
    private UrgingOrderAdapter mUrgingOrderAdapter;
    private MaterialRefreshLayout refreshLayout;
    private ListView mListView;
    private List<User> urgsOrder_baidu;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    if (urgsOrder_baidu == null){
                        urgsOrder_baidu.clear();
                    }
                    initBaiduData("baidu");
                    initMeitData("meituan");
                    initElemData("eleme");
                    break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_first_remind_urge, container, false);
        mListView = (ListView) view.findViewById(R.id.urg_listview);
        refreshLayout = (MaterialRefreshLayout) view.findViewById(R.id.refresh);
        requestQueue = NoHttp.newRequestQueue();
        urgsOrder_baidu = new ArrayList<>();
        initBaiduData("baidu");
        initMeitData("meituan");
        initElemData("eleme");
        getRepeatData();
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                if (urgsOrder_baidu == null){
                    urgsOrder_baidu.clear();
                }
                initBaiduData("baidu");
                initMeitData("meituan");
                initElemData("eleme");
                materialRefreshLayout.finishRefresh();
            }
        });
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void getRepeatData() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
            }
        },1000,10*1000);
    }

    //百度催单
    private void initBaiduData(String wmName) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(wmName+"cookie", Context.MODE_PRIVATE);
        Request<String> request_notification = NoHttp.createStringRequest(API.url_baidu_neworder_notification, RequestMethod.GET);
        request_notification.addHeader("Cookie",sharedPreferences.getString("cookie",""));
        requestQueue.add(10, request_notification, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                super.onSucceed(what, response);
                try {
                    JSONObject jsonObject  = new JSONObject(response.get());
                    JSONObject data = jsonObject.optJSONObject("data");
                    if (data != null) {
                        int remind_count = data.optInt("remind_count");
                        if (remind_count > 0) {
                            //获取催单详情
                            Request<String> request_urgingorder_details = NoHttp.createStringRequest(API.url_baidu_urgingOrder_details, RequestMethod.GET);
                            request_urgingorder_details.addHeader("Cookie", sharedPreferences.getString("cookie", ""));
                            requestQueue.add(11, request_urgingorder_details, new SimpleResponseListener<String>() {
                                @Override
                                public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                                    super.onSucceed(what, response);
                                    Log.e(TAG, "百度催单详情：\n" + response.get() + "onSucceed: ");

                                    urgsOrder_baidu = NewOrderData_baidu.getNewOrderData(response.get());
                                    if (mUrgingOrderAdapter == null) {
                                        mUrgingOrderAdapter = new UrgingOrderAdapter(getContext(), urgsOrder_baidu, "baidu");
                                        mListView.setAdapter(mUrgingOrderAdapter);
                                    } else {
                                        mUrgingOrderAdapter.notifyDataSetChanged();
                                    }

                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    private void initMeitData(String wmName) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(wmName+"cookie", Context.MODE_PRIVATE);
        String cookie = sharedPreferences.getString("cookie", "");
        Request<String> request = NoHttp.createStringRequest(API.url_meituan_reminder, RequestMethod.GET);
            request.addHeader("Cookie",cookie);
        requestQueue.add(20, request, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                super.onSucceed(what, response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.get());
                    int data = jsonObject.optInt("data");
                    if (data > 0){
                        Request<String> request_details = NoHttp.createStringRequest(API.url_meituan_reminder_details, RequestMethod.GET);
                        request_details.addHeader("Cookie",cookie);
                        requestQueue.add(21, request_details, new SimpleResponseListener<String>() {
                            @Override
                            public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                                super.onSucceed(what, response);
                                Log.e(TAG, "美团催单详情：\n"+response.get());
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void initElemData(String wmName) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(wmName+"cookie", Context.MODE_PRIVATE);
        String uuid = sharedPreferences.getString("uuid", "");
        String ksid = sharedPreferences.getString("ksid", "");
        String shopId = sharedPreferences.getString("shopId", "");
        String json ="{\"id\":\"" + uuid + "\",\"method\":\"pollingForLowFrequency\",\"service\":" +
                "\"PollingService\",\"params\":{\"shopId\":\"" + shopId + "\"},\"metas\":{\"appName\":\"melody\"," +
                "\"appVersion\":\"4.4.1\",\"ksid\":\"" + ksid + "\"},\"ncp\":\"2.0.0\"}";

        Request<String> request_notification = NoHttp.createStringRequest(API.url_eleme_reminder, RequestMethod.POST);
        request_notification.setDefineRequestBodyForJson(json);
        requestQueue.add(30, request_notification, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                super.onSucceed(what, response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.get());
                    JSONObject data = jsonObject.getJSONObject("result");
                    int urgeOrderCount = data.optInt("urgeOrderCount");
                    if ( urgeOrderCount > 0){
                        String json ="{\"id\":\"" + uuid + "\",\"method\":\"queryOrder\",\"service\":" +
                                "\"OrderService\",\"params\":{\"shopId\":\"" + shopId + "\",\"orderFilter\":\"QUERY_ALL_REMIND_ORDERS\",\"condition\":{" +
                                "                    \"remindOrderTypes\":[\"EXCEPTION_ORDER\",\"REFUND_ORDER\",\"CANCEL_ORDER\",\"REMIND_ORDER\",\"BOOKING_ORDER\"]" +
                                "                }},\"metas\":{\"appName\":\"melody\"," +
                                "\"appVersion\":\"4.4.2\",\"ksid\":\"" + ksid + "\"},\"ncp\":\"2.0.0\"}";
                        Request<String> request_details = NoHttp.createStringRequest(API.url_eleme_reminder_details, RequestMethod.POST);
                        request_notification.setDefineRequestBodyForJson(json);
                        requestQueue.add(31, request_details, new SimpleResponseListener<String>() {
                            @Override
                            public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                                super.onSucceed(what, response);
                                L.e("饿了么催单详情：\n"+response.get());
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}