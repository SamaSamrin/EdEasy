package com.example.user.edeasy;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Welcome extends AppCompatActivity {

    private static final String TAG = "**Welcome**";
    TabHost tabHost_login;
    EditText normalEmailInput;
    EditText normalPasswordInput;
    EditText googleEmailInput;
    EditText googlePasswordInput;

    FirebaseDatabase database;
    DatabaseReference usersDatabaseReference;
    ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        databaseInitialization();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);

        tabHostHandling();

        normalEmailInput = (EditText) findViewById(R.id.email_input);
        normalPasswordInput = (EditText) findViewById(R.id.password_input);
        googleEmailInput = (EditText) findViewById(R.id.email_input_google);
        googlePasswordInput = (EditText) findViewById(R.id.password_input_google);
    }

    void databaseInitialization(){
        database = FirebaseDatabase.getInstance();
        usersDatabaseReference = database.getReference("users");
        childEventListener = new ChildEventListener() {
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
        usersDatabaseReference.addChildEventListener(childEventListener);
    }

    void tabHostHandling(){
        tabHost_login = (TabHost) findViewById(R.id.tabHost_login);
        tabHost_login.setup();

        TabHost.TabSpec spec = tabHost_login.newTabSpec("usual login");
        spec.setContent(R.id.tab1_login);
        spec.setIndicator("Login");
        tabHost_login.addTab(spec);

        spec = tabHost_login.newTabSpec("google login");
        spec.setContent(R.id.tab2_login);
        spec.setIndicator("Google");
        tabHost_login.addTab(spec);
    }

    void signIn(){
        String email = normalEmailInput.getText().toString();
        String password = normalPasswordInput.getText().toString();
        if(email!=null && password!=null) {
            if (email.equals("") && password.equals(""))
                Log.e(TAG, "email or password is empty");
            else {
                User user = new User(email, password);//added to users database
                usersDatabaseReference.push().setValue(user);///****
            }
        }else{
            Log.e(TAG, "email or password is null");
        }
    }

    public void postSignIn(View view){
        signIn();
        Intent intent = new Intent(Welcome.this, Dashboard.class);
        startActivity(intent);
    }

    public void signUp(View view){

    }
}
