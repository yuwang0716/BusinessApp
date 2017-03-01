package com.liuhesan.app.businessapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.bean.User;
import com.liuhesan.app.businessapp.service.PrintDataService;
import com.liuhesan.app.businessapp.widget.LinearLayoutForButton;
import com.liuhesan.app.businessapp.widget.ListViewForScrollView;
import com.liuhesan.app.businessapp.widget.RelativeLayoutForButton;

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

public class DistributionAdapter extends BaseAdapter {
    private final static String TAG = "DistributionAdapter";
    private List<User> newOrder_data;
    private Context mContext;
    private boolean tag = false;
    private String name;
    private String wmPoiId;
    private String acctId;
    private String token;
    HashMap<Integer, View> map = new HashMap<Integer, View>();


    public DistributionAdapter(Context mContext, List<User> newOrder_data) {
        this.mContext = mContext;
        this.newOrder_data = newOrder_data;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (map.get(position) == null) {
            convertView = View.inflate(mContext, R.layout.waitdistribution_item, null);
            mViewHolder = new ViewHolder(convertView);
            mViewHolder.ridename_phone = (Button) convertView.findViewById(R.id.rideman_phone);
            mViewHolder.orderCancel = (Button) convertView.findViewById(R.id.order_cancel);
            mViewHolder.orderDistribution = (Button) convertView.findViewById(R.id.order_distribution);
            mViewHolder.username = (TextView) convertView.findViewById(R.id.username);
            mViewHolder.usersex = (TextView) convertView.findViewById(R.id.usersex);
            mViewHolder.orderNumsLinearlayout = (LinearLayoutForButton) convertView.findViewById(R.id.order_nums_linearlayout);
            mViewHolder.expand = (LinearLayoutForButton) convertView.findViewById(R.id.expand);
            mViewHolder.contentImagebuttons = (RelativeLayoutForButton) convertView.findViewById(R.id.content_imagebuttons);
            mViewHolder.useraddress_expand = (LinearLayoutForButton) convertView.findViewById(R.id.useraddress_expand);
            mViewHolder.rideman_expand = (TextView) convertView.findViewById(R.id.rideman_expand);
            mViewHolder.online_pay_expand = (LinearLayoutForButton) convertView.findViewById(R.id.online_pay_expand);
            mViewHolder.reason_expand = (LinearLayoutForButton) convertView.findViewById(R.id.reason_expand);
            mViewHolder.loseorder_reason = (Button) convertView.findViewById(R.id.loseorder_reason);
            mViewHolder.content_print = (Button) convertView.findViewById(R.id.content_print);
            convertView.setTag(mViewHolder);
            map.put(position, convertView);
        } else {
            convertView = map.get(position);
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        mViewHolder.ordernumberone.setText("#" + newOrder_data.get(position).getOrder_self_id());
        mViewHolder.wmNum.setText("#" + newOrder_data.get(position).getOrder_wm_id());
        mViewHolder.username.setText(newOrder_data.get(position).getUser_real_name());
        mViewHolder.usersex.setText(newOrder_data.get(position).getSex());
        if (newOrder_data.get(position).getUser_order_num_str().equals("0")){
            mViewHolder.orderNums.setText("首次下单");
        }else {
            mViewHolder.orderNums.setText(newOrder_data.get(position).getUser_order_num_str());
        }
        mViewHolder.useraddress.setText(newOrder_data.get(position).getUser_address());
        mViewHolder.shopPrice.setText("总共" + newOrder_data.get(position).getShop_price() + "元");
        name = newOrder_data.get(position).getWm_order_platform();
        String date = getDate(position, "yyyy/M/d HH:mm");
        int lastIndexOf = date.lastIndexOf(" ");
        String substring = date.substring(lastIndexOf);
        mViewHolder.ordertime.setText(substring + "下单");
        mViewHolder.orderdata.setText(date);
        if (name.equals("baidu")) {
            mViewHolder.logo.setBackground(mContext.getResources().getDrawable(R.mipmap.wm_baidu));
        } else if (name.equals("meit")) {
            mViewHolder.logo.setBackground(mContext.getResources().getDrawable(R.mipmap.wm_meituan));
        } else {
            mViewHolder.logo.setBackground(mContext.getResources().getDrawable(R.mipmap.wm_elem));
        }
        if (newOrder_data.get(position).getSend_time().equals("立即送餐")) {
            mViewHolder.sendTime.setText(newOrder_data.get(position).getSend_time());
        } else {
            String sendtime = "<font color='#e60012'>" + newOrder_data.get(position).getSend_time() + "</font>";
            mViewHolder.sendTime.setText(Html.fromHtml(sendtime));
        }
        mViewHolder.orderMealFee.setText(newOrder_data.get(position).getOrder_meal_fee_price());
        mViewHolder.shippingFee.setText(newOrder_data.get(position).getTakeout_cost_price());
        mViewHolder.shopOtherDiscount.setText(newOrder_data.get(position).getShop_other_discount_price());
        mViewHolder.totalPrice.setText(newOrder_data.get(position).getShop_price());
        mViewHolder.caution.setText(newOrder_data.get(position).getCaution());
        if (newOrder_data.get(position).getPay_type() == 2) {
            mViewHolder.onlinePay.setVisibility(View.VISIBLE);
            mViewHolder.sendgoodsPay.setVisibility(View.GONE);
        } else {
            mViewHolder.onlinePay.setVisibility(View.GONE);
            mViewHolder.sendgoodsPay.setVisibility(View.VISIBLE);
        }
        mViewHolder.ordernumberTwo.setText(newOrder_data.get(position).getOrder_id());
        mViewHolder.userphone.setText(newOrder_data.get(position).getUser_phone());
        List<Map<String, String>> goods_list = newOrder_data.get(position).getGoods_list();
        GoodsAdapter mGoodsAdapter = new GoodsAdapter(mContext, goods_list);
        mViewHolder.orderContent.setAdapter(mGoodsAdapter);
        //打印内容
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("login", Context.MODE_PRIVATE);
        mViewHolder.content_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceAddress = sharedPreferences.getString("deviceAddress", null);
                boolean isConnection = sharedPreferences.getBoolean("isConnection", false);
                if (!TextUtils.isEmpty(deviceAddress)) {
                    PrintDataService printDataService = new PrintDataService(mContext, deviceAddress);
                    printDataService.connect();
                    PrintDataService.selectCommand(PrintDataService.RESET);
                    PrintDataService.selectCommand(PrintDataService.LINE_SPACING_DEFAULT);
                    PrintDataService.selectCommand(PrintDataService.ALIGN_CENTER);
                    PrintDataService.selectCommand(PrintDataService.DOUBLE_HEIGHT_WIDTH);
                    PrintDataService.selectCommand(PrintDataService.BOLD);
                    printDataService.send(isConnection, "\n\n\n"+"商家小票\n");
                    PrintDataService.selectCommand(PrintDataService.BOLD_CANCEL);
                    PrintDataService.selectCommand(PrintDataService.DOUBLE_HEIGHT);
                    printDataService.send(isConnection, newOrder_data.get(position).getWm_poi_name() + "\n");
                    PrintDataService.selectCommand(PrintDataService.NORMAL);
                    PrintDataService.selectCommand(PrintDataService.ALIGN_LEFT);
                    printDataService.send(isConnection, "下单时间：" + getDate(position, "yyyy-MM-dd HH:mm:ss")+"\n");
                    printDataService.send(isConnection, "--------------------------------\n");
                    PrintDataService.selectCommand(PrintDataService.BOLD);
                    printDataService.send(isConnection, "今日  ");
                    PrintDataService.selectCommand(PrintDataService.DOUBLE_HEIGHT_WIDTH);
                    PrintDataService.selectCommand(PrintDataService.BOLD_CANCEL);
                    printDataService.send(isConnection, "#" + newOrder_data.get(position).getOrder_self_id());
                    PrintDataService.selectCommand(PrintDataService.NORMAL);
                    PrintDataService.selectCommand(PrintDataService.BOLD);
                    printDataService.send(isConnection,  newOrder_data.get(position).getWm_order_platform() + "  ");
                    PrintDataService.selectCommand(PrintDataService.DOUBLE_HEIGHT_WIDTH);
                    PrintDataService.selectCommand(PrintDataService.BOLD_CANCEL);
                    printDataService.send(isConnection, "#" + newOrder_data.get(position).getOrder_wm_id() + "\n");
                    PrintDataService.selectCommand(PrintDataService.NORMAL);
                    PrintDataService.selectCommand(PrintDataService.ALIGN_LEFT);
                    PrintDataService.selectCommand(PrintDataService.BOLD_CANCEL);
                    printDataService.send(isConnection, "*********\t" + "味点口袋\t" + "*********\n");
                    printDataService.send(isConnection, PrintDataService.printThreeData("商品", "数量", "实付\n"));
                    PrintDataService.selectCommand(PrintDataService.DOUBLE_HEIGHT);
                    String number;
                    for (int i = 0; i < goods_list.size(); i++) {
                        number = goods_list.get(i).get("number").replace("×", "");
                        printDataService.send(isConnection, PrintDataService.printThreeData(goods_list.get(i).get("name"), number, goods_list.get(i).get("shop_price") + "\n"));
                    }
                    PrintDataService.selectCommand(PrintDataService.NORMAL);
                    PrintDataService.selectCommand(PrintDataService.ALIGN_LEFT);
                    printDataService.send(isConnection, "*********\t" + "其它\t" + "*********\n");
                    PrintDataService.selectCommand(PrintDataService.BOLD);
                    printDataService.send(isConnection, PrintDataService.printTwoData("配送费",newOrder_data.get(position).getTakeout_cost_price()+"\n" ));
                    printDataService.send(isConnection, PrintDataService.printTwoData("餐盒费",newOrder_data.get(position).getOrder_meal_fee_price()+"\n" ));
                    printDataService.send(isConnection, PrintDataService.printTwoData("优惠",newOrder_data.get(position).getShop_other_discount_price()+"\n" ));
                    printDataService.send(isConnection, PrintDataService.printTwoData("外卖平台抽取佣金",newOrder_data.get(position).getCommission_total()+"\n" ));
                    printDataService.send(isConnection, PrintDataService.printTwoData("备注：",newOrder_data.get(position).getCaution()+"\n"));
                    PrintDataService.selectCommand(PrintDataService.DOUBLE_WIDTH);
                    PrintDataService.selectCommand(PrintDataService.BOLD_CANCEL);
                    if (newOrder_data.get(position).getPay_type() == 2) {
                        printDataService.send(isConnection,"在线支付");
                    } else {
                        printDataService.send(isConnection,"货到付款");
                    }
                    PrintDataService.selectCommand(PrintDataService.DOUBLE_HEIGHT_WIDTH);
                    PrintDataService.selectCommand(PrintDataService.BOLD_CANCEL);
                    printDataService.send(isConnection,newOrder_data.get(position).getShop_price()+"\n");
                    PrintDataService.selectCommand(PrintDataService.NORMAL);
                    PrintDataService.selectCommand(PrintDataService.ALIGN_LEFT);
                    printDataService.send(isConnection, "--------------------------------\n");
                    PrintDataService.selectCommand(PrintDataService.BOLD);
                    PrintDataService.selectCommand(PrintDataService.DOUBLE_HEIGHT_WIDTH);
                    printDataService.send(isConnection,newOrder_data.get(position).getUser_address()+"\n");
                    printDataService.send(isConnection,newOrder_data.get(position).getUser_phone()+"\n");
                    printDataService.send(isConnection,newOrder_data.get(position).getUser_real_name());
                    PrintDataService.selectCommand(PrintDataService.BOLD_CANCEL);
                    PrintDataService.selectCommand(PrintDataService.NORMAL);
                    printDataService.send(isConnection,"("+newOrder_data.get(position).getUser_order_num_str()+")"+"\n\n");
                }
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

        //订单操作
        mViewHolder.orderDistribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "配送", Toast.LENGTH_SHORT).show();
            }
        });
        mViewHolder.orderCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        //骑手信息
        if (TextUtils.isEmpty(newOrder_data.get(position).getRideman_name())) {
            mViewHolder.rideman.setVisibility(View.GONE);
        } else {
            mViewHolder.rideman.setVisibility(View.VISIBLE);
            mViewHolder.ridename_name.setText(newOrder_data.get(position).getRideman_name());
            mViewHolder.ridename_phone.setText(newOrder_data.get(position).getRideman_phone());
            mViewHolder.orderButtons.setVisibility(View.GONE);
        }
        mViewHolder.ridename_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel://" + newOrder_data.get(position).getUser_phone()));
                mContext.startActivity(intent);
            }
        });
        //取消理由
        if (!TextUtils.isEmpty(newOrder_data.get(position).getCancel_reason())) {
            mViewHolder.reason_expand.setVisibility(View.VISIBLE);
            mViewHolder.loseorder_reason.setText(newOrder_data.get(position).getCancel_reason());
            mViewHolder.orderButtons.setVisibility(View.GONE);
        }
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
            mViewHolder.firstMore.setVisibility(View.VISIBLE);
            mViewHolder.firstLess.setVisibility(View.GONE);
            mViewHolder.orderButtons.setBackground(mContext.getResources().getDrawable(R.color.white));
            tag = false;
        } else {
            mViewHolder.expand.setVisibility(View.VISIBLE);
            mViewHolder.firstMore.setVisibility(View.GONE);
            mViewHolder.firstLess.setVisibility(View.VISIBLE);
            mViewHolder.orderButtons.setBackground(mContext.getResources().getDrawable(R.color.colorFragment));
            tag = true;
        }
    }
    static class ViewHolder {
        @BindView(R.id.ordernumberone)
        TextView ordernumberone;
        @BindView(R.id.waitdistribution_logo)
        ImageView logo;
        @BindView(R.id.userphone)
        Button userphone;
        @BindView(R.id.order_nums)
        TextView orderNums;

        @BindView(R.id.first_more)
        ImageButton firstMore;
        @BindView(R.id.first_less)
        ImageButton firstLess;

        @BindView(R.id.useraddress)
        TextView useraddress;
        @BindView(R.id.online_pay)
        TextView onlinePay;
        @BindView(R.id.sendgoods_pay)
        TextView sendgoodsPay;
        @BindView(R.id.shop_price)
        TextView shopPrice;
        @BindView(R.id.ordertime)
        TextView ordertime;

        @BindView(R.id.send_time)
        TextView sendTime;
        @BindView(R.id.orderdata)
        TextView orderdata;
        @BindView(R.id.ordernumber_two)
        TextView ordernumberTwo;
        @BindView(R.id.order_content)
        ListViewForScrollView orderContent;

        @BindView(R.id.order_buttons)
        LinearLayout orderButtons;
        @BindView(R.id.wm_num)
        TextView wmNum;
        @BindView(R.id.rideman)
        LinearLayout rideman;
        @BindView(R.id.rideman_name)
        TextView ridename_name;
        @BindView(R.id.order_meal_fee)
        TextView orderMealFee;
        @BindView(R.id.shipping_fee)
        TextView shippingFee;
        @BindView(R.id.shop_other_discount)
        TextView shopOtherDiscount;
        @BindView(R.id.total_price)
        TextView totalPrice;
        @BindView(R.id.caution)
        TextView caution;
        Button ridename_phone;
        Button orderCancel;
        Button orderDistribution;
        Button loseorder_reason;
        Button content_print;
        TextView rideman_expand;
        TextView username;
        TextView usersex;
        LinearLayoutForButton orderNumsLinearlayout;
        LinearLayoutForButton useraddress_expand, online_pay_expand, reason_expand;
        LinearLayoutForButton expand;
        RelativeLayoutForButton contentImagebuttons;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
