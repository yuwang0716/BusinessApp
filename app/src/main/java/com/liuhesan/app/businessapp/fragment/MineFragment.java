package com.liuhesan.app.businessapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.ui.personcenter.AutoGetOrderActivity;
import com.liuhesan.app.businessapp.ui.personcenter.MystoreActivity;
import com.liuhesan.app.businessapp.ui.personcenter.PrintSettingActivity;
import com.liuhesan.app.businessapp.ui.personcenter.RemindSettingActivity;
import com.liuhesan.app.businessapp.ui.personcenter.RunningstateActivity;
import com.liuhesan.app.businessapp.ui.personcenter.SafeSettingActivity;
import com.liuhesan.app.businessapp.ui.personcenter.SmsManagerActivity;
import com.liuhesan.app.businessapp.utility.API;
import com.liuhesan.app.businessapp.widget.CircleImageView;
import com.liuhesan.app.businessapp.widget.LinearLayoutForButton;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.SimpleResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Home on 2016/11/8.
 */

public class MineFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MineFragment";
    @BindView(R.id.todayincome)
    TextView tv_todayIncome;
    @BindView(R.id.efficacyorder)
    TextView tv_efficacyOrder;
    @BindView(R.id.loseefficacyorder)
    TextView tv_loseEfficacyOrder;
    @BindView(R.id.mine_mystore)
    LinearLayoutForButton mineMystore;
    @BindView(R.id.mine_state)
    LinearLayoutForButton mineState;
    @BindView(R.id.mine_time)
    LinearLayoutForButton mineTime;
    @BindView(R.id.mine_inventory)
    LinearLayoutForButton mineInventory;
    @BindView(R.id.mine_sms)
    LinearLayoutForButton mineSms;
    @BindView(R.id.mine_logistics)
    LinearLayoutForButton mineLogistics;
    @BindView(R.id.mine_ordersetting)
    LinearLayoutForButton mineOrdersetting;
    @BindView(R.id.mine_remindersetting)
    LinearLayoutForButton mineRemindersetting;
    @BindView(R.id.mine_print)
    LinearLayoutForButton minePrint;
    @BindView(R.id.mine_limit)
    LinearLayoutForButton mineLimit;
    @BindView(R.id.mine_safety)
    LinearLayoutForButton mineSafety;
    @BindView(R.id.circle_image)
    CircleImageView circleImage;
    @BindView(R.id.mine_qualification)
    TextView mineQualification;
    @BindView(R.id.mine_certificationID)
    TextView mineCertificationID;
    @BindView(R.id.shopName)
    TextView shopName;

    private View view;
    private Intent intent;
    private Unbinder unbinder;
    private Context mContext;
    private String url;
    private LocalBroadcastManager localBroadcastManager;
    private ReseizeReceive reseizeReceive;
    private IntentFilter intentFilter;
    private int certificationID, qualification;
    private RequestQueue requestQueue;
    private String todayIncome,efficacyOrder,loseEfficacyOrder;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mine, container, false);
        unbinder = ButterKnife.bind(this, view);
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.liuhesan.app.HEAD");
        reseizeReceive = new ReseizeReceive();
        localBroadcastManager.registerReceiver(reseizeReceive, intentFilter);
        requestQueue = NoHttp.newRequestQueue();
        initView();
        initData();
        return view;
    }

    private void initData() {
        Request<String> request_statistics = NoHttp.createStringRequest(API.url_system_statistics, RequestMethod.POST);
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("login",Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        request_statistics.add("token",token);
        requestQueue.add(0, request_statistics, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                super.onSucceed(what, response);
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    int errno = jsonObject.optInt("errno");
                    if (errno == 200){
                        JSONObject data = jsonObject.optJSONObject("data");
                        todayIncome = "<font color ='#e60012'align= 'center'>"+data.optString("currentPrice")+"</font><br/>今日收入";
                        efficacyOrder = "<font color ='#e60012'align= 'center'>"+data.optString("currentOrder")+"</font><br/>有效订单";
                        loseEfficacyOrder = "<font color ='#e60012'align= 'center'>"+data.optString("noOrder")+"</font><br/>无效订单";
                        tv_todayIncome.setText(Html.fromHtml(todayIncome));
                        tv_efficacyOrder.setText(Html.fromHtml(efficacyOrder));
                        tv_loseEfficacyOrder.setText(Html.fromHtml(loseEfficacyOrder));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("login", Context.MODE_PRIVATE);
        url = sharedPreferences.getString("headportrait", "");
        Glide.with(MineFragment.this).load(url)
                .placeholder(R.mipmap.default_personal_image)
                .error(R.mipmap.default_personal_image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        circleImage.setImageDrawable(resource);
                    }
                });
        shopName.setText(sharedPreferences.getString("shop_name",""));
        certificationID = sharedPreferences.getInt("certificationID", 0);
        Drawable drawable = getResources().getDrawable(R.mipmap.mine_phone_verification);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        if (certificationID == 1) {
            mineCertificationID.setText("\t身份证认证");
            mineCertificationID.setCompoundDrawables(drawable, null, null, null);
        } else {
            mineCertificationID.setText("\t身份证未认证");
            mineCertificationID.setCompoundDrawables(null, null, null, null);
        }
        qualification = sharedPreferences.getInt("qualification", 0);
        if (qualification == 1) {
            mineQualification.setText("\t资质认证");
            mineQualification.setCompoundDrawables(drawable, null, null, null);
        } else {
            mineQualification.setText("\t资质未认证");
            mineQualification.setCompoundDrawables(null, null, null, null);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @OnClick({R.id.mine_mystore, R.id.mine_state, R.id.mine_time, R.id.mine_inventory,
            R.id.mine_sms, R.id.mine_logistics, R.id.mine_ordersetting, R.id.mine_remindersetting,
            R.id.mine_print, R.id.mine_limit, R.id.mine_safety, R.id.circle_image})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mine_mystore:
                startActivity(new Intent(mContext, MystoreActivity.class));
                break;
            case R.id.mine_state:
                intent = new Intent(mContext, RunningstateActivity.class);
                startActivity(intent);
                break;
            case R.id.mine_time:
                break;
            case R.id.mine_inventory:
                break;
            case R.id.mine_sms:
                startActivity(new Intent(mContext, SmsManagerActivity.class));
                break;
            case R.id.mine_logistics:
                break;
            case R.id.mine_ordersetting:
                startActivity(new Intent(mContext, AutoGetOrderActivity.class));
                break;
            case R.id.mine_remindersetting:
                startActivity(new Intent(mContext, RemindSettingActivity.class));
                break;
            case R.id.mine_print:
                startActivity(new Intent(mContext, PrintSettingActivity.class));
                break;
            case R.id.mine_limit:
                break;
            case R.id.mine_safety:
                startActivity(new Intent(mContext, SafeSettingActivity.class));
                break;
            case R.id.circle_image:
                startActivity(new Intent(mContext, MystoreActivity.class));
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        localBroadcastManager.unregisterReceiver(reseizeReceive);
    }

    class ReseizeReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            url = intent.getStringExtra("headportrait");
            Glide.with(MineFragment.this).load(url)
                    .placeholder(R.mipmap.default_personal_image)
                    .error(R.mipmap.default_personal_image)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            circleImage.setImageDrawable(resource);
                        }
                    });

        }
    }
}
