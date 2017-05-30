package com.example.user.edeasy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SignUp extends AppCompatActivity {

    String[] departments = {"CSE", "EEE", "MNS", "PHR", "ECO", "BBS", "BIL", "ARC"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Spinner departmentDropdown = (Spinner) findViewById(R.id.department_dropdown);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                departments);
        departmentDropdown.setAdapter(adapter);
    }

    public void goBackToSignIn(View view){
        Intent intent = new Intent(SignUp.this, Welcome.class);
        startActivity(intent);
    }
}
