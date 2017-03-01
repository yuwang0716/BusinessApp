package com.liuhesan.app.businessapp.ui.personcenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.liuhesan.app.businessapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RemindSettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private final static String TAG = "RemindSettingActivity";
    @BindView(R.id.remindSetting_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.remindSetting_news)
    Switch sw_news;
    @BindView(R.id.remindSetting_sound)
    Switch sw_sound;
    @BindView(R.id.remindSetting_shake)
    Switch sw_shake;
    @BindView(R.id.rl_sound)
    RelativeLayout rl_sound;
    @BindView(R.id.rl_shake)
    RelativeLayout rl_shake;
    private LocalBroadcastManager localBroadcastManager;
    private Intent intent;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    private boolean isSound, isShake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind_setting);
        ButterKnife.bind(this);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intent = new Intent("com.liuhesan.app.NOTIFICATIONSETTING");
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        edit = sharedPreferences.edit();
        initView();
    }

    private void initView() {
        mToolbar.setNavigationIcon(R.mipmap.mine_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        isSound = sharedPreferences.getBoolean("isSound", false);
        isShake = sharedPreferences.getBoolean("isShake", false);
        if (isShake || isShake) {
            sw_news.setChecked(true);
            rl_sound.setVisibility(View.VISIBLE);
            rl_shake.setVisibility(View.VISIBLE);
            sw_sound.setChecked(isSound);
            sw_shake.setChecked(isShake);
        }
        if (!isSound && !isShake) {
            sw_news.setChecked(false);
            rl_sound.setVisibility(View.GONE);
            rl_shake.setVisibility(View.GONE);
            sw_sound.setChecked(isSound);
            sw_shake.setChecked(isShake);
        }
        sw_news.setOnCheckedChangeListener(this);
        sw_sound.setOnCheckedChangeListener(this);
        sw_shake.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.remindSetting_news:
                if (isChecked) {
                    rl_sound.setVisibility(View.VISIBLE);
                    rl_shake.setVisibility(View.VISIBLE);
                    edit.commit();
                } else {
                    rl_sound.setVisibility(View.GONE);
                    rl_shake.setVisibility(View.GONE);
                    edit.putBoolean("isSound", false);
                    edit.putBoolean("isShake", false);
                    edit.commit();
                }
                break;
            case R.id.remindSetting_sound:
                    if (isChecked) {
                        intent.putExtra("isSound", true);
                        localBroadcastManager.sendBroadcast(intent);
                        edit.putBoolean("isSound", true);
                        edit.commit();
                    } else {
                        intent.putExtra("isSound", false);
                        localBroadcastManager.sendBroadcast(intent);
                        edit.putBoolean("isSound", false);
                        edit.commit();
                    }
                break;
            case R.id.remindSetting_shake:
                    if (isChecked) {
                        intent.putExtra("isShake", true);
                        localBroadcastManager.sendBroadcast(intent);
                        edit.putBoolean("isShake", true);
                        edit.commit();
                    } else {
                        intent.putExtra("isShake", false);
                        localBroadcastManager.sendBroadcast(intent);
                        edit.putBoolean("isShake", false);
                        edit.commit();
                    }
                break;
        }
    }
}
