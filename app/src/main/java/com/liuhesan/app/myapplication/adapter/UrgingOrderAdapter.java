package com.liuhesan.app.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.liuhesan.app.myapplication.R;
import com.liuhesan.app.myapplication.bean.User;
import com.liuhesan.app.myapplication.widget.LinearLayoutForButton;
import com.liuhesan.app.myapplication.widget.ListViewForScrollView;
import com.liuhesan.app.myapplication.widget.RelativeLayoutForButton;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.RequestQueue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tao on 2016/11/14.
 */

public class UrgingOrderAdapter extends BaseAdapter {
    private static final String TAG = "UrgingOrderAdapter";
    private List<User> newOrder_data;
    private Context mContext;
    private boolean tag = false;
    private ViewHolder mViewHolder;
    private String name;
    private String wmPoiId;
    private String acctId;
    private String token;
    HashMap<Integer, View> map = new HashMap<Integer, View>();
    private RequestQueue requestQueue;
    private List<Long> urginglists;

    public UrgingOrderAdapter(Context mContext, List<User> newOrder_data, String name) {
        this.mContext = mContext;
        this.newOrder_data = newOrder_data;
        this.name = name;
        requestQueue = NoHttp.newRequestQueue();
        urginglists = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return newOrder_data.size();
    }

    @Override
    public Object getItem(int position) {
        return newOrder_data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (map.get(position) == null) {
            convertView = View.inflate(mContext, R.layout.urgingorder_item, null);
            mViewHolder = new ViewHolder(convertView);
            convertView.setTag(mViewHolder);
            map.put(position, convertView);
            mViewHolder.useraddress_expand = (LinearLayoutForButton) convertView.findViewById(R.id.useraddress_expand);
            mViewHolder.rideman_expand = (TextView) convertView.findViewById(R.id.rideman_expand);
            mViewHolder.online_pay_expand = (LinearLayoutForButton) convertView.findViewById(R.id.online_pay_expand);
            mViewHolder.orderNumsLinearlayout = (LinearLayoutForButton) convertView.findViewById(R.id.order_nums_linearlayout);
            mViewHolder.contentImagebuttons = (RelativeLayoutForButton) convertView.findViewById(R.id.content_imagebuttons);
            mViewHolder.reason_expand = (LinearLayoutForButton) convertView.findViewById(R.id.reason_expand);
        } else {
            convertView = map.get(position);
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.ordernumbertwo.setText(newOrder_data.get(position).getOrder_id());
        mViewHolder.username.setText(newOrder_data.get(position).getUser_real_name());
        mViewHolder.usersex.setText(newOrder_data.get(position).getSex());
        if (newOrder_data.get(position).getUser_order_num_str().equals("0")){
            mViewHolder.order_nums.setText("首次下单");
        }else if (newOrder_data.get(position).getUser_order_num_str().equals("首次下单")){
            mViewHolder.order_nums.setText("首次下单");
        }else {
            mViewHolder.order_nums.setText(newOrder_data.get(position).getUser_order_num_str()+"次下单");
        }
        mViewHolder.useraddress.setText(newOrder_data.get(position).getUser_address());
        mViewHolder.shop_price.setText("总共" + newOrder_data.get(position).getShop_price() + "元");
        if (name == "baidu") {
            String date = getDate(position, "yyyy/M/d HH:mm");
            int lastIndexOf = date.lastIndexOf(" ");
            String substring = date.substring(lastIndexOf);
            mViewHolder.ordertime.setText(substring + "下单");
            mViewHolder.orderdata.setText(date);
        } else {
            mViewHolder.ordertime.setText(newOrder_data.get(position).getCreate_time());
            mViewHolder.orderdata.setText(newOrder_data.get(position).getCreate_time());
        }
        mViewHolder.send_time.setText(newOrder_data.get(position).getSend_time());

        mViewHolder.ordernumber_two.setText(newOrder_data.get(position).getOrder_id());
        mViewHolder.userphone.setText(newOrder_data.get(position).getUser_phone());
        List<Map<String, String>> goods_list = newOrder_data.get(position).getGoods_list();
        GoodsAdapter mGoodsAdapter = new GoodsAdapter(mContext, goods_list);
        mViewHolder.order_content.setAdapter(mGoodsAdapter);

        mViewHolder.order_meal_fee.setText(newOrder_data.get(position).getOrder_meal_fee_price());
        mViewHolder.shipping_fee.setText(newOrder_data.get(position).getTakeout_cost_price());
        mViewHolder.shop_other_discount.setText(newOrder_data.get(position).getShop_other_discount_price());
        mViewHolder.totalPrice.setText(newOrder_data.get(position).getShop_price());
        mViewHolder.caution.setText(newOrder_data.get(position).getCaution());

        urginglists = newOrder_data.get(position).getRemind_list();
        if (urginglists != null){
            UrgsAdapter urgsAdapter = new UrgsAdapter(mContext, urginglists);
            mViewHolder.urginglist.setAdapter(urgsAdapter);
        }

        //美团操作订单的一些参数
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("meituan", Context.MODE_PRIVATE);
        String cookie = sharedPreferences.getString("cookie", "");
        String[] split = cookie.split(";");
        if (split.length > 10) {
            wmPoiId = split[10].split("=")[1];
            acctId = split[7].split("=")[1];
            token = split[8].split("=")[1];
        }
        //饿了么操作订单的一些参数
        sharedPreferences = mContext.getSharedPreferences("meituan", Context.MODE_PRIVATE);
        String uuid = sharedPreferences.getString("cookie", "");
        String ksid = sharedPreferences.getString("ksid", "");
        String typeCode = "FORCE_REJECT_ORDER";
        // TODO: 2016/12/9
        //后期typeCode这个参数需要做选择


        //订单操作


        //拨电话
        mViewHolder.userphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel://" + newOrder_data.get(position).getUser_phone()));
                mContext.startActivity(intent);
            }
        });

        //设置订单详情的展示或隐藏
        mViewHolder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExpand(mViewHolder);
            }
        });
        mViewHolder.usersex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExpand(mViewHolder);
            }
        });
        mViewHolder.orderNumsLinearlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExpand(mViewHolder);

            }
        });
        mViewHolder.useraddress_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExpand(mViewHolder);

            }
        });
        mViewHolder.rideman_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExpand(mViewHolder);

            }
        });
        mViewHolder.online_pay_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExpand(mViewHolder);

            }
        });
        mViewHolder.expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExpand(mViewHolder);
            }
        });
        mViewHolder.contentImagebuttons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExpand(mViewHolder);
            }
        });
        mViewHolder.reason_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getExpand(mViewHolder);
            }
        });
        return convertView;


    }


    private String getDate(int position, String formatType) {
        long i = Integer.parseInt(newOrder_data.get(position).getCreate_time());
        SimpleDateFormat sdf = new SimpleDateFormat(formatType);
        Date dt = new Date(i * 1000);
        String mDateTime = sdf.format(dt);
        return mDateTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void getExpand(ViewHolder mViewHolder) {
        if (tag) {
            mViewHolder.expand.setVisibility(View.GONE);
            mViewHolder.first_more.setVisibility(View.VISIBLE);
            mViewHolder.first_less.setVisibility(View.GONE);

            tag = false;
        } else {
            mViewHolder.expand.setVisibility(View.VISIBLE);
            mViewHolder.first_more.setVisibility(View.GONE);
            mViewHolder.first_less.setVisibility(View.VISIBLE);

            tag = true;
        }
    }
    static class ViewHolder {
        @BindView(R.id.ordernumbertwo)
        TextView ordernumbertwo;
        @BindView(R.id.username)
        TextView username;
        @BindView(R.id.usersex)
        TextView usersex;
        @BindView(R.id.userphone)
        Button userphone;
        @BindView(R.id.order_nums)
        TextView order_nums;
        @BindView(R.id.order_nums_linearlayout)
        LinearLayoutForButton order_nums_linearlayout;
        @BindView(R.id.first_more)
        ImageButton first_more;
        @BindView(R.id.first_less)
        ImageButton first_less;
        @BindView(R.id.content_imagebuttons)
        RelativeLayoutForButton content_imagebuttons;
        @BindView(R.id.useraddress)
        TextView useraddress;
        @BindView(R.id.shop_price)
        TextView shop_price;
        @BindView(R.id.ordertime)
        TextView ordertime;
        @BindView(R.id.send_time)
        TextView send_time;
        @BindView(R.id.orderdata)
        TextView orderdata;
        @BindView(R.id.ordernumber_two)
        TextView ordernumber_two;
        @BindView(R.id.order_content)
        ListViewForScrollView order_content;
        @BindView(R.id.expand)
        LinearLayoutForButton expand;
        @BindView(R.id.urginglist)
        ListViewForScrollView urginglist;
        @BindView(R.id.order_meal_fee)
        TextView order_meal_fee;
        @BindView(R.id.shipping_fee)
        TextView shipping_fee;
        @BindView(R.id.shop_other_discount)
        TextView shop_other_discount;
        @BindView(R.id.total_price)
        TextView totalPrice;
        @BindView(R.id.caution)
        TextView caution;
        TextView rideman_expand;
        LinearLayoutForButton orderNumsLinearlayout;
        LinearLayoutForButton useraddress_expand, online_pay_expand, reason_expand;
        RelativeLayoutForButton contentImagebuttons;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
