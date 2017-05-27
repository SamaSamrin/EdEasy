package com.example.user.edeasy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class CalendarDisplay extends Activity {

    ListView eventsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_display);

        eventsView = (ListView) findViewById(R.id.calendar_events_display);
        //ListAdapter adapter = new ArrayAdapter<String>();
    }
}
