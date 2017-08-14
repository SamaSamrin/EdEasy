package com.example.user.edeasy.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;

import com.example.user.edeasy.R;
import com.example.user.edeasy.adapters.ExpandableListViewAdapterDemo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class PreviousResults extends Activity {

    private static final String TAG = "**Previous Results**";

    ExpandableListView previous_results_list ;
    List<String> semester_headers;
    HashMap<String, List<String>> semester_result_details;
    ExpandableListViewAdapterDemo adapter;

    String username;
    String user_email;
    String user_role;

    int numberOfCompletedCourses;//of a particular previous semester
    int numberOfPreviousSemester;
    String[] departments;
    int[] numberOfCoursesDone;
    List<String> resultList;
    String course;
    String section;

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    DatabaseReference studentsDatabaseReference;
    DatabaseReference teachersDatabaseReference;
    DatabaseReference currentUserRef;
    DatabaseReference previousResultsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_results);

        previous_results_list = (ExpandableListView) findViewById(R.id.previous_results_semesterwise);

        //getting all values from Dashboard Intent
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            username = bundle.getString("username");
            Log.e(TAG, "username = "+username);
            user_email = bundle.getString("email");
            Log.e(TAG, "email = "+user_email);
            user_role = bundle.getString("role");
            Log.e(TAG, "role = "+user_role);
        }else
            Log.e(TAG, "#49 : received bundle is null");

        int croppedEmailIdLimit = user_email.length() - 4;
        String emailID = user_email.substring(0, croppedEmailIdLimit);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        studentsDatabaseReference = databaseReference.child("users").child("students");
        teachersDatabaseReference = databaseReference.child("users").child("teachers");

        if (user_role.equals("student"))
            currentUserRef = studentsDatabaseReference.child(emailID);
        else if (user_role.equals("teacher"))
            currentUserRef = teachersDatabaseReference.child(emailID);
        Log.e(TAG, "current user ref = "+currentUserRef.toString());

        previousResultsRef = currentUserRef.child("courses_completed");

        semester_headers = new ArrayList<String>();
        semester_result_details = new HashMap<String, List<String>>();
        numberOfCoursesDone = new int[4];

        populateLists();

        //Log.e(TAG, "details size = "+semester_result_details.size()+" headers size = "+semester_headers.size());

        adapter = new ExpandableListViewAdapterDemo(this, semester_headers, semester_result_details, numberOfCoursesDone);
        previous_results_list.setAdapter(adapter);
    }

    private void populateLists(){

        previousResultsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                numberOfPreviousSemester = (int) count;
                numberOfCoursesDone = new int[numberOfPreviousSemester];
                resultList = new ArrayList<String>();
                int semesterCount = 0;
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String semester = snap.getKey();
                    semester = reverseName(semester);
                    semester_headers.add(semester);//semester's name
                    long c = snap.getChildrenCount();
                    numberOfCompletedCourses = (int) c;
                    numberOfCoursesDone[semesterCount] = numberOfCompletedCourses;
                    int index = 0;
                    Double[] gpas = new Double[numberOfCompletedCourses];
                    for (DataSnapshot snapChild : snap.getChildren()){
                        resultList.add(snapChild.getKey());//course name
                        if(snapChild.child("gpa").getValue(Double.class)!=null) {
                            gpas[index] = snapChild.child("gpa").getValue(Double.class);//gpa
                        }
                        resultList.add(String.valueOf(gpas[index]));
                        resultList.add(snapChild.child("grade").getValue(String.class));//grade
                        index++;
                        semester_result_details.put(semester, resultList);
                        adapter.notifyDataSetChanged();
                    }
                    semesterCount++;
                    Log.e(TAG, "#151 : details size = "+semester_result_details.size());
                    adapter = new ExpandableListViewAdapterDemo(PreviousResults.this,
                            semester_headers, semester_result_details, numberOfCoursesDone);
                    previous_results_list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//
//        semester_headers.add("Spring 2016");
//        semester_headers.add("Summer 2016");
//        semester_headers.add("Fall 2016");
//        semester_headers.add("Spring 2017");
//
//        List<String> spring14 = new ArrayList<String>();
//        spring14.add("CSE110");
//        spring14.add("4.0");
//        spring14.add("A");
//        spring14.add("MAT110");
//        spring14.add("3.7");
//        spring14.add("A-");
//        spring14.add("PHY111");
//        spring14.add("3.0");
//        spring14.add("B");
//        spring14.add("ENG101");
//        spring14.add("3.3");
//        spring14.add("B+");
//
//        List<String> summer14 = new ArrayList<String>();
//        summer14.add("CSE111");
//        summer14.add("4.0");
//        summer14.add("A");
//        summer14.add("MAT120");
//        summer14.add("3.0");
//        summer14.add("B");
//        summer14.add("PHY112");
//        summer14.add("3.3");
//        summer14.add("B+");
//        summer14.add("BUS101");
//        summer14.add("3.3");
//        summer14.add("B=");
//
//        List<String> fall2014 = new ArrayList<String>();
//        fall2014.add("DEV101");
//        fall2014.add("4.0");
//        fall2014.add("A");
//        fall2014.add("HUM103");
//        fall2014.add("3.7");
//        fall2014.add("A-");
//        fall2014.add("ENG102");
//        fall2014.add("3.7");
//        fall2014.add("A-");
//
//        List<String> spring17 = new ArrayList<String>();
//        spring17.add("CSE260");
//        spring17.add("4.0");
//        spring17.add("A");
//        spring17.add("CSE230");
//        spring17.add("3.7");
//        spring17.add("A-");
//        spring17.add("ENV103");
//        spring17.add("3.7");
//        spring17.add("A-");
//        spring17.add("MAT215");
//        spring17.add("3.3");
//        spring17.add("B+");
//
//        semester_result_details.put(semester_headers.get(0), spring14);
//        semester_result_details.put(semester_headers.get(1), fall2014);
//        semester_result_details.put(semester_headers.get(2), summer14);
//        semester_result_details.put(semester_headers.get(3), spring17);
        //adapter.notifyDataSetChanged();
    }

    String reverseName(String semesterName){
        String properName = "";
        String[] temp = semesterName.split(" ");
        properName = temp[1]+" "+temp[0];
        return properName;
    }

    List addAllToList(List list, String[] array){
        for (String anArray : array) list.add(anArray);
        return list;
    }

    List addAllToList(List list, Double[] array){
        for (Double anArray : array) list.add(String.valueOf(anArray));
        return list;
    }

    void printAll(){
        Log.e(TAG, "headers count = "+semester_headers.size());
        for (int i = 0; i < semester_headers.size() ; i++) {
            Log.e(TAG, "header at i="+i+" ,"+semester_headers.get(i));
        }
        //printMap(semester_result_details);
    }

    private static void printMap(HashMap mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            //Log.e(TAG, "#237 : "+pair.getKey() + " = " + pair.getValue());
            //it.remove(); // avoids a ConcurrentModificationException
        }
    }

}
