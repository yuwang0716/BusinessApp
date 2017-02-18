package com.liuhesan.app.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liuhesan.app.myapplication.R;
import com.liuhesan.app.myapplication.ui.personcenter.MystoreActivity;
import com.liuhesan.app.myapplication.ui.personcenter.PrintSettingActivity;
import com.liuhesan.app.myapplication.ui.personcenter.RunningstateActivity;
import com.liuhesan.app.myapplication.widget.LinearLayoutForButton;

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
    private View view;
    private Intent intent;
    private Unbinder unbinder;
    private Context mContext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mine, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @OnClick({R.id.mine_mystore, R.id.mine_state, R.id.mine_time, R.id.mine_inventory,
            R.id.mine_sms, R.id.mine_logistics, R.id.mine_ordersetting, R.id.mine_remindersetting,
            R.id.mine_print, R.id.mine_limit, R.id.mine_safety})
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
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
