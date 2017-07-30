package com.example.user.edeasy;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class FullRoutine extends Activity {

    private static final String TAG = "**Full Routine**";


    HashMap<String, List<String>> sundayRoutine;
    HashMap<String, List<String>> mondayRoutine;
    HashMap<String, List<String>> tuesdayRoutine;
    HashMap<String, List<String>> wednesdayRoutine;
    HashMap<String, List<String>> thursdayRoutine;
    HashMap<String, List<String>> saturdayRoutine;
    HashMap<String, List<String>> fridayRoutine;
    HashMap<String, List<String>> slot_details;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_routine);

        //getting all necessary values from Current Routine
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            sundayRoutine = (HashMap<String, List<String>>) bundle.get("sunday");
            Log.e(TAG, "sunday routine : ");
            printMap(sundayRoutine);
            mondayRoutine = (HashMap<String, List<String>>) bundle.get("monday");
            Log.e(TAG, "monday routine : ");
            printMap(mondayRoutine);
        }else
            Log.e(TAG, "#76 : received bundle is null");

        //setting the toolbar title
        if (Build.VERSION.SDK_INT>=21) {
            if (getActionBar()!=null)
                getActionBar().setTitle("Full Routine");//working :D
        }
    }

    void printMap(HashMap<String, List<String>> map){
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            Log.e(TAG, "#164 : "+pair.getKey().toString() + " = " + pair.getValue());
            //it.remove(); // avoids a ConcurrentModificationException
        }
    }
}
