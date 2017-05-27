package com.example.user.edeasy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

//import com.roomorama.caldroid.CaldroidFragment;
//import com.roomorama.caldroid.CaldroidListener;

@SuppressLint("SimpleDateFormat")
public class CalendarDisplay extends AppCompatActivity {

    ListView eventsView;

//    private boolean undo = false;
//    private CaldroidFragment caldroidFragment;
//    private CaldroidFragment dialogCaldroidFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_display);

        eventsView = (ListView) findViewById(R.id.calendar_events_display);
        //ListAdapter adapter = new ArrayAdapter<String>();
    }
}
