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

public class NewOrderData_eleme {
    private static List<User> newOrder_data;

    public static List<User> getNewOrderData(String json) {
        newOrder_data = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
                JSONArray order_list = jsonObject.getJSONArray("result");
                for (int i = 0; i < order_list.length(); i++) {
                    User user = new User();
                    JSONObject order_list_data = order_list.optJSONObject(i);
                    user.setOrder_id(order_list_data.optString("daySn"));
                    user.setOrder_wm_id(order_list_data.optString("id"));
                    String name_data = order_list_data.optString("consigneeName");
                    user.setUser_real_name(name_data.substring(0,name_data.indexOf(" ")));
                    user.setSex(name_data.substring(name_data.indexOf(" ")+1));
                    String consigneePhones = order_list_data.optString("consigneePhones");
                    consigneePhones = consigneePhones.substring(2,consigneePhones.length()-2);
                    user.setUser_phone(consigneePhones);

                    user.setUser_address(order_list_data.optString("consigneeAddress"));
                    user.setShop_price(order_list_data.optString("payAmount"));
                    user.setCreate_time(order_list_data.optString("activeTime").replace("T","\t"));
                    String bookedTime = order_list_data.optString("bookedTime");
                    if ("null".equals(bookedTime)) {
                        bookedTime = "立即送餐";
                    }
                    String payment = order_list_data.optString("payment");
                    if ("ONLINE".equals(payment)){
                        user.setPay_type(2);
                    }else {
                        user.setPay_type(1);
                    }
                    user.setSend_time(bookedTime);
                    user.setOrder_meal_fee_price(order_list_data.optString("deliveryFee"));
                    user.setTakeout_cost_price(order_list_data.optString("deliveryFee"));
                    JSONArray groups = order_list_data.optJSONArray("groups");
                    JSONObject object_groups_second = groups.optJSONObject(1);
                    JSONArray items = object_groups_second.optJSONArray("items");
                    JSONObject object_items = items.optJSONObject(0);
                    user.setOrder_meal_fee_price(object_items.optString("total"));
                    String activityTotal =order_list_data.optDouble("activityTotal")+order_list_data.optDouble("hongbao")+"";
                    user.setShop_other_discount_price(activityTotal);
                    String remark = order_list_data.optString("remark");
                    user.setCaution(remark);
                    //详单
                    JSONObject object_groups_first = groups.optJSONObject(0);
                    JSONArray items_goodsdetails = object_groups_first.optJSONArray("items");
                    List<Map<String, String>> goods_lists = new ArrayList<>();
                    for (int j = 0; j < items_goodsdetails.length(); j++) {
                        Map<String, String> map = new HashMap<>();
                        JSONObject order_goods_data = items_goodsdetails.optJSONObject(j);
                        map.put("name", order_goods_data.optString("name"));
                        map.put("number", "×" + order_goods_data.optString("quantity"));
                        map.put("shop_price", order_goods_data.optString("total"));
                        goods_lists.add(map);
                    }
                    user.setGoods_list(goods_lists);
                    newOrder_data.add(user);
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return newOrder_data;
    }
}
