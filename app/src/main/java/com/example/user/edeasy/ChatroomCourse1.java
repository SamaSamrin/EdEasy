package com.example.user.edeasy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatroomCourse1 extends Activity {

    private final String TAG = "** Chatroom Course 1**";

    String userFrom;
    String courseName;
    String section;

    DatabaseReference chatsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_course1);

        Intent intent = getIntent();
        userFrom = intent.getStringExtra("from");
        //userFrom = getOnlyName(userFrom);
        Log.e(TAG, "#32 : user from = "+userFrom);
        courseName = intent.getStringExtra("courseName");
        Log.e(TAG, "#34 : course name = "+courseName);
        section = intent.getStringExtra("section");
        Log.e(TAG, "#36 : section = "+section);

        if (courseName!=null & section != null) {
            chatsReference = FirebaseDatabase.getInstance().getReference()
                    .child("departments/CSE/courses").child(courseName).child("sections").
                            child(section).child("chatroom");
            Log.e(TAG, "chats reference is "+chatsReference.toString());
        }
    }

    public void sendMessage(View view){

    }

}
