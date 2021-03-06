package com.liuhesan.app.businessapp.fragment;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.adapter.NewOrderAdapter;
import com.liuhesan.app.businessapp.bean.User;
import com.liuhesan.app.businessapp.jsonstring.Jsonneworder_meituan;
import com.liuhesan.app.businessapp.utility.API;
import com.liuhesan.app.businessapp.utility.NewOrderData_baidu;
import com.liuhesan.app.businessapp.utility.NewOrderData_eleme;
import com.liuhesan.app.businessapp.utility.NewOrderData_meituan;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.SimpleResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

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
    private List<User> newOrderData_baidu, newOrderData_meit,newOrderData_eleme;
    private Set<List<User>> lists;
    private IntentFilter intentFilter_notification;
    private boolean isSound, isShake;
    private NotificationReceive  notificationReceive;
    private LocalBroadcastManager localBroadcastManager;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    if (newOrder_data != null){
                        newOrder_data.clear();
                    }
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
        newOrderData_baidu = new ArrayList<>();
        newOrderData_meit = new ArrayList<>();
        newOrderData_eleme = new ArrayList<>();
        lists = new HashSet<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        //通知接收注册
        intentFilter_notification = new IntentFilter();
        intentFilter_notification.addAction("com.liuhesan.app.distributionapp.NOTIFICATIONSETTING");
        notificationReceive = new NotificationReceive();
        localBroadcastManager.registerReceiver(notificationReceive, intentFilter_notification);
        initView();
        initData();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        localBroadcastManager.unregisterReceiver(notificationReceive);
    }

    //初始化新订单
    private void initData() {
        getBaiduNewOrderData();
        getMeituanOrderData();
        getElemeOrderData();
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
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
        },0,30*1000);
    }

    //获取百度新订单
    private void getBaiduNewOrderData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("baiducookie", Context.MODE_PRIVATE);
        Request<String> request_newOrder = NoHttp.createStringRequest(API.url_baidu_neworder_notification, RequestMethod.GET);
        request_newOrder.addHeader("Cookie",sharedPreferences.getString("cookie",""));
        requestQueue.add(10, request_newOrder, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                super.onSucceed(what, response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response.get());
                    JSONObject data = jsonObject.optJSONObject("data");
                    if (data != null) {
                        int new_order_count = data.optInt("new_order_count");
                        if (  new_order_count > 0) {

                            //上报新订单
                            reportedData("baidu", response.get());

                            //获取新订单详情
                            Request<String> request_newOrder_details = NoHttp.createStringRequest(API.url_baidu_neworder_details, RequestMethod.GET);
                            request_newOrder_details.addHeader("Cookie", sharedPreferences.getString("cookie", ""));
                            requestQueue.add(12, request_newOrder_details, new SimpleResponseListener<String>() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                @Override
                                public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                                    super.onSucceed(what, response);
                                    Log.e(TAG, "百度新订单详情：\n" + response.get() + "onSucceed: ");
                                    newOrder_data = NewOrderData_baidu.getNewOrderData(response.get());
                                    if (mNewOrderAdapter == null) {
                                        Log.e(TAG, "百度新订：\n" + newOrder_data + "onSucceed: ");
                                        lists.add(newOrderData_baidu);
                                        newOrder_data.addAll(newOrderData_baidu);
                                        mNewOrderAdapter = new NewOrderAdapter(context, newOrder_data, "baidu");
                                        mListView.setAdapter(mNewOrderAdapter);

                                    } else {
                                        if (!lists.contains(newOrderData_baidu)){
                                            newOrder_data.clear();
                                            newOrder_data.addAll(newOrderData_baidu);
                                            mNewOrderAdapter.notifyDataSetChanged();

                                        }
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

        SharedPreferences sharedPreferences = context.getSharedPreferences("meit" + "cookie", Context.MODE_PRIVATE);
        String cookie = sharedPreferences.getString("cookie", "");
        Request<String> request = NoHttp.createStringRequest(API.url_meituan_notification, RequestMethod.GET);
        request.addHeader("Cookie",cookie );
        requestQueue.add(20, request, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                super.onSucceed(what, response);
                JSONObject jsonObject = null;
                Log.e(TAG, "美团新订单：\n"+response.get()+"onSucceed: ");
                try {
                    jsonObject = new JSONObject(response.get());
                    JSONObject data = jsonObject.optJSONObject("data");
                    int count = 0;
                    if (data != null) {
                        count = data.optInt("count");
                    }
                    //新订单详情
                    if ( count > 0) {
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
                                        Log.e(TAG, "美团新订单详情：\n"+Jsonneworder_meituan.neworder+"onSucceed: ");
                                        if (mNewOrderAdapter == null) {
                                            lists.add(newOrderData_meit);
                                            newOrder_data.addAll(newOrderData_meit);
                                            mNewOrderAdapter = new NewOrderAdapter(context, newOrder_data,"meit");
                                            mListView.setAdapter(mNewOrderAdapter);
                                        }else {
                                           lists.add(newOrderData_meit);
                                            if (newOrderData_baidu == null && !lists.contains(newOrderData_meit)){
                                                newOrder_data.clear();
                                                newOrder_data.addAll(newOrderData_meit);
                                                mNewOrderAdapter.notifyDataSetChanged();
                                            }else if (newOrderData_baidu != null && !lists.contains(newOrderData_meit)){
                                                newOrder_data.clear();
                                                newOrder_data.addAll(newOrderData_baidu);
                                                newOrder_data.addAll(newOrderData_meit);
                                                mNewOrderAdapter.notifyDataSetChanged();
                                            }else if (newOrder_data.size() == newOrderData_baidu.size()){
                                                newOrder_data.addAll(newOrderData_meit);
                                                mNewOrderAdapter.notifyDataSetChanged();

                                            }
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
             SharedPreferences sharedPreferences = context.getSharedPreferences("elemcookie", Context.MODE_PRIVATE);
             uuid = sharedPreferences.getString("uuid", "");
             ksid = sharedPreferences.getString("ksid", "");
             shopId = sharedPreferences.getString("shopId", "");
             String json = "{\"id\":\"" + uuid + "\",\"method\":\"pollingForHighFrequency\",\"service\":" +
                     "\"PollingService\",\"params\":{\"shopId\":" + shopId + ",\"orderIds\":[]},\"metas\":{\"appName\":\"melody\"," +
                     "\"appVersion\":\"4.4.1\",\"ksid\":\"" + ksid + "\"},\"ncp\":\"2.0.0\"}";
             Log.e(TAG, json);
             Request<String> request_notification = NoHttp.createStringRequest(API.url_eleme_notification, RequestMethod.POST);
             request_notification.setDefineRequestBodyForJson(json);
             requestQueue.add(30, request_notification, new SimpleResponseListener<String>() {
                 @Override
                 public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                     super.onSucceed(what, response);
                     try {
                         JSONObject jsonObject = new JSONObject(response.get());
                         JSONObject result = jsonObject.optJSONObject("result");
                         Log.e(TAG, "饿了么新订单：\n"+response.get()+"onSucceed: ");
                         if (result != null){
                             int newOrderCount = result.optInt("newOrderCount");
                             if (newOrderCount > 0) {
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
                                         newOrderData_eleme = NewOrderData_eleme.getNewOrderData(response.get());
                                         Log.e(TAG, "饿了么新订单详情：\n"+response.get()+"onSucceed: ");
                                         if (mNewOrderAdapter == null) {
                                             lists.add(newOrderData_eleme);
                                             newOrder_data.addAll(newOrderData_eleme);
                                             mNewOrderAdapter = new NewOrderAdapter(context, newOrder_data,"elem");
                                             mListView.setAdapter(mNewOrderAdapter);
                                         }else {
                                             newOrder_data.addAll( newOrderData_eleme);
                                             mNewOrderAdapter.notifyDataSetChanged();

                                             lists.add( newOrderData_eleme);
                                             if (newOrderData_baidu == null && newOrderData_meit == null && !lists.contains(newOrderData_meit)){
                                                 newOrder_data.clear();
                                                 newOrder_data.addAll(newOrderData_meit);
                                                 mNewOrderAdapter.notifyDataSetChanged();
                                             }else if (!lists.contains(newOrderData_meit)){
                                                 newOrder_data.clear();
                                                 newOrder_data.addAll(newOrderData_baidu);
                                                 newOrder_data.addAll(newOrderData_meit);
                                                 newOrder_data.addAll(newOrderData_eleme);
                                                 mNewOrderAdapter.notifyDataSetChanged();
                                             }else if (newOrder_data.size() == newOrderData_baidu.size() || (newOrder_data.size() == newOrderData_meit.size())){
                                                 newOrder_data.addAll(newOrderData_meit);
                                                 mNewOrderAdapter.notifyDataSetChanged();
                                             }
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
                Intent intent = new Intent(context,com.liuhesan.app.businessapp.ui.personcenter.MainActivity.class);
                intent.putExtra("neworder","neworder");
                PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
                NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = new NotificationCompat.Builder(context)
                        .setContentTitle("有一笔新订单!!")
                        //  .setContentText(data.get(0).getName())
                        .setWhen(System.currentTimeMillis())
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon_logo))
                        .setContentIntent(pi)
                        .setAutoCancel(true)
                        .setSmallIcon(R.mipmap.icon_logo)
                        .build();
                int i = 0;
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                if (isSound) {
                    notification.sound = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.neworder);
                } else {
                    builder.build().sound = null;
                }
                if (isShake) {
                    notification.vibrate = new long[]{0,1000,1000,1000};
                } else {
                    notification.vibrate = null;
                }
                manager.notify(i++, notification);

            }
        });
    }
    class NotificationReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //通知管理
            isSound = intent.getBooleanExtra("isSound", false);
            isShake = intent.getBooleanExtra("isShake", false);
        }
    }
}

