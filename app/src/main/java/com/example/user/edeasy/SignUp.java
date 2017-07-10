package com.example.user.edeasy;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {

    String TAG = "Sign Up";

    String[] departments = {"CSE", "EEE", "MNS", "PHR", "ECO", "BBS", "BIL", "ARC"};
    EditText fullNameInput;
    EditText emailInput;
    EditText passwordInput;
    EditText passwordConfirmationInput;
    EditText studentIDInput;
    EditText phoneNumberInput;
    RadioGroup roleGroup;
    //EditText departmentInput;

    String fullName;
    String email;
    String password;
    String passwordReEntered;
    String studentID;
    String phoneNumber;
    String department;
    String role;
    boolean passwordMatched = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final Spinner departmentDropdown = (Spinner) findViewById(R.id.department_dropdown);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                departments);
        departmentDropdown.setAdapter(adapter);
        departmentDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                department = departments[i];
                Log.e(TAG, "selected department = "+department);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fullNameInput = (EditText) findViewById(R.id.fullNameInput);
        emailInput = (EditText) findViewById(R.id.email_input_signup);
        passwordInput = (EditText) findViewById(R.id.normal_password_input);
        passwordConfirmationInput = (EditText) findViewById(R.id.password_confirmation_input);
        studentIDInput = (EditText) findViewById(R.id.student_id_input);
        phoneNumberInput = (EditText) findViewById(R.id.phone_number_input);
        roleGroup = (RadioGroup) findViewById(R.id.role_signup);
        roleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case (R.id.role_teacher_signup) :
                        role = "teacher";
                        Log.e(TAG, "teacher role selected");
                        break;
                    case (R.id.role_student_signup) :
                        role = "student";
                        Log.e(TAG, "student role selected");
                        break;
                }
            }
        });
    }

    void getAllInputs(){
        fullName = fullNameInput.getText().toString();
        //Log.e(TAG, "name input = "+fullName);
        if (emailInput != null) {
            email = emailInput.getText().toString();
            Log.e(TAG, "email input = "+email);
        }else
            Log.e(TAG, "email input null");
        if (passwordInput != null) {
            password = passwordInput.getText().toString();
            //Log.e(TAG, "password input = "+password);
        }else
            Log.e(TAG, "password input null");
        if (passwordConfirmationInput != null) {
            passwordReEntered = passwordConfirmationInput.getText().toString();
            //Log.e(TAG, "password confirmation input = "+passwordReEntered);
        }else
            Log.e(TAG, "password confirmation input null");
        if (passwordInput!= null && passwordInput != null && !password.equals(passwordReEntered))
            passwordMatched = false;
        //Log.e(TAG, "password matched? = "+passwordMatched);
        if (studentIDInput != null) {
            studentID = studentIDInput.getText().toString();
            //Log.e(TAG, "student ID input = "+studentID);
        }else
            Log.e(TAG, "student ID input null");
        if (phoneNumberInput != null) {
             phoneNumber = phoneNumberInput.getText().toString();
            Log.e(TAG, "phone number input = "+phoneNumber);
        }else
            Log.e(TAG, "phone number input null");
    }

    public void goBackToSignIn(View view){//goes back to user profile
        getAllInputs();
        if (!passwordMatched)
            Toast.makeText(SignUp.this, "passwords dont match", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SignUp.this, AccountProfile.class);
        intent.putExtra("fullname", fullName);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        intent.putExtra("password matched", passwordMatched);
        intent.putExtra("studentID", studentID);
        intent.putExtra("phone number", phoneNumber);
        intent.putExtra("department", department);
        intent.putExtra("role", role);
        startActivity(intent);
    }
}
