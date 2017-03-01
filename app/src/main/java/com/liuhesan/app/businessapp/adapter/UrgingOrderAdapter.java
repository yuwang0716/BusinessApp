package com.liuhesan.app.businessapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.bean.UrgeDetails;
import com.liuhesan.app.businessapp.bean.User;
import com.liuhesan.app.businessapp.utility.API;
import com.liuhesan.app.businessapp.widget.LinearLayoutForButton;
import com.liuhesan.app.businessapp.widget.ListViewForScrollView;
import com.liuhesan.app.businessapp.widget.RelativeLayoutForButton;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.SimpleResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private List<User> urgeOrder_data;
    private Context mContext;
    private boolean tag = false;
    private ViewHolder mViewHolder;
    private String name;
    private String wmPoiId;
    private String acctId;
    private String token;
    HashMap<Integer, View> map = new HashMap<Integer, View>();
    private RequestQueue requestQueue;
    private List<UrgeDetails> urgeDetailses;
    public UrgingOrderAdapter(Context mContext, List<User> urgeOrder_data, String name) {
        this.mContext = mContext;
        this.urgeOrder_data = urgeOrder_data;
        this.name = name;
        requestQueue = NoHttp.newRequestQueue();
    }

    @Override
    public int getCount() {
        return urgeOrder_data.size();
    }

    @Override
    public Object getItem(int position) {
        return urgeOrder_data.get(position);
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
        mViewHolder.ordernumbertwo.setText("#"+urgeOrder_data.get(position).getOrder_id());
        mViewHolder.username.setText(urgeOrder_data.get(position).getUser_real_name());
        mViewHolder.usersex.setText(urgeOrder_data.get(position).getSex());
        mViewHolder.useraddress.setText(urgeOrder_data.get(position).getUser_address());
        mViewHolder.shop_price.setText("总共" + urgeOrder_data.get(position).getShop_price() + "元");
        mViewHolder.order_nums.setVisibility(View.GONE);
        if (name.equals("baidu")) {
            String date = getDate(position, "yyyy/M/d HH:mm");
            int lastIndexOf = date.lastIndexOf(" ");
            String substring = date.substring(lastIndexOf);
            mViewHolder.ordertime.setText(substring + "下单");
            mViewHolder.orderdata.setText(date);
            mViewHolder.logo.setBackground(mContext.getResources().getDrawable(R.mipmap.wm_baidu));
        } else if (name.equals("meit")){
            mViewHolder.ordertime.setText(urgeOrder_data.get(position).getCreate_time().substring(urgeOrder_data.get(position).getCreate_time().indexOf(" ")+1)+ "下单");
            mViewHolder.orderdata.setText(urgeOrder_data.get(position).getCreate_time());
            mViewHolder.logo.setBackground(mContext.getResources().getDrawable(R.mipmap.wm_meituan));
        }else {
            mViewHolder.ordertime.setText(urgeOrder_data.get(position).getCreate_time().substring(urgeOrder_data.get(position).getCreate_time().indexOf(" ")+1)+ "下单");
            mViewHolder.orderdata.setText(urgeOrder_data.get(position).getCreate_time());
            mViewHolder.logo.setBackground(mContext.getResources().getDrawable(R.mipmap.wm_elem));
        }
        mViewHolder.send_time.setText(urgeOrder_data.get(position).getSend_time());

        mViewHolder.ordernumber_two.setText(urgeOrder_data.get(position).getOrder_wm_id());
        mViewHolder.userphone.setText(urgeOrder_data.get(position).getUser_phone());
        List<Map<String, String>> goods_list = urgeOrder_data.get(position).getGoods_list();
        GoodsAdapter mGoodsAdapter = new GoodsAdapter(mContext, goods_list);
        mViewHolder.order_content.setAdapter(mGoodsAdapter);

        mViewHolder.order_meal_fee.setText(urgeOrder_data.get(position).getOrder_meal_fee_price());
        mViewHolder.shipping_fee.setText(urgeOrder_data.get(position).getTakeout_cost_price());
        mViewHolder.shop_other_discount.setText(urgeOrder_data.get(position).getShop_other_discount_price());
        mViewHolder.totalPrice.setText(urgeOrder_data.get(position).getShop_price());
        mViewHolder.caution.setText(urgeOrder_data.get(position).getCaution());

        urgeDetailses = urgeOrder_data.get(position).getRemind_list();
        if (urgeDetailses != null){
            UrgsAdapter urgsAdapter = new UrgsAdapter(mContext, urgeDetailses,"baidu");
            mViewHolder.urginglist.setAdapter(urgsAdapter);
        }
        if (name== "meit"){
            getData(position,mViewHolder.urginglist);

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
                intent.setData(Uri.parse("tel://" + urgeOrder_data.get(position).getUser_phone()));
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

    private void getData(int position, ListViewForScrollView urginglist) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("meituancookie", Context.MODE_PRIVATE);
        String cookie = sharedPreferences.getString("cookie", "");
        Request<String> request = NoHttp.createStringRequest(API.url_meituan_reminder_times, RequestMethod.GET);
        request.addHeader("Cookie",cookie);
        request.add("orderInfos","[{wmOrderId:"+urgeOrder_data.get(position).getOrder_self_id()+
                ",wmPoiId:"+urgeOrder_data.get(position).getWmPoiId()+"}]");
        requestQueue.add(0, request, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                super.onSucceed(what, response);
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    JSONObject data = jsonObject.optJSONObject("data");
                    JSONArray jsonArray = data.optJSONArray(urgeOrder_data.get(position).getOrder_wm_id());
                    urgeDetailses = new ArrayList<UrgeDetails>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject reason = jsonArray.optJSONObject(i);
                        UrgeDetails urgeDetails = new UrgeDetails();
                        urgeDetails.setReminder_time(reason.optString("reminder_time_fmt"));
                        urgeDetails.setResponse_content(reason.optString("response_content"));
                        urgeDetails.setResponse_time(reason.optString("response_time"));
                        urgeDetailses.add(urgeDetails);
                    }
                    UrgsAdapter urgsAdapter = new UrgsAdapter(mContext, urgeDetailses,"meit");
                    urginglist.setAdapter(urgsAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private String getDate(int position, String formatType) {
        long i = Integer.parseInt(urgeOrder_data.get(position).getCreate_time());
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
        @BindView(R.id.logo)
        ImageView logo;
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
