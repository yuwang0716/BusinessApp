package com.liuhesan.app.myapplication.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.liuhesan.app.myapplication.R;
import com.liuhesan.app.myapplication.adapter.NewOrderAdapter;
import com.liuhesan.app.myapplication.bean.User;
import com.liuhesan.app.myapplication.http.HttpMethods;
import com.liuhesan.app.myapplication.http.HttpMethods_eleme_data;

import com.liuhesan.app.myapplication.http.HttpMethods_third_data;

import com.liuhesan.app.myapplication.utility.API;
import com.liuhesan.app.myapplication.utility.L;
import com.liuhesan.app.myapplication.utility.NewOrderData_baidu;
import com.liuhesan.app.myapplication.utility.NewOrderData_eleme;
import com.liuhesan.app.myapplication.utility.NewOrderData_meituan;
import com.liuhesan.app.myapplication.utility.StringToList;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.SimpleResponseListener;


import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;

import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Subscriber;

/**
 * Created by Home on 2016/11/9.
 */

public class NeworderFragment extends Fragment {
    private final static String TAG = "NeworderFragment";
    private ListView mListView;
    private MaterialRefreshLayout refreshLayout;
    private View view;
    private List<User> newOrder_data;
    private NewOrderAdapter mNewOrderAdapter;
    private String uuid;
    private String ksid;
    private String shopId;
    private ImageView logo;
    private Context context;
    private RequestQueue requestQueue;
    private String token;
    private List<User> newOrderData_meit;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    getBaiduNewOrderData();
                    getMeituanOrderData();
                    getElemeOrderData();
                    break;
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_first_neworder, container, false);
        requestQueue = NoHttp.newRequestQueue();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        newOrder_data = new ArrayList<>();
        initView();
        initData();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    //初始化新订单
    private void initData() {
        getBaiduNewOrderData();
        getMeituanOrderData();
        getElemeOrderData();
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                if (newOrder_data == null){
                    newOrder_data.clear();
                }
                getBaiduNewOrderData();
                getMeituanOrderData();
                getElemeOrderData();
                materialRefreshLayout.finishRefresh();
            }
        });
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

    //获取百度新订单
    private void getBaiduNewOrderData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("baiducookie", Context.MODE_PRIVATE);
        Request<String> request_notification = NoHttp.createStringRequest(API.url_baidu_neworder_notification, RequestMethod.GET);
        request_notification.addHeader("Cookie",sharedPreferences.getString("cookie",""));
        requestQueue.add(10, request_notification, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                super.onSucceed(what, response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.get());
                    JSONObject data = jsonObject.optJSONObject("data");
                    if (data != null) {
                        int new_order_count = data.optInt("new_order_count");
                        if (new_order_count > 0) {

                            //上报新订单
                            reportedData("baidu", response.get());

                            //获取新订单详情
                            Request<String> request_newOrder_details = NoHttp.createStringRequest(API.url_baidu_neworder_details, RequestMethod.GET);
                            request_notification.addHeader("Cookie", sharedPreferences.getString("cookie", ""));
                            requestQueue.add(12, request_newOrder_details, new SimpleResponseListener<String>() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                @Override
                                public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                                    super.onSucceed(what, response);
                                    Log.e(TAG, "百度新订单详情：\n" + response.get() + "onSucceed: ");
                                    newOrder_data = NewOrderData_baidu.getNewOrderData(response.get());
                                    if (mNewOrderAdapter == null) {
                                        mNewOrderAdapter = new NewOrderAdapter(getContext(), newOrder_data, "baidu");
                                        mListView.setAdapter(mNewOrderAdapter);
                                    } else {
                                        mNewOrderAdapter.notifyDataSetChanged();
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

    //获取美团新订单
    private void getMeituanOrderData() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("meituan" + "cookie", Context.MODE_PRIVATE);
        String cookie = sharedPreferences.getString("cookie", "");
        Request<String> request = NoHttp.createStringRequest(API.url_meituan_notification, RequestMethod.GET);
        request.addHeader("Cookie",cookie );
        requestQueue.add(20, request, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                super.onSucceed(what, response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.get());
                    JSONObject data = jsonObject.optJSONObject("data");
                    int count = 0;
                    if (data != null) {
                        count = data.optInt("count");
                    }
                    //新订单详情
                    if (count > 0) {
                        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd");
                        Date curDate=new Date(System.currentTimeMillis());//获取当前时间     
                        String time = formatter.format(curDate);
                        Request<String> request_details = NoHttp.createStringRequest(API.url_meituan_data+time+"&isQuery=0&getNewVo=1", RequestMethod.GET);
                        request_details.addHeader("Cookie", sharedPreferences.getString("cookie", "") );
                        requestQueue.add(21, request_details, new SimpleResponseListener<String>() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                                super.onSucceed(what, response);

                                //新订单上报服务器
                                reportedData("meit",response.get());

                                //获取新订单详情
                                requestQueue.add(22, request_details, new SimpleResponseListener<String>() {
                                    @Override
                                    public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                                        super.onSucceed(what, response);
                                        newOrderData_meit = NewOrderData_meituan.getNewOrderData(response.get());
                                        Log.e(TAG, "美团新订单详情：\n"+response.get()+"onSucceed: ");
                                        if (mNewOrderAdapter == null) {
                                            mNewOrderAdapter = new NewOrderAdapter(getContext(), newOrderData_meit,"meit");
                                            mListView.setAdapter(mNewOrderAdapter);
                                        }else {
                                            newOrder_data.addAll(newOrderData_meit);
                                            mNewOrderAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });

                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

         //获取饿了么新订单
         private void getElemeOrderData() {
             SharedPreferences sharedPreferences = context.getSharedPreferences("elemecookie", Context.MODE_PRIVATE);
             uuid = sharedPreferences.getString("uuid", "");
             ksid = sharedPreferences.getString("ksid", "");
             shopId = sharedPreferences.getString("shopId", "");
             String json = "{\"id\":\"" + uuid + "\",\"method\":\"pollingForHighFrequency\n\",\"service\":" +
                     "\"PollingService\",\"params\":{\"shopId\":" + shopId + "},\"metas\":{\"appName\":\"melody\"," +
                     "\"appVersion\":\"4.4.0\",\"ksid\":\"" + ksid + "\"},\"ncp\":\"2.0.0\"}";
             Request<String> request_notification = NoHttp.createStringRequest(API.url_eleme_notification, RequestMethod.POST);
             request_notification.setDefineRequestBodyForJson(json);
             requestQueue.add(30, request_notification, new SimpleResponseListener<String>() {
                 @Override
                 public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                     super.onSucceed(what, response);
                     try {
                         JSONObject jsonObject = new JSONObject(response.get());
                         String result = jsonObject.optString("result");
                         if (!"null".equals(result)) {
                             //新订单上报服务器
                             reportedData("elem",response.get());
                             //获取新订单详情
                             String json = "{\"id\":\"" + uuid + "\",\"method\":\"queryOrder\",\"service\":" +
                                     "\"OrderService\",\"params\":{\"shopId\":\"" + shopId + "\",\"orderFilter\":" +
                                     "\"UNPROCESSED_ORDERS\"},\"metas\":{\"appName\":\"melody\",\"appVersion\"" +
                                     ":\"4.4.0\",\"ksid\":\"" + ksid + "\"},\"ncp\":\"2.0.0\"}";
                             Request<String> request_neworder_details = NoHttp.createStringRequest(API.url_eleme_notification, RequestMethod.POST);
                             request_notification.setDefineRequestBodyForJson(json);
                             requestQueue.add(31, request_neworder_details, new SimpleResponseListener<String>() {
                                 @Override
                                 public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                                     super.onSucceed(what, response);
                                     newOrderData_meit = NewOrderData_meituan.getNewOrderData(response.get());
                                     Log.e(TAG, "饿了么新订单详情：\n"+response.get()+"onSucceed: ");
                                     if (mNewOrderAdapter == null) {
                                         mNewOrderAdapter = new NewOrderAdapter(getContext(),  newOrderData_meit,"meit");
                                         mListView.setAdapter(mNewOrderAdapter);
                                     }else {
                                         newOrder_data.addAll( newOrderData_meit);
                                         mNewOrderAdapter.notifyDataSetChanged();
                                     }
                                 }
                             });
                         }
                     } catch (JSONException e) {
                     }
                 }
             });
         }

         private void initView() {
             mListView = (ListView) view.findViewById(R.id.first_listview);
             refreshLayout = (MaterialRefreshLayout) view.findViewById(R.id.refresh);
             logo = (ImageView) view.findViewById(R.id.logo);
         }


    //上报新订单
    private void reportedData(String wmName,String data){
        Request<String> request_noticeNewOrder = NoHttp.createStringRequest(API.url_system_neworder, RequestMethod.POST);
        request_noticeNewOrder.add("platform",wmName);
        request_noticeNewOrder.add("data",data);
        request_noticeNewOrder.add("token",token);
        requestQueue.add(0, request_noticeNewOrder, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                super.onSucceed(what, response);
                Log.e(TAG, wmName+"上报新订单:\n"+response.get());
            }
        });
    }
}

