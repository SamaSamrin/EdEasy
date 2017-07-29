package com.example.user.edeasy;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class Dashboard extends Fragment {

    private static final String TAG = "**Dashboard**";
    private OnFragmentInteractionListener mListener;

    String username;
    String user_email;
    String user_role = "default role";
    String user_department;
    String user_id;
    String user_key;
    int numberOfCourses;
    String[][] assignedCourses;
    String[] departments;
    MenuItem[] menuItems;
    User user;

    private View containerView;
    android.support.v7.widget.Toolbar toolbar;
    Menu myMenu;

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    DatabaseReference studentsDatabaseReference;
    DatabaseReference teachersDatabaseReference;
    DatabaseReference currentUserRef;
    StorageReference storageReference;
    StorageReference CSE_storageRef;
    FirebaseUser currentUser;

    GridView dashboard_gv;

    public Dashboard() {
        // Required empty public constructor
    }

    @Override
     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_nav_drawer);

        Log.e(TAG, "onCreate");

        Intent intent = getActivity().getIntent();
        username = intent.getStringExtra("username");
        user_email = intent.getStringExtra("email");
        Log.e(TAG, "username="+username+" user_email="+user_email);
        user_role = intent.getStringExtra("role");
        Log.e(TAG, "#92 : role = "+user_role);

        Bundle args = getArguments();
        if (args!=null) {
            user_role = args.getString("role");
            Log.e(TAG, "#95 : role = "+user_role);
            departments = args.getStringArray("departments");
            if (departments!=null){
                for (int i=0; i<departments.length; i++)
                    Log.e(TAG, "#97 : department at "+String.valueOf(i)+" = "+departments[i]);
            }else
                Log.e(TAG, "#99 : departments array is null");
            numberOfCourses = args.getInt("numberOfCourses");
            Log.e(TAG, "#100 : number of courses = "+numberOfCourses);
            assignedCourses = new String[numberOfCourses][2];
            for (int i=0; i<numberOfCourses; i++) {
                String[] courseInfo = args.getStringArray("course"+String.valueOf(i+1));
                if (courseInfo!=null){
                    Log.e(TAG, "#105 : course"+String.valueOf(i+1)+" = "+courseInfo[0]);
                    assignedCourses[i][0] = courseInfo[0];
                    Log.e(TAG, "#105 : section = "+courseInfo[1]);
                    assignedCourses[i][1] = courseInfo[1];
                }
            }
        }else
            Log.e(TAG, "#102 : received args is null");

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        studentsDatabaseReference = databaseReference.child("users").child("students");
        teachersDatabaseReference = databaseReference.child("users").child("teachers");
        storageReference = FirebaseStorage.getInstance().getReference();
        CSE_storageRef = storageReference.child("CSE");
        currentUser = auth.getCurrentUser();
        handleCurrentUserInfo();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        //Log.e(TAG, "Dashboard - onCreateView");

        View view = inflater.inflate(R.layout.activity_dashboard, container, false);

        dashboard_gv = (GridView) view.findViewById(R.id.dashboard_gridview);

        if(Build.VERSION.SDK_INT >= 23)
            dashboard_gv.setAdapter(new GridAdapter(getContext()));
        else
            dashboard_gv.setAdapter(new GridAdapter(getActivity().getApplicationContext()));

        dashboard_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(Dashboard.this, ""+String.valueOf(i+1), Toast.LENGTH_LONG).show();
                Intent intent = null;
                if(i==0){
                    intent = new Intent(getContext(), PreviousResults.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", user_email);
                    intent.putExtra("role", user_role);
                    startActivity(intent);
                }else if(i==1){
                    intent = new Intent(getContext(), CurrentRoutine.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", user_email);
                    intent.putExtra("role", user_role);
                    intent.putExtra("number", numberOfCourses);
                    intent.putExtra("departments", departments);
                    intent.putExtra("number", numberOfCourses);
                    for (int j=0; j<numberOfCourses; j++){
                        intent.putExtra("course"+String.valueOf(j+1),
                                assignedCourses[j]);
                    }
                    startActivity(intent);
                }else if(i==2){//done
                    intent = new Intent(getContext(), CurrentMarksheet.class);
                    startActivity(intent);
                }else if (i==3){
                    intent = new Intent(getContext(), Chatroom.class);
                    intent.putExtra("username", username);
                    Log.e(TAG, "#164 : username = "+username);
                    Log.e(TAG, "department's length = "+departments.length);
                    for (int j = 0; j <departments.length ; j++) {
                        Log.e(TAG, "#165 : department of course "+j+" = "+departments[j]);
                    }
                    intent.putExtra("departments", departments);
                    intent.putExtra("number", numberOfCourses);
                    for (int j=0; j<numberOfCourses; j++){
                        intent.putExtra("course"+String.valueOf(j+1),
                                assignedCourses[j]);
                    }
                    startActivity(intent);
                }else if (i==4){
                    intent = new Intent(getContext(), CalendarDisplay.class);
                    intent.putExtra("departments", departments);
                    intent.putExtra("number", numberOfCourses);
                    for (int j=0; j<numberOfCourses; j++){
                        intent.putExtra("course"+String.valueOf(j+1),
                                assignedCourses[j]);
                    }
                    startActivity(intent);
                }else if (i==5){
                    intent = new Intent(getContext(), NotificationsDisplay.class);
                    startActivity(intent);
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            Log.e(TAG, "context is an instance of OnFragmentInteractionListener");
            mListener = (OnFragmentInteractionListener) context;

        } else {
            Log.e(TAG, "context is NOT an instance of OnFragmentInteractionListener");
            /*throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.nav_drawer, menu);
        Log.e(TAG, "on create options menu");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    void handleCurrentUserInfo(){//whole process of retrieving current user data
        Log.e(TAG, "handleCurrentUserInfo");
        if(currentUser != null){
            Log.e(TAG, "line 334: current user is not null");
            user_email = currentUser.getEmail();
            Log.e(TAG, "line 336: current user email = " + user_email);

            //retrieving user's info from database
            int croppedEmailIdLimit = user_email.length() - 4;
            String emailID = user_email.substring(0, croppedEmailIdLimit);
            //assuming the user is a student
            currentUserRef = studentsDatabaseReference.child(emailID);
            if (currentUserRef != null){
                Log.e(TAG, "line 344: current user reference is - "+currentUserRef.toString());
                //****************NAME*****************
                DatabaseReference nameRef = currentUserRef.child("name");
                nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        username = dataSnapshot.getValue(String.class);
                        Log.e(TAG, "line 351: the current username from snapshot is = "+username);
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
                        Log.e(TAG, "#366 : number of courses = "+ String.valueOf(numberOfCourses));
                        assignedCourses = new String[numberOfCourses][2];
                        departments = new String[numberOfCourses];
                        int i = 1;
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            String key = postSnapshot.getKey();
                            String course = postSnapshot.child("course_code").getValue(String.class);
                           // Log.e(TAG, "course = "+course);
                            Long section = postSnapshot.child("section").getValue(long.class);
                            //Log.e(TAG, "section = "+String.valueOf(section));
                            assignedCourses[i-1][0] = course;//courseID
                            assignedCourses[i-1][1] = String.valueOf(section);//section
                            String department = postSnapshot.child("department").getValue(String.class);
                            //Log.e(TAG, "department = "+department);
                            departments[i-1] = department;
                            i++;
                        }
                        //NavDrawer.this.notifyAll();
                        //checking if assigned courses are retrieved correctly
                        if (assignedCourses != null){
                            for (int k=0; k<assignedCourses.length; k++){
                                Log.e(TAG, "#382: department="+departments[k]+" course "+String.valueOf(k)+" : "+
                                        assignedCourses[k][0]+" section "+assignedCourses[k][1]);
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

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}

class GridAdapter extends BaseAdapter{

    Context context;
    String[] dashboard_items = {"PREVIOUS RESULTS", "CURRENT ROUTINE", "CURRENT MARKSHEET",
            "CHATROOM", "CALENDER", "NOTIFICATIONS"};
    String[] colorBacks = {"#FFEA00", "#76FF03", "#00B0FF", "#BA68C8", "#EC407A", "#FF3D00"};
//    int[] colors = {Color.CYAN, Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.RED, Color.BLACK};
//    int[] colors2 = {R.color.previousResultsBackg, R.color.currentRoutineBackg,
//                      R.color.currentMarksheetBackg, R.color.courseMaterialsBackg,
//                      R.color.calendarBackg, R.color.notificationsBackg};

    GridAdapter(Context context){
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        return dashboard_items.length;
    }

    @Override
    public Object getItem(int i) {
        return dashboard_items[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView itemText = null;
        if(view==null){
            itemText = new TextView(context);
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int screenwidth = metrics.widthPixels;
            int orientation = context.getResources().getConfiguration().orientation;
            if(orientation== Configuration.ORIENTATION_PORTRAIT)
                itemText.setLayoutParams(new GridView.LayoutParams(screenwidth/2, screenwidth/2));
            else
                itemText.setLayoutParams(new GridView.LayoutParams(screenwidth/3, screenwidth/3));
            itemText.setPadding(10, 10, 10, 10);
            itemText.setGravity(Gravity.CENTER);
            itemText.setTextColor(Color.WHITE);
            itemText.setTypeface(itemText.getTypeface(), Typeface.BOLD);
        }else{
            itemText = (TextView) view;
        }
        itemText.setText(dashboard_items[i]);
        itemText.setBackgroundColor(Color.parseColor(colorBacks[i]));
        return itemText;
    }
}
