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
    String[] courses = new String[5];
    User user;

    private View containerView;
    Toolbar toolbar;

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    DatabaseReference studentsDatabaseReference;
    DatabaseReference teachersDatabaseReference;
    DatabaseReference currentUserRef;
    StorageReference storageReference;
    StorageReference CSE_storageRef;
    FirebaseUser currentUser;

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
        studentsDatabaseReference = databaseReference.child("students");
        teachersDatabaseReference = databaseReference.child("teachers");
        storageReference = FirebaseStorage.getInstance().getReference();
        CSE_storageRef = storageReference.child("CSE");
        currentUser = auth.getCurrentUser();

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            if (bundle.getString("parent")!=null) {
                Log.e(TAG, "bundle is not null");
                username = bundle.getString("name");
                user_email = bundle.getString("email");
                user_role = bundle.getString("role");
                //user_key = bundle.getString("key");
                //setting drawer's header's name and email, but cannot get the header view
//                if (drawer_header_name != null) {
//                    drawer_header_name.setText(username);
//                    Log.e(TAG, "drawer header username = "+username);
//                }else{
//                    Log.e(TAG, "drawer header name textview is null");
//                }
//                if (drawer_header_email != null) {
//                    drawer_header_email.setText(user_email);
//                    Log.e(TAG, "drawer header email = "+user_email);
//                }else{
//                    Log.e(TAG, "drawer header email textview is null");
//                }
            }else{
                Log.e(TAG, "bundle's parent is null");
                //cannot get the header views properly, always show null
//                if (drawer_header_name != null && drawer_header_email != null) {
//                    drawer_header_name.setText(username);
//                    drawer_header_email.setText(user_email);
//                }else{
//                    Log.e(TAG, "drawer header email and name both null");
//                }
            }
        }else{
            Log.e(TAG, "bundle is null");
        }

        if(currentUser != null){
            user_id = currentUser.getUid();
            Log.e(TAG, "current user ID = "+user_id);
            user_email = currentUser.getEmail();
            Log.e(TAG, "current user email = " + user_email);
            //GET THE USER KEY FROM THE USERS' DATABASE USING THIS EMAIL

            user_key = studentsDatabaseReference.child("email").orderByValue().equalTo(user_email).toString();
            Log.e(TAG, "current user key = "+user_key);

            //if (user_role.equals("student")) {****USER ROLE NOT RECEIVED PROPERLY******
                currentUserRef = studentsDatabaseReference.child(user_id);
//            }else if (user_role.equals("teacher")){
//                currentUserRef = teachersDatabaseReference.child(user_id);
//            }
            //username = currentUser.getDisplayName();
        }else{
            Log.e(TAG, "current Firebase user is null");
        }

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
        //containerView = navigationView;
        navigationView.setNavigationItemSelectedListener(this);

        //cannot get the views properly
        TextView drawer_header_name = (TextView) navigationView.findViewById(R.id.drawer_header_title);
        if (drawer_header_name != null)
            Log.e(TAG, "drawer header name is - "+drawer_header_name.toString());
        else
            Log.e(TAG, "drawer header name is still null");
        TextView drawer_header_email = (TextView) findViewById(R.id.drawer_header_subtitle);


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
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        Log.e(TAG, "on create options menu");
        return true;
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
        //Log.e(TAG, "on Navigation Item Selected");

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
                    toolbar.setTitle("CSE110");
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
            case (R.id.course2_drawer_item) :
                fragment = new CourseTwoMaterials();
                toolbar.setTitle("MAT120");
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
            case (R.id.course3_drawer_item) :
                fragment = new CourseThreeMaterials();
                toolbar.setTitle("PHY111");
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                break;
            case (R.id.course4_drawer_item) :
                fragment = new CourseFourMaterials();
                toolbar.setTitle("ENG101");
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