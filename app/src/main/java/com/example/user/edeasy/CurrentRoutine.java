package com.example.user.edeasy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class CurrentRoutine extends Activity {

    private static final String TAG = "**Current Routine**";

    String username;
    String user_email;
    String role;
    HashMap<String, List<String>> sundayRoutine;
    HashMap<String, List<String>> mondayRoutine;
    HashMap<String, List<String>> tuesdayRoutine;
    HashMap<String, List<String>> wednesdayRoutine;
    HashMap<String, List<String>> thursdayRoutine;
    HashMap<String, List<String>> saturdayRoutine;
    HashMap<String, List<String>> fridayRoutine;
    HashMap<String, List<String>> slot_details;
    //each slot name is the name of a weekday, mapped to the slot time, coursename, section, room number
    String[][] routine;
    int numberOfCourses;
    int numberOfClasses;
    String[][] assignedCourses;
    String[] departments;
    String todaysName;
    ListView todaysRoutine;
    ListAdapter adapter;

    DatabaseReference databaseReference;
    DatabaseReference studentsDatabaseReference;
    DatabaseReference teachersDatabaseReference;
    DatabaseReference currentUserRef;
    DatabaseReference allDepartmentsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_routine);

        //getting all values from Dashboard Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            username = bundle.getString("username");
            //Log.e(TAG, "username = "+username);
            user_email = bundle.getString("email");
            //Log.e(TAG, "email = "+user_email);
            role = bundle.getString("role");
            //Log.e(TAG, "role = "+role);
            numberOfCourses = bundle.getInt("number");
            //Log.e(TAG, "#64 : number of courses = "+numberOfCourses);
            routine = new String[numberOfCourses][3];
            departments = new String[numberOfCourses];
            departments = bundle.getStringArray("departments");
            for (int i=0; i<numberOfCourses; i++)
                Log.e(TAG, "department "+String.valueOf(i+1)+" = "+departments[i]);
            assignedCourses = new String[numberOfCourses][2];
            for (int i=0; i<numberOfCourses; i++){
                assignedCourses[i] = bundle.getStringArray("course"+String.valueOf(i+1));
                //Log.e(TAG, "course "+String.valueOf(i+1)+" = "+assignedCourses[i][0]);
                //Log.e(TAG, "section = "+assignedCourses[i][1]);
            }
        }else
            Log.e(TAG, "#76 : received bundle is null");

        int croppedEmailIdLimit = user_email.length() - 4;
        String emailID = user_email.substring(0, croppedEmailIdLimit);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        studentsDatabaseReference = databaseReference.child("users").child("students");
        teachersDatabaseReference = databaseReference.child("users").child("teachers");
        allDepartmentsRef = databaseReference.child("departments");

        if (role.equals("student"))
            currentUserRef = studentsDatabaseReference.child(emailID);
        else if (role.equals("teacher"))
            currentUserRef = teachersDatabaseReference.child(emailID);
        //Log.e(TAG, "current user ref = "+currentUserRef.toString());

        //initializing main list and hashmap
        sundayRoutine = new HashMap<String, List<String>>();
        mondayRoutine = new HashMap<String, List<String>>();
        tuesdayRoutine = new HashMap<String, List<String>>();
        wednesdayRoutine = new HashMap<String, List<String>>();
        thursdayRoutine = new HashMap<String, List<String>>();
        saturdayRoutine = new HashMap<String, List<String>>();
        fridayRoutine = new HashMap<String, List<String>>();

        slot_details = new HashMap<String, List<String>>();

        //getting reference to views
        int todayInt = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        todaysName = new DateFormatSymbols().getWeekdays()[todayInt];//today's week day name
        TextView currentDaysNameDisplay = (TextView) findViewById(R.id.current_routine_day_name);
        currentDaysNameDisplay.setText(todaysName.toUpperCase());
        Log.e(TAG, "#123 : today = "+todaysName);
        todaysRoutine = (ListView) findViewById(R.id.todays_routine);//getting the listview of routine
        getTodaysRoutine();
    }

    void getTodaysRoutine(){
        Log.e(TAG, "getTodaysRoutine");
        allDepartmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //retrieving events from dataSnapshot
                for (int i=0; i<numberOfCourses; i++) {
                    DataSnapshot sectionSnap = dataSnapshot.child(departments[i])
                            .child("courses").child(assignedCourses[i][0])
                            .child("sections").child(assignedCourses[i][1]);
                    String faculty = sectionSnap.child("faculty").getValue(String.class);
                    DataSnapshot routineRef = sectionSnap.child("routine");
                    //List<String> details = new ArrayList<String>();
                    String day = "";
                    String time = "";
                    String course = "";
                    String section = "";
                    HashMap<String, List<String>> dayWiseRoutine = new HashMap<String, List<String>>();
                    long theoryClassCount = routineRef.child("theory").getChildrenCount();
                    for (DataSnapshot theorySnap : routineRef.child("theory").getChildren()) {
                        course = assignedCourses[i][0];
                        section = assignedCourses[i][1];
                        Log.e(TAG, "#150 : theory's course = "+course+ " section = "+section);
                        time = theorySnap.child("time").getValue(String.class);
                        Log.e(TAG, "#152 : time = " + time);
                        day = theorySnap.child("day").getValue(String.class);
                        dayWiseRoutine = getRoutineFromDay(day);
                        Log.e(TAG, "#155 : day = " + day);
                        List<String> details = new ArrayList<String>();
                        details.add(course);
                        details.add(section);
                        details.add(faculty);
                        setDetailsByDay(day, time, details);
                        //i++;
                    }
                    long labClassCount = 0;
                    if (routineRef.child("lab") != null){
                        labClassCount=1;
                        for (DataSnapshot labSnap : routineRef.child("lab").getChildren()) {
                            //Log.e(TAG, "#129 : lab's snap = "+labSnap.toString());
                            if (labSnap.getKey().equals("day")) {
                                day = labSnap.getValue(String.class);
                                Log.e(TAG, "#170 : lab's day = "+day);
                            }
                            if (labSnap.getKey().equals("time")) {
                                time = labSnap.getValue(String.class);
                                Log.e(TAG, "#174 : lab's time ="+time);
                            }
                        }
                        course = assignedCourses[i][0] + " LAB";
                        section = assignedCourses[i][1];
                        Log.e(TAG, "#179 : theory's course = "+course+ " section = "+section);
                        List<String> details = new ArrayList<String>();
                        details.add(course);
                        details.add(section);
                        setDetailsByDay(day, time, details);
                    }
                    i++;
                    long classCount = theoryClassCount + labClassCount;
                    int courseClassesNumber = (int) classCount;
                    //slot_details.put(course, details);
                    numberOfClasses = numberOfClasses + courseClassesNumber;
                    Log.e(TAG, "#190 : course's number of classes" + String.valueOf(courseClassesNumber));
                }
                HashMap<String, List<String>> routine = getRoutineFromDay(todaysName);
                Log.e(TAG, "sending routine's size = "+routine.size());
                Iterator it = routine.entrySet().iterator();
                while (it.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry)it.next();
                    Log.e(TAG, "#197 : "+pair.getKey().toString() + " = " + pair.getValue());
                    //it.remove(); // avoids a ConcurrentModificationException
                }
                adapter = new RoutineAdapter(CurrentRoutine.this, routine);
                todaysRoutine.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    HashMap<String, List<String>> getRoutineFromDay(String day) {
        switch (day) {
            case "Sunday":
                return sundayRoutine;
            case "Monday":
                return mondayRoutine;
            case "Tuesday":
                return tuesdayRoutine;
            case "Wednesday":
                return wednesdayRoutine;
            case "Thursday":
                return thursdayRoutine;
            case "Saturday":
                return saturdayRoutine;
            case "Friday":
                return fridayRoutine;
            default:
                return null;
            }
        }

    void setDetailsByDay(String day, String slot, List<String> details) {
        switch (day) {
            case "Sunday":
                sundayRoutine.put(slot, details);
                break;
            case "Monday":
                mondayRoutine.put(slot, details);
                break;
            case "Tuesday":
                tuesdayRoutine.put(slot, details);
                break;
            case "Wednesday":
                wednesdayRoutine.put(slot, details);
                break;
            case "Thursday":
                thursdayRoutine.put(slot, details);
                break;
            case "Saturday":
                saturdayRoutine.put(slot, details);
                break;
            case "Friday":
                fridayRoutine.put(slot, details);
                break;
        }
    }

    void printMap(HashMap<String, List<String>> map){
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            Log.e(TAG, "#164 : "+pair.getKey().toString() + " = " + pair.getValue());
            //it.remove(); // avoids a ConcurrentModificationException
        }
    }

    public void viewFullRoutine(View view){

    }
}

class RoutineAdapter extends BaseAdapter{

    private static final String TAG ="**Routine Adapter**";

    Context context;
    //String[][] routine;//four values for each entry - slot, course, section, faculty
    //number of classes = routine's length
    LayoutInflater inflater;
    HashMap<String, List<String>> routine;
    String[] slotTitles;

    RoutineAdapter(Context context, HashMap<String, List<String>> wholeRoutine){
        this.context = context;
        routine = wholeRoutine;
        if (routine==null)
            Log.e(TAG, "routine is null");
        else {
            Log.e(TAG, "routine size = " + routine.size());
            slotTitles = new String[routine.size()];
            Iterator it = routine.entrySet().iterator();
            int i=0;
            while (it.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry)it.next();
                slotTitles[i] = pair.getKey().toString();
                Log.e(TAG, "#252 : "+pair.getKey().toString() + " = " + pair.getValue());
                i++;
                //it.remove(); // avoids a ConcurrentModificationException
            }
        }
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return slotTitles.length;
    }

    @Override
    public Object getItem(int position) {
        return slotTitles[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null ) {
            view = inflater.inflate(R.layout.routine_slot_item, null);
        }
        TextView slotText = (TextView) view.findViewById(R.id.slot);
        TextView courseText = (TextView) view.findViewById(R.id.course_name);
        TextView sectionText = (TextView) view.findViewById(R.id.section_number);
        TextView facultyInitialText = (TextView) view.findViewById(R.id.room_number);

        String slot = slotTitles[position];
        Log.e(TAG, "slot = "+slot);
        slotText.setText(slot);
        String course = routine.get(slot).get(0);
        Log.e(TAG, "course = "+course);
        courseText.setText(course);
        String section = routine.get(slot).get(1);
        Log.e(TAG, "section = "+section);
        sectionText.setText(" Section "+section);

        return view;
    }
}

