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

public class NewOrderData_baidu {
    private static List<User> newOrder_data;

    public static List<User> getNewOrderData(String json) {
        newOrder_data = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null) {
                JSONArray order_list = data.getJSONArray("order_list");
                for (int i = 0; i < order_list.length(); i++) {
                    User user = new User();
                    JSONObject order_list_data = order_list.optJSONObject(i);
                    JSONObject order_basic = order_list_data.optJSONObject("order_basic");
                    JSONObject order_total = order_list_data.optJSONObject("order_total");
                    JSONObject order_meal_fee = order_list_data.optJSONObject("order_meal_fee");
                    JSONObject takeout_cost = order_list_data.optJSONObject("takeout_cost");
                    JSONObject shop_other_discount = order_list_data.optJSONObject("shop_other_discount");


                    user.setOrder_id(order_basic.optString("order_id"));
                    user.setUser_real_name(order_basic.optString("user_real_name"));
                    user.setSex(order_basic.optString("sex"));
                    user.setUser_phone(order_basic.optString("user_phone"));
                    user.setUser_order_num_str(order_basic.optString("user_order_num_str"));
                    user.setUser_address(order_basic.optString("user_address"));
                    user.setShop_price(order_total.optString("shop_price"));
                    user.setCreate_time(order_basic.optString("create_time"));
                    user.setSend_time(order_basic.optString("send_time"));
                    user.setOrder_meal_fee_price(order_meal_fee.optString("price"));
                    user.setTakeout_cost_price(takeout_cost.optString("price"));
                    user.setShop_other_discount_price(shop_other_discount.optString("price"));

                    JSONObject order_goods = order_list_data.getJSONObject("order_goods");
                    JSONArray goods_list = order_goods.getJSONArray("goods_list");
                    List<Map<String, String>> goods_lists = new ArrayList<>();
                    for (int j = 0; j < goods_list.length(); j++) {
                        Map<String, String> map = new HashMap<>();
                        JSONObject order_goods_data = goods_list.optJSONObject(j);
                        map.put("name", order_goods_data.optString("name"));
                        map.put("number", "×" + order_goods_data.optString("number"));
                        map.put("shop_price", order_goods_data.optString("shop_price"));
                        goods_lists.add(map);
                    }
                    //催单次数
                    JSONArray order_remind = order_basic.optJSONArray("remind_list");
                    List<Long> remind_list = new ArrayList<>();
                    if (remind_list != null) {
                        for (int k = 0; k < order_remind.length(); k++) {
                            JSONObject order_reminds = order_remind.optJSONObject(k);
                            long create_time = order_reminds.optLong("create_time");
                            remind_list.add(create_time);
                        }
                        user.setRemind_list(remind_list);
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
