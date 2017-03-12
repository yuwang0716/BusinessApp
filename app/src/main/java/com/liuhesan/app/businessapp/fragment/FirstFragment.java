package com.liuhesan.app.businessapp.fragment;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.ui.personcenter.LoginActivity;
import com.liuhesan.app.businessapp.ui.personcenter.LoginBaiduActivity;
import com.liuhesan.app.businessapp.ui.personcenter.LoginThirdActivity;
import com.liuhesan.app.businessapp.utility.API;
import com.liuhesan.app.businessapp.utility.AppManager;
import com.liuhesan.app.businessapp.widget.CircleImageView;
import com.liuhesan.app.businessapp.widget.NoScrollViewPager;
import com.liuhesan.app.businessapp.widget.RelativeLayoutForButton;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.SimpleResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Home on 2016/11/8.
 */

public class FirstFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "FirstFragment";
    @BindView(R.id.neworder)
    Button neworder;
    @BindView(R.id.remind)
    Button remind;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.first_viewpager)
    NoScrollViewPager mViewpager;
    @BindView(R.id.drawer_toolbar)
    Toolbar drawerToolbar;
    @BindView(R.id.my_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.first_baidu_open)
    ImageButton firstBaiduOpen;
    @BindView(R.id.first_baiduclose)
    ImageButton firstBaiduclose;
    @BindView(R.id.first_meituan_open)
    ImageButton firstMeituanOpen;
    @BindView(R.id.first_meituan_close)
    ImageButton firstMeituanClose;
    @BindView(R.id.first_eleme_open)
    ImageButton firstElemeOpen;
    @BindView(R.id.first_eleme_close)
    ImageButton firstElemeClose;
    @BindView(R.id.first_weidian_open)
    ImageButton firstWeidianOpen;
    @BindView(R.id.first_weidian_close)
    ImageButton firstWeidianClose;
    @BindView(R.id.door_baidu)
    RelativeLayoutForButton doorBaidu;
    @BindView(R.id.door_meituan)
    RelativeLayoutForButton doorMeituan;
    @BindView(R.id.door_eleme)
    RelativeLayoutForButton doorEleme;
    @BindView(R.id.door_weidian)
    RelativeLayoutForButton doorWeidian;
    @BindView(R.id.shopstatus)
    RadioGroup shopstatus;
    @BindView(R.id.exit_login)
    ImageButton exit_login;
    @BindView(R.id.shopName)
    TextView shopName;
    @BindView(R.id.closeshop)
    RadioButton closeshop;
    @BindView(R.id.first_headphoto)
    CircleImageView firstHeadphoto;
    private View view;
    private List<Fragment> mFragments;
    private FragmentPagerAdapter mAdapter;
    private Unbinder unbinder;
    private boolean isSuccsess_meituan = false;
    private boolean tag_weidian = false;
    private boolean isSuccsess_baidu = false;
    private boolean isSuccsess_elem = false;
    private SharedPreferences sharedPreferences;
    private String token;
    private RequestQueue requestQueue;
    private Request<String> request_shop;
    private Context mContext;
    private SharedPreferences sharedPreferences_baidu;
    private String cookie_baidu;
    private SharedPreferences sharedPreferences_meit;
    private String cookie_meit;
    private SharedPreferences sharedPreferences_elem;
    private String uuid;
    private String ksid;
    private String shopId;
    private String wmPoiId;
    private String token_meit;
    private String url;
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;
    private HeadPhotoReceive headPhotoReceive;
    private Request<String> request_login;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_first, container, false);
        unbinder = ButterKnife.bind(this, view);
        requestQueue = NoHttp.newRequestQueue();
        request_shop = NoHttp.createStringRequest(API.url_system_shopstatus, RequestMethod.POST);

        //获取头像
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.liuhesan.app.HEAD");
        headPhotoReceive = new HeadPhotoReceive();
        localBroadcastManager.registerReceiver(headPhotoReceive, intentFilter);
        getCookie();
        initView();
        initAdapter();
        return view;
    }

    private void getCookie() {
        //系统登录凭证
        sharedPreferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        //百度登录凭证
        sharedPreferences_baidu = mContext.getSharedPreferences("baiducookie", Context.MODE_PRIVATE);
        cookie_baidu = sharedPreferences.getString("cookie", "");
        //美团登录凭证
        sharedPreferences_meit = mContext.getSharedPreferences("meituancookie", Context.MODE_PRIVATE);
        cookie_meit = sharedPreferences.getString("cookie", "");
        if (!TextUtils.isEmpty(cookie_meit)) {
            wmPoiId = cookie_meit.substring(cookie_meit.indexOf("wmPoiId=") + 1, cookie_meit.indexOf(";"));
            token_meit = cookie_meit.substring(cookie_meit.indexOf("token=") + 1, cookie_meit.indexOf(";"));
        }
        //饿了么登录凭证
        sharedPreferences_elem = mContext.getSharedPreferences("elemecookie", Context.MODE_PRIVATE);
        uuid = sharedPreferences_elem.getString("uuid", "");
        ksid = sharedPreferences_elem.getString("ksid", "");
        shopId = sharedPreferences_elem.getString("shopId", "");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    //初始适配器
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

    //初始化控件
    private void initView() {

        //店铺状态一键开关三方平台
       /* shopstatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                request_shop.add("token", token);

                if (checkedId == R.id.openshop ){
                    Request<String> request_baidu_shopstatus = NoHttp.createStringRequest(API.url_baidu_shopstatus+1, RequestMethod.GET);
                    request_baidu_shopstatus.addHeader("Cookie",cookie_baidu);
                    requestQueue.add(10, request_baidu_shopstatus, new SimpleResponseListener<String>() {
                        @Override
                        public void onSucceed(int what, Response<String> response) {
                            super.onSucceed(what, response);
                            Log.e(TAG, "百度店铺开启\n"+response.get()+"onSucceed: ");
                        }
                    });
                    Request<String> request_meit_shopstatus = NoHttp.createStringRequest(API.url_meituan_shopstatus, RequestMethod.POST);
                    request_meit_shopstatus.addHeader("Cookie",cookie_meit);
                    request_meit_shopstatus.add("wmPoiId",wmPoiId);
                    request_meit_shopstatus.add("token",token_meit);
                    request_meit_shopstatus.add("status",1);
                    request_meit_shopstatus.add("csrfToken","");
                    requestQueue.add(11, request_baidu_shopstatus, new SimpleResponseListener<String>() {
                        @Override
                        public void onSucceed(int what, Response<String> response) {
                            super.onSucceed(what, response);
                            Log.e(TAG, "美团店铺开启\n"+response.get()+"onSucceed: ");
                        }
                    });
                    String json ="{\"id\":\"" + uuid + "\",\"method\":\"openShop\",\"service\":" +
                            "\"setShop\",\"params\":{\"shopId\":\"" + shopId + "\"},\"metas\":{\"appName\":\"melody\"," +
                            "\"appVersion\":\"4.4.1\",\"ksid\":\"" + ksid + "\"},\"ncp\":\"2.0.0\"}";
                    Request<String> request_elem_shopstatus = NoHttp.createStringRequest(API.url_eleme_shopstatus+"setShop.openShop", RequestMethod.POST);
                    request_elem_shopstatus.setDefineRequestBodyForJson(json);
                    requestQueue.add(12, request_elem_shopstatus, new SimpleResponseListener<String>() {
                        @Override
                        public void onSucceed(int what, Response<String> response) {
                            super.onSucceed(what, response);
                            Log.e(TAG, "饿了么店铺开启\n"+response.get()+"onSucceed: ");
                        }
                    });

                    request_shop.add("status", "open");
                }else {
                    Request<String> request_baidu_shopstatus = NoHttp.createStringRequest(API.url_baidu_shopstatus+2, RequestMethod.GET);
                    requestQueue.add(10, request_baidu_shopstatus, new SimpleResponseListener<String>() {
                        @Override
                        public void onSucceed(int what, Response<String> response) {
                            super.onSucceed(what, response);
                            Log.e(TAG, "百度店铺关闭\n"+response.get()+"onSucceed: ");
                        }
                    });

                    Request<String> request_meit_shopstatus = NoHttp.createStringRequest(API.url_meituan_shopstatus, RequestMethod.POST);
                    request_meit_shopstatus.addHeader("Cookie",cookie_meit);
                    request_meit_shopstatus.add("wmPoiId",wmPoiId);
                    request_meit_shopstatus.add("token",token_meit);
                    request_meit_shopstatus.add("status",3);
                    request_meit_shopstatus.add("csrfToken","");
                    requestQueue.add(11, request_meit_shopstatus, new SimpleResponseListener<String>() {
                        @Override
                        public void onSucceed(int what, Response<String> response) {
                            super.onSucceed(what, response);
                            Log.e(TAG, "美团店铺关闭\n"+response.get()+"onSucceed: ");
                        }
                    });
                    String json ="{\"id\":\"" + uuid + "\",\"method\":\"closeShop\",\"service\":" +
                            "\"setShop\",\"params\":{\"shopId\":\"" + shopId + "\"},\"metas\":{\"appName\":\"melody\"," +
                            "\"appVersion\":\"4.4.1\",\"ksid\":\"" + ksid + "\"},\"ncp\":\"2.0.0\"}";
                    Request<String> request_elem_shopstatus = NoHttp.createStringRequest(API.url_eleme_shopstatus+"setShop.closeShop", RequestMethod.POST);
                    request_elem_shopstatus.setDefineRequestBodyForJson(json);
                    requestQueue.add(12, request_elem_shopstatus, new SimpleResponseListener<String>() {
                        @Override
                        public void onSucceed(int what, Response<String> response) {
                            super.onSucceed(what, response);
                            Log.e(TAG, "饿了么店铺关闭\n"+response.get()+"onSucceed: ");
                        }
                    });
                    request_shop.add("status", "close");
                }
                requestQueue.add(1, request_shop, new SimpleResponseListener<String>() {
                    @Override
                    public void onSucceed(int what, Response<String> response) {
                        super.onSucceed(what, response);
                        Log.e(TAG, response.get() + "onSucceed: ");
                    }
                });
            }
        });*/
        // 在这里我们获取了主题暗色，并设置了status bar的颜色
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        int color = typedValue.data;

        // 注意setStatusBarBackgroundColor方法需要你将fitsSystemWindows设置为true才会生效
        drawerLayout.setStatusBarBackgroundColor(color);

        //设置第三方开始登录状态
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //判断第三方是否登陆
                getLogin("baidu");
                getLogin("meit");
                getLogin("elem");
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        //主页toolbar
        mToolbar.setNavigationIcon(R.mipmap.first_menu);
        mToolbar.inflateMenu(R.menu.first_menu);
        mToolbar.setNavigationOnClickListener(v -> {
            drawerLayout.openDrawer(Gravity.LEFT);
        });
        mToolbar.setOnMenuItemClickListener(item -> {
            Toast.makeText(getContext(), "搜索", Toast.LENGTH_SHORT).show();
            return true;
        });
        //DrawerLayout中toolbar
        url = sharedPreferences.getString("headportrait", "");
        Log.e(TAG, url + "onResourceReady: ");
        Glide.with(FirstFragment.this).load(url)
                .placeholder(R.mipmap.default_personal_image)
                .error(R.mipmap.default_personal_image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        firstHeadphoto.setImageDrawable(resource);
                    }
                });
        shopName.setText(sharedPreferences.getString("shop_name", ""));
        mViewpager.setNoScroll(true);

        NeworderFragment mNeworderFragment = new NeworderFragment();
        RemindFragment mRemindFragment = new RemindFragment();
        mFragments = new ArrayList<>();
        mFragments.add(mNeworderFragment);
        mFragments.add(mRemindFragment);
    }

    //PC端是否登陆
    private void getLogin(String name) {

        request_login = NoHttp.createStringRequest(API.url_login_wm, RequestMethod.POST);
        request_login.add("platform",name);
        request_login.add("token",token);
        requestQueue.add(1, request_login, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                super.onSucceed(what, response);
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    int errno = jsonObject.optInt("errno");
                    if (errno == 200) {
                        Log.e(TAG, name+"\n"+response.get() );
                        JSONObject data = jsonObject.optJSONObject("data");
                        JSONObject info = data.optJSONObject("info");
                        sharedPreferences = getContext().getSharedPreferences(name + "cookie", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (info != null) {
                            String cookies = info.optString("cookies");
                            if ("baidu".equals(name)) {
                                firstBaiduclose.setVisibility(View.GONE);
                                firstBaiduOpen.setVisibility(View.VISIBLE);
                                isSuccsess_baidu = true;
                                editor.putString("cookie", cookies);
                            }
                            if ("meit".equals(name)) {
                                firstMeituanClose.setVisibility(View.GONE);
                                firstMeituanOpen.setVisibility(View.VISIBLE);
                                isSuccsess_meituan = true;
                                editor.putString("cookie", cookies);
                            }
                            if ("elem".equals(name)) {
                                firstElemeClose.setVisibility(View.GONE);
                                firstElemeOpen.setVisibility(View.VISIBLE);
                                isSuccsess_elem = true;
                                editor.putString("uuid", cookies.split("=")[1].split(";")[0]);
                                editor.putString("ksid", cookies.split("=")[2].split(";")[0]);
                                editor.putString("shopId", cookies.split("=")[4].split(";")[0]);
                                editor.commit();
                            }
                        }else {
                            if ("baidu".equals(name)) {
                                firstBaiduclose.setVisibility(View.VISIBLE);
                                firstBaiduOpen.setVisibility(View.GONE);
                                isSuccsess_baidu = false;

                            }else if ("meit".equals(name)){
                                firstMeituanClose.setVisibility(View.VISIBLE);
                                firstMeituanOpen.setVisibility(View.GONE);
                                isSuccsess_meituan = false;
                                editor.clear().commit();
                            }else {
                                firstElemeClose.setVisibility(View.VISIBLE);
                                firstElemeOpen.setVisibility(View.GONE);
                                isSuccsess_elem = false;
                            }
                            editor.clear().commit();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick({R.id.neworder, R.id.remind, R.id.door_baidu, R.id.door_meituan, R.id.door_eleme, R.id.door_weidian, R.id.exit_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.neworder:
                mViewpager.setCurrentItem(0);
                neworder.setBackground(mContext.getDrawable(R.drawable.neworderbutton));
                remind.setBackground(mContext.getDrawable(R.drawable.remindbutton));

                neworder.setTextColor(getResources().getColor(R.color.colorPrimary));
                remind.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.remind:
                mViewpager.setCurrentItem(1);
                neworder.setBackground(mContext.getDrawable(R.drawable.neworderbuttontwo));
                remind.setBackground(mContext.getDrawable(R.drawable.remindbuttontwo));

                neworder.setTextColor(getResources().getColor(R.color.white));
                remind.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;

            //百度外卖登录
            case R.id.door_baidu:
                if (isSuccsess_baidu) {
                    firstBaiduOpen.setVisibility(View.GONE);
                    firstBaiduclose.setVisibility(View.VISIBLE);
                    isSuccsess_baidu = false;
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("baiducookie", Context.MODE_APPEND);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.commit();
                } else {
                    Intent intent = new Intent(mContext, LoginBaiduActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("title", "baidu");
                    intent.putExtra("title", bundle);
                    startActivityForResult(intent, 10);
                }
                break;

            //美团外卖登录
            case R.id.door_meituan:
                if (isSuccsess_meituan) {
                    firstMeituanOpen.setVisibility(View.GONE);
                    firstMeituanClose.setVisibility(View.VISIBLE);
                    isSuccsess_meituan = false;
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("meitcookie", Context.MODE_APPEND);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.commit();
                } else {
                    Intent intent = new Intent(getActivity(), LoginThirdActivity.class);
                    intent.putExtra("title", "登录美团外卖");
                    startActivityForResult(intent, 20);
                }
                break;

            //饿了么外卖登录
            case R.id.door_eleme:
                if (isSuccsess_elem) {
                    firstElemeOpen.setVisibility(View.GONE);
                    firstElemeClose.setVisibility(View.VISIBLE);
                    isSuccsess_elem = false;
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("elemcookie", Context.MODE_APPEND);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.commit();
                } else {
                    Intent intent = new Intent(mContext, LoginThirdActivity.class);
                    intent.putExtra("title", "登录饿了么外卖");
                    startActivityForResult(intent, 30);
                }
                break;
            case R.id.door_weidian:
                if (tag_weidian) {
                    firstWeidianOpen.setVisibility(View.GONE);
                    firstWeidianClose.setVisibility(View.VISIBLE);
                    tag_weidian = false;
                } else {
                    firstWeidianClose.setVisibility(View.GONE);
                    firstWeidianOpen.setVisibility(View.VISIBLE);
                    tag_weidian = true;
                }
                break;
            case R.id.exit_login:
                sharedPreferences.edit().clear().commit();
                sharedPreferences_baidu.edit().clear().commit();
                sharedPreferences_meit.edit().clear().commit();
                sharedPreferences_elem.edit().clear().commit();
                startActivity(new Intent(mContext, LoginActivity.class));
                AppManager.getAppManager().finishAllActivity();
                NotificationManager manger = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
                manger.cancelAll();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        localBroadcastManager.unregisterReceiver(headPhotoReceive);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            //百度外卖返回
            if (requestCode == 10 && resultCode == 11) {
                isSuccsess_baidu = data.getBooleanExtra("isSuccsess", false);
                if (isSuccsess_baidu) {
                    firstBaiduclose.setVisibility(View.GONE);
                    firstBaiduOpen.setVisibility(View.VISIBLE);
                    commitThird(data, "baidu");
                }
            }
            //美团外卖返回
            if (requestCode == 20 && resultCode == 21) {
                isSuccsess_meituan = data.getBooleanExtra("isSuccsess_meituan", false);
                if (isSuccsess_meituan) {
                    firstMeituanClose.setVisibility(View.GONE);
                    firstMeituanOpen.setVisibility(View.VISIBLE);
                    commitThird(data,  "meit");
                }
            }
            //饿了么外卖返回
            if (requestCode == 30 && resultCode == 31) {
                isSuccsess_elem = data.getBooleanExtra("isSuccsess_elem", false);
                if (isSuccsess_elem) {
                    firstElemeClose.setVisibility(View.GONE);
                    firstElemeOpen.setVisibility(View.VISIBLE);
                    commitThird(data, "elem");
                }
            }
        }
    }

    private void commitThird(Intent data, String platform) {
        sharedPreferences = mContext.getSharedPreferences(platform + "cookie", Context.MODE_APPEND);
        String cookie = sharedPreferences.getString("cookie", "");
        Request<String> request_wmCommit = NoHttp.createStringRequest(API.url_system_wmCommit, RequestMethod.POST);
        request_wmCommit.add("token", token);
        request_wmCommit.add("platform", platform);
        request_wmCommit.add("username", data.getStringExtra("name"));
        request_wmCommit.add("password", data.getStringExtra("pwd"));
        request_wmCommit.add("cookies", cookie);
        requestQueue.add(2, request_wmCommit, new SimpleResponseListener<String>() {
            @Override
            public void onSucceed(int what, Response<String> response) {
                super.onSucceed(what, response);
                Log.i("TAGcommitThird", response.get() + "onNext:\n "+cookie);
            }
        });
    }

    class NotificationReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String notification = intent.getStringExtra("notification");
            if (!TextUtils.isEmpty(notification)) {
                if (notification.equals("notification")) {
                    mViewpager.setCurrentItem(0);
                }
            }
        }
    }
    class HeadPhotoReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            url = intent.getStringExtra("headportrait");
            Glide.with(FirstFragment.this).load(url)
                    .placeholder(R.mipmap.default_personal_image)
                    .error(R.mipmap.default_personal_image)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            firstHeadphoto.setImageDrawable(resource);
                        }
                    });

        }
    }
}
