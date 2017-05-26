package com.example.user.edeasy;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

public class CurrentMarksheet extends Activity {

    private static final String TAG = "**Current Marksheet**";
    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_marksheet);
        Log.e(TAG, "activity reached");
        tabHost = (TabHost) findViewById(R.id.current_marksheet_tab_host);
        tabHost.setup();
        tabHost.setActivated(true);
//        tabHost.showContextMenu();
        Log.e(TAG, tabHost.toString());
        if(tabHost.isShown())
            Log.e(TAG, "tab host is shown");
        if (tabHost.isActivated())
            Log.e(TAG, "tab host is activated");
        TabHost.TabSpec spec = tabHost.newTabSpec("course1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("CSE110");
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("course2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("MAT110");
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("course3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("PHY111");
        tabHost.addTab(spec);
        spec = tabHost.newTabSpec("course4");
        spec.setContent(R.id.tab4);
        spec.setIndicator("ENG101");
        tabHost.addTab(spec);
        Log.e(TAG, spec.toString());

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                View view = tabHost.getCurrentView();
                //view.setBackgroundColor(Color.GREEN);
                String title = tabHost.getCurrentTabTag();
                Toast.makeText(CurrentMarksheet.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
