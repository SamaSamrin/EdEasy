package com.example.user.edeasy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

//import com.roomorama.caldroid.CaldroidFragment;
//import com.roomorama.caldroid.CaldroidListener;

@SuppressLint("SimpleDateFormat")
public class CalendarDisplay extends AppCompatActivity {

    private static final String TAG = "**Calendar**";

    ListView eventsView;
    int numberOfCourses;
    String[][] assignedCourses;
    String[][] events;
    String[] departments;
    String course;
    String section;
    ListAdapter calendarAdapter;
    int index = 1;

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    DatabaseReference studentsDatabaseReference;
    DatabaseReference teachersDatabaseReference;
    DatabaseReference currentUserRef;
    StorageReference storageReference;
    StorageReference CSE_storageRef;
    DatabaseReference allDepartmentsRef;
    DatabaseReference departmentRef;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_display);

        Log.e(TAG, "onCreate");

        //getting all values from Dashboard Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            numberOfCourses = bundle.getInt("number");
            Log.e(TAG, "#37 : number of courses = "+numberOfCourses);
            departments = new String[numberOfCourses];
            departments = bundle.getStringArray("departments");
            for (int i=0; i<numberOfCourses; i++)
                Log.e(TAG, "department "+String.valueOf(i+1)+" = "+departments[i]);
            assignedCourses = new String[numberOfCourses][2];
            for (int i=0; i<numberOfCourses; i++){
                assignedCourses[i] = bundle.getStringArray("course"+String.valueOf(i+1));
                Log.e(TAG, "course "+String.valueOf(i+1)+" = "+assignedCourses[i][0]);
                Log.e(TAG, "section = "+assignedCourses[i][1]);
            }
        }else
            Log.e(TAG, "#49 : received bundle is null");

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        studentsDatabaseReference = databaseReference.child("users").child("students");
        teachersDatabaseReference = databaseReference.child("users").child("teachers");
        storageReference = FirebaseStorage.getInstance().getReference();
        //CSE_storageRef = storageReference.child("CSE");
        allDepartmentsRef = databaseReference.child("departments");
        currentUser = auth.getCurrentUser();

        events = new String[numberOfCourses][2];
        eventsView = (ListView) findViewById(R.id.calendar_events_display);
       // calendarAdapter = new CalendarEventsAdapter(this, events);
        //ListAdapter adapter = new ArrayAdapter<String>();

        getEventsFromDatabase();
    }

    void getEventsFromDatabase(){
        for (int i=0; i<numberOfCourses; i++){
            DatabaseReference eventsRef = allDepartmentsRef.child(departments[i])
                    .child("courses").child(assignedCourses[i][0])
                    .child("sections").child(assignedCourses[i][1])
                    .child("events");
            Log.e(TAG, "#92 : events ref = "+eventsRef.toString());
            //departments/CSE/courses/CSE110/sections/1/events
        }
        allDepartmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i=0; i<numberOfCourses; i++) {
                    DataSnapshot ds = dataSnapshot.child(departments[i])
                            .child("courses").child(assignedCourses[i][0])
                            .child("sections").child(assignedCourses[i][1])
                            .child("events");
                    long eventsCount = ds.getChildrenCount();
                    int numberOfEvents = (int) eventsCount;
                    Log.e(TAG, "#104 : " + String.valueOf(numberOfEvents));
                    for (DataSnapshot snap : ds.getChildren()) {
                        //Log.e(TAG, "index=" + String.valueOf(i));
                        events[i][0] = snap.child("name").getValue(String.class);
                        Log.e(TAG, "#106 : event name = " + events[i][0]);
                        events[i][1] = snap.child("due date").getValue(String.class);
                        Log.e(TAG, "#110 : due date = " + events[i][1]);
                    }
                    calendarAdapter = new CalendarEventsAdapter(CalendarDisplay.this, events);
                    eventsView.setAdapter(calendarAdapter);
                    //index++;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}

class CalendarEventsAdapter extends BaseAdapter{

    private static final String TAG = "**Calendar Adapter**";

    int numberOfEvents;
    String[] eventNames;
    String[] eventDates;
    Context context;
    private static LayoutInflater inflater = null;

    CalendarEventsAdapter(Context c, String[][] eventsInput){
        numberOfEvents = eventsInput.length;
        eventNames = new String[numberOfEvents];
        eventDates = new String[numberOfEvents];
        context = c;
        for (int i = 0; i < numberOfEvents; i++) {
            eventNames[i] = eventsInput[i][0];
            eventDates[i] = eventsInput[i][1];
            Log.e(TAG, "at i="+String.valueOf(i)+"event name = "+eventNames[i]+" on "+eventDates[i]);
        }
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return numberOfEvents;
    }

    @Override
    public Object getItem(int position) {
        return eventDates[position]+eventNames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e(TAG, "position="+position);
        View view = convertView;
        if (view == null ) {
            view = inflater.inflate(R.layout.calendar_event_item, null);
        }
        TextView event_tv = (TextView) view.findViewById(R.id.event_textview);
        TextView date_tv = (TextView) view.findViewById(R.id.date_textview);
        if (eventNames[position]!=null && eventDates[position]!=null){
            event_tv.setText(eventNames[position]);
            date_tv.setText(eventDates[position]);
        }
        return view;
    }
}