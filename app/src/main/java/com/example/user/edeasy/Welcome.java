package com.example.user.edeasy;

import android.content.Intent;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Welcome extends AppCompatActivity {

    private static final String TAG = "**Welcome**";
    TabHost tabHost_login;
    EditText normalEmailInput;
    EditText normalPasswordInput;
    EditText googleEmailInput;
    EditText googlePasswordInput;

    String selectedRole = "";
    String username = "ANONYMOUS";
    String email;
    String password;

    //database
    FirebaseDatabase database;
    DatabaseReference studentsDatabaseReference;
    DatabaseReference teachersDatabaseReference;
    ChildEventListener studentsChildEventListener;
    ChildEventListener teachersChildEventListener;
    //auth
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        databaseInitialization();
        authenticationInitialization();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);

        tabHostHandling();

        normalEmailInput = (EditText) findViewById(R.id.email_input);
        normalPasswordInput = (EditText) findViewById(R.id.password_input);
        googleEmailInput = (EditText) findViewById(R.id.email_input_google);
        googlePasswordInput = (EditText) findViewById(R.id.password_input_google);

        roleSelection();

    }

    void databaseInitialization(){
        database = FirebaseDatabase.getInstance();
        studentsDatabaseReference = database.getReference("users").child("students");
        teachersDatabaseReference = database.getReference("users").child("teachers");

        attachStudentDatabaseListener();
        attachTeachersDatabaseListener();
    }

    void authenticationInitialization(){
        auth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null)
                    Log.e(TAG, user.getDisplayName());
            }
        };

    }

    void tabHostHandling(){
        tabHost_login = (TabHost) findViewById(R.id.tabHost_login);
        tabHost_login.setup();

        TabHost.TabSpec spec = tabHost_login.newTabSpec("usual login");
        spec.setContent(R.id.tab1_login);
        if(Build.VERSION.SDK_INT >= 21)
            spec.setIndicator("", getResources().getDrawable(R.drawable.email_1, null));
        else
            spec.setIndicator("Login");
        tabHost_login.addTab(spec);

        spec = tabHost_login.newTabSpec("google login");
        spec.setContent(R.id.tab2_login);
        if(Build.VERSION.SDK_INT >= 21)
            spec.setIndicator("", getResources().getDrawable(R.drawable.google, null));
        else
            spec.setIndicator("Google");
        tabHost_login.addTab(spec);
    }

     void roleSelection(){
        RadioGroup roleSelectionGroup = (RadioGroup) findViewById(R.id.radiogroup_user_type_selection);
        roleSelectionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.radiobutton_student :
                        selectedRole = "student";
                        break;
                    case R.id.radiobutton_teacher :
                        selectedRole = "teacher";
                        break;
                }
            }
        });
    }

    void attachStudentDatabaseListener(){
        studentsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                if(username!=null) username = user.getFull_name();
                Toast.makeText(Welcome.this, "username is = "+username, Toast.LENGTH_SHORT).show();
//                HashMap<String, Object> objectHashMap = (HashMap<String, Object>) dataSnapshot.getValue();
//                if (objectHashMap == null){
//                    Log.e(TAG, "null hashmap dataSnapshot");
//                }else {
//                    System.out.println(objectHashMap.keySet());
//                    //String username = objectHashMap.get("full_name").toString();
//                    //Log.e(TAG, "child added = " + username);
//                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        studentsDatabaseReference.addChildEventListener(studentsChildEventListener);
    }

    void attachTeachersDatabaseListener(){
        teachersChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        teachersDatabaseReference.addChildEventListener(teachersChildEventListener);
    }


    void signIn(){
        //roleSelection();
        Log.e(TAG, "selected role = "+selectedRole);
        email = normalEmailInput.getText().toString();
        String password = normalPasswordInput.getText().toString();
        if(email!=null && password!=null) {
            if (email.equals("") && password.equals(""))
                Log.e(TAG, "email or password is empty");
            else {
                User user = new User(email, password);//added to users database
                //user.setRole(selectedRole);
                if(!selectedRole.equals("")) {
                    user.setRole(selectedRole);
                    Log.e(TAG, "created user is a "+user.getRole());
                    if (selectedRole.equals("student"))
                        studentsDatabaseReference.push().setValue(user);///****
                    else if (selectedRole.equals("teacher"))
                        teachersDatabaseReference.push().setValue(user);
                }else{
                    Log.e(TAG, "user's role is not set");
                }
            }
        }else{
            Log.e(TAG, "email or password is null");
        }
    }

    public void postSignIn(View view){
        signIn();
        //auth.signInWithEmailAndPassword(email, "student1");
        Intent intent = new Intent(Welcome.this, NavDrawer.class);
        intent.putExtra("username", username);
        intent.putExtra("role", selectedRole);
        intent.putExtra("email", email);
        startActivity(intent);
    }

    public void goToSignUp(View view){
        Intent intent = new Intent(Welcome.this, SignUp.class);
        startActivity(intent);
    }
}
