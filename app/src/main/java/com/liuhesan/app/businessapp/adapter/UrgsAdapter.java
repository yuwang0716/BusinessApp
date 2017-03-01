package com.liuhesan.app.businessapp.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.bean.UrgeDetails;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tao on 2016/11/14.
 */

public class UrgsAdapter extends BaseAdapter {
    private List<UrgeDetails> urgeDetailses;
    private Context mContext;
    private String wmName;
    public UrgsAdapter(Context mContext, List<UrgeDetails> urgeDetailses,String wmName) {
        this.mContext = mContext;
        this.urgeDetailses = urgeDetailses;
        this.wmName = wmName;
    }

    @Override
    public int getCount() {
        return urgeDetailses.size();
    }

    @Override
    public Object getItem(int position) {
        return urgeDetailses.get(position);
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
        String reminder_time = urgeDetailses.get(position).getReminder_time();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        if (wmName == "baidu") {
            long i = Long.parseLong(reminder_time);

            Date dt = new Date(i * 1000);
            String mDateTime = sdf.format(dt);
            mViewHolder.urgingtime.setText("\t"+mDateTime);
        }else if (wmName == "meit"){
            mViewHolder.urgingtime.setText("\t" + reminder_time.substring(reminder_time.indexOf("-")+1));
        }

            if (!TextUtils.isEmpty(urgeDetailses.get(position).getResponse_content()) ){
                Date dt1 = new Date(Long.parseLong(urgeDetailses.get(position).getResponse_time())* 1000);
                String reason = "<strong>商家回复时间: </strong>"  +sdf.format(dt1) +"<br/><strong>商家回复: </strong>"  + urgeDetailses.get(position).getResponse_content();
                mViewHolder.tv_reason.setText(Html.fromHtml(reason));
            }else {
                mViewHolder.tv_reason.setText("未处理");
            }

        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.urgingtimes)
        TextView urgingtimes;
        @BindView(R.id.urgingtime)
        TextView urgingtime;
        @BindView(R.id.reason)
        TextView tv_reason;
        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
