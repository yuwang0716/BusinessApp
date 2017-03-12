package com.liuhesan.app.businessapp.utility;

import android.text.TextUtils;

import com.liuhesan.app.businessapp.bean.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tao on 2016/12/2.
 */

public class NewOrderData_meituan {
    private static List<User> newOrder_data;

    public static List<User> getNewOrderData(String json) {
        newOrder_data = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray order_list = jsonObject.getJSONArray("data");
            for (int i = 0; i < order_list.length(); i++) {
                User user = new User();
                JSONObject order_list_data = order_list.optJSONObject(i);
                user.setOrder_id(order_list_data.optString("num"));
                String name_data = order_list_data.optString("recipient_name");
                user.setUser_real_name(name_data.substring(0,name_data.indexOf("(")));
                user.setSex(name_data.substring(name_data.indexOf("(")+1,name_data.indexOf(")")));
                user.setUser_phone(order_list_data.optString("recipient_phone"));
                user.setOrder_wm_id(order_list_data.optString("wm_order_id_view_str"));
                user.setUser_address(order_list_data.optString("recipient_address"));
                user.setShop_price(order_list_data.optDouble("total_after")+"");
                user.setCreate_time(order_list_data.optString("order_time_fmt"));
                String send_time;
                if (TextUtils.isEmpty(order_list_data.optString("send_time"))){
                    send_time = "立即送餐";
                }else{
                    send_time = order_list_data.optString("send_time");
                }
                int wm_order_pay_type = order_list_data.optInt("wm_order_pay_type");
                if (wm_order_pay_type == 2){
                    user.setPay_type(2);
                }else {
                    user.setPay_type(1);
                }
                String remark = order_list_data.optString("remark");
                user.setCaution(remark);
                user.setSend_time(send_time);
                user.setOrder_meal_fee_price(order_list_data.optDouble("boxPriceTotal")+"");//餐盒费用
                user.setTakeout_cost_price(order_list_data.optDouble("shipping_fee")+"");//配送费用
                JSONArray discounts = order_list_data.optJSONArray("discounts");
                double Shop_other_discount_price = 0.0;
                for (int j = 0; j < discounts.length(); j++) {
                    JSONObject discounts_list_data = discounts.optJSONObject(j);
                    String info = discounts_list_data.optString("info");
                    String substring = info.substring(2);
                    double parseDouble = Double.parseDouble(substring);
                    Shop_other_discount_price += parseDouble;
                }
                user.setShop_other_discount_price("-"+Shop_other_discount_price);
                //详单
                JSONArray goods_list = order_list_data.getJSONArray("cartDetailVos");
                JSONObject cartDetailVos = goods_list.optJSONObject(0);
                JSONArray details = cartDetailVos.optJSONArray("details");
                List<Map<String, String>> goods_lists = new ArrayList<>();
                for (int j = 0; j < details.length(); j++) {
                    Map<String, String> map = new HashMap<>();
                    JSONObject order_goods_data = details.optJSONObject(j);
                    map.put("name", order_goods_data.optString("food_name"));
                    map.put("number", "×" + order_goods_data.optString("box_num"));
                    map.put("shop_price", order_goods_data.optDouble("origin_food_price")+"");
                    goods_lists.add(map);
                }
                user.setGoods_list(goods_lists);
                newOrder_data.add(user);
            }
        }
      catch (JSONException e) {
            e.printStackTrace();
        }
        return newOrder_data;
    }
}
