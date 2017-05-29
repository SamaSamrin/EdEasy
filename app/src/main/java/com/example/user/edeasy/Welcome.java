package com.example.user.edeasy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;

public class Welcome extends AppCompatActivity {

    private static final String TAG = "**Welcome**";
    TabHost tabHost_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);

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

    public void postSignIn(View view){
        Intent intent = new Intent(Welcome.this, Dashboard.class);
        startActivity(intent);
    }

    public void signUp(View view){

    }
}
