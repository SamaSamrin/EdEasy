package com.example.user.edeasy;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class CurrentMarksheet extends Activity {

    private static final String TAG = "**Current Marksheet**";
    TabHost tabHost;
    String[] courseNames;
    int numberOfCourses;
    GestureDetector gestureDetector;
    ViewFlipper flipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_marksheet);
       // Log.e(TAG, "activity reached");

        numberOfCourses = 4;
        //courseNames = new String[numberOfCourses];
        courseNames = new String[]{"CSE220", "STA201", "CSE251", "ECO101"};

        //setGestureDetector();
        flipper = new ViewFlipper(this);
        tabHost = (TabHost) findViewById(R.id.current_marksheet_tab_host);
        tabHost.setup();
        tabHost.setActivated(true);
//        tabHost.showContextMenu();
        //Log.e(TAG, tabHost.toString());
        if(tabHost.isShown())
            Log.e(TAG, "tab host is shown");
        if (tabHost.isActivated())
            Log.e(TAG, "tab host is activated");
        TabHost.TabSpec spec = tabHost.newTabSpec("course1");
        spec.setContent(R.id.tab1);
        spec.setIndicator(courseNames[0]);
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("course2");
        spec.setContent(R.id.tab2);
        spec.setIndicator(courseNames[1]);
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("course3");
        spec.setContent(R.id.tab3);
        spec.setIndicator(courseNames[2]);
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("course4");
        spec.setContent(R.id.tab4);
        spec.setIndicator(courseNames[3]);
        tabHost.addTab(spec);
        Log.e(TAG, spec.toString());


        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                View view = tabHost.getCurrentView();
                //view.setBackgroundColor(Color.GREEN);
                int index = tabHost.getCurrentTab();
                Log.e(TAG, "selected tab index = "+ String.valueOf(index));
                String selectedCourseName=courseNames[index];
                //Toast.makeText(CurrentMarksheet.this, selectedCourseName, Toast.LENGTH_SHORT).show();
            }
        });

        //tabHost.on
    }
}