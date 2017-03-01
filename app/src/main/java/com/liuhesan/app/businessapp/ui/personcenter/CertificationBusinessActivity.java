package com.liuhesan.app.businessapp.ui.personcenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.utility.AppManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CertificationBusinessActivity extends AppCompatActivity {

    @BindView(R.id.certificate_business_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.business_license)
    TextView businessLicense;
    @BindView(R.id.special_certification)
    TextView specialCertification;
    @BindView(R.id.corporate_certification)
    TextView corporateCertification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certification_business);
        ButterKnife.bind(this);
        AppManager.getAppManager().addActivity(CertificationBusinessActivity.this);
        mToolbar.setNavigationIcon(R.mipmap.mine_back);
        mToolbar.setNavigationOnClickListener(v -> {
            finish();
        });
    }

    @OnClick({R.id.business_license, R.id.special_certification, R.id.corporate_certification})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.business_license:
                startActivity(new Intent(CertificationBusinessActivity.this,BusinessLicenseActivity.class));
                break;
            case R.id.special_certification:
                startActivity(new Intent(CertificationBusinessActivity.this,SpecialCertificationActivity.class));
                break;
            case R.id.corporate_certification:
                startActivity(new Intent(CertificationBusinessActivity.this,CorporateCertificationActivity.class));
                break;
        }
    }
}
