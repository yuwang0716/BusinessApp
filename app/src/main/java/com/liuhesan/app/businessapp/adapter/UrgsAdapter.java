package com.liuhesan.app.businessapp.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.liuhesan.app.businessapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tao on 2016/11/14.
 */

public class UrgsAdapter extends BaseAdapter {
    private List<Long> urgs_list;
    private Context mContext;

    public UrgsAdapter(Context mContext, List<Long> urgs_list) {
        this.mContext = mContext;
        this.urgs_list = urgs_list;
    }

    @Override
    public int getCount() {
        return urgs_list.size();
    }

    @Override
    public Object getItem(int position) {
        return urgs_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.urgingtimes_item, null);
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        int times = position+1;
        mViewHolder.urgingtimes.setText("\t第"+times+"次催单");
        long i = urgs_list.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date dt = new Date(i * 1000);
        String mDateTime = sdf.format(dt);
        mViewHolder.urgingtime.setText("\t时间\t"+mDateTime);
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.urgingtimes)
        TextView urgingtimes;
        @BindView(R.id.urgingtime)
        TextView urgingtime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
