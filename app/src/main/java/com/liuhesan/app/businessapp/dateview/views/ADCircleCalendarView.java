package com.liuhesan.app.businessapp.dateview.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liuhesan.app.businessapp.R;
import com.liuhesan.app.businessapp.dateview.component.ADCircleMonthView;
import com.liuhesan.app.businessapp.dateview.component.MonthView;
import com.liuhesan.app.businessapp.dateview.component.WeekView;
import com.liuhesan.app.businessapp.dateview.entity.CalendarInfo;
import com.liuhesan.app.businessapp.dateview.theme.IDayTheme;
import com.liuhesan.app.businessapp.dateview.theme.IWeekTheme;

import java.util.List;

/**
 * Created by Administrator on 2016/8/7.
 */
public class ADCircleCalendarView extends LinearLayout implements View.OnClickListener {
    private WeekView weekView;
    private ADCircleMonthView circleMonthView;
    private TextView textViewYear,textViewMonth;
    public ADCircleCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
        LayoutParams llParams =
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(context).inflate(R.layout.display_grid_date,null);
        weekView = new WeekView(context,null);
        circleMonthView = new ADCircleMonthView(context,null);
        addView(view,llParams);
        addView(weekView,llParams);
        addView(circleMonthView,llParams);

        view.findViewById(R.id.left).setOnClickListener(this);
        view.findViewById(R.id.right).setOnClickListener(this);
        textViewYear = (TextView) view.findViewById(R.id.year);
        textViewMonth = (TextView) view.findViewById(R.id.month);
        textViewYear.setText(circleMonthView.currYear+"年");
        if (circleMonthView.currMonth >12){
            circleMonthView.currMonth = 1;
        }
        textViewMonth.setText(circleMonthView.currMonth+1+"月");
        circleMonthView.setMonthLisener(new MonthView.IMonthLisener() {
            @Override
            public void setTextMonth() {
                textViewYear.setText(circleMonthView.getSelYear()+"年");
                textViewMonth.setText((circleMonthView.getSelMonth() + 1)+"月");
            }
        });
    }

    /**
     * 设置日历点击事件
     * @param dateClick
     */
    public void setDateClick(MonthView.IDateClick dateClick){
        circleMonthView.setDateClick(dateClick);
    }

    /**
     * 设置星期的形式
     * @param weekString
     * 默认值	"日","一","二","三","四","五","六"
     */
    public void setWeekString(String[] weekString){
        weekView.setWeekString(weekString);
    }

    public void setCalendarInfos(List<CalendarInfo> calendarInfos){
        circleMonthView.setCalendarInfos(calendarInfos);
        textViewYear.setText(circleMonthView.getSelYear()+"年");
        textViewMonth.setText((circleMonthView.getSelMonth() + 1)+"月");
    }

    public void setDayTheme(IDayTheme theme){
        circleMonthView.setTheme(theme);
    }

    public void setWeekTheme(IWeekTheme weekTheme){
        weekView.setWeekTheme(weekTheme);
    }

   public void setSelectDate(int year,int month,int day){
        circleMonthView.setSelectDate(year,month,day);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.left){
            circleMonthView.onLeftClick();
        }else{
            circleMonthView.onRightClick();
        }
    }
}
