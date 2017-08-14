package com.example.user.edeasy.activities.chatrooms;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.example.user.edeasy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Chatroom extends AppCompatActivity {

    private static final String TAG = "**CHAT ROOM**";

    GridView chatsGrid;

    String user_email;
    FirebaseUser currentUser;
    String username;
    String[][] current_courses;
    int numberOfCourses;
    String[][] assignedCourses;
    String[][] events;
    String[] departments;
    String course;
    String section;

    DatabaseReference studentsRef;
    DatabaseReference currentUserRef;
    GridAdapterForChat adapterForChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chatroom);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            username = bundle.getString("username");
            Log.e(TAG, "#65 : username = "+username);
            numberOfCourses = bundle.getInt("number");
            Log.e(TAG, "#65 : number of courses = "+numberOfCourses);
            departments = new String[numberOfCourses];
            departments = bundle.getStringArray("departments");
            for (int i=0; i<numberOfCourses; i++)
                Log.e(TAG, "#69 : department "+String.valueOf(i+1)+" = "+departments[i]);
            assignedCourses = new String[numberOfCourses][2];
            for (int i=0; i<numberOfCourses; i++){
                assignedCourses[i] = bundle.getStringArray("course"+String.valueOf(i+1));
                Log.e(TAG, "course "+String.valueOf(i+1)+" = "+assignedCourses[i][0]);
                Log.e(TAG, "section = "+assignedCourses[i][1]);
            }
        }else
            Log.e(TAG, "received bundle is null");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        studentsRef = FirebaseDatabase.getInstance().getReference().child("users/students");

        //handleCurrentUserInfo();//includes set up chats grid
        setUpChatsGrid();
    }

    void setUpChatsGrid(){
        Log.e(TAG, "set up chats grid");
        chatsGrid = (GridView) findViewById(R.id.chats_grid);
        adapterForChat = new GridAdapterForChat(this, assignedCourses, departments);
        chatsGrid.setAdapter(adapterForChat);
        chatsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = null;
                switch (i) {
                    case 0 :
                        intent = new Intent(Chatroom.this, ChatroomCourse1.class);
                        break;
                    case 1 :
                        intent = new Intent(Chatroom.this, ChatroomCourse2.class);
                        break;
                    case 2 :
                        intent = new Intent(Chatroom.this, ChatroomCourse3.class);
                        break;
                    case 3 :
                        intent = new Intent(Chatroom.this, ChatroomCourse4.class);
                        break;
                    case 4 :
                        intent = new Intent(Chatroom.this, ChatroomCourse5.class);
                        break;
                }
                if (intent!=null) {
                    intent.putExtra("from", username);
                    Log.e(TAG, "#87 : username = "+username);
                    intent.putExtra("courseName", assignedCourses[i][0]);
                    Log.e(TAG, "#89 : course name = "+assignedCourses[i][0]);
                    intent.putExtra("section", assignedCourses[i][1]);
                    Log.e(TAG, "#91 : course section = "+assignedCourses[i][1]);
                    intent.putExtra("department", departments[i]);
                    Log.e(TAG, "#120 : deparment = "+departments[i]);
//                    intent.putExtra("courseName", current_courses[i][0]);
//                    Log.e(TAG, "#89 : course name = "+current_courses[i][0]);
//                    intent.putExtra("section", current_courses[i][1]);
//                    Log.e(TAG, "#91 : course section = "+current_courses[i][1]);
                }else
                    Log.e(TAG, "intent null");
                startActivity(intent);
            }
        });
    }

    void handleCurrentUserInfo(){//whole process of retrieving current user data
        Log.e(TAG, "handle current user info");
        if(currentUser != null){
            //Log.e(TAG, "line 321: current user is not null");
            user_email = currentUser.getEmail();
            Log.e(TAG, "line 325: current user email = " + user_email);

            //retrieving user's info from database
            int croppedEmailIdLimit = user_email.length() - 4;
            String emailID = user_email.substring(0, croppedEmailIdLimit);
            //assuming the user is a student
            currentUserRef = studentsRef.child(emailID);
            if (currentUserRef != null){
                Log.e(TAG, "line 338: current user reference is - "+currentUserRef.toString());
                //****************NAME*****************
                DatabaseReference nameRef = currentUserRef.child("name");
                nameRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e(TAG, "name: onDataChange");
                        username = dataSnapshot.getValue(String.class);
                        Log.e(TAG, "line 363: the current username from snapshot is = "+username);
                        //setUpChatsGrid();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "line 351: database error = "+databaseError.toString());
                    }
                });
                //********************COURSES*******************
                MenuItem drawerCourseMenu = (MenuItem) findViewById(R.id.course_materials_section);
                DatabaseReference coursesRef = currentUserRef.child("courses_assigned");
                coursesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e(TAG, "course: onDataChange");
                        long numberOfChildren = dataSnapshot.getChildrenCount();
                        numberOfCourses = (int) numberOfChildren;
                        Log.e(TAG, "#137 : number of courses = "+ String.valueOf(numberOfCourses));
                        current_courses = new String[numberOfCourses][2];
                        int i = 1;
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            String key = postSnapshot.getKey();
                            String course = postSnapshot.child("course_code").getValue(String.class);
                            Log.e(TAG, "course = "+course);
                            Long section = postSnapshot.child("section").getValue(long.class);
                            Log.e(TAG, "section = "+String.valueOf(section));
                            current_courses[i-1][0] = course;//courseID
                            current_courses[i-1][1] = String.valueOf(section);//section
                            i++;
                        }
                        setUpChatsGrid();
                        if (adapterForChat!=null)
                            adapterForChat.notifyDataSetChanged();
                        else
                            Log.e(TAG, "#154 : adapter for chat is null");
                        //checking if assigned courses are retrieved correctly
                        if (current_courses != null){
                            for (int k=0; k<current_courses.length; k++){
                                Log.e(TAG, "#382: course "+String.valueOf(k)+" : "+
                                        current_courses[k][0]+" section "+current_courses[k][1]);
                            }
                        }else
                            Log.e(TAG, "#386 : null assigned courses");
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }else{
                Log.e(TAG, "line 392: current user reference is null");
            }
        }else{
            Log.e(TAG, "line 395: current Firebase user is null");
        }
    }

}

class GridAdapterForChat extends BaseAdapter {

    private String TAG = "**CHAT ADAPTER**";

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference studentsReference = FirebaseDatabase.getInstance().getReference().child("users").child("students");
    DatabaseReference teachersReference = FirebaseDatabase.getInstance().getReference().child("users").child("teachers");
    DatabaseReference currentUserRef;
    String user_email;
    String username;
    String[] departments;
    String[][] courses;// = new String[5][2]
    int numberOfCourses;
    TextView[] gridViews = new TextView[5];
    Context context;
    String[] chatrooms = {"CSE100", "MAT100", "PHY100", "ENG100"};
    String[] colorBacks = {"#FFEF00", "#76FF03", "#FF8F00", "#00C4FF", "#E040FB" };

    GridAdapterForChat(Context context, String[][] courses, String[] departments){
        super();
        Log.e(TAG, "adapter constructor");
        this.context = context;
        this.courses = courses;
        this.departments = departments;
        //Log.e(TAG, "department's length = "+departments.length);
        for (int j = 0; j <departments.length ; j++) {
            //Log.e(TAG, "#238 : department of course "+j+" = "+departments[j]);
        }
        numberOfCourses = departments.length;
        Log.e(TAG, "number of courses = "+numberOfCourses);
        //handleCurrentUserInfo();
    }

    @Override
    public int getCount() {
        return chatrooms.length;
    }

    @Override
    public Object getItem(int i) {
        Log.e(TAG, "adapter : getItem");
        return null;
    }

    @Override
    public long getItemId(int i) {
        Log.e(TAG, "adapter : getItemId");
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //handleCurrentUserInfo();
        Log.e(TAG, "adapter : getView");
        TextView itemText = null;
        if(view==null){
            itemText = new TextView(context);
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int screenwidth = metrics.widthPixels;
            //int orientation = context.getResources().getConfiguration().orientation;
            itemText.setLayoutParams(new GridView.LayoutParams(screenwidth/2, screenwidth/2));
            itemText.setPadding(10, 10, 10, 10);
            itemText.setGravity(Gravity.CENTER);
            itemText.setTypeface(itemText.getTypeface(), Typeface.BOLD);
        }else{
            itemText = (TextView) view;
        }
        Log.e(TAG, "#226 : i="+String.valueOf(i));
        //Log.e(TAG, "#227 : itemText="+itemText.toString());
        gridViews[i] = itemText;
        if (courses[i][0] != null) {
            itemText.setText(courses[i][0]);
            Log.e(TAG, "#246 : department = "+departments[i]+" course name="+courses[i][0]);
        }else {
            itemText.setText(chatrooms[i]);
            Log.e(TAG, "#249 : course null");
        }

        if (courses[i][1] != null) {
            //itemText.setText(courses[i][1]);
            Log.e(TAG, "#254 : section name="+courses[i][1]);
        }else {
            //itemText.setText(chatrooms[i]);
            Log.e(TAG, "#257 : section null");
        }

        itemText.setBackgroundColor(Color.parseColor(colorBacks[i]));
        return itemText;
    }

    void handleCurrentUserInfo(){//whole process of retrieving current user data
        //currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.e(TAG, "adapter : handle current user info");
        if(currentUser != null){
            Log.e(TAG, "line 321: current user is not null");
            user_email = currentUser.getEmail();
            Log.e(TAG, "line 325: current user email = " + user_email);

            //retrieving user's info from database
            int croppedEmailIdLimit = user_email.length() - 4;
            String emailID = user_email.substring(0, croppedEmailIdLimit);
            //assuming the user is a student
            currentUserRef = studentsReference.child(emailID);
            if (currentUserRef != null){
                //Log.e(TAG, "line 338: current user reference is - "+currentUserRef.toString());
                //****************NAME*****************
                DatabaseReference nameRef = currentUserRef.child("name");
                nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        username = dataSnapshot.getValue(String.class);
                        //Log.e(TAG, "line 363: the current username from snapshot is = "+username);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "line 351: database error = "+databaseError.toString());
                    }
                });
                //********************COURSES*******************
                DatabaseReference coursesRef = currentUserRef.child("courses_assigned");
                coursesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e(TAG, "onDataChange : COURSES");
                        long numberOfChildren = dataSnapshot.getChildrenCount();
                        numberOfCourses = (int) numberOfChildren;
                        //Log.e(TAG, "#366 : number of courses = "+ String.valueOf(numberOfCourses));
                        courses = new String[numberOfCourses][2];
                        gridViews = new TextView[numberOfCourses];
                        int i = 1;
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            String key = postSnapshot.getKey();
                            String course = postSnapshot.child("course_code").getValue(String.class);
                            Log.e(TAG, "#299 : course = "+course);
                            Long section = postSnapshot.child("section").getValue(long.class);
                            Log.e(TAG, "#391 : section = "+String.valueOf(section));
                            courses[i-1][0] = course;//courseID
                            courses[i-1][1] = String.valueOf(section);//section
                            i++;
                        }
                        notifyDataSetChanged();
                        //checking if assigned courses are retrieved correctly
                        if (courses != null){
                            for (int k=0; k<courses.length; k++){
                                //Log.e(TAG, "#382: course "+String.valueOf(k)+" : "+
                                  //      courses[k][0]+" section "+courses[k][1]);
                                if (gridViews[k]!=null)
                                    gridViews[k].setText(courses[k][0]);
                                else
                                    Log.e(TAG, "#197 : grid textview array null");
                            }
                        }else
                            Log.e(TAG, "#386 : null assigned courses");
                        //filling up the drawer options
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }else{
                Log.e(TAG, "line 392: current user reference is null");
            }
        }else{
            Log.e(TAG, "line 395: current Firebase user is null");
        }
    }

}

