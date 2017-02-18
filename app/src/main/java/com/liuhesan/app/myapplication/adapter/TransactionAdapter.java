package com.liuhesan.app.myapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.liuhesan.app.myapplication.R;
import com.liuhesan.app.myapplication.bean.TransactionData;

import java.util.List;

/**
 * Created by Tao on 2016/11/14.
 */

public class TransactionAdapter extends BaseAdapter {
    private List<TransactionData> data;
    private Context mContext;

    public TransactionAdapter(Context mContext, List<TransactionData> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null){
            convertView = View.inflate(mContext, R.layout.transaction_item,null);
            mViewHolder = new ViewHolder();
            mViewHolder.date = (TextView) convertView.findViewById(R.id.transaction_date);
            mViewHolder.ordernum = (TextView) convertView.findViewById(R.id.transaction_ordernum);
            mViewHolder.year = (TextView) convertView.findViewById(R.id.transaction_year);
            mViewHolder.state = (Button) convertView.findViewById(R.id.transaction_state);
            convertView.setTag(mViewHolder);
        }else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.date.setText(data.get(position).getDate());
        mViewHolder.ordernum.setText(data.get(position).getOrdernum());
        mViewHolder.year.setText(data.get(position).getYear());
        if (data.get(position).isState()) {
            mViewHolder.state.setBackgroundResource(R.drawable.transationstatebutton);
            mViewHolder.state.setText("已打款");
        }else {
            mViewHolder.state.setBackgroundResource(R.drawable.transationstatebuttontwo);
            mViewHolder.state.setText("未打款");
        }
        return convertView;
    }
    class ViewHolder{
        TextView date,ordernum,year;
        Button state;
    }
}
