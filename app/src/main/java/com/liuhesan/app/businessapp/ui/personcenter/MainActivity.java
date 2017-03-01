package com.liuhesan.app.businessapp.ui.personcenter;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.bean.Tab;
import com.liuhesan.app.businessapp.fragment.FirstFragment;
import com.liuhesan.app.businessapp.fragment.MineFragment;
import com.liuhesan.app.businessapp.fragment.OrderFragment;
import com.liuhesan.app.businessapp.fragment.RunningFragment;
import com.liuhesan.app.businessapp.utility.AppManager;
import com.liuhesan.app.businessapp.widget.FragmentTabHost;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LayoutInflater mInflater;
    private long mExitTime;
    private FragmentTabHost mTabhost;
    private List<Tab> mTabs = new ArrayList<>(4);
    private String neworder = "0";
    private LocalBroadcastManager localBroadcastManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppManager.getAppManager().addActivity(MainActivity.this);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        initTab();

    }
    private void initTab() {


        Tab tab_first = new Tab(FirstFragment.class,R.string.first,R.drawable.selector_icon_first);
        Tab tab_order = new Tab(OrderFragment.class,R.string.order,R.drawable.selector_icon_order);
        Tab tab_running = new Tab(RunningFragment.class,R.string.running,R.drawable.selector_icon_running);
        Tab tab_mine = new Tab(MineFragment.class,R.string.mine,R.drawable.selector_icon_mine);

        mTabs.add(tab_first);
        mTabs.add(tab_order);
        mTabs.add(tab_running);
        mTabs.add(tab_mine);



        mInflater = LayoutInflater.from(this);
        mTabhost = (FragmentTabHost) this.findViewById(android.R.id.tabhost);
        mTabhost.setup(this,getSupportFragmentManager(),R.id.realtabcontent);

        for (Tab tab : mTabs){
            TabHost.TabSpec tabSpec = mTabhost.newTabSpec(getString(tab.getTitle()));

            tabSpec.setIndicator(buildIndicator(tab));

            mTabhost.addTab(tabSpec,tab.getFragment(),null);

        }

        mTabhost.getTabWidget().setShowDividers(LinearLayout.SHOW_DIVIDER_NONE);
        mTabhost.setCurrentTab(0);


    }


    private View buildIndicator(Tab tab){

        View view =mInflater.inflate(R.layout.tab_indicator,null);
        ImageView img = (ImageView) view.findViewById(R.id.icon_tab);
        TextView text = (TextView) view.findViewById(R.id.txt_indicator);

        img.setBackgroundResource(tab.getIcon());
        text.setText(tab.getTitle());

        return  view;
    }
    //两次返回键退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){
            if ((System.currentTimeMillis() - mExitTime) > 2000){
                Toast.makeText(MainActivity.this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            }else {
                finish();
            }
            return  true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        neworder = intent.getStringExtra("neworder");
        if (!TextUtils.isEmpty(neworder)){
            if (neworder.equals("neworder")){
                mTabhost.setCurrentTab(0);
                Intent intent_news = new Intent("com.liuhesan.app.NEWORDER");
                intent_news.putExtra("neworder", "neworder");
                localBroadcastManager.sendBroadcast(intent_news);
            }
        }
    }
}
