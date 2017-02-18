package com.liuhesan.app.myapplication.ui.personcenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.liuhesan.app.myapplication.R;
import com.liuhesan.app.myapplication.utility.AppManager;

/**
 * Created by Tao on 2016/11/24.
 */

public class RunningstateActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runningstate);
        AppManager.getAppManager().addActivity(RunningstateActivity.this);
        initView();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.runningstate_toolbar);
        mToolbar.setNavigationIcon(R.mipmap.mine_back);
        mToolbar.setTitle("营业状态");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
