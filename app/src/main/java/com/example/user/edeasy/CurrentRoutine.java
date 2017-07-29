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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CurrentRoutine extends Activity {

    private static final String TAG = "**Current Routine**";

    String username;
    String user_email;
    String role;
    String[][] routine;
    int numberOfCourses;
    String[][] assignedCourses;
    String[] departments;
    String todaysName;
    ListView todaysRoutine;
    ListAdapter adapter;

    DatabaseReference databaseReference;
    DatabaseReference studentsDatabaseReference;
    DatabaseReference teachersDatabaseReference;
    DatabaseReference currentUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_routine);

        //getting all values from Dashboard Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            username = bundle.getString("username");
            Log.e(TAG, "username = "+username);
            user_email = bundle.getString("email");
            Log.e(TAG, "email = "+user_email);
            role = bundle.getString("role");
            Log.e(TAG, "role = "+role);
            numberOfCourses = bundle.getInt("number");
            Log.e(TAG, "#53 : number of courses = "+numberOfCourses);
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
            Log.e(TAG, "#42 : received bundle is null");

        int croppedEmailIdLimit = user_email.length() - 4;
        String emailID = user_email.substring(0, croppedEmailIdLimit);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        studentsDatabaseReference = databaseReference.child("users").child("students");
        teachersDatabaseReference = databaseReference.child("users").child("teachers");

        if (role.equals("student"))
            currentUserRef = studentsDatabaseReference.child(emailID);
        else if (role.equals("teacher"))
            currentUserRef = teachersDatabaseReference.child(emailID);
        Log.e(TAG, "current user ref = "+currentUserRef.toString());

        //getting reference to views
        int todayInt = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
//        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.forLanguageTag());
//        Date d = new Date();
//        todaysName = sdf.format(d);//today's name from Calendar
        todaysName = new DateFormatSymbols().getWeekdays()[todayInt];
        TextView currentDaysNameDisplay = (TextView) findViewById(R.id.current_routine_day_name);
        currentDaysNameDisplay.setText(todaysName.toUpperCase());
        Log.e(TAG, "today = "+todaysName);
        todaysRoutine = (ListView) findViewById(R.id.todays_routine);
        getTodaysRoutine();
        adapter = new RoutineAdapter(this, routine);
        todaysRoutine.setAdapter(adapter);
    }

    void getTodaysRoutine(){

    }

    public void viewFullRoutine(View view){

    }
}

class RoutineAdapter extends BaseAdapter{

    Context context;
    String[][] routine;//four values for each entry - slot, course, section, faculty
    //number of classes = routine's length
    LayoutInflater inflater;

    RoutineAdapter(Context context, String[][] wholeRoutine){
        this.context = context;
        routine = wholeRoutine;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return 0;
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
        TextView facultyInitialText = (TextView) view.findViewById(R.id.faculty_initial);

        return null;
    }
}

