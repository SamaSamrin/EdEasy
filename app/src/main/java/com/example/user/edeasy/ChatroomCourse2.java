package com.example.user.edeasy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatroomCourse2 extends Activity {

    private final String TAG = "** Chatroom Course 2**";

    String userFrom;
    String courseName;
    String section;
    String department;
    EditText messageInput;
    String message;
    ListView conversationList;
    ListAdapter messageListAdapter;
    //ArrayList<String> messages;
    String[] messages;
    String[] userFroms;
    String[] senders;
    //DatabaseReference chatsRef;
    int previousMessagesNumber;

    DatabaseReference chatsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_course2);

        //getting values from intent
        Intent intent = getIntent();
        userFrom = intent.getStringExtra("from");
        Log.e(TAG, "#32 : user from = "+userFrom);
        courseName = intent.getStringExtra("courseName");
        Log.e(TAG, "#34 : course name = "+courseName);
        section = intent.getStringExtra("section");
        Log.e(TAG, "#36 : section = "+section);
        department = intent.getStringExtra("department");
        Log.e(TAG, "#60 : department = "+department);

        if (Build.VERSION.SDK_INT>=21) {
            if (getActionBar()!=null)
                getActionBar().setTitle(courseName+" Section "+section);//working :D
        }

        //getting database reference to the respective chatroom
        if (courseName!=null & section != null) {
            chatsReference = FirebaseDatabase.getInstance().getReference()
                    .child("departments").child(department)
                    .child("courses").child(courseName)
                    .child("sections").child(section).child("chatroom");
            Log.e(TAG, "#70 : chats reference is "+chatsReference.toString());
        }

        //initializing all views
        messageInput = (EditText) findViewById(R.id.course2_message_input);
        conversationList = (ListView) findViewById(R.id.course2_chatroom_list);
        messages = new String[1];
        userFroms = new String[1];
        messages[0] = "Hey";
        userFroms[0] = userFrom;
        getAllMessage();
//        messageListAdapter = new ArrayAdapter<>(ChatroomCourse1.this,
//                android.R.layout.simple_list_item_1, messages);
        messageListAdapter = new ChatMessageAdapter(this, chatsReference,
                messages, userFroms, userFrom);
        conversationList.setAdapter(messageListAdapter);
    }

    void getAllMessage(){
        Log.e(TAG, "getAllMessage");
        chatsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "Activity: onDataChange");
                long messageNumber = dataSnapshot.getChildrenCount() - 1;
                previousMessagesNumber = (int) messageNumber;
                if (previousMessagesNumber<0)
                    previousMessagesNumber=0;
                else if (previousMessagesNumber==0)
                    previousMessagesNumber=1;
                messages = new String[previousMessagesNumber];
                senders = new String[previousMessagesNumber];
                if (previousMessagesNumber>0){
                    int messageCounter = 0;
                    for (DataSnapshot snap : dataSnapshot.getChildren()){
                        if (!snap.getKey().equals("description")){
                            String message = snap.child("text").getValue(String.class);
                            String senderName = snap.child("name").getValue(String.class);
                            messages[messageCounter] = message;
                            senders[messageCounter] = senderName;
                        }
                    }
                    ((BaseAdapter) messageListAdapter).notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendMessage(View view){
        Log.e(TAG, "sendMessage");
        message = messageInput.getText().toString();
        messageInput.setText("");
        messageInput.setHint("");
        DatabaseReference newMsgRef = chatsReference.push();
        FriendlyMessage friendlyMessage = new FriendlyMessage(message, userFrom );
        newMsgRef.setValue(friendlyMessage);
        getAllMessage();
    }


}