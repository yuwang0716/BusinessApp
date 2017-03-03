package com.liuhesan.app.businessapp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.bean.User;
import com.liuhesan.app.businessapp.utility.API;
import com.liuhesan.app.businessapp.widget.LinearLayoutForButton;
import com.liuhesan.app.businessapp.widget.ListViewForScrollView;
import com.liuhesan.app.businessapp.widget.RelativeLayoutForButton;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.SimpleResponseListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tao on 2016/11/14.
 */

public class NewOrderAdapter extends BaseAdapter {
    private static final String TAG = "NewOrderAdapter";
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
    private SharedPreferences sharedPreferences_wm;
    private String cookie_baidu;
    private String order_wm_id;
    private String cookie_meit;

    public NewOrderAdapter(Context mContext, List<User> newOrder_data, String name) {
        this.mContext = mContext;
        this.newOrder_data = newOrder_data;
        this.name = name;
        requestQueue = NoHttp.newRequestQueue();
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
            convertView = View.inflate(mContext, R.layout.first_item, null);
            mViewHolder = new ViewHolder(convertView);
            mViewHolder.cancelFirst = (Button) convertView.findViewById(R.id.cancel_first);
            mViewHolder.printFirst = (Button) convertView.findViewById(R.id.print_first);
            convertView.setTag(mViewHolder);
            map.put(position, convertView);
            mViewHolder.useraddress_expand = (LinearLayoutForButton) convertView.findViewById(R.id.useraddress_expand);
            mViewHolder.rideman_expand = (TextView) convertView.findViewById(R.id.rideman_expand);
            mViewHolder.online_pay_expand = (LinearLayoutForButton) convertView.findViewById(R.id.online_pay_expand);
            mViewHolder.orderNumsLinearlayout = (LinearLayoutForButton) convertView.findViewById(R.id.order_nums_linearlayout);
            mViewHolder.contentImagebuttons = (RelativeLayoutForButton) convertView.findViewById(R.id.content_imagebuttons);
            mViewHolder.reason_expand = (LinearLayoutForButton) convertView.findViewById(R.id.reason_expand);
            mViewHolder.order_nums.setVisibility(View.GONE);
        } else {
            convertView = map.get(position);
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.order_id.setText("#" + newOrder_data.get(position).getOrder_id());
        mViewHolder.username.setText(newOrder_data.get(position).getUser_real_name());
        mViewHolder.usersex.setText(newOrder_data.get(position).getSex());
        mViewHolder.useraddress.setText(newOrder_data.get(position).getUser_address());
        if (newOrder_data.get(position).getPay_type() == 2) {
            mViewHolder.onlinePay.setVisibility(View.VISIBLE);
            mViewHolder.sendgoodsPay.setVisibility(View.GONE);
        } else {
            mViewHolder.onlinePay.setVisibility(View.GONE);
            mViewHolder.sendgoodsPay.setVisibility(View.VISIBLE);
        }
        mViewHolder.shop_price.setText("总共" + newOrder_data.get(position).getShop_price() + "元");

        if (name == "baidu") {
            String date = getDate(position, "yyyy/M/d HH:mm");
            int lastIndexOf = date.lastIndexOf(" ");
            String substring = date.substring(lastIndexOf);
            mViewHolder.ordertime.setText(substring + "下单");
            mViewHolder.orderdata.setText(date);
            mViewHolder.logo.setBackground(mContext.getResources().getDrawable(R.mipmap.icon_baidu));
        } else if (name == "meit") {
            String date = newOrder_data.get(position).getCreate_time();
            int lastIndexOf = date.lastIndexOf(" ");
            String substring = date.substring(lastIndexOf);
            mViewHolder.ordertime.setText(substring + "下单");
            mViewHolder.orderdata.setText(newOrder_data.get(position).getCreate_time());
            mViewHolder.logo.setBackground(mContext.getResources().getDrawable(R.mipmap.icon_meituan));
        } else {
            String date = newOrder_data.get(position).getCreate_time();

            int lastIndexOf = date.indexOf(":");
            String substring = date.substring(lastIndexOf-2);
            mViewHolder.ordertime.setText(substring + "下单");
            mViewHolder.orderdata.setText(newOrder_data.get(position).getCreate_time());
            mViewHolder.logo.setBackground(mContext.getResources().getDrawable(R.mipmap.icon_eleme));
        }
        mViewHolder.send_time.setText(newOrder_data.get(position).getSend_time());

        mViewHolder.ordernumber_two.setText(newOrder_data.get(position).getOrder_wm_id());
        mViewHolder.userphone.setText(newOrder_data.get(position).getUser_phone());
        List<Map<String, String>> goods_list = newOrder_data.get(position).getGoods_list();
        GoodsAdapter mGoodsAdapter = new GoodsAdapter(mContext, goods_list);
        mViewHolder.order_content.setAdapter(mGoodsAdapter);


        mViewHolder.order_meal_fee.setText(newOrder_data.get(position).getOrder_meal_fee_price());
        mViewHolder.shipping_fee.setText(newOrder_data.get(position).getTakeout_cost_price());
        mViewHolder.shop_other_discount.setText(newOrder_data.get(position).getShop_other_discount_price());
        mViewHolder.totalPrice.setText(newOrder_data.get(position).getShop_price());
        mViewHolder.caution.setText(newOrder_data.get(position).getCaution());
        //百度操作订单的一些参数
        sharedPreferences_wm = mContext.getSharedPreferences(name + "cookie", Context.MODE_PRIVATE);
        cookie_baidu = sharedPreferences_wm.getString("cookie", "");
        order_wm_id = newOrder_data.get(position).getOrder_wm_id();

        //美团操作订单的一些参数
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("meituancookie", Context.MODE_PRIVATE);
        cookie_meit = sharedPreferences.getString("cookie", "");
        token = sharedPreferences.getString("accessToken", "");
        wmPoiId = sharedPreferences.getString("wmPoiId", "");
        acctId = sharedPreferences.getString("acctId", "");
        //饿了么操作订单的一些参数
        sharedPreferences = mContext.getSharedPreferences("elemecookie", Context.MODE_PRIVATE);
        String uuid = sharedPreferences.getString("uuid", "");
        String ksid = sharedPreferences.getString("ksid", "");


        //订单操作
        mViewHolder.printFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //百度接单
                if (name == "baidu") {
                    Request<String> request_sure = NoHttp.createStringRequest(API.url_baidu_neworder_sure, RequestMethod.POST);
                    request_sure.addHeader("Cookie", cookie_baidu);
                    request_sure.add("order_id", order_wm_id);
                    request_sure.add("pc_ver", "");
                    request_sure.add("from", "pc");
                    requestQueue.add(10, request_sure, new SimpleResponseListener<String>() {
                        @Override
                        public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                            super.onSucceed(what, response);
                            Log.e(TAG, response.get() + "onSucceed: ");
                            //newOrder_data.remove(position);
                        }
                    });
                }
                //美团的接单
                if (name == "meit") {

                    Request<String> request_sure = NoHttp.createStringRequest(API.url_meituan_neworder_sure, RequestMethod.GET);
                    request_sure.addHeader("Cookie", cookie_meit);
                    request_sure.add("token", token);
                    request_sure.add("orderId", order_wm_id);
                    request_sure.add("wmPoiId", wmPoiId);
                    request_sure.add("acctId", acctId);
                    request_sure.add("appType", 3);
                    request_sure.add("isPrint", "0");
                    request_sure.add("isAutoAccept", "0");
                    request_sure.add("csrfToken", "");
                    requestQueue.add(20, request_sure, new SimpleResponseListener<String>() {
                        @Override
                        public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                            super.onSucceed(what, response);
                            Log.e(TAG, response.get() + "onSucceed: ");
                        }
                    });

                }
                //饿了么接单
                if (name == "eleme") {
                    String params = "{\"id\":\"" + uuid + "\",\"method\":\"confirmOrder\",\"service\"" +
                            ":\"order\",\"params\": {\"orderId\":\"" + order_wm_id + "},\"metas\": {\"appName\":\"melody\",\"" +
                            "appVersion\":\"4.4.0\",\"ksid\":\"" + ksid + "\"},\"ncp\":\"2.0.0\"}";

                    Request<String> request_sure = NoHttp.createStringRequest(API.url_eleme_neworder_sure, RequestMethod.POST);
                    request_sure.setDefineRequestBodyForJson(params);
                    requestQueue.add(30, request_sure, new SimpleResponseListener<String>() {
                        @Override
                        public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                            super.onSucceed(what, response);
                            Log.e(TAG, response.get() + "onSucceed: ");
                        }
                    });
                }

            }
        });

        mViewHolder.cancelFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //百度订单取消
                if (name == "baidu") {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("取消原因");
                    final String[] reasons_baidu = {"不在配送范围", "美食已售完", "用户致电取消", "假订单", "重复订单", "联系不上用户", "餐厅太忙"};
                    final int[] reason_status_baidu = {1, 3, 5, 9, 10, 11, 12};
                    builder.setItems(reasons_baidu, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Request<String> request_cancel = NoHttp.createStringRequest(API.url_baidu_neworder_cancel, RequestMethod.POST);
                            request_cancel.addHeader("Cookie", cookie_baidu);
                            request_cancel.add("order_id", order_wm_id);
                            request_cancel.add("cancel_reason", reasons_baidu[which]);
                            request_cancel.add("cancel_reason_status", reason_status_baidu[which]);
                            requestQueue.add(11, request_cancel, new SimpleResponseListener<String>() {
                                @Override
                                public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                                    super.onSucceed(what, response);
                                    Log.e(TAG, response.get() + "onSucceed: ");
                                    // newOrder_data.remove(position);
                                }
                            });
                        }
                    });
                    builder.show();

                }
                //美团的订单取消
                if (name == "meit") {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("取消原因");
                    final String[] reasons_meit = {"不在配送范围", "美食已售完", "用户致电取消", "配送员取餐慢", "配送员送餐慢", "重复订单", "联系不上用户", "餐厅太忙"};
                    final int[] reason_status_meit = {336, 338, 358, 858, 859, 357, 351, 335};
                    builder.setItems(reasons_meit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Request<String> request_cancel = NoHttp.createStringRequest(API.url_meituan_neworder_sure, RequestMethod.GET);
                            request_cancel.addHeader("Cookie", cookie_meit);
                            request_cancel.add("token", token);
                            request_cancel.add("orderId", order_wm_id);
                            request_cancel.add("wmPoiId", wmPoiId);
                            request_cancel.add("acctId", acctId);
                            request_cancel.add("appType", 3);
                            request_cancel.add("reasonId", reason_status_meit[which]);
                            request_cancel.add("remark", "");
                            request_cancel.add("csrfToken", "");
                            request_cancel.add("subRadioId", "");
                            requestQueue.add(20, request_cancel, new SimpleResponseListener<String>() {
                                @Override
                                public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                                    super.onSucceed(what, response);
                                    Log.e(TAG, response.get() + "onSucceed: ");
                                }
                            });
                        }
                    });


                    builder.show();
                }
                //饿了么取消订单
                if (name == "eleme") {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("取消原因");
                    final String[] reasons_elem = {"不在配送范围", "美食已售完", "用户致电取消", "联系不上用户", "餐厅太忙","其它"};
                    final String[] reason_status_elem = {"DISTANCE_TOO_FAR", "FOOD_SOLD_OUT","FORCE_REJECT_ORDER","CONTACT_USER_FAILED", "RESTAURANT_TOO_BUSY", "OTHERS"};
                    builder.setItems(reasons_elem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String params = "{\"id\":\"" + uuid + "\",\"method\":\"invalidateOrder\",\"service\"" +
                                    ":\"OrderService\",\"params\": {\"orderId\":\"" + order_wm_id + "\",\"typeCode\":\""
                                    +reason_status_elem[which]+"\",\"remark\":\"\"},\"metas\": {\"appName\":\"melody\",\"appVersion\"" +
                                    ":\"4.4.0\",\"ksid\":\"" + ksid + "\"},\"ncp\":\"2.0.0\"}";
                            Request<String> request_cancel = NoHttp.createStringRequest(API.url_eleme_neworder_cancel, RequestMethod.POST);
                            request_cancel.setDefineRequestBodyForJson(params);
                            requestQueue.add(31, request_cancel, new SimpleResponseListener<String>() {
                                @Override
                                public void onSucceed(int what, com.yolanda.nohttp.rest.Response<String> response) {
                                    super.onSucceed(what, response);
                                    Log.e(TAG,response.get()+ "onSucceed: " );
                                }
                            });
                        }
                    });
                    builder.show();
                }

            }
        });
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
            mViewHolder.first_buttons.setBackground(mContext.getResources().getDrawable(R.color.white));
            tag = false;
        } else {
            mViewHolder.expand.setVisibility(View.VISIBLE);
            mViewHolder.first_more.setVisibility(View.GONE);
            mViewHolder.first_less.setVisibility(View.VISIBLE);
            mViewHolder.first_buttons.setBackground(mContext.getResources().getDrawable(R.color.colorFragment));
            tag = true;
        }
    }

    static class ViewHolder {
        @BindView(R.id.logo)
        ImageView logo;
        @BindView(R.id.order_id)
        TextView order_id;
        @BindView(R.id.username)
        TextView username;
        @BindView(R.id.usersex)
        TextView usersex;
        @BindView(R.id.order_nums)
        TextView order_nums;
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
        @BindView(R.id.userphone)
        Button userphone;
        @BindView(R.id.first_more)
        ImageButton first_more;
        @BindView(R.id.first_less)
        ImageButton first_less;
        @BindView(R.id.order_content)
        ListViewForScrollView order_content;
        @BindView(R.id.expand)
        LinearLayoutForButton expand;
        @BindView(R.id.order_nums_linearlayout)
        LinearLayout order_nums_linearlayout;
        @BindView(R.id.first_buttons)
        LinearLayout first_buttons;
        @BindView(R.id.content_imagebuttons)
        RelativeLayoutForButton content_imagebuttons;
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
        Button cancelFirst;
        Button printFirst;
        LinearLayoutForButton orderNumsLinearlayout;
        LinearLayoutForButton useraddress_expand, online_pay_expand, reason_expand;
        RelativeLayoutForButton contentImagebuttons;
        @BindView(R.id.online_pay)
        TextView onlinePay;
        @BindView(R.id.sendgoods_pay)
        TextView sendgoodsPay;
        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
