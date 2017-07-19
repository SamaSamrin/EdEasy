package com.example.user.edeasy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ChatroomCourse1 extends Activity {

    private final String TAG = "** Chatroom Course 1**";

    String userFrom;
    String userTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_course1);

        Intent intent = getIntent();
        userFrom = intent.getStringExtra("from");
        //userFrom = getOnlyName(userFrom);
        Log.e(TAG, "#21 : user from = "+userFrom);
    }
}
