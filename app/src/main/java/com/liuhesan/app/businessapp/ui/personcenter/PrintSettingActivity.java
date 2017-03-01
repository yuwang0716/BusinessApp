package com.liuhesan.app.businessapp.ui.personcenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.service.PrintDataService;
import com.liuhesan.app.businessapp.utility.AppManager;
import com.liuhesan.app.businessapp.widget.RelativeLayoutForButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrintSettingActivity extends AppCompatActivity {

    @BindView(R.id.tv_setting_print)
    TextView tvSettingPrint;
    @BindView(R.id.setting_print)
    RelativeLayoutForButton settingPrint;
    @BindView(R.id.setting_print_receipt)
    RelativeLayoutForButton settingPrintReceipt;
    @BindView(R.id.help_print)
    RelativeLayoutForButton helpPrint;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.print)
    Button print;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_setting);
        AppManager.getAppManager().addActivity(PrintSettingActivity.this);
        ButterKnife.bind(this);
        mToolbar.setNavigationIcon(R.mipmap.mine_back);
        mToolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        boolean isConnection = sharedPreferences.getBoolean("isConnection", false);
        if (isConnection) {
            tvSettingPrint.setText("已连接");
        } else {
            tvSettingPrint.setText("未连接");
        }
    }

    @OnClick({R.id.setting_print, R.id.setting_print_receipt, R.id.help_print,R.id.print})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_print:
                startActivity(new Intent(PrintSettingActivity.this, BluetoothPrintActivity.class));
                break;
            case R.id.setting_print_receipt:
                break;
            case R.id.help_print:
                break;
            case R.id.print:
                SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                String deviceAddress = sharedPreferences.getString("deviceAddress", null);
                boolean isConnection = sharedPreferences.getBoolean("isConnection", false);
                if (!TextUtils.isEmpty(deviceAddress)) {
                    PrintDataService printDataService = new PrintDataService(PrintSettingActivity.this, deviceAddress);
                    printDataService.connect();
                    PrintDataService.selectCommand(PrintDataService.RESET);
                    PrintDataService.selectCommand(PrintDataService.LINE_SPACING_DEFAULT);
                    PrintDataService.selectCommand(PrintDataService.DOUBLE_HEIGHT_WIDTH);
                    PrintDataService.selectCommand(PrintDataService.BOLD);
                    PrintDataService.selectCommand(PrintDataService.ALIGN_CENTER);
                    printDataService.send(isConnection, "\n\n\n商家小票\n");
                    PrintDataService.selectCommand(PrintDataService.BOLD_CANCEL);
                    PrintDataService.selectCommand(PrintDataService.DOUBLE_HEIGHT);
                    printDataService.send(isConnection, "门店名称\n");
                    PrintDataService.selectCommand(PrintDataService.NORMAL);
                    PrintDataService.selectCommand(PrintDataService.ALIGN_LEFT);
                    printDataService.send(isConnection, "下单时间：" + "2016-11-07 09：00\n");
                    printDataService.send(isConnection, "--------------------------------\n");
                    PrintDataService.selectCommand(PrintDataService.BOLD);
                    printDataService.send(isConnection, "今日  ");
                    PrintDataService.selectCommand(PrintDataService.DOUBLE_HEIGHT_WIDTH);
                    PrintDataService.selectCommand(PrintDataService.BOLD_CANCEL);
                    printDataService.send(isConnection, "#1");
                    PrintDataService.selectCommand(PrintDataService.NORMAL);
                    PrintDataService.selectCommand(PrintDataService.BOLD);
                    printDataService.send(isConnection,  "  编号");
                    PrintDataService.selectCommand(PrintDataService.DOUBLE_HEIGHT_WIDTH);
                    PrintDataService.selectCommand(PrintDataService.BOLD_CANCEL);
                    printDataService.send(isConnection, "#1\n");
                    PrintDataService.selectCommand(PrintDataService.NORMAL);
                    PrintDataService.selectCommand(PrintDataService.ALIGN_LEFT);
                    PrintDataService.selectCommand(PrintDataService.BOLD_CANCEL);
                    printDataService.send(isConnection, "*********");
                    PrintDataService.selectCommand(PrintDataService.ALIGN_CENTER);
                    printDataService.send(isConnection, "味点口袋");
                    PrintDataService.selectCommand(PrintDataService.ALIGN_RIGHT);
                    printDataService.send(isConnection,  "*********\n");
                    printDataService.send(isConnection, PrintDataService.printThreeData("商品", "数量", "实付\n"));
                    PrintDataService.selectCommand(PrintDataService.DOUBLE_HEIGHT);
                    printDataService.send(isConnection, PrintDataService.printThreeData("榴莲酥", "2", "36.00\n"));
                    PrintDataService.selectCommand(PrintDataService.NORMAL);
                    PrintDataService.selectCommand(PrintDataService.ALIGN_LEFT);
                    printDataService.send(isConnection, "*********");
                    PrintDataService.selectCommand(PrintDataService.ALIGN_CENTER);
                    printDataService.send(isConnection, "其它");
                    PrintDataService.selectCommand(PrintDataService.ALIGN_RIGHT);
                    printDataService.send(isConnection,  "*********\n");
                    PrintDataService.selectCommand(PrintDataService.BOLD);
                    printDataService.send(isConnection, PrintDataService.printTwoData("配送费","10.00\n" ));
                    printDataService.send(isConnection, PrintDataService.printTwoData("餐盒费","2.00\n" ));
                    PrintDataService.selectCommand(PrintDataService.DOUBLE_WIDTH);
                    PrintDataService.selectCommand(PrintDataService.BOLD_CANCEL);
                    printDataService.send(isConnection,"在线支付");
                    PrintDataService.selectCommand(PrintDataService.DOUBLE_HEIGHT_WIDTH);
                    PrintDataService.selectCommand(PrintDataService.BOLD_CANCEL);
                    printDataService.send(isConnection,"48.00\n");
                    PrintDataService.selectCommand(PrintDataService.NORMAL);
                    PrintDataService.selectCommand(PrintDataService.ALIGN_LEFT);
                    printDataService.send(isConnection, "--------------------------------\n");
                    PrintDataService.selectCommand(PrintDataService.DOUBLE_HEIGHT_WIDTH);
                    printDataService.send(isConnection,"客户地址\n");
                    printDataService.send(isConnection,"12345678909\n味点测试商户");
                    PrintDataService.selectCommand(PrintDataService.NORMAL);
                    printDataService.send(isConnection,"（新客户)\n\n");
                }
                break;
        }
    }

}
