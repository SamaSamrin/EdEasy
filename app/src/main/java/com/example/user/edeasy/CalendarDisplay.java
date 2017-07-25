package com.example.user.edeasy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

//import com.roomorama.caldroid.CaldroidFragment;
//import com.roomorama.caldroid.CaldroidListener;

@SuppressLint("SimpleDateFormat")
public class CalendarDisplay extends AppCompatActivity {

    private static final String TAG = "**Calendar**";

    ListView eventsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_display);

        Log.e(TAG, "onCreate");

        eventsView = (ListView) findViewById(R.id.calendar_events_display);
        //ListAdapter adapter = new ArrayAdapter<String>();


    }
}
