package com.liuhesan.app.businessapp.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liuhesan.app.businessapp.R;

import java.util.ArrayList;
import java.util.List;

import shanyao.tabpagerindictor.TabPageIndicator;

import static com.liuhesan.app.businessapp.R.id.viewPager;

/**
 * Created by Home on 2016/11/8.
 */

public class RunningFragment extends Fragment{

    private View view;
    private Toolbar mToolbar;
    private TabPageIndicator indicator;
    private ViewPager mViewPager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_running, container, false);
        initView();
        return view;
    }
    private void initView() {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        indicator = (TabPageIndicator) view.findViewById(R.id.indicator);
        mViewPager = (ViewPager) view.findViewById(viewPager);
        BasePagerAdapter adapter = new BasePagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        indicator.setViewPager(mViewPager);
        setTabPagerIndicator();
    }

    private void setTabPagerIndicator() {
        indicator.setIndicatorMode(TabPageIndicator.IndicatorMode.MODE_WEIGHT_NOEXPAND_SAME);// 设置模式，一定要先设置模式
        indicator.setIndicatorColor(Color.parseColor("#23c0af"));// 设置底部导航线的颜色
        indicator.setDividerColor(Color.parseColor("#ffffff"));// 设置分割线的颜色
        indicator.setDividerPadding(10);//设置
        indicator.setTextColorSelected(Color.BLACK);// 设置tab标题选中的颜色
        indicator.setTextColor(Color.parseColor("#979696"));// 设置tab标题未被选中的颜色
        indicator.setTextSize(28);// 设置字体大小
        indicator.setBackgroundColor(Color.parseColor("#ffffff"));

    }

    private class BasePagerAdapter extends FragmentPagerAdapter {
        String[] titles;

        public BasePagerAdapter(FragmentManager fm) {
            super(fm);
            this.titles = getResources().getStringArray(R.array.running_titles);
        }



        @Override
        public Fragment getItem(int position) {
            List<Fragment> fragments = new ArrayList<>();

            OrderdataFragment orderdataFragment = new OrderdataFragment();
            UserdataFragment userdataFragment = new UserdataFragment();
            FinancedataFragment financedataFragment = new FinancedataFragment();

            fragments.add(orderdataFragment);
            fragments.add(userdataFragment);
            fragments.add(financedataFragment);

            FragmentFactory fragmentFactory = new FragmentFactory(fragments);

            return fragmentFactory.createForNoExpand(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
