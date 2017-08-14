package com.example.user.edeasy.push_notification_helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.user.edeasy.R;
import com.example.user.edeasy.activities.Welcome;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by ASUS on 06-Aug-17.
 */

public class EdEasyMessagingService extends FirebaseMessagingService {

    private static final String TAG = "**FCM Service**";

    //https://firebase.google.com/docs/cloud-messaging/android/receive
    //https://firebase.google.com/docs/cloud-messaging/concept-options#senderid
    //https://console.firebase.google.com/project/edeasy-510c6/settings/serviceaccounts/adminsdk

    @Override
    public void onCreate() {
        super.onCreate();
        RemoteMessage.Builder builder = new RemoteMessage.Builder("Message from Program");
        builder.setMessageType(NOTIFICATION_SERVICE);
        RemoteMessage message = builder.build();
    }

    @Override
    public void onMessageSent(String s) {
        Log.e(TAG, s+" : message sent");
        super.onMessageSent(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        Intent intent = new Intent(this, Welcome.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // this is my insertion, looking for a solution
        int icon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                R.drawable.main_yellow_icon_round: R.mipmap.main_yellow_icon_round;
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(remoteMessage.getFrom())
                .setContentText(remoteMessage.getNotification().getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
