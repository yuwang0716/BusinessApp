package com.liuhesan.app.businessapp.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

/**
 * Created by Tao on 2016/12/15.
 */

public class QueryHistoryorder {
    private static Context mContext;
    //在访问QueryHistoryorder时创建单例
    private static class SingletonHolder{
        private static final QueryHistoryorder  INSTANCE = new QueryHistoryorder ();
    }

    //获取单例
    public static QueryHistoryorder  getInstance(Context context){
        mContext = context;
        return SingletonHolder.INSTANCE;
    }
     public void queryData(int page, String pageSize, String order_status, String date,StringCallback stringCallback){
         SharedPreferences sharedPreferences = mContext.getSharedPreferences("login", Context.MODE_PRIVATE);
         String token = sharedPreferences.getString("token","");
         OkGo.post(API.url_login+"order/getOrder")
                 .tag(this)
                 .params("token",token)
                 .params("page",page)
                 .params("pagesize",pageSize)
                 .params("status",order_status)
                 .params("sdate",date)
                 .params("edate",date)
                 .execute(stringCallback);
     }
}
