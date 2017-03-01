package com.liuhesan.app.businessapp.ui.personcenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.utility.AppManager;

/**
 * Created by Tao on 2016/11/24.
 */

public class RunningstateActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    private RadioButton openshop,closeshop;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runningstate);
        AppManager.getAppManager().addActivity(RunningstateActivity.this);
        initView();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.runningstate_toolbar);
        openshop = (RadioButton) findViewById(R.id.openshop);
        closeshop = (RadioButton) findViewById(R.id.closeshop);
        mToolbar.setNavigationIcon(R.mipmap.mine_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       openshop.setOnClickListener(this);
       closeshop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.openshop:
                openshop.setChecked(true);
                closeshop.setChecked(false);

                break;
            case R.id.closeshop:
                    openshop.setChecked(false);
                    closeshop.setChecked(true);
                break;
        }
    }
}
