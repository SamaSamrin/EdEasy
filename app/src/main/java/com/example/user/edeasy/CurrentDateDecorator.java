package com.example.user.edeasy;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

/**
 * Created by ASUS on 05-Aug-17.
 */

class CurrentDateDecorator implements DayViewDecorator {

    private Drawable highlightDrawable;
    private Context context;

    public CurrentDateDecorator( Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT>20)
            highlightDrawable = this.context.getDrawable(R.drawable.circlebackground);
        else
            highlightDrawable = ResourcesCompat.getDrawable(context.getResources(),
                    R.drawable.circlebackground, context.getTheme());
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {

        return day.equals(CalendarDay.today());
    }

    @Override
    public void decorate(DayViewFacade view) {

        view.setBackgroundDrawable(highlightDrawable);
        view.addSpan(new ForegroundColorSpan(Color.WHITE));
//        view.addSpan(new StyleSpan(Typeface.BOLD));
//        view.addSpan(new RelativeSizeSpan(1.5f));

    }
}
