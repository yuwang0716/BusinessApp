package com.liuhesan.app.businessapp.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Tao on 2016/11/12.
 */

public class FinancedataFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.account)
    Button account;
    @BindView(R.id.transaction)
    Button transaction;
    @BindView(R.id.running_financedata_viewpager)
    NoScrollViewPager mViewpager;
    private View view;
    private List<Fragment> mFragments;
    private FragmentPagerAdapter mAdapter;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_running_financedata, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        initAdapter();
        return view;
    }

    private void initAdapter() {
        mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };
        mViewpager.setAdapter(mAdapter);
    }

    private void initView() {
        mViewpager.setNoScroll(true);

        AccountFragment mAccountFragment = new AccountFragment();
        TransactionFragment mTransactionFragment = new TransactionFragment();
        mFragments = new ArrayList<>();
        mFragments.add(mAccountFragment);
        mFragments.add(mTransactionFragment);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @OnClick({R.id.account, R.id.transaction})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.account:
                mViewpager.setCurrentItem(0);
                account.setBackground(getContext().getDrawable(R.drawable.accountbutton));
                transaction.setBackground(getContext().getDrawable(R.drawable.transactionbutton));
                account.setTextColor(getResources().getColor(R.color.white));
                transaction.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;
            case R.id.transaction:
                mViewpager.setCurrentItem(1);
                account.setBackground(getContext().getDrawable(R.drawable.accountbuttontwo));
                transaction.setBackground(getContext().getDrawable(R.drawable.transactionbuttontwo));
                account.setTextColor(getResources().getColor(R.color.colorPrimary));
                transaction.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
