package com.example.user.edeasy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatroomCourse1 extends Activity {

    private final String TAG = "** Chatroom Course 1**";

    String userFrom;
    String courseName;
    String section;
    EditText messageInput;
    String message;
    ListView conversationList;
    ListAdapter messageListAdapter;
    ArrayList<String> messages;

    DatabaseReference chatsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_course1);

        //getting values from intent
        Intent intent = getIntent();
        userFrom = intent.getStringExtra("from");
        Log.e(TAG, "#32 : user from = "+userFrom);
        courseName = intent.getStringExtra("courseName");
        Log.e(TAG, "#34 : course name = "+courseName);
        section = intent.getStringExtra("section");
        Log.e(TAG, "#36 : section = "+section);

        //getting database reference to the respective chatroom
        if (courseName!=null & section != null) {
            chatsReference = FirebaseDatabase.getInstance().getReference()
                    .child("departments/CSE/courses").child(courseName).child("sections").
                            child(section).child("chatroom");
            Log.e(TAG, "chats reference is "+chatsReference.toString());
        }

        //initializing all views
        messageInput = (EditText) findViewById(R.id.course1_message_input);
        conversationList = (ListView) findViewById(R.id.course1_chatroom_list);
        messages = new ArrayList<String>(0);
        messageListAdapter = new ChatMessageAdapter(this, chatsReference);
        conversationList.setAdapter(messageListAdapter);
    }

    public void sendMessage(View view){
        message = messageInput.getText().toString();
        Log.e(TAG, "#53 : message = "+message);
        messageInput.setText("");
        messageInput.setHint("");
        DatabaseReference newMsgRef = chatsReference.push();
        Log.e(TAG, "#83: new message ref = "+newMsgRef.toString());
        FriendlyMessage friendlyMessage = new FriendlyMessage(message, userFrom );
        Log.e(TAG, "#85 : friendly message object = "+friendlyMessage.toString());
        newMsgRef.setValue(friendlyMessage);
    }

}

class ChatMessageAdapter extends BaseAdapter{

    private final String TAG = "**MESSAGE ADAPTER**";

    Context context;
    ArrayList<String> messages = new ArrayList<String>(0);
    DatabaseReference chatsRef;
    int previousMessagesNumber;

    ChatMessageAdapter(Context context, DatabaseReference chatsRef){
        super();
        this.context = context;
        this.chatsRef = chatsRef;
        getAllMessage();
    }

     void getAllMessage(){
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long messageNumber = dataSnapshot.getChildrenCount() - 1;
                previousMessagesNumber = (int) messageNumber;
                Log.e(TAG, "#105: number of previous messages = "+previousMessagesNumber);
                if (previousMessagesNumber>0){
                    for (DataSnapshot snap : dataSnapshot.getChildren()){
                        if (!snap.getKey().equals("description")){
//                            HashMap<String, String> msg = (HashMap<String, String>)  snap.getValue();
//                            Log.e(TAG, "#110: previous msg = "+msg);
//                            if (msg!=null)
//                                messages.add(msg.get(snap.getKey()));
                        }
                    }
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView messageView = (TextView) parent.findViewById(R.id.messageTextView);
        TextView nameTextView;
        if (convertView==null){
            Activity activity = (Activity) context;
            convertView = activity.getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }else{
            nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
        }
        EditText inputMessage = (EditText) parent.findViewById(R.id.course1_message_input);
        messageView.setText(inputMessage.getText().toString());
        return messageView;
    }
}