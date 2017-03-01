package com.liuhesan.app.businessapp.ui.personcenter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.liuhesan.app.businessapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SafeSettingActivity extends AppCompatActivity {

    @BindView(R.id.safe_setting_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.changepwd)
    TextView changepwd;
    @BindView(R.id.certificate_phone)
    TextView certificatePhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_setting);
        ButterKnife.bind(this);
        mToolbar.setNavigationIcon(R.mipmap.mine_back);
        mToolbar.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    @OnClick({R.id.changepwd, R.id.certificate_phone})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.changepwd:

                break;
            case R.id.certificate_phone:
                break;
        }
    }
}
