package com.example.user.edeasy.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.edeasy.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by ASUS on 15-Aug-17.
 */


public class ChatMessageAdapter extends BaseAdapter {

    private final String TAG = "**MESSAGE ADAPTER**";

    Context context;
    String[] messages;
    String[] senders;
    String username;
    String department;
    DatabaseReference chatsRef;
    int previousMessagesNumber;
    private static LayoutInflater inflater = null;

    public ChatMessageAdapter(Context context, DatabaseReference chatsRef,
                       String[] messages, String[] senders, String username){
        super();
        this.context = context;
        this.chatsRef = chatsRef;
        Log.e(TAG, "#163 : chats ref = "+chatsRef.toString());
        this.messages = messages;
        this.senders = senders;
        this.username = username;
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
                if (previousMessagesNumber<0)
                    previousMessagesNumber=0;
                else if (previousMessagesNumber==0)
                    previousMessagesNumber=1;
                Log.e(TAG, "#181: number of previous messages = " + previousMessagesNumber);
                messages = new String[previousMessagesNumber];
                senders = new String[previousMessagesNumber];
                if (previousMessagesNumber > 0) {
                    int messageCounter = 0;
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        if (!snap.getKey().equals("description") && messageCounter<previousMessagesNumber) {
                            String key = snap.getKey();
                            String message = snap.child("text").getValue(String.class);
                            String senderName = snap.child("name").getValue(String.class);
                            if (senderName != null)
                                senders[messageCounter] = senderName;
                            messages[messageCounter] = message;
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
        //Log.e(TAG, "position="+position);
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
            Log.e(TAG, "message at this position = "+position+" : " +
                    messages[position]+" sent by "+senders[position]);
        }
        //notifyDataSetChanged();
        return vi;
    }
}