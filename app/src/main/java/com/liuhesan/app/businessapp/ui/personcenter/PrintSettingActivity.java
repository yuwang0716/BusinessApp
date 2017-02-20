package com.liuhesan.app.businessapp.ui.personcenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.utility.AppManager;
import com.liuhesan.app.businessapp.widget.RelativeLayoutForButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrintSettingActivity extends AppCompatActivity {

    @BindView(R.id.print_setting_back)
    ImageButton back;
    @BindView(R.id.tv_setting_print)
    TextView tvSettingPrint;
    @BindView(R.id.setting_print)
    RelativeLayoutForButton settingPrint;
    @BindView(R.id.setting_print_receipt)
    RelativeLayoutForButton settingPrintReceipt;
    @BindView(R.id.help_print)
    RelativeLayoutForButton helpPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_setting);
        AppManager.getAppManager().addActivity(PrintSettingActivity.this);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.print_setting_back, R.id.setting_print, R.id.setting_print_receipt, R.id.help_print})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.print_setting_back:
                finish();
                break;
            case R.id.setting_print:
                startActivity(new Intent(PrintSettingActivity.this,BluetoothPrintActivity.class));
                break;
            case R.id.setting_print_receipt:
                break;
            case R.id.help_print:
                break;
        }
    }
}
