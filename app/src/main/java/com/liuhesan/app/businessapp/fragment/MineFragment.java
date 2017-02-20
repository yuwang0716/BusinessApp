package com.liuhesan.app.businessapp.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.liuhesan.app.businessapp.ui.personcenter.MystoreActivity;
import com.liuhesan.app.businessapp.ui.personcenter.PrintSettingActivity;
import com.liuhesan.app.businessapp.ui.personcenter.RunningstateActivity;
import com.liuhesan.app.businessapp.widget.CircleImageView;
import com.liuhesan.app.businessapp.widget.LinearLayoutForButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Home on 2016/11/8.
 */

public class MineFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.todayincome)
    TextView tv_todayincome;
    @BindView(R.id.efficacyorder)
    TextView tv_efficacyorder;
    @BindView(R.id.loseefficacyorder)
    TextView tv_loseefficacyorder;
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
    private View view;
    private Intent intent;
    private Unbinder unbinder;
    private Context mContext;
    private String url;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mine, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("login",Context.MODE_PRIVATE);
        url = sharedPreferences.getString("headportrait","");
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @OnClick({R.id.mine_mystore, R.id.mine_state, R.id.mine_time, R.id.mine_inventory,
            R.id.mine_sms, R.id.mine_logistics, R.id.mine_ordersetting, R.id.mine_remindersetting,
            R.id.mine_print, R.id.mine_limit, R.id.mine_safety,R.id.circle_image})
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
                break;
            case R.id.mine_logistics:
                break;
            case R.id.mine_ordersetting:
                break;
            case R.id.mine_remindersetting:
                break;
            case R.id.mine_print:
                startActivity(new Intent(mContext, PrintSettingActivity.class));
                break;
            case R.id.mine_limit:
                break;
            case R.id.mine_safety:
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
