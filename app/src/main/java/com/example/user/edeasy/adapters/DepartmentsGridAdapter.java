package com.example.user.edeasy.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

/**
 * Created by ASUS on 15-Aug-17.
 */

public class DepartmentsGridAdapter extends BaseAdapter {

    Context context;
    String[] departments = {"BBS","BIL","CSE","EEE","MNS","PHR"};
    String[] backgroundColors = {"#B3E5FC", "#EDE7F6",
            "#EDE7F6", "#B3E5FC", "#B3E5FC", "#EDE7F6"};

    public DepartmentsGridAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return departments.length;
    }

    @Override
    public Object getItem(int i) {
        return departments[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView itemText = null;
        if(view==null){
            itemText = new TextView(context);
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int screenwidth = metrics.widthPixels;
            int orientation = context.getResources().getConfiguration().orientation;
            if(orientation== Configuration.ORIENTATION_PORTRAIT)
                itemText.setLayoutParams(new GridView.LayoutParams(screenwidth/2, screenwidth/2));
            else
                itemText.setLayoutParams(new GridView.LayoutParams(screenwidth/3, screenwidth/3));
            itemText.setPadding(10, 10, 10, 10);
            itemText.setGravity(Gravity.CENTER);
            itemText.setTextColor(Color.BLACK);
            itemText.setTypeface(itemText.getTypeface(), Typeface.BOLD);
        }else{
            itemText = (TextView) view;
        }
        itemText.setText(departments[i]);
        itemText.setBackgroundColor(Color.parseColor(backgroundColors[i]));
        return itemText;
    }
}