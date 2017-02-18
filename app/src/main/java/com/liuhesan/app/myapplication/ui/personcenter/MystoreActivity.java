package com.liuhesan.app.myapplication.ui.personcenter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.liuhesan.app.myapplication.R;
import com.liuhesan.app.myapplication.utility.AppManager;
import com.liuhesan.app.myapplication.utility.ImageUtils;
import com.liuhesan.app.myapplication.widget.RelativeLayoutForButton;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MystoreActivity extends AppCompatActivity {
    private final static  String TAG =" MystoreActivity";
    @BindView(R.id.mystore_back)
    ImageButton mystoreBack;
    @BindView(R.id.mystore_head)
    com.liuhesan.app.myapplication.widget.CircleImageView mystoreHead;
    @BindView(R.id.mystore_username)
    TextView tv_mystoreUsername;
    @BindView(R.id.mystore_qualification)
    TextView tv_mystoreQualification;
    @BindView(R.id.mystore_certificationID)
    TextView tv_mystoreCertificationID;
    @BindView(R.id.rl_mystore_name)
    RelativeLayoutForButton rl_mystoreName;
    @BindView(R.id.mystore_name)
    TextView tv_mystore_name;
    @BindView(R.id.mystore_shop_address)
    TextView tv_mystoreShopAddress;
    @BindView(R.id.rl_mystore_shop_phone)
    RelativeLayoutForButton rl_mystoreShopPhone;
    @BindView(R.id.mystore_shop_phone)
    TextView tv_mystore_shop_phone;
    private int certificationID,qualification;
    private String mystoreUsername,mystoreShopAddress,mystore_name,mystore_shop_phone;
    private Bitmap bitmap;
    private Intent intent;
    private LocalBroadcastManager localBroadcastManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mystore);
        localBroadcastManager =LocalBroadcastManager.getInstance(MystoreActivity.this);
        AppManager.getAppManager().addActivity(MystoreActivity.this);
        ButterKnife.bind(this);
        initData();

    }

    private void initData() {
        SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        mystoreUsername = sharedPreferences.getString("username","");
        tv_mystoreUsername.setText(mystoreUsername);
        certificationID = sharedPreferences.getInt("certificationID", 0);
        if (certificationID == 1){
            tv_mystoreCertificationID.setText("\n身份证认证");
        }else {
            tv_mystoreCertificationID.setText("\n身份证未认证");
        }
        qualification = sharedPreferences.getInt("qualification", 0);
        if (qualification == 1){
            tv_mystoreQualification.setText("\n资质认证");
        }else {
            tv_mystoreQualification.setText("\n资质未认证");
        }
       mystore_name = sharedPreferences.getString("shop_name","");
        tv_mystore_name.setText(mystore_name);
        mystoreShopAddress = sharedPreferences.getString("address","");
        tv_mystoreShopAddress.setText(mystoreShopAddress);
        mystore_shop_phone = sharedPreferences.getString("standby_tel","");
        tv_mystore_shop_phone.setText(mystore_shop_phone);
    }

    @OnClick({R.id.mystore_back, R.id.mystore_head, R.id.mystore_name, R.id.mystore_shop_phone})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mystore_back:
                finish();
                break;
            case R.id.mystore_head:
                ImageUtils.takeOrChoosePhoto(MystoreActivity.this, ImageUtils.TAKE_OR_CHOOSE_PHOTO);
                break;
            case R.id.mystore_name:
                showDialog("店名");
                break;
            case R.id.mystore_shop_phone:
                showDialog("订餐电话");
                break;
        }
    }

    private void showDialog(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MystoreActivity.this);
        builder.setTitle(title);
        EditText editText = new EditText(MystoreActivity.this);
        builder.setView(editText,20,10,20,10);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (title.equals("店名")){
                    tv_mystore_name.setText(editText.getText().toString().trim());
                }else {
                    tv_mystore_shop_phone.setText(editText.getText().toString().trim());
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
                mystoreHead .setImageBitmap(bitmap);
                File file = ImageUtils.saveImage(bitmap);
                SharedPreferences sharedPreferences =  getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putString("headportrait",file.toString()).commit();
                intent = new Intent("com.liuhesan.app.HEAD");
                intent.putExtra("headportrait",file.toString());
                localBroadcastManager.sendBroadcast(intent);
                break;
        }
    }
}
