package com.example.user.edeasy;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class NotificationsDisplay extends AppCompatActivity {

    ListView notificationsList;
    String[] notifications = {"He posted new doc", "Sameer sent new message in chatroom", "SKJ updated CSE110 marksheet"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_notifications);
        setSupportActionBar(toolbar);

        notificationsList = (ListView) findViewById(R.id.notifications_listView);
        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, notifications);
        notificationsList.setAdapter(adapter);
        notificationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String x = String.valueOf(adapterView.getItemAtPosition(i));
                Toast.makeText(NotificationsDisplay.this, "you clicked "+x, Toast.LENGTH_SHORT).show();
            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }
}
