package com.liuhesan.app.businessapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.adapter.DistributionAdapter;
import com.liuhesan.app.businessapp.bean.User;
import com.liuhesan.app.businessapp.utility.HistoryOrderData;
import com.liuhesan.app.businessapp.utility.QueryHistoryorder;
import com.lzy.okgo.callback.StringCallback;

import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Tao on 2016/11/11.
 */

public class WaitDistributionFragment extends Fragment {
    private final static String TAG = "WaitDistribution";
    private View view;
    private Context mContext;
    private MaterialRefreshLayout refreshLayout;
    private ListView listView;
    private List<User> users ,users_new;
    private DistributionAdapter distributionAdapter;
    private int page = 1;
    private TextView data_total;
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;
    private DateReceive dateReceive;
    public int currYear,currMonth,currDay;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_waitdistribution, container, false);
        //日期接收注册
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.liuhesan.app.date");
        dateReceive = new DateReceive();
        localBroadcastManager.registerReceiver(dateReceive, intentFilter);
        initData(page);
        initView();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        localBroadcastManager.unregisterReceiver(dateReceive);
    }

    private void initData(int page) {
        Calendar calendar = Calendar.getInstance();
        currYear = calendar.get(Calendar.YEAR);
        currMonth = calendar.get(Calendar.MONTH)+1;
        if (currMonth >12){
            currMonth = 1;
        }
        currDay = calendar.get(Calendar.DATE);
        String date = currYear+"-"+currMonth+"-"+currDay;
        getData(page,date);
    }

    private void getData(int page,String date) {

        QueryHistoryorder.getInstance(getContext()).queryData(page, "5", "8",date, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.e(TAG, s+"onSuccess: " );
                    if (distributionAdapter == null) {
                        users = HistoryOrderData.getNewOrderData(s);
                        if (users != null) {
                            distributionAdapter = new DistributionAdapter(getContext(), users);
                            listView.setAdapter(distributionAdapter);
                        }
                    } else {
                        users_new = HistoryOrderData.getNewOrderData(s);
                        if (users_new != null) {
                            distributionAdapter = new DistributionAdapter(getContext(), users);
                            listView.setAdapter(distributionAdapter);
                        }

                    }
            }
        });
    }

    private void initView() {
        refreshLayout = (MaterialRefreshLayout) view.findViewById(R.id.refresh);
        listView = (ListView) view.findViewById(R.id.order_waitdistribution_listview);
        data_total = (TextView) view.findViewById(R.id.data_total);
        refreshLayout.setLoadMore(true);
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                page = 1;
                initData(page);
                refreshLayout.finishRefresh();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                initData(page++);
                refreshLayout.finishRefreshLoadMore();
            }
        });


    }
    class  DateReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, intent.getStringExtra("date")+"onReceive: ");
            getData(page,intent.getStringExtra("date"));
        }
    }
}
