package com.example.user.edeasy.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

/**
 * Created by ASUS on 15-Aug-17.
 */

public class GridAdapter extends BaseAdapter {

    private static final String TAG = "**Grid Adapter**";

    Context context;
    String user_role;
    String[] dashboard_items = {"PREVIOUS RESULTS", "CURRENT ROUTINE", "CURRENT MARKSHEET",
            "CHATROOM", "CALENDER", "NOTIFICATIONS"};
    String[] colorBacks = {"#fcde00", "#64DD17", "#00B0FF", "#BA68C8", "#EC407A", "#FF3D00"};
//    int[] colors = {Color.CYAN, Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.RED, Color.BLACK};
//    int[] colors2 = {R.color.previousResultsBackg, R.color.currentRoutineBackg,
//                      R.color.currentMarksheetBackg, R.color.courseMaterialsBackg,
//                      R.color.calendarBackg, R.color.notificationsBackg};

    public GridAdapter(Context context, String role){
        super();
        this.context = context;
        user_role = role;
        Log.e(TAG, "role = "+user_role);
    }

    @Override
    public int getCount() {
        return dashboard_items.length;
    }

    @Override
    public Object getItem(int i) {
        return dashboard_items[i];
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
            itemText.setTextColor(Color.WHITE);
            itemText.setTypeface(itemText.getTypeface(), Typeface.BOLD);
        }else{
            itemText = (TextView) view;
        }
        if (i!=0)
            itemText.setText(dashboard_items[i]);
        else {if (user_role!=null) {
                if (user_role.equals("student"))
                    itemText.setText(dashboard_items[i]);
                else if (user_role.equals("teacher")) {
                    String teachersChoice = "LESSON PLANNER";
                    itemText.setText(teachersChoice);
                }
            }else
                itemText.setText(dashboard_items[i]);
        }
        itemText.setBackgroundColor(Color.parseColor(colorBacks[i]));
        return itemText;
    }
}
