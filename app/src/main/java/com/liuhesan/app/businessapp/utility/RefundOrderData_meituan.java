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

public class RefundOrderData_meituan {
    private static List<User> urgeOrder_data;

    public static List<User> getRefundOrderData(String json) {
       urgeOrder_data = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject data  = jsonObject.optJSONObject("data") ;
            JSONArray wmOrderList = data.getJSONArray("orderList");
            for (int i = 0; i < wmOrderList.length(); i++) {
                User user = new User();
                JSONObject order_list_data = wmOrderList.optJSONObject(i);
                user.setOrder_self_id(order_list_data.optString("id"));
                user.setWmPoiId(order_list_data.optString("wm_poi_id"));
                user.setOrder_id(order_list_data.optString("num"));
                user.setOrder_wm_id(order_list_data.optString("wm_order_id_view"));
                String name_data = order_list_data.optString("recipient_name");
                user.setUser_real_name(name_data.substring(0,name_data.indexOf("(")));
                user.setSex(name_data.substring(name_data.indexOf("(")+1,name_data.indexOf(")")));
                user.setUser_phone(order_list_data.optString("recipient_phone"));
                user.setUser_address(order_list_data.optString("recipient_address"));
                user.setShop_price(order_list_data.optString("total_after"));
                user.setCreate_time(order_list_data.optString("order_time_fmt"));
                user.setPay_type(order_list_data.optInt("wm_order_pay_type"));
                String send_time;
                if (TextUtils.isEmpty(order_list_data.optString("delivery_btime_fmt"))){
                    send_time = "立即送餐";
                }else{
                    send_time = order_list_data.optString("delivery_btime_fmt");
                }
                user.setSend_time(send_time);
                user.setOrder_meal_fee_price(order_list_data.optString("boxPriceTotal"));//餐盒费用
                user.setTakeout_cost_price(order_list_data.optString("shipping_fee"));//配送费用
                JSONArray discounts = order_list_data.optJSONArray("discounts");//优惠
                double Shop_other_discount_price = 0.0;
                for (int j = 0; j < discounts.length(); j++) {
                    JSONObject discounts_list_data = discounts.optJSONObject(j);
                    String info = discounts_list_data.optString("info");
                    String substring = info.substring(2);
                    double parseDouble = Double.parseDouble(substring);
                    Shop_other_discount_price += parseDouble;
                }
                user.setShop_other_discount_price("-"+Shop_other_discount_price);
                user.setCaution(order_list_data.optString("remark"));
                //详单
                JSONArray goods_list = order_list_data.getJSONArray("cartDetailVos");
                JSONObject cartDetailVos = goods_list.optJSONObject(0);
                JSONArray details = cartDetailVos.optJSONArray("details");
                List<Map<String, String>> goods_lists = new ArrayList<>();
                for (int j = 0; j < details.length(); j++) {
                    Map<String, String> map = new HashMap<>();
                    JSONObject order_goods_data = details.optJSONObject(j);
                    double count = order_goods_data.optDouble("count");
                    double food_price = order_goods_data.optDouble("food_price");
                    String shop_price = Double.toString(count*food_price);
                    map.put("name", order_goods_data.optString("food_name"));
                    map.put("number", "×" + count);
                    map.put("shop_price", shop_price);
                    goods_lists.add(map);
                }
                user.setGoods_list(goods_lists);
                urgeOrder_data.add(user);
            }
        }
      catch (JSONException e) {
            e.printStackTrace();
        }
        return urgeOrder_data;
    }
}
