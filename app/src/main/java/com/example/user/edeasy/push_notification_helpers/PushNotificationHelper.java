package com.example.user.edeasy.push_notification_helpers;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ASUS on 06-Aug-17.
 */

public class PushNotificationHelper {
    public final static String AUTH_KEY_FCM = "Your api key";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    final static String TAG = "**Notif Helper**";

    public static String sendPushNotification(String deviceToken)
            throws IOException, JSONException {
        Log.e(TAG, "send notif method");
        String result = "";
        //starting connection to server
        URL url = new URL(API_URL_FCM);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
        conn.setRequestProperty("Content-Type", "application/json");

        JSONObject json = new JSONObject();

        Log.e(TAG, "token = "+deviceToken.trim());

        json.put("to", deviceToken.trim());//"/topics/all"

        JSONObject info = new JSONObject();
        info.put("message", "Title"); // Notification title
        info.put("data", "body"); // Notification body
        json.put("notification", info);
        try {
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();

            InputStream inputStream = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (inputStream)));
//            String output;
//            System.out.println("Output from Server .... \n");
//            while ((output = br.readLine()) != null) {
//                System.out.println(output);
//            }
            result = CommonConstants.SUCCESS;
            Log.e(TAG, "sucessful notif");
        } catch (Exception e) {
            e.printStackTrace();
            result = CommonConstants.FAILURE;
            Log.e(TAG, "failed notif because : "+e.toString());//android.os.NetworkOnMainThreadException
        }
        return result;

    }
}