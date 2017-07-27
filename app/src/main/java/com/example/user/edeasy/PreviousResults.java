package com.example.user.edeasy;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    List [] resultLists;
    String course;
    String section;
    int index = 1;
    int resultIndex = 0;

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

        populateLists();

        Log.e(TAG, "details size = "+semester_result_details.size()+" headers size = "+semester_headers.size());

        adapter = new ExpandableListViewAdapterDemo(this, semester_headers, semester_result_details);
        previous_results_list.setAdapter(adapter);
    }

    private void populateLists(){

//        previousResultsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                long count = dataSnapshot.getChildrenCount();
//                numberOfPreviousSemester = (int) count;
//                resultLists = new List[numberOfPreviousSemester];
//                Log.e(TAG, "number of previous semesters = "+numberOfPreviousSemester);
//                for (DataSnapshot snap : dataSnapshot.getChildren()) {
//                    String semester = snap.getKey();
//                    semester = reverseName(semester);
//                    //semester_headers.add(semester);//semester's name
//                    long c = snap.getChildrenCount();
//                    numberOfCompletedCourses = (int) c;
//                    int index = 0;
//                    resultLists[resultIndex] = new ArrayList();
//                    String[] courses = new String[numberOfCompletedCourses];
//                    String[] grades = new String[numberOfCompletedCourses];
//                    Double[] gpas = new Double[numberOfCompletedCourses];
//                    for (DataSnapshot snapChild : snap.getChildren()){
//                        courses[index] = snapChild.getKey();//coursename
//                        if(snapChild.child("gpa").getValue(Double.class)!=null) {
//                            gpas[index] = snapChild.child("gpa").getValue(Double.class);//gpa
//                        }
//                        grades[index] = String.valueOf(snapChild.child("grade").getValue(String.class));//grade
//                        index++;
//                    }
//                    resultLists[resultIndex] = addAllToList(resultLists[resultIndex], courses);//first all course names
//                    resultLists[resultIndex] = addAllToList(resultLists[resultIndex], gpas);//second all corresponding gpas
//                    resultLists[resultIndex] = addAllToList(resultLists[resultIndex], grades);//lastly all corresponding grades
//                    //semester_result_details.put(semester, resultLists[resultIndex]);
//                }
//                printAll();
//               // adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        semester_headers.add("Spring 2016");
        semester_headers.add("Summer 2016");
        semester_headers.add("Fall 2016");
        semester_headers.add("Spring 2017");

        List<String> spring14 = new ArrayList<String>();
        spring14.add("CSE110");
        spring14.add("4.0");
        spring14.add("A");
        spring14.add("MAT110");
        spring14.add("3.7");
        spring14.add("A-");
        spring14.add("PHY111");
        spring14.add("3.0");
        spring14.add("B");
        spring14.add("ENG101");
        spring14.add("3.3");
        spring14.add("B+");

        List<String> summer14 = new ArrayList<String>();
        summer14.add("CSE111");
        summer14.add("4.0");
        summer14.add("A");
        summer14.add("MAT120");
        summer14.add("3.0");
        summer14.add("B");
        summer14.add("PHY112");
        summer14.add("3.3");
        summer14.add("B+");
        summer14.add("BUS101");
        summer14.add("3.3");
        summer14.add("B=");

        List<String> fall2014 = new ArrayList<String>();
        fall2014.add("DEV101");
        fall2014.add("4.0");
        fall2014.add("A");
        fall2014.add("HUM103");
        fall2014.add("3.7");
        fall2014.add("A-");
        fall2014.add("ENG102");
        fall2014.add("3.7");
        fall2014.add("A-");

        List<String> spring17 = new ArrayList<String>();
        spring17.add("CSE260");
        spring17.add("4.0");
        spring17.add("A");
        spring17.add("CSE230");
        spring17.add("3.7");
        spring17.add("A-");
        spring17.add("ENV103");
        spring17.add("3.7");
        spring17.add("A-");
        spring17.add("MAT215");
        spring17.add("3.3");
        spring17.add("B+");

        semester_result_details.put(semester_headers.get(0), spring14);
        semester_result_details.put(semester_headers.get(1), fall2014);
        semester_result_details.put(semester_headers.get(2), summer14);
        semester_result_details.put(semester_headers.get(3), spring17);
        //adapter.notifyDataSetChanged();

        Log.e(TAG, "details size = "+semester_result_details.size());
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
        printMap(semester_result_details);
    }

    private static void printMap(HashMap mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            Log.e(TAG, "#237 : "+pair.getKey() + " = " + pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

}

class ExpandableListViewAdapterDemo extends BaseExpandableListAdapter{

    Context context = null;
    List<String> headersList;//semester's name and year
    HashMap<String, List<String>> tableList;//course names with its grades and gpa

    static final String TAG = "**Adapter Demo**";

    ExpandableListViewAdapterDemo(Context context, List<String> list,
                                  HashMap<String, List<String>> hashMap){
        this.context = context;
        headersList = list;
        tableList = hashMap;
        //notifyDataSetChanged();
        Log.e(TAG, "hashmap list value = "+hashMap.get("Spring 2016"));
        Log.e(TAG, "initial table list value = "+tableList.get("Spring 2016"));
        //printMap(tableList);
        //printAll();
        Log.e(TAG, "groupCount = "+getGroupCount());

    }

    void printAll(){
        Log.e(TAG, "headers count = "+headersList.size());
        for (int i = 0; i < headersList.size() ; i++) {
            Log.e(TAG, "header at i="+i+" ,"+headersList.get(i));
        }
        //printMap(tableList);
    }

//    private static void printMap(HashMap mp) {
//        Iterator it = mp.entrySet().iterator();
//        while (it.hasNext()) {
//            HashMap.Entry pair = (HashMap.Entry)it.next();
//            Log.e(TAG, "#253 : "+pair.getKey() + " = " + pair.getValue());
//            it.remove(); // avoids a ConcurrentModificationException
//        }
//    }

    @Override
    public int getGroupCount() {
        //Log.e(TAG, "#299 : table list value = "+tableList.get("Spring 2016"));
        return headersList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        Log.e(TAG, "at i="+i);
        int returns = 0;
        //Log.e(TAG, "#307 : table list value = "+tableList.get("Spring 2016"));
        if (tableList.get(headersList.get(i)) != null)
            returns = tableList.get(headersList.get(i)).size();
        else
            Log.e(TAG, "tableList is null");
        //Log.e(TAG, "details size = "+returns);
       // Log.e(TAG, "group count = "+getGroupCount());
//        int tosubtract = 2 * getGroupCount();
//        if (returns>tosubtract)
//            returns = returns - tosubtract - 2;
        //returns = returns/3;
        Log.e(TAG, "child count returns = "+String.valueOf(returns) );
        return returns/3;
    }

    @Override
    public Object getGroup(int i) {
        //Log.e(TAG, "#323 : table list value = "+tableList.get("Spring 2016"));
        return headersList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        Log.e(TAG, "#329 : getChild = "+tableList.get(headersList.get(i)).get(i1));
        return tableList.get(headersList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        //Log.e(TAG, "#335 : table list value = "+tableList.get("Spring 2016"));
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
       // Log.e(TAG, "#340 : table list value = "+tableList.get("Spring 2016"));
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        //Log.e(TAG, "#347 : table list value = "+tableList.get("Spring 2016"));
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String semesterTitle = (String) getGroup(i);
        //Log.e(TAG, "#353 : semester = "+semesterTitle);
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
        Log.e(TAG, "#367 : i = "+i+ " i1 = "+i1);
        if (view == null){
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.previous_semesters_results_list_child, null);
        }
        TextView courseIdValue = (TextView) view.findViewById(R.id.course_id_column_value);
        TextView gradeValue = (TextView) view.findViewById(R.id.grade_column_value);
        TextView gpaValue = (TextView) view.findViewById(R.id.gpa_column_value);

        Log.e(TAG, "#372 : number of children = "+getChildrenCount(i));
            int courseNumber = getChildrenCount(i);
            Log.e(TAG, "#375 : course numbers = "+courseNumber);
//            int index = 0;
//            for (int k = 0; k < courseNumber; k++) {
        if (i1==0) {
            String courseIdTitle = (String) getChild(i, i1);
            Log.e(TAG, "course id = " + courseIdTitle + " at k=" + i1);
            String gpa = (String) getChild(i, i1 + 1);//previously i1+4
            Log.e(TAG, "gpa = " + gpa + " at k=" + String.valueOf(i1 + 1));
            String grade = (String) getChild(i, i1 + 2);
            Log.e(TAG, "grade = " + grade + " at k=" + String.valueOf(i1 + 2));

            courseIdValue.setText(courseIdTitle);
            gradeValue.setText(grade);
            gpaValue.setText(gpa);
        }
        else if (i1==1){//|| (i1-1)%3==0
            String courseIdTitle = (String) getChild(i, i1+2);
            Log.e(TAG, "course id = " + courseIdTitle + " at k=" + String.valueOf(i1+2));
            String gpa = (String) getChild(i, i1+3);//previously i1+4
            Log.e(TAG, "gpa = " + gpa + " at k=" + String.valueOf(i1+3));
            String grade = (String) getChild(i, i1 + 4);
            Log.e(TAG, "grade = " + grade + " at k=" + String.valueOf(i1 + 4));

            courseIdValue.setText(courseIdTitle);
            gradeValue.setText(grade);
            gpaValue.setText(gpa);
        }else if (i1==2 ){//|| (i1-2)%3==0
            String courseIdTitle = (String) getChild(i, i1+4);
            Log.e(TAG, "course id = " + courseIdTitle + " at k=" + String.valueOf(i1+4));
            String gpa = (String) getChild(i, i1+5);//previously i1+4
            Log.e(TAG, "gpa = " + gpa + " at k=" + String.valueOf(i1+5));
            String grade = (String) getChild(i, i1+6);
            Log.e(TAG, "grade = " + grade + " at k=" + String.valueOf(i1+6));

            courseIdValue.setText(courseIdTitle);
            gradeValue.setText(grade);
            gpaValue.setText(gpa);
//            String grade = (String) getChild(i, i1);//previously i1+4
//            Log.e(TAG, "grade = " + grade + " at k=" + String.valueOf(i1));
//            gradeValue.setText(grade);
        }else if (i1==3 ){//|| (i1-2)%3==0
            String courseIdTitle = (String) getChild(i, i1+6);
            Log.e(TAG, "course id = " + courseIdTitle + " at k=" + String.valueOf(i1+6));
            String gpa = (String) getChild(i, i1+7);//previously i1+4
            Log.e(TAG, "gpa = " + gpa + " at k=" + String.valueOf(i1+7));
            String grade = (String) getChild(i, i1+8);
            Log.e(TAG, "grade = " + grade + " at k=" + String.valueOf(i1+8));

            courseIdValue.setText(courseIdTitle);
            gradeValue.setText(grade);
            gpaValue.setText(gpa);
//            String grade = (String) getChild(i, i1);//previously i1+4
//            Log.e(TAG, "grade = " + grade + " at k=" + String.valueOf(i1));
//            gradeValue.setText(grade);
        }
//
//                index = index + 3;
//            }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        //Log.e(TAG, "#386 : table list value = "+tableList.get("Spring 2016"));
        return true;
    }
}