package com.liuhesan.app.businessapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.adapter.TransactionAdapter;
import com.liuhesan.app.businessapp.bean.TransactionData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tao on 2016/11/12.
 */

public class TransactionFragment extends Fragment {

    private View view;
    private ListView mListView;
    private List<TransactionData> mDatas;
    private TransactionAdapter mAdapter;
    private TransactionData data;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_running_financedata_transaction, container, false);
        initData();
        initView();
        return view;
    }

    private void initData() {
        mDatas = new ArrayList<>();
        data = new TransactionData("11月2日","共31单","2015",true);
        mDatas.add(data);
        data = new TransactionData("11月1日","共31单","2015",false);
        mDatas.add(data);
        data = new TransactionData("10月31日","共31单","2015",true);
        mDatas.add(data);
        data = new TransactionData("10月30日","共31单","2015",true);
        mDatas.add(data);
        data = new TransactionData("10月29日","共31单","2015",true);
        mDatas.add(data);
        data = new TransactionData("10月28日","共31单","2015",true);
        mDatas.add(data);

    }

    private void initView() {
        mListView = (ListView) view.findViewById(R.id.transaction_listview);
        mAdapter = new TransactionAdapter(getContext(),mDatas);
        mListView.setAdapter(mAdapter);

    }
}
