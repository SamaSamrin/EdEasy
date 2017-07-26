package com.example.user.edeasy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CalendarView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
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

    int numberOfCourses;
    String[][] assignedCourses;
    String[][] events;
    String[] departments;
    String course;
    String section;
    ListAdapter calendarAdapter;
    int index = 1;
    CalendarView calendarView;

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    DatabaseReference studentsDatabaseReference;
    DatabaseReference teachersDatabaseReference;
    StorageReference storageReference;
    DatabaseReference allDepartmentsRef;
    //DatabaseReference departmentRef;
    FirebaseUser currentUser;
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

        storageReference = FirebaseStorage.getInstance().getReference();

        populateLists();

        adapter = new ExpandableListViewAdapterDemo(this, semester_headers, semester_result_details);
        previous_results_list.setAdapter(adapter);
    }

    private void populateLists(){
        semester_headers = new ArrayList<String>();
        semester_result_details = new HashMap<String, List<String>>();

        semester_headers.add("Spring 2014");
        semester_headers.add("Summer 2014");
        semester_headers.add("Fall 2014");

        List<String> spring14 = new ArrayList<String>();
        spring14.add("CSE110");
        spring14.add("MAT110");
        spring14.add("PHY111");
        spring14.add("ENG101");
        spring14.add("4.0");
        spring14.add("3.7");
        spring14.add("3.0");
        spring14.add("3.3");
        spring14.add("A");
        spring14.add("A-");
        spring14.add("B");
        spring14.add("B+");

        List<String> summer14 = new ArrayList<String>();
        summer14.add("CSE111");
        summer14.add("MAT120");
        summer14.add("PHY112");
        summer14.add("ENG102");
        summer14.add("3.0");
        summer14.add("2.7");
        summer14.add("4.0");
        summer14.add("2.3");
        summer14.add("B");
        summer14.add("C+");
        summer14.add("A");
        summer14.add("C");

        List<String> fall2014 = new ArrayList<String>();
        fall2014.add("DEV101");
        fall2014.add("HUM101");
        fall2014.add("ENG204");
        fall2014.add("2.0");
        fall2014.add("3.7");
        fall2014.add("3.0");
        fall2014.add("3.3");
        fall2014.add("C-");
        fall2014.add("A-");
        fall2014.add("B");
        fall2014.add("B+");

        semester_result_details.put(semester_headers.get(0), spring14);
        semester_result_details.put(semester_headers.get(1), summer14);
        semester_result_details.put(semester_headers.get(2), fall2014);
    }

}

class ExpandableListViewAdapterDemo extends BaseExpandableListAdapter{

    Context context = null;
    private List<String> headersList;//semester's name and year
    private HashMap<String, List<String>> tableList;//course names with its grades and gpa

    static final String TAG = "**Adapter Demo**";

    ExpandableListViewAdapterDemo(Context context, List<String> list,
                                  HashMap<String, List<String>> hashMap){
        this.context = context;
        headersList = list;
        tableList = hashMap;
    }

    @Override
    public int getGroupCount() {
        return headersList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        int returns = this.tableList.get(this.headersList.get(i)).size();
        int tosubtract = 2 * getGroupCount();
        returns = returns - tosubtract - 2;
        Log.e(TAG, "child count returns = "+String.valueOf(returns) );
        return returns;
    }

    @Override
    public Object getGroup(int i) {
        return this.headersList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.tableList.get(this.headersList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String semesterTitle = (String) getGroup(i);
        if (view == null){
            LayoutInflater inf = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.previous_semesters_result_list_headers, null);
        }

        TextView semesterName = (TextView) view.findViewById(R.id.semester_name);
        semesterName.setText(semesterTitle);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String courseIdTitle = (String) getChild(i, i1);
        String gpa = (String) getChild(i, i1+getChildrenCount(i));//previously i1+4
        String grade = (String) getChild(i, i1+getChildrenCount(i)+getChildrenCount(i));
        if (view == null){
            LayoutInflater inf = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.previous_semesters_results_list_child, null);
        }
        TextView courseIdValue = (TextView) view.findViewById(R.id.course_id_column_value);
        courseIdValue.setText(courseIdTitle);
        TextView gradeValue = (TextView) view.findViewById(R.id.grade_column_value);
        gradeValue.setText(grade);
        TextView gpaValue = (TextView) view.findViewById(R.id.gpa_column_value);
        gpaValue.setText(gpa);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}