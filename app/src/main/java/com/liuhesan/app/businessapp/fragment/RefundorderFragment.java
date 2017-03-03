package com.liuhesan.app.businessapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.adapter.UrgingOrderAdapter;
import com.liuhesan.app.businessapp.bean.User;
import com.liuhesan.app.businessapp.utility.API;
import com.liuhesan.app.businessapp.utility.L;
import com.liuhesan.app.businessapp.utility.RefundOrderData_meituan;
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

/**
 * Created by Tao on 2016/11/11.
 */

public class RefundorderFragment extends Fragment {
    private final static String TAG = "RefundorderFragment";
    private View view;
    private Context mContext;
    private RequestQueue requestQueue;
    private List<User> refundOrder_baidu,refundOrder_meit;
    private UrgingOrderAdapter mUrgingOrderAdapter;
    private ListView mListView;
    private MaterialRefreshLayout refreshLayout;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
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
        view = inflater.inflate(R.layout.fragment_first_remind_refundorder, container, false);
        requestQueue = NoHttp.newRequestQueue();
        refundOrder_baidu = new ArrayList<>();
        mListView = (ListView) view.findViewById(R.id.refund_listview);
        refreshLayout = (MaterialRefreshLayout) view.findViewById(R.id.refresh);
        initBaiduData("baidu");
        initMeitData("meituan");
        initElemData("eleme");
        getRepeatData();
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                if (refundOrder_baidu == null){
                    refundOrder_baidu.clear();
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
        },1000,30*1000);
    }
    //百度退单
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
                        int apply_cancel_count = data.optInt("apply_cancel_count");
                        Log.e(TAG, "百度退单：\n" + response.get() + "onSucceed: ");
                        //apply_cancel_count
                        if (1 > 0) {
                            //获取退单详情
                            Request<String> request_redund_details = NoHttp.createStringRequest(API.url_baidu_refundOrder_details, RequestMethod.GET);
                            request_redund_details.addHeader("Cookie", sharedPreferences.getString("cookie", ""));
                            requestQueue.add(11, request_redund_details, new SimpleResponseListener<String>() {
                                @Override
                                public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                                    super.onSucceed(what, response);
                                    Log.e(TAG, "百度退单详情：\n" + response.get() + "onSucceed: ");
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
        Request<String> request = NoHttp.createStringRequest(API.url_meituan_refund, RequestMethod.GET);
        request.addHeader("Cookie",cookie);
        requestQueue.add(20, request, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                super.onSucceed(what, response);
                Log.e(TAG, "美团退单：\n"+response.get());
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.get());
                    JSONObject data = jsonObject.optJSONObject("data");
                    int toRefundCount = data.optInt("toRefundCount");
                    if (toRefundCount >0) {
                        //获取退单详情
                        Request<String> request_redund_details = NoHttp.createStringRequest(API.url_meituan_refund_details, RequestMethod.GET);
                        request_redund_details.addHeader("Cookie", sharedPreferences.getString("cookie", ""));
                        requestQueue.add(21, request_redund_details, new SimpleResponseListener<String>() {
                            @Override
                            public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                                super.onSucceed(what, response);
                                Log.e(TAG, "美团退单详情：\n" + response.get() + "onSucceed: ");
                                refundOrder_meit = RefundOrderData_meituan.getRefundOrderData(response.get());
                                if (mUrgingOrderAdapter == null) {
                                    mUrgingOrderAdapter = new UrgingOrderAdapter(getContext(), refundOrder_meit, "meit");
                                    mListView.setAdapter(mUrgingOrderAdapter);
                                } else {
                                    refundOrder_baidu.addAll(refundOrder_meit);
                                    mUrgingOrderAdapter.notifyDataSetChanged();
                                }
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
                Log.e(TAG,"饿了么退单：\n"+response.get()+ "onSucceed: ");
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.get());
                    JSONObject data = jsonObject.getJSONObject("result");
                    int refundOrderCount = data.optInt("refundOrderCount");
                    if ( refundOrderCount > 0){
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
                                L.e("饿了么退单详情：\n"+response.get());
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
