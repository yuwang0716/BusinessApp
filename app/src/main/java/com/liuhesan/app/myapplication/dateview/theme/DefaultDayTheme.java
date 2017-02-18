package com.liuhesan.app.myapplication.dateview.theme;

import android.graphics.Color;

/**
 * Created by Administrator on 2016/7/30.
 */
public class DefaultDayTheme implements IDayTheme {
    @Override
    public int colorSelectBG() {
        return Color.parseColor("#23C0AF");
    }

    @Override
    public int colorSelectDay() {
        return Color.parseColor("#FFFFFF");
    }

    @Override
    public int colorToday() {
        return Color.parseColor("#1C1C1C");
    }

    @Override
    public int colorBeforeday() {
        return Color.parseColor("#1C1C1C");
    }

    @Override
    public int colorLaterday() {
        return Color.parseColor("#E5E5E5");
    }

    @Override
    public int colorMonthView() {
        return Color.parseColor("#FFFFFF");
    }

    @Override
    public int colorWeekday() {
        return Color.parseColor("#4F4F4F");
    }

    @Override
    public int colorWeekend() {
        return Color.parseColor("#BEBEBE");
    }

    @Override
    public int colorDecor() {
        return Color.parseColor("#4AB9AE");
    }

    @Override
    public int colorRest() {
        return Color.parseColor("#2AC5C8");
    }

    @Override
    public int colorWork() {
        return Color.parseColor("#C78D7D");
    }

    @Override
    public int colorDesc() {
        return Color.parseColor("#4F4F4F");
    }

    @Override
    public int sizeDay() {
        return 30;
    }

    @Override
    public int sizeDesc() {
        return 14;
    }

    @Override
    public int sizeDecor() {
        return 4;
    }

    @Override
    public int dateHeight() {
        return 60;
    }

    @Override
    public int colorLine() {
        return Color.parseColor("#FFFFFF");
    }

    @Override
    public int smoothMode() {
        return 0;
    }
}
