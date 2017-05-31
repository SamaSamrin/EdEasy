package com.example.user.edeasy;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class Chatroom extends AppCompatActivity {

    GridView chatsGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chatroom);
        setSupportActionBar(toolbar);

        chatsGrid = (GridView) findViewById(R.id.chats_grid);
        chatsGrid.setAdapter(new GridAdapterForChat(this));
        chatsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = null;
                switch (i) {
                    case 0 :
                        intent = new Intent(Chatroom.this, ChatroomCourse1.class);
                        break;
                    case 1 :
                        intent = new Intent(Chatroom.this, ChatroomCourse2.class);
                        break;
                    case 2 :
                        intent = new Intent(Chatroom.this, ChatroomCourse3.class);
                        break;
                    case 3 :
                        intent = new Intent(Chatroom.this, ChatroomCourse4.class);
                        break;
                    case 4 :
                        intent = new Intent(Chatroom.this, ChatroomCourse5.class);
                        break;
                }
                startActivity(intent);
            }
        });
    }

}

class GridAdapterForChat extends BaseAdapter {

    Context context;
    String[] chatrooms = {"CSE110", "MAT110", "PHY111", "ENG101"};
    String[] colorBacks = {"#FFEF00", "#76FF03", "#FF8F00", "#00C4FF", "#E040FB" };
//    int[] colors = {Color.CYAN, Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.RED, Color.BLACK};
//    int[] colors2 = {R.color.previousResultsBackg, R.color.currentRoutineBackg,
//                      R.color.currentMarksheetBackg, R.color.courseMaterialsBackg,
//                      R.color.calendarBackg, R.color.notificationsBackg};

    GridAdapterForChat(Context context){
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        return chatrooms.length;
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
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int screenwidth = metrics.widthPixels;
            //int orientation = context.getResources().getConfiguration().orientation;
            itemText.setLayoutParams(new GridView.LayoutParams(screenwidth/2, screenwidth/2));
            itemText.setPadding(10, 10, 10, 10);
            itemText.setGravity(Gravity.CENTER);
            itemText.setTypeface(itemText.getTypeface(), Typeface.BOLD);
        }else{
            itemText = (TextView) view;
        }
        itemText.setText(chatrooms[i]);
        itemText.setBackgroundColor(Color.parseColor(colorBacks[i]));
        return itemText;
    }
}

