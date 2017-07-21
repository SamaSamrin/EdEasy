package com.example.user.edeasy;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

//import layout.CourseOneMaterials;

public class NavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "**NavDrawer**";

    String username;
    String user_email;
    String user_role = "default role";
    String user_department;
    String user_id;
    String user_key;
    int numberOfCourses;
    String[][] assignedCourses;
    MenuItem[] menuItems;
    User user;

    private View containerView;
    Toolbar toolbar;
    Menu myMenu;

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    DatabaseReference studentsDatabaseReference;
    DatabaseReference teachersDatabaseReference;
    DatabaseReference currentUserRef;
    StorageReference storageReference;
    StorageReference CSE_storageRef;
    FirebaseUser currentUser;

    Bundle bundle;
    TextView nav_user;
    TextView nav_user_email;

    String courseOneName;
    String courseTwoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        //containerView = (View) findViewById(R.id.nav_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Dashboard");

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        studentsDatabaseReference = databaseReference.child("users").child("students");
        teachersDatabaseReference = databaseReference.child("users").child("teachers");
        storageReference = FirebaseStorage.getInstance().getReference();
        CSE_storageRef = storageReference.child("CSE");
        currentUser = auth.getCurrentUser();

        //myMenu = (Menu)findViewById(R.id.top_drawer_menu);
        menuItems = new MenuItem[5];

        bundle = getIntent().getExtras();

        Fragment fragment = new Dashboard();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.fragment_container,fragment).commit();
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        Log.e(TAG, "line 109: headerView is - "+hView.toString());
        nav_user = (TextView)hView.findViewById(R.id.drawer_header_title);
        Log.e(TAG, "line 111: setting username - "+username);
        nav_user.setText(username);
        nav_user_email = (TextView) hView.findViewById(R.id.drawer_header_subtitle);
        nav_user_email.setText(user_email);
        myMenu = navigationView.getMenu();

        navigationView.setNavigationItemSelectedListener(this);

        handleCurrentUserInfo();//whole process of retrieving current user data
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.e(TAG, "on create options menu");
        getMenuInflater().inflate(R.menu.activity_nav_drawer_drawer, menu);
        return true;
    }

    public void updateOptionsMenu(Menu menu){
        if (assignedCourses!=null) {
            MenuItem item = null;
            for (int i=0; i<assignedCourses.length; i++) {
                switch (i){
                    case 0 :
                        item = menu.findItem(R.id.course1_drawer_item);
                        break;
                    case 1 :
                        item = menu.findItem(R.id.course2_drawer_item);
                        break;
                    case 2 :
                        item = menu.findItem(R.id.course3_drawer_item);
                        break;
                    case 3 :
                        item = menu.findItem(R.id.course4_drawer_item);
                        break;
                    case 4 :
                        item = menu.findItem(R.id.course5_drawer_item);
                        break;
                }
                if (item!=null)
                    item.setTitle(assignedCourses[i][0]);
                else
                    Log.e(TAG, "#168, menu item is null");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //Log.e(TAG, "on Options Item Selected");
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_log_out) {
            logOut();
            return true;
        }else if (id == R.id.action_settings){
            Log.e(TAG, "settings action");
            Intent i = new Intent(NavDrawer.this, Settings.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Log.e(TAG, "on Navigation Item Selected");

        int id = item.getItemId();
        Fragment fragment = new Dashboard();
        FragmentManager fragmentManager = getSupportFragmentManager();
        //fragmentManager.beginTransaction().add(containerView.getId(), fragment).commit();

        Intent i = null;
        switch (id) {
            case (R.id.dashboard_drawer_option) :
                //loads dashboard activity
                Log.e(TAG, "dashboard drawer");
                fragment = new Dashboard();
               // fragmentManager.beginTransaction().commit();//.addToBackStack(null).commit()
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                //i = new Intent(NavDrawer.this, Dashboard_Activity.class);
//                startActivity(i);
                break;
            case  (R.id.account_drawer_option) :
                //loads account activity
                Log.e(TAG, "account drawer");
                toolbar.setTitle("Account");
                fragment = new AccountProfile();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
            case (R.id.course1_drawer_item) :
                fragment = new CourseOneMaterials();
                //Toolbar toolbar = (Toolbar) view.findViewById(R.id.dashboard_toolbar);
                if(toolbar != null)
                    toolbar.setTitle(assignedCourses[0][0]);
                Bundle args = new Bundle();
                args.putString("username", username);
                args.putString("course", assignedCourses[1][0]);
                args.putString("section", assignedCourses[1][1]);
                fragment.setArguments(args);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
            case (R.id.course2_drawer_item) :
                fragment = new CourseTwoMaterials();
                toolbar.setTitle(assignedCourses[1][0]);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
            case (R.id.course3_drawer_item) :
                fragment = new CourseThreeMaterials();
                toolbar.setTitle(assignedCourses[2][0]);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
            case (R.id.course4_drawer_item) :
                fragment = new CourseFourMaterials();
                toolbar.setTitle(assignedCourses[3][0]);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
            case (R.id.course5_drawer_item) :
                fragment = new CourseFiveMaterials();
                toolbar.setTitle(assignedCourses[4][0]);
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
//            case (R.id.settings_drawer_option) :
//                //loads settings activity
//                Log.e(TAG, "settings drawer");
//                i = new Intent(NavDrawer.this, Settings.class);
//                startActivity(i);
//                break;
            case (R.id.logout_drawer_option) :
                //loads logout dialogue
                Log.e(TAG, "logout drawer");
                logOut();
                break;
            }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer!=null)
            drawer.closeDrawer(GravityCompat.START);
        else
            Log.e(TAG, "null drawer");
        return true;
    }

    public void logOut(){
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Log out")
                .setMessage("Do you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        FirebaseUser user = auth.getCurrentUser();
                       // AuthUI authUI = AuthUI.getInstance();
                        if (user == null)
                            Log.e(TAG, "before signout : no current user");
                        auth.signOut();
//                        if (authUI != null)
//                            authUI.signOut(NavDrawer.this);
//                        else
//                            Log.e(TAG, "auth UI instance is null");
                        //if (user == null)
                          //  Log.e(TAG, "no current user");
                        Toast.makeText(NavDrawer.this, "Logging Out", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(NavDrawer.this, Welcome.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialogInterface) {
//                dialog.getButton(R.id.dia)
//            }
//        });
    }

    void databaseSingleEventListener(){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
                        nav_user.setText(username);
                        setUsername(username);
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
                        Log.e(TAG, "onDataChange : COURSES");
                        long numberOfChildren = dataSnapshot.getChildrenCount();
                        numberOfCourses = (int) numberOfChildren;
                        Log.e(TAG, "#366 : number of courses = "+ String.valueOf(numberOfCourses));
                        assignedCourses = new String[numberOfCourses][2];
                        int i = 1;
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            String key = postSnapshot.getKey();
                            String course = postSnapshot.child("course_code").getValue(String.class);
                            Log.e(TAG, "course = "+course);
                            Long section = postSnapshot.child("section").getValue(long.class);
                            Log.e(TAG, "section = "+String.valueOf(section));
                            assignedCourses[i-1][0] = course;//courseID
                            assignedCourses[i-1][1] = String.valueOf(section);//section
                            i++;
                        }
                        //NavDrawer.this.notifyAll();
                        //checking if assigned courses are retrieved correctly
                        if (assignedCourses != null){
                            for (int k=0; k<assignedCourses.length; k++){
                                Log.e(TAG, "#382: course "+String.valueOf(k)+" : "+
                                        assignedCourses[k][0]+" section "+assignedCourses[k][1]);
                            }
                        }else
                            Log.e(TAG, "#386 : null assigned courses");
                        //filling up the drawer options
                        updateOptionsMenu(myMenu);
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

    private void setUsername(String newname){
        username = newname;
        Log.e(TAG, "line 387: setUsername: "+username);
    }

    public String getUsername(){
        handleCurrentUserInfo();
        Log.e(TAG, "line 392: returning username = "+username);
        return username;
    }

}

class DrawerItemClickListener implements ListView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
        Log.e("NavDrawer : onItemClick", String.valueOf(position));
    }


    /**
     * Swaps fragments in the main content view
     */
    private void selectItem(int position) {
//        // Create a new fragment and specify the planet to show based on position
//        Fragment fragment = new PlanetFragment();
//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);
//
//        // Insert the fragment by replacing any existing fragment
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.content_frame, fragment)
//                .commit();
//
//        // Highlight the selected item, update the title, and close the drawer
//        mDrawerList.setItemChecked(position, true);
//        setTitle(mPlanetTitles[position]);
//        mDrawerLayout.closeDrawer(mDrawerList);
    }

//    @Override
    public void setTitle(CharSequence title) {
//        mTitle = title;
//        getActionBar().setTitle(mTitle);
    }
}

class FragmentListener implements Dashboard.OnFragmentInteractionListener{
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}