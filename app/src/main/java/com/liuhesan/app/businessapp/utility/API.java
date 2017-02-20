package com.liuhesan.app.businessapp.utility;

/**
 * Created by Tao on 2016/11/21.
 */

public class API {
    //系统登录
    public static final String url_login = "http://crm.weidab.cn/Rest/";
    public static final String url_system_neworder = API.url_login+"order/newOrder";
    public static final String url_system_shopstatus = API.url_login+"order/setShopStatus";
    public static final String url_system_shopdetails = API.url_login+"user/getShopinfo";

    //百度外卖
    public static final String url_baidu = "https://wmpass.baidu.com/";
    public static final String url_baidu_data = "https://wmcrm.baidu.com/";
    public static final String url_baidu_toke = url_baidu+"wmpass/openservice/captchapair?protocal=https";
    public static final String url_baidu_img = url_baidu+"wmpass/openservice/captchapair";
    public static final String url_baidu_shopstatus = url_baidu+"?qt=modifyshopstatus&status=";
    public static final String url_baidu_login = url_baidu+"api/login";
    public static final String url_baidu_neworder_notification = url_baidu_data+"order/notice?type=1";
    public static final String url_baidu_neworder_details = url_baidu_data+"crm?qt=neworderlist&display=json";
    public static final String url_baidu_urgingOrder_details = url_baidu_data+"crm?qt=getremindorderlist&replied=0&display=json";
    public static final String url_baidu_refundOrder_details = url_baidu_data+"crm?qt=getusercancellist&type=1&display=json";
    public static final String url_baidu_neworder_sure = url_baidu_data+"crm?qt=confirmorder";

    //美团外卖
    public static final String url_meituan = "http://e.waimai.meituan.com/";
    public static final String url_meituan_base = "https://waimaie.meituan.com/v2/order/";
    public static final String url_meituan_uuid = url_meituan+"logon";
    public static final String url_meituan_logon = url_meituan+"v2/logon/pass/step1/logon";
    public static final String url_meituan_shopstatus = "https://waimaie.meituan.com/api/poi/change_status";
    public static final String url_meituan_notification = url_meituan_base+"receive/unprocessed/r/count";
    public static final String url_meituan_data = url_meituan_base +"receive/unprocessed/r/ofq?time=";
    public static final String url_meituan_reminder = url_meituan_base+"customer/reminder/unprocessed/r/count";
    public static final String url_meituan_reminder_details = url_meituan_base+"customer/reminder/unprocessed/list?pageNum=1";
    public static final String url_meituan_refund = url_meituan_base+"history/r/refundOrderCount/notify";
    public static final String url_meituan_refund_details = url_meituan_base+"refund/unprocessed/r/query?getNewVo=1&pageNum=1";

    //饿了么外卖
    public static final String url_uuid_eleme = "http://melody.shop.ele.me/eleme/";
    public static final String url_eleme = "https://app-api.shop.ele.me/";
    public static final String url_uuid_eleme_uuid = url_uuid_eleme+"napos.picto.melody/login";
    public static final String url_eleme_shopstatus = url_eleme+"shop/invoke/?method=";
    public static final String url_eleme_logon = url_eleme+"arena/invoke/?method=LoginService.loginByUsername";
    public static final String url_eleme_notification = url_eleme+"nevermore/invoke/?method=PollingService.pollingForHighFrequency";
    public static final String url_eleme_reminder = url_eleme+"nevermore/invoke/?method=PollingService.pollingForLowFrequency";
    public static final String url_eleme_reminder_details = url_eleme+"nevermore/invoke/?method=OrderService.queryOrder";

    private static String REURL;

    public static String getREURL() {
        return REURL;
    }

    public static void setREURL(String REURL) {
        API.REURL = REURL;
    }
}