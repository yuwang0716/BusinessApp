package com.liuhesan.app.myapplication.utility;

import android.content.Context;

/**
 * Created by Tao on 2017/2/6.
 */

public class DensityUtil {
    // dip转化为px
    public static int dip2px(Context context, float dipValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    // px转化为dip
    public static int px2dip(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
