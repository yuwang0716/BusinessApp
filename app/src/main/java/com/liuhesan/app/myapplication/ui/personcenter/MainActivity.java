package com.liuhesan.app.myapplication.ui.personcenter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.liuhesan.app.myapplication.R;
import com.liuhesan.app.myapplication.bean.Tab;
import com.liuhesan.app.myapplication.fragment.FirstFragment;
import com.liuhesan.app.myapplication.fragment.MineFragment;
import com.liuhesan.app.myapplication.fragment.OrderFragment;
import com.liuhesan.app.myapplication.fragment.RunningFragment;
import com.liuhesan.app.myapplication.utility.AppManager;
import com.liuhesan.app.myapplication.widget.FragmentTabHost;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LayoutInflater mInflater;

    private FragmentTabHost mTabhost;
    private List<Tab> mTabs = new ArrayList<>(4);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppManager.getAppManager().addActivity(MainActivity.this);
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


}
