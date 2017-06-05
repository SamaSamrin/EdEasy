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

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
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
    String studentID;
    String roleFromSignup;

    //database
    FirebaseDatabase database;
    DatabaseReference studentsDatabaseReference;
    DatabaseReference teachersDatabaseReference;
    ChildEventListener studentsChildEventListener;
    ChildEventListener teachersChildEventListener;
    //auth
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    private static final int RC_SIGN_IN = 1;

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

        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                username = bundle.getString("fullname");
                Log.e(TAG, "username = " + username);
                email = bundle.getString("email");
                Log.e(TAG, "email = "+email);
                password = bundle.getString("password");
                Log.e(TAG, "password = " + password);
                studentID = bundle.getString("studentID");
                Log.e(TAG, "student ID = " + studentID);
                boolean passwordMatched = bundle.getBoolean("password matched");
                Log.e(TAG, "received password matched? = "+passwordMatched);
                roleFromSignup = bundle.getString("role");
                Log.e(TAG, "role from signup = "+roleFromSignup);
                if (roleFromSignup != null) {
                    if (roleFromSignup.equals("teacher"))
                        attachTeachersDatabaseListener();
                    else if (roleFromSignup.equals("student"))
                        attachTeachersDatabaseListener();
                }
                createAccount();
                //Toast.makeText(Welcome.this, "received password match? " + String.valueOf(passwordMatched), Toast.LENGTH_SHORT).show();
            } else
                Log.e(TAG, "received bundle is null");
        }else
            Log.e(TAG, "received intent is null");
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
                if(user!=null) {
                    //user is signed in
                    Log.e(TAG, "user signed in");
                    String user_name = user.getDisplayName();
                    String user_email = user.getEmail();
                    Log.e(TAG, "on auth state changed: name = "+user_name+" , email = "+user_email);
                    whenSignedIn("Username", user_email);
                    String user_id = user.getUid();
                    Log.e(TAG, "on auth state changed: user id = "+user_id);
                    }
                else {
                    //user is signed out
                    Log.e(TAG, "user signed out");
                    //onSignedOut();
//                    startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
//                            .setIsSmartLockEnabled(false)
//                            .setProviders(Arrays.asList(
//                                    new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
//                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
//                            ))
//                            .build(), RC_SIGN_IN);
                }
            }
        };

    }

    void getValues(){
        if (email==null && password==null){
            if (normalEmailInput.getText() != null)
                email = normalEmailInput.getText().toString().trim();
            if (email.length()==0){
                normalEmailInput.setError("email cannot be empty");
                normalEmailInput.requestFocus();
            }
            if (normalPasswordInput.getText() != null)
                password = normalPasswordInput.getText().toString().trim();
            if (password.length()==0){
                normalPasswordInput.setError("password cannot be empty");
                normalPasswordInput.requestFocus();
            }
        }
    }

    void whenSignedIn(String name, String email){
        Intent intent = new Intent(Welcome.this, NavDrawer.class);
        intent.putExtra("email", email);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    void onSignedIn(String username){
        this.username = username;
        if (selectedRole.equals("student"))
            attachStudentDatabaseListener();
        else if(selectedRole.equals("teacher"))
            attachTeachersDatabaseListener();
    }

    void onSignedOut(){
        this.username = "ANONYMOUS";
        detachDatabaseListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            Log.e(TAG, "Activity from RC_SIGN_IN");
            if (resultCode == RESULT_OK)
                Toast.makeText(Welcome.this, "Signed in", Toast.LENGTH_SHORT).show();
            else if (resultCode == RESULT_CANCELED)
                Toast.makeText(Welcome.this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener != null)
            auth.removeAuthStateListener(authStateListener);
        detachDatabaseListeners();
    }

    private void createAccount() {//for sign up
        Log.e(TAG, "inside createAccount");
        if (email!=null && password!=null) {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.e(TAG, "create account : on complete");
                            if (task.isSuccessful())
                                Log.e(TAG, "task successful");
                            else {
                                Log.e(TAG, "task not successful"+task.getException().getMessage());
                            }
                        }
                    });
        }else
            Log.e(TAG, "createAccount() : email or password is null");
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

    void detachDatabaseListeners(){
        if (studentsChildEventListener != null) {
            studentsDatabaseReference.removeEventListener(studentsChildEventListener);
            studentsChildEventListener = null;
        }
        if (teachersChildEventListener != null) {
            teachersDatabaseReference.removeEventListener(teachersChildEventListener);
            teachersChildEventListener = null;
        }
    }


    void signIn(){
        //roleSelection();
        Log.e(TAG, "selected role = "+selectedRole);
        if (email!=null && password!=null ) {
            if (email.length()!=0 && password.length()!=0) {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.e(TAG, "sign in : on complete ");
                                if (task.isSuccessful())
                                    Log.e(TAG, "task successful");
                                else {
                                    if (task.getException().getMessage().contains("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                        createAccount();
                                        signIn();
                                    }
                                    Log.e(TAG, "task not successful " + task.getException().getMessage());
                                }
                            }
                        });
            }else
                Log.e(TAG, "signIn() : email or password is empty");
        }else
            Log.e(TAG, "signIn() : email or password is null");
    }

    public void postSignIn(View view){
        getValues();
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
