package com.liuhesan.app.businessapp.ui.personcenter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.liuhesan.app.businessapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SmsManagerActivity extends AppCompatActivity {

    @BindView(R.id.sms_toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_manager);
        ButterKnife.bind(this);
        mToolbar.setNavigationIcon(R.mipmap.mine_back);
        mToolbar.setNavigationOnClickListener(v ->{
            finish();
        });
    }
}
