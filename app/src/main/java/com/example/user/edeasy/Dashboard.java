package com.example.user.edeasy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class Dashboard extends AppCompatActivity {

    GridView dashboard_gv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dashboard_gv = (GridView) findViewById(R.id.dashboard_gridview);
        dashboard_gv.setAdapter(new GridAdapter(this));
        dashboard_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(Dashboard.this, ""+String.valueOf(i+1), Toast.LENGTH_LONG).show();
                Intent intent = null;
                if(i==0){
                    intent = new Intent(Dashboard.this, PreviousResults.class);
                    startActivity(intent);
                }else if(i==1){
                    intent = new Intent(Dashboard.this, CurrentRoutine.class);
                    startActivity(intent);
                }else if(i==2){
                    intent = new Intent(Dashboard.this, CurrentMarksheet.class);
                    startActivity(intent);
                }else if (i==3){
                    intent = new Intent(Dashboard.this, CourseMaterials.class);
                    startActivity(intent);
                }else if (i==4){
                    intent = new Intent(Dashboard.this, CalendarDisplay.class);
                    startActivity(intent);
                }else if (i==5){
                    intent = new Intent(Dashboard.this, NotificationsDisplay.class);
                    startActivity(intent);
                }
            }
        });
    }
}

class GridAdapter extends BaseAdapter{

    Context context;
    String[] dashboard_items = {"PREVIOUS RESULTS", "CURRENT ROUTINE", "CURRENT MARKSHEET",
            "COURSE MATERIALS", "CALENDER", "NOTIFICATIONS"};
    String[] colorBacks = {"#FFEA00", "#76FF03", "#00B0FF", "#BA68C8", "#E91E63", "#FF3D00"};
//    int[] colors = {Color.CYAN, Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.RED, Color.BLACK};
//    int[] colors2 = {R.color.previousResultsBackg, R.color.currentRoutineBackg,
//                      R.color.currentMarksheetBackg, R.color.courseMaterialsBackg,
//                      R.color.calendarBackg, R.color.notificationsBackg};

    GridAdapter(Context context){
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        return dashboard_items.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView itemText = null;
        if(view==null){
            itemText = new TextView(context);
            itemText.setLayoutParams(new GridView.LayoutParams(350, 350));
            itemText.setPadding(10, 10, 10, 10);
            itemText.setGravity(Gravity.CENTER);
            itemText.setTextColor(Color.WHITE);
            itemText.setTypeface(itemText.getTypeface(), Typeface.BOLD);
        }else{
            itemText = (TextView) view;
        }
        itemText.setText(dashboard_items[i]);
        itemText.setBackgroundColor(Color.parseColor(colorBacks[i]));
        return itemText;
    }
}
