package com.liuhesan.app.businessapp.ui.personcenter;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.utility.ImageUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BusinessLicenseActivity extends AppCompatActivity {

    @BindView(R.id.business_license_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.business_license_photo)
    ImageView businessLicensePhoto;
    @BindView(R.id.keep)
    TextView keep;
    private Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_license);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mToolbar.setNavigationIcon(R.mipmap.mine_back);
        mToolbar.setNavigationOnClickListener(v -> {
            finish();
        });
    }


    @OnClick({R.id.business_license_photo,R.id.keep})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.keep:
                break;
            case R.id.business_license_photo:
                ImageUtils.takeOrChoosePhoto(BusinessLicenseActivity.this,ImageUtils.TAKE_OR_CHOOSE_PHOTO);
                break;
        }
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
                File file = ImageUtils.getPhotoFromResult(BusinessLicenseActivity.this, data);
                Glide.with(BusinessLicenseActivity.this).load(file)
                        .placeholder(R.mipmap.default_personal_image)
                        .error(R.mipmap.default_personal_image)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(businessLicensePhoto);
                break;


        }
    }
}
