package com.liuhesan.app.businessapp.utility;

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

public class HistoryOrderData {
    private static List<User> newOrder_data;

    public static List<User> getNewOrderData(String json) {
        newOrder_data = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject data = jsonObject.getJSONObject("data");

                JSONArray order_list = data.getJSONArray("list");
            if (order_list!= null) {
                for (int i = 0; i < order_list.length(); i++) {
                    User user = new User();
                    JSONObject order_list_data = order_list.optJSONObject(i);
                    user.setOrder_id(order_list_data.optString("wm_order_id_view"));
                    user.setOrder_self_id(order_list_data.optString("id"));
                    user.setOrder_wm_id(order_list_data.optString("order_daysn"));
                    user.setWm_order_platform(order_list_data.optString("wm_order_platform"));
                    user.setWm_poi_name(order_list_data.optString("wm_poi_name"));
                    user.setUser_real_name(order_list_data.optString("recipient_name"));
                    user.setSex(order_list_data.optString("recipient_sex"));
                    user.setUser_phone(order_list_data.optString("recipient_phone"));
                    user.setUser_order_num_str(order_list_data.optString("user_order_num_str"));
                    user.setUser_address(order_list_data.optString("recipient_address"));
                    user.setShop_price(order_list_data.optString("total_price"));
                    user.setCreate_time(order_list_data.optString("order_receive_time"));
                    user.setSend_time(order_list_data.optString("order_send_time"));
                    user.setOrder_meal_fee_price(order_list_data.optString("order_meal_fee"));
                    user.setTakeout_cost_price(order_list_data.optString("shipping_fee"));
                    user.setShop_other_discount_price(order_list_data.optString("shop_other_discount"));
                    user.setPay_type(order_list_data.optInt("pay_type"));
                    user.setRideman_name(order_list_data.optString("logistics_dispatcher_name"));
                    user.setRideman_phone(order_list_data.optString("logistics_dispatcher_mobile"));
                    user.setCancel_reason(order_list_data.optString("cancel_reason"));
                    user.setCaution(order_list_data.optString("caution"));
                    user.setCommission_total(order_list_data.optString("commission_total"));
                    JSONArray goods_list =  order_list_data.optJSONArray("goods");
                    List<Map<String, String>> goods_lists = new ArrayList<>();
                    for (int j = 0; j < goods_list.length(); j++) {
                        Map<String, String> map = new HashMap<>();
                        JSONObject order_goods_data = goods_list.optJSONObject(j);
                        map.put("name", order_goods_data.optString("name"));
                        map.put("number", "×" + order_goods_data.optString("number"));
                        map.put("shop_price", order_goods_data.optString("price"));
                        goods_lists.add(map);
                    }
                    user.setGoods_list(goods_lists);
                    newOrder_data.add(user);
                }

            }
        }  catch (JSONException e) {
            e.printStackTrace();
        }

        return newOrder_data;
    }
}
