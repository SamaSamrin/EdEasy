package com.example.user.edeasy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CalendarView;
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

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import java.util.Calendar;
import java.sql.Date;

@SuppressLint("SimpleDateFormat")
public class CalendarDisplay extends AppCompatActivity {

    private static final String TAG = "**Calendar**";

    ListView eventsView;
    int numberOfCourses;
    int totalNumberOfEvents;
    String[][] assignedCourses;
    String[][] events;
    String[] departments;
    String course;
    String section;
    ListAdapter calendarAdapter;
    int index = 1;
    CalendarView calendarView;
    MaterialCalendarView materialCalendarView;

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    DatabaseReference studentsDatabaseReference;
    DatabaseReference teachersDatabaseReference;
    StorageReference storageReference;
    DatabaseReference allDepartmentsRef;
    //DatabaseReference departmentRef;
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
        allDepartmentsRef = databaseReference.child("departments");
        currentUser = auth.getCurrentUser();

        events = new String[50][2];
        eventsView = (ListView) findViewById(R.id.calendar_events_display);
        //calendarView = (CalendarView) findViewById(R.id.calendarView);
        materialCalendarView = (MaterialCalendarView) findViewById(R.id.material_calendar_view);
        String todaysDate = String.valueOf(Calendar.getInstance().get(Calendar.DATE));
        int month = Calendar.getInstance().get(Calendar.MONTH);
        String todaysMonth = String.valueOf(month+1);
        String todaysYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        String fulldate = todaysYear+"-"+todaysMonth+"-"+todaysDate;
        Date currentDate = Date.valueOf(fulldate);
        materialCalendarView.setCurrentDate(currentDate);
        materialCalendarView.addDecorator(new CurrentDateDecorator(this));
//        materialCalendarView.setDateSelected(currentDate, true);
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
                //retrieving events from dataSnapshot
//                DataSnapshot ds = dataSnapshot.child("CSE")
//                        .child("courses").child("CSE220")
//                        .child("sections").child(assignedCourses[0][1])
//                        .child("events");
//                int i = 0;
                totalNumberOfEvents = 0;
                int eventIndex = 0;
                for (int i=0; i<numberOfCourses; i++) {
                    DataSnapshot ds = dataSnapshot.child(departments[i])
                            .child("courses").child(assignedCourses[i][0])
                            .child("sections").child(assignedCourses[i][1])
                            .child("events");
                    long eventsCount = ds.getChildrenCount();
                    int numberOfEvents = (int) eventsCount;
                    totalNumberOfEvents = totalNumberOfEvents + numberOfEvents;
                    Log.e(TAG, "#104 : for course "+assignedCourses[i][0]
                            +" number of events = " + String.valueOf(numberOfEvents));
                    for (DataSnapshot snap : ds.getChildren()) {
                        String eventName = snap.child("name").getValue(String.class);
                        events[eventIndex][0] = assignedCourses[i][0]+" "+eventName;
                        Log.e(TAG, "#106 : event name = " + events[eventIndex][0]);
                        events[eventIndex][1] = snap.child("due date").getValue(String.class);
                        Log.e(TAG, "#110 : due date = " + events[eventIndex][1]);
                        eventIndex++;
                    }
                }

                //applying it on the CalendarView Adapter
                calendarAdapter = new CalendarEventsAdapter(CalendarDisplay.this,
                        events, materialCalendarView, totalNumberOfEvents);
                eventsView.setAdapter(calendarAdapter);
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
    MaterialCalendarView calendar;
    String currentMonth = convertToMonthName(Calendar.getInstance().get(Calendar.MONTH));
    //String todaysDate = currentMonth+" "+String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    String selectedDate;

    CalendarEventsAdapter(Context c, String[][] eventsInput, MaterialCalendarView cal,
                          int eventsCount){
        //Log.e(TAG, "today's date = "+todaysDate);
        Log.e(TAG, "month = "+currentMonth);
        Log.e(TAG, "events number = "+eventsCount);
        numberOfEvents = 0;
        eventNames = new String[eventsInput.length];
        eventDates = new String[eventsInput.length];
        context = c;
        for (int i = 0; i < eventsInput.length; i++) {
            eventNames[i] = eventsInput[i][0];
            eventDates[i] = eventsInput[i][1];
            if (eventsInput[i]!=null && eventDates[i]!=null) {
                numberOfEvents++;
                Log.e(TAG, "at i="+String.valueOf(i)+"event name = "+eventNames[i]+" on "+eventDates[i]);
            }
        }
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        calendar = cal;
        calendarListenerSetup();
    }

    private String dateToWords(String date){
        String dateInWords = "";
        //Log.e(TAG, "date="+date);
        if (date!=null){

        String[] sections = date.split("\\.");
            if (sections.length>0) {
                String day = sections[0];
                String month = convertToMonthName(sections[1]);
                String year = sections[2];
                dateInWords = month + " " + day + "," + year;
                //Log.e(TAG, "date in words = "+dateInWords);
            }else {
                Log.e(TAG, "splitted array length is zero ");
                dateInWords = date;
            }
        }else{
            Log.e(TAG, "date argument is null");
        }
        return dateInWords;
    }

    String convertToMonthName(String monthNumber){
        String month = "";
        switch (monthNumber) {
            case "01":
                month = "January";
                break;
            case "02":
                month = "February";
                break;
            case "03":
                month = "March";
                break;
            case "04":
                month = "April";
                break;
            case "05":
                month = "May";
                break;
            case "06":
                month = "June";
                break;
            case "07":
                month = "July";
                break;
            case "08":
                month = "August";
                break;
            case "09":
                month = "September";
                break;
            case "10":
                month = "October";
                break;
            case "11":
                month = "November";
                break;
            case "12":
                month = "December";
                break;
        }
        return  month;
    }

    String convertToMonthName(int monthNumber){
        String month = "";
        switch (monthNumber) {
            case 0:
                month = "January";
                break;
            case 1:
                month = "February";
                break;
            case 2:
                month = "March";
                break;
            case 3:
                month = "April";
                break;
            case 4:
                month = "May";
                break;
            case 5:
                month = "June";
                break;
            case 6:
                month = "July";
                break;
            case 7:
                month = "August";
                break;
            case 8:
                month = "September";
                break;
            case 9:
                month = "October";
                break;
            case 10:
                month = "November";
                break;
            case 11:
                month = "December";
                break;
        }
        return  month;
    }

    String formatDates(String previousDate){
        String formattedDate = "";
        String[] dateArray = previousDate.split("\\.");
        formattedDate = dateArray[2]+"-"+dateArray[1]+"-"+dateArray[0];
        //Log.e(TAG, "#305 : formatted date = "+formattedDate);
        return  formattedDate;
    }

    void calendarListenerSetup(){
//        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                String dateInt = String.valueOf(dayOfMonth)+"."+
//                        String.valueOf(month+1)+"."+
//                        String.valueOf(year);
//                selectedDate = dateToWords(dateInt);
//                currentMonth = convertToMonthName(month);
//                notifyDataSetChanged();
//                Log.e(TAG, "selected month = "+currentMonth);
//            }
//        });
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
        if (calendar!=null) {
            //Log.e(TAG, "calendar date = " + calendar.getDate());
        }else
            Log.e(TAG, "calendar is null");

        View view = convertView;
        if (view == null ) {
            view = inflater.inflate(R.layout.calendar_event_item, null);
        }
        TextView event_tv = (TextView) view.findViewById(R.id.event_textview);
        TextView date_tv = (TextView) view.findViewById(R.id.date_textview);

        if (eventNames[position]!=null && eventDates[position]!=null){
            String month = (dateToWords(eventDates[position]).split(" ")) [0];
            //Log.e(TAG, "month="+month+" current month="+currentMonth);
                event_tv.setText(eventNames[position]);
                date_tv.setText(dateToWords(eventDates[position]));
                Date date = Date.valueOf(formatDates(eventDates[position]));
                calendar.setDateSelected(date, true);
        }else{
                event_tv.setText("");
                date_tv.setText("");
            }
        return view;
    }
}