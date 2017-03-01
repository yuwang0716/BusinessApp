package com.liuhesan.app.businessapp.ui.personcenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.utility.API;
import com.liuhesan.app.businessapp.utility.AppManager;
import com.liuhesan.app.businessapp.utility.ImageUtils;
import com.liuhesan.app.businessapp.widget.CircleImageView;
import com.liuhesan.app.businessapp.widget.RelativeLayoutForButton;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;
import com.yolanda.nohttp.rest.SimpleResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MystoreActivity extends AppCompatActivity {
    private final static String TAG = " MystoreActivity";
    @BindView(R.id.mystore_head)
    CircleImageView mystoreHead;
    @BindView(R.id.mystore_username)
    TextView tv_mystoreUsername;
    @BindView(R.id.mystore_qualification)
    TextView tv_mystoreQualification;
    @BindView(R.id.mystore_certificationID)
    TextView tv_mystoreCertificationID;
    @BindView(R.id.mystore_name)
    TextView tv_mystore_name;
    @BindView(R.id.mystore_shop_address)
    TextView tv_mystoreShopAddress;
    @BindView(R.id.rl_mystore_shop_phone)
    RelativeLayoutForButton rl_mystoreShopPhone;
    @BindView(R.id.mystore_shop_phone)
    TextView tv_mystore_shop_phone;
    @BindView(R.id.mystore_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.certificate_business)
    TextView certificateBusiness;
    private int certificationID, qualification;
    private String mystoreUsername, mystoreShopAddress, mystore_name, mystore_shop_phone;
    private Bitmap bitmap;
    private Intent intent;
    private LocalBroadcastManager localBroadcastManager;
    private String url;
    private RequestQueue requestQueue;
    private Request<String> request_updatePhone, request_updatePhoto;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mystore);
        AppManager.getAppManager().addActivity(MystoreActivity.this);
        ButterKnife.bind(this);
        localBroadcastManager = LocalBroadcastManager.getInstance(MystoreActivity.this);
        requestQueue = NoHttp.newRequestQueue();
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        initData();

    }

    private void initData() {
        mToolbar.setNavigationIcon(R.mipmap.mine_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        mystoreUsername = sharedPreferences.getString("shop_name", "");
        tv_mystoreUsername.setText(mystoreUsername);
        certificationID = sharedPreferences.getInt("certificationID", 0);
        Drawable drawable = getResources().getDrawable(R.mipmap.mine_phone_verification);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        if (certificationID == 1) {
            tv_mystoreCertificationID.setText("\t身份证认证");
            tv_mystoreCertificationID.setCompoundDrawables(drawable, null, null, null);
        } else {
            tv_mystoreCertificationID.setText("\t身份证未认证");
            tv_mystoreCertificationID.setCompoundDrawables(null, null, null, null);
        }
        qualification = sharedPreferences.getInt("qualification", 0);
        if (qualification == 1) {
            tv_mystoreQualification.setText("\t资质认证");
            tv_mystoreQualification.setCompoundDrawables(drawable, null, null, null);
        } else {
            tv_mystoreQualification.setText("\t资质未认证");
            tv_mystoreQualification.setCompoundDrawables(null, null, null, null);
        }
        mystore_name = sharedPreferences.getString("shop_name", "");
        tv_mystore_name.setText(mystore_name);
        mystoreShopAddress = sharedPreferences.getString("address", "");
        tv_mystoreShopAddress.setText(mystoreShopAddress);
        mystore_shop_phone = sharedPreferences.getString("phone", "");
        tv_mystore_shop_phone.setText(mystore_shop_phone);
        url = sharedPreferences.getString("headportrait", "");
        Glide.with(MystoreActivity.this).load(url)
                .error(R.mipmap.default_personal_image)
                .placeholder(R.mipmap.default_personal_image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        mystoreHead.setImageDrawable(resource);
                    }
                });
    }

    @OnClick({R.id.mystore_head, R.id.rl_mystore_shop_phone,R.id.certificate_business})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mystore_head:
                ImageUtils.takeOrChoosePhoto(MystoreActivity.this, ImageUtils.TAKE_OR_CHOOSE_PHOTO);
                break;
            case R.id.rl_mystore_shop_phone:
                showDialog("订餐电话");
                break;
            case R.id.certificate_business:
                startActivity(new Intent(MystoreActivity.this,CertificationBusinessActivity.class));
                break;
        }
    }

    private void showDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MystoreActivity.this);
        builder.setTitle(title);
        EditText editText = new EditText(MystoreActivity.this);
        builder.setView(editText, 20, 10, 20, 10);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (title.equals("店名")) {
                    tv_mystore_name.setText(editText.getText().toString().trim());
                } else {
                    request_updatePhone = NoHttp.createStringRequest(API.url_system_phone, RequestMethod.POST);
                    request_updatePhone.add("token", sharedPreferences.getString("token", ""));
                    request_updatePhone.add("phone", editText.getText().toString().trim());
                    requestQueue.add(0, request_updatePhone, new SimpleResponseListener<String>() {
                        @Override
                        public void onSucceed(int what, Response<String> response) {
                            super.onSucceed(what, response);
                            try {
                                JSONObject jsonObject = new JSONObject(response.get());
                                int errno = jsonObject.optInt("errno");
                                if (errno == 200) {
                                    tv_mystore_shop_phone.setText(editText.getText().toString().trim());
                                    SharedPreferences.Editor edit = sharedPreferences.edit();
                                    edit.putString("phone", editText.getText().toString().trim()).commit();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.e(TAG, response.get() + "onSucceed: ");
                        }
                    });
                }
            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ImageUtils.TAKE_OR_CHOOSE_PHOTO:

                //获取到了原始图片
                File f = ImageUtils.getPhotoFromResult(MystoreActivity.this, data);
                //裁剪方法
                ImageUtils.doCropPhoto(MystoreActivity.this, f);
                break;
            case ImageUtils.PHOTO_PICKED_WITH_DATA:
                //获取到剪裁后的图片
                bitmap = ImageUtils.getCroppedImage();
                File file = ImageUtils.saveImage(bitmap);
                request_updatePhoto = NoHttp.createStringRequest(API.url_system_photo, RequestMethod.POST);
                request_updatePhoto.add("token", sharedPreferences.getString("token", ""));
                request_updatePhoto.add("head", file);
                requestQueue.add(1, request_updatePhoto, new SimpleResponseListener<String>() {
                    @Override
                    public void onSucceed(int what, Response<String> response) {
                        super.onSucceed(what, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response.get());
                            int errno = jsonObject.optInt("errno");
                            if (errno == 200) {
                                SharedPreferences.Editor edit = sharedPreferences.edit();
                                edit.putString("headportrait", file.getAbsolutePath()).commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Glide.with(MystoreActivity.this).load(file)
                        .placeholder(R.mipmap.default_personal_image)
                        .error(R.mipmap.default_personal_image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(new SimpleTarget<GlideDrawable>() {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                mystoreHead.setImageBitmap(bitmap);
                            }
                        });
                SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("headportrait", file.toString()).commit();
                intent = new Intent("com.liuhesan.app.HEAD");
                intent.putExtra("headportrait", file.toString());
                localBroadcastManager.sendBroadcast(intent);
                break;
        }
    }
}
