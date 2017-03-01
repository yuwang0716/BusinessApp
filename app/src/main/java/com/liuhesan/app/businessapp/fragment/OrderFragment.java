package com.liuhesan.app.businessapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.dateview.component.MonthView;
import com.liuhesan.app.businessapp.dateview.views.ADCircleCalendarView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import shanyao.tabpagerindictor.TabPageIndicator;

/**
 * Created by Home on 2016/11/8.
 */

public class OrderFragment extends Fragment{
    private static final String TAG = "OrderFragment";
    private View view;
    private Context mContext;
    private Toolbar mToolbar;
    private TabPageIndicator indicator;
    private ViewPager viewPager;
    private Button date;
    private PopupWindow popupwindow;
    public int currYear,currMonth,currDay;
    private LocalBroadcastManager localBroadcastManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order, container, false);
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        initView();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void initView() {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        date = (Button) view.findViewById(R.id.order_date);
        date.setOnClickListener(new NoDoubleClickListener());
        indicator = (TabPageIndicator) view.findViewById(R.id.indicator);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        BasePagerAdapter adapter = new BasePagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
        setTabPagerIndicator();

        Calendar calendar = Calendar.getInstance();
        currYear = calendar.get(Calendar.YEAR);
        currMonth = calendar.get(Calendar.MONTH)+1;
        if (currMonth >12){
            currMonth = 1;
        }
        currDay = calendar.get(Calendar.DATE);
        date.setText(" "+currDay);
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

    public class NoDoubleClickListener implements View.OnClickListener {
        public static final int MIN_CLICK_DELAY_TIME = 1000;
        private long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                onNoDoubleClick(v);
            }
        }

        private void onNoDoubleClick(View v) {
            if (popupwindow != null&&popupwindow.isShowing()) {
                popupwindow.dismiss();
                return;
            } else {
                initmPopupWindowView();
                popupwindow.showAsDropDown(v, 0, 10);
            }
        }

    }

    private void initmPopupWindowView() {

            View customView = View.inflate(mContext,R.layout.popview_item,null);
            popupwindow = new PopupWindow(customView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            //popupwindow.setAnimationStyle(R.style.AnimationFade);
            popupwindow.setOutsideTouchable(true);
            customView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (popupwindow != null && popupwindow.isShowing()) {
                        popupwindow.dismiss();
                        popupwindow = null;
                    }

                    return false;
                }
            });
        ADCircleCalendarView mAdCircleCalendarView = (ADCircleCalendarView) customView.findViewById(R.id.pop_date);
        mAdCircleCalendarView.setDateClick(new MonthView.IDateClick() {
            @Override
            public void onClickOnDate(int year, int month, int day) {

                if (day>0){
                    if (day <= currDay && month == currMonth && year == currYear || month <currMonth && year == currYear || year <currYear){
                        OrderFragment.this.date.setText(" "+day);
                        Intent intent = new Intent("com.liuhesan.app.date");
                        intent.putExtra("date",year+"-"+month+"-"+day);
                        localBroadcastManager.sendBroadcastSync(intent);
                        if (popupwindow != null && popupwindow.isShowing()) {
                            popupwindow.dismiss();
                            popupwindow = null;
                        }
                    }
                }

            }
        });

    }

    private class BasePagerAdapter extends FragmentPagerAdapter {
        String[] titles;

        public BasePagerAdapter(FragmentManager fm) {
            super(fm);
            this.titles = getResources().getStringArray(R.array.order_titles);
        }



        @Override
        public Fragment getItem(int position) {
            List<Fragment> fragments = new ArrayList<>();

            WaitDistributionFragment waitDistributionFragment = new WaitDistributionFragment();
            DistributionFragment distributionFragment = new DistributionFragment();
            FinishFragment finishFragment = new FinishFragment();
            LoseEfficacyFragment loseEfficacyFragment = new LoseEfficacyFragment();

            fragments.add(waitDistributionFragment);
            fragments.add(distributionFragment);
            fragments.add(finishFragment);
            fragments.add(loseEfficacyFragment);

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
