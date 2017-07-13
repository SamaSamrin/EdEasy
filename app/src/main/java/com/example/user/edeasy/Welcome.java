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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    String department;
    Course[] courses;
    String roleFromSignup;
    String user_key;

    User user;
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
    //storage
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference CSE_storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        databaseInitialization();
        authenticationInitialization();
        storageInitialization();

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
                Log.e(TAG, "checking Intent: username = " + username);
                email = bundle.getString("email");
                Log.e(TAG, "checking Intent: email = "+email);
                password = bundle.getString("password");
                Log.e(TAG, "checking Intent: password = " + password);
                studentID = bundle.getString("studentID");
                Log.e(TAG, "checking Intent: student ID = " + studentID);
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

    void databaseInitialization() {
        Log.e(TAG, "databaseInitialization");
        database = FirebaseDatabase.getInstance();
        Log.e(TAG, "database reference = "+database.getReference().toString());
        studentsDatabaseReference = database.getReference("users").child("students");
        teachersDatabaseReference = database.getReference("users").child("teachers");

        attachStudentDatabaseListener();
        attachTeachersDatabaseListener();
    }

    void authenticationInitialization(){
        Log.e(TAG, "authenticationInitialization");
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
                    String user_id = user.getUid();
                    //user_key = user_id;
                    Log.e(TAG, "on auth state changed: user id = "+user_id);
                    //go to nav drawer with these user info
                    whenSignedIn("Username", user_email);
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

    void storageInitialization(){
        Log.e(TAG, "storageInitialization");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        CSE_storageReference = storageReference.child("CSE");
    }

    void getValues(){
        Log.e(TAG, "getValues");
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
            if (selectedRole.isEmpty()) {
                Log.e(TAG, "getValues() - role is empty");
                roleSelection();
            }
        }
    }

    void whenSignedIn(String name, String email){
        Log.e(TAG, "whenSignedIn");
        Intent intent = new Intent(Welcome.this, NavDrawer.class);
        intent.putExtra("email", email);
        intent.putExtra("name", name);
        intent.putExtra("parent", "Welcome");
        intent.putExtra("role", selectedRole);
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
        Log.e(TAG, "onActivityResult");
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
        Log.e(TAG, "onStart");
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
        if(authStateListener != null)
            auth.removeAuthStateListener(authStateListener);
        detachDatabaseListeners();
    }

    private void createAccount() {//for sign up
        Log.e(TAG, "createAccount");
        if (email!=null && password!=null) {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.e(TAG, "create account : on complete");
                            if (task.isSuccessful()) {
                                Log.e(TAG, "creating account successful");
                                addNewUserToDatabase(email);
                            }
                            else {
                                Log.e(TAG, "creating account not successful"+task.getException().getMessage());
                            }
                        }
                    });
        }else
            Log.e(TAG, "createAccount() : email or password is null");
    }

    void tabHostHandling(){
        Log.e(TAG, "tabHostHandling");
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
         Log.e(TAG, "roleSelection");
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
        Log.e(TAG, "attachStudentDatabaseListener");
        studentsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                user = dataSnapshot.getValue(User.class);
                Log.e(TAG, "on child added, s="+s);
                if(username!=null) username = user.getFull_name();
                Log.e(TAG, "new student : username = "+username);
                Toast.makeText(Welcome.this, "username is = "+username, Toast.LENGTH_SHORT).show();
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
        Log.e(TAG, "attachTeachersDatabaseListener");
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
        Log.e(TAG, "detachDatabaseListeners");
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
        Log.e(TAG, "signIn");
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
        Log.e(TAG, "postSignIn");
        getValues();
        signIn();
        //addNewUserToDatabase(email);
        //auth.signInWithEmailAndPassword(email, "student1");
        FirebaseUser user = auth.getCurrentUser();
        if (user==null)
            Log.e(TAG, "postSignIng: current user is null");
        else
            Log.e(TAG, "postSignIn: current user is - "+user.getEmail());

        Intent intent = new Intent(Welcome.this, NavDrawer.class);
        intent.putExtra("username", username);
        Log.e(TAG, "postSignIn: username = "+username);
        intent.putExtra("role", selectedRole);
        Log.e(TAG, "postSignIn: role = "+selectedRole);
        intent.putExtra("email", email);
        Log.e(TAG, "postSignIn: email = "+email);
        intent.putExtra("parent", "Welcome");
//        if (user_key != null) {
//            intent.putExtra("key", user_key);
//            Log.e(TAG, "postSignIn: "+user_key);
//        }else{
//            Log.e(TAG, "postSignIn: user key is null");
//        }
        startActivity(intent);
    }

    public void goToSignUp(View view){
        Intent intent = new Intent(Welcome.this, SignUp.class);
        startActivity(intent);
    }

    void addNewUserToDatabase(String emailInput){
        Log.e(TAG, "addNewUserToDatabase");
        int croppedEmailIdLimit = emailInput.length() - 4;
        String emailID = emailInput.substring(0, croppedEmailIdLimit);
        User user = new User(emailInput, password);
        DatabaseReference newRef = null;
        //user_key = auth.getCurrentUser().getUid();
        if (emailInput != null) {
            if (selectedRole.equals("student")) {
                newRef = studentsDatabaseReference.child(emailID);
                //newRef.setValue(user);
                //user_key = newRef.getKey();
            } else if (selectedRole.equals("teacher")) {
                newRef = teachersDatabaseReference.child(emailID);
                //newRef.setValue(user);
                //user_key = newRef.getKey();
            }
            if (newRef != null) {
                Log.e(TAG, "adding values to new keys of the new user");
                newRef.child("email").setValue(emailInput);
                newRef.child("password").setValue(password);
                newRef.child("role").setValue(selectedRole);
//                user_key = auth.getCurrentUser().getUid();
//                user.setKey(user_key);
//                Log.e(TAG, "new user's key: " + user_key);
            }else{
                Log.e(TAG, "addNewUser to db: new Ref is null");
            }
        }
    }
}
