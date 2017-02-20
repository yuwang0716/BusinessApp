package com.liuhesan.app.businessapp.bean;

import java.util.List;
import java.util.Map;

/**
 * Created by Tao on 2016/11/21.
 */

public class User {
    private String order_id;
    private String order_self_id;
    private String order_wm_id;
    private String wm_order_platform;
    private String wm_poi_name;
    private String user_real_name;
    private String sex;
    private String user_phone;
    private String user_order_num_str;
    private String user_address;
    private String shop_price;
    private String create_time;
    private String send_time;
    private int pay_type;
    private List<Map<String, String>> goods_list;
    private List<Long> remind_list;
    private String order_meal_fee_price;
    private String takeout_cost_price;
    private String shop_other_discount_price;
    private String rideman_name;
    private String rideman_phone;
    private String cancel_reason;
    private String caution;
    private String commission_total;

    public List<Long> getRemind_list() {
        return remind_list;
    }

    public void setRemind_list(List<Long> remind_list) {
        this.remind_list = remind_list;
    }

    public String getCommission_total() {
        return commission_total;
    }

    public void setCommission_total(String commission_total) {
        this.commission_total = commission_total;
    }

    public String getCaution() {
        return caution;
    }

    public void setCaution(String caution) {
        this.caution = caution;
    }

    public String getWm_poi_name() {
        return wm_poi_name;
    }

    public void setWm_poi_name(String wm_poi_name) {
        this.wm_poi_name = wm_poi_name;
    }

    public String getCancel_reason() {
        return cancel_reason;
    }

    public void setCancel_reason(String cancel_reason) {
        this.cancel_reason = cancel_reason;
    }

    public String getRideman_name() {
        return rideman_name;
    }

    public void setRideman_name(String rideman_name) {
        this.rideman_name = rideman_name;
    }

    public String getRideman_phone() {
        return rideman_phone;
    }

    public void setRideman_phone(String rideman_phone) {
        this.rideman_phone = rideman_phone;
    }

    public int getPay_type() {
        return pay_type;
    }

    public void setPay_type(int pay_type) {
        this.pay_type = pay_type;
    }

    public String getWm_order_platform() {
        return wm_order_platform;
    }

    public void setWm_order_platform(String wm_order_platform) {
        this.wm_order_platform = wm_order_platform;
    }

    public String getOrder_wm_id() {
        return order_wm_id;
    }

    public void setOrder_wm_id(String order_wm_id) {
        this.order_wm_id = order_wm_id;
    }

    public String getOrder_self_id() {
        return order_self_id;
    }

    public void setOrder_self_id(String order_self_id) {
        this.order_self_id = order_self_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getUser_real_name() {
        return user_real_name;
    }

    public void setUser_real_name(String user_real_name) {
        this.user_real_name = user_real_name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public String getUser_order_num_str() {
        return user_order_num_str;
    }

    public void setUser_order_num_str(String user_order_num_str) {
        this.user_order_num_str = user_order_num_str;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public String getShop_price() {
        return shop_price;
    }

    public void setShop_price(String shop_price) {
        this.shop_price = shop_price;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

    public List<Map<String, String>> getGoods_list() {
        return goods_list;
    }

    public void setGoods_list(List<Map<String, String>> goods_list) {
        this.goods_list = goods_list;
    }

    public String getOrder_meal_fee_price() {
        return order_meal_fee_price;
    }

    public void setOrder_meal_fee_price(String order_meal_fee_price) {
        this.order_meal_fee_price = order_meal_fee_price;
    }

    public String getTakeout_cost_price() {
        return takeout_cost_price;
    }

    public void setTakeout_cost_price(String takeout_cost_price) {
        this.takeout_cost_price = takeout_cost_price;
    }

    public String getShop_other_discount_price() {
        return shop_other_discount_price;
    }

    public void setShop_other_discount_price(String shop_other_discount_price) {
        this.shop_other_discount_price = shop_other_discount_price;
    }
}
