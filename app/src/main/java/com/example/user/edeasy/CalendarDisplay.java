package com.example.user.edeasy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

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
            eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long eventsCount = dataSnapshot.getChildrenCount();
                    int numberOfEvents = (int) eventsCount;
                    Log.e(TAG, "#104 : "+String.valueOf(numberOfEvents));
                    int i = 1;
                    for (DataSnapshot snap : dataSnapshot.getChildren() ){
                        events[i][0] = snap.child("name").getValue(String.class);
                        Log.e(TAG, "#106 : event name = " + events[i][0]);
                        events[i][1] = snap.child("due date").getValue(String.class);
                        Log.e(TAG, "#110 : due date = "+events[i][1]);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
