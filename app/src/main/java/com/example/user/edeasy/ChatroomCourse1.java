package com.example.user.edeasy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    //ArrayList<String> messages;
    String[] messages;
    String[] userFroms;
    //DatabaseReference chatsRef;
    int previousMessagesNumber;

    DatabaseReference chatsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
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

        if (Build.VERSION.SDK_INT>=21) {
            Toolbar toolbar = new Toolbar(this);
            if (getActionBar()!=null)
            getActionBar().setTitle(courseName+" Section "+section);
        }

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
                messages = new String[previousMessagesNumber];
                //Log.e(TAG, "#105: number of previous messages = "+previousMessagesNumber);
                if (previousMessagesNumber>0){
                    int messageCounter = 0;
                    for (DataSnapshot snap : dataSnapshot.getChildren()){
                        if (!snap.getKey().equals("description")){
                            String key = snap.getKey();
                            String message = snap.child("text").getValue(String.class);
                            String senderName = snap.child("name").getValue(String.class);
                            //Log.e(TAG, "#115: key= "+key+" | message= "+message);
                            if (senderName!=null){
                                if (senderName.equals(userFrom)){}
                                    //rightmost
                                else{}
                                    //leftmost
                            }
                            messages[messageCounter] = message;
                            //messages.add(snap.child("message").getValue(String.class));
                            //Toast.makeText(ChatroomCourse1.this, "message="+message, Toast.LENGTH_SHORT).show();
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
        Log.e(TAG, "#53 : message = "+message);
        messageInput.setText("");
        messageInput.setHint("");
        DatabaseReference newMsgRef = chatsReference.push();
        Log.e(TAG, "#83: new message ref = "+newMsgRef.toString());
        FriendlyMessage friendlyMessage = new FriendlyMessage(message, userFrom );
        Log.e(TAG, "#85 : friendly message object = "+friendlyMessage.toString());
        newMsgRef.setValue(friendlyMessage);
        getAllMessage();
    }


}

class ChatMessageAdapter extends BaseAdapter{

    private final String TAG = "**MESSAGE ADAPTER**";

    Context context;
//    ArrayList<String> messages = new ArrayList<String>(0);
    String[] messages;
    String[] senders;
    String username;
    DatabaseReference chatsRef;
    int previousMessagesNumber;
    private static LayoutInflater inflater = null;

    ChatMessageAdapter(Context context, DatabaseReference chatsRef,
                       String[] messages, String[] senders, String username){
        super();
        this.context = context;
        this.chatsRef = chatsRef;
        this.messages = messages;
        this.senders = senders;
        this.username = username;
        Log.e(TAG,"username="+username);
        getAllMessage();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

     void getAllMessage() {
         chatsRef.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 Log.e(TAG, "Adapter: onDataChange");
                 long messageNumber = dataSnapshot.getChildrenCount() - 1;
                 previousMessagesNumber = (int) messageNumber;
                 messages = new String[previousMessagesNumber];
                 senders = new String[previousMessagesNumber];
                 Log.e(TAG, "#105: number of previous messages = " + previousMessagesNumber);
                 if (previousMessagesNumber > 0) {
                     int messageCounter = 0;
                     for (DataSnapshot snap : dataSnapshot.getChildren()) {
                         if (!snap.getKey().equals("description")) {
                             String key = snap.getKey();
                             String message = snap.child("text").getValue(String.class);
                             String senderName = snap.child("name").getValue(String.class);
                             //Log.e(TAG, "#115: key= " + key + " | message= " + message);
                             if (senderName != null)
                                 senders[messageCounter] = senderName;
                             //Log.e(TAG, "message counter = "+String.valueOf(messageCounter));
                             messages[messageCounter] = message;
                             //messages.add(snap.child("message").getValue(String.class));
                             //Toast.makeText(ChatroomCourse1.this, "message="+message, Toast.LENGTH_SHORT).show();
                         }
                         messageCounter++;
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
        if (messages!=null)
            return messages.length;
        else
            return  0;
    }

    @Override
    public Object getItem(int position) {
        return messages[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e(TAG, "position="+position);
        View vi = convertView;
        if (vi == null ) {
            if (!senders[position].equals(username))
               vi = inflater.inflate(R.layout.list_item_receiver, null);
            else
               vi = inflater.inflate(R.layout.list_item, null);
        }
        TextView text = (TextView) vi.findViewById(R.id.messageView);
        if (messages[position]!=null) {
            text.setText(messages[position]);
            Log.e(TAG, "message at this position = " + messages[position]);
            Log.e(TAG, "sender of this message = "+senders[position]);
//            if (!senders[position].equals(username)){
//                Log.e(TAG, "sender is not username");
//                ((ConstraintLayout.LayoutParams)text.getLayoutParams()).setLayoutDirection(Layout.DIR_RIGHT_TO_LEFT);
//            }else
//                ((ConstraintLayout.LayoutParams)text.getLayoutParams()).setLayoutDirection(Layout.DIR_LEFT_TO_RIGHT);
//            //((GridLayout.LayoutParams)button.getLayoutParams()).setGravity(int)
        }
        //notifyDataSetChanged();
        return vi;
    }
}