package com.example.user.edeasy.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.user.edeasy.R;

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
//    HashMap<String, List<String>> saturdayRoutine;
//    HashMap<String, List<String>> fridayRoutine;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
            tuesdayRoutine = (HashMap<String, List<String>>) bundle.get("tuesday");
            Log.e(TAG, "tuesday routine : ");
            printMap(tuesdayRoutine);
            wednesdayRoutine = (HashMap<String, List<String>>) bundle.get("wednesday");
            Log.e(TAG, "wednesday routine : ");
            printMap(wednesdayRoutine);
            thursdayRoutine = (HashMap<String, List<String>>) bundle.get("thursday");
            Log.e(TAG, "thursday routine : ");
            printMap(thursdayRoutine);
        }else
            Log.e(TAG, "#76 : received bundle is null");

        //setting the toolbar title
        if (Build.VERSION.SDK_INT>=21) {
            if (getActionBar()!=null)
                getActionBar().setTitle("Full Routine");
        }

        setUpRoutine();
    }

    void printMap(HashMap<String, List<String>> map){
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            Log.e(TAG, "#164 : "+pair.getKey().toString() + " = " + pair.getValue());
            //it.remove(); // avoids a ConcurrentModificationException
        }
    }

    void setUpRoutine(){
        String slot = "";
        String coursename = "";
        String section = "";
        String faculty = "";
        //SUNDAY
        Iterator sunIt = sundayRoutine.entrySet().iterator();
        while (sunIt.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)sunIt.next();
            slot = pair.getKey().toString();
            coursename = sundayRoutine.get(slot).get(0);
            section = sundayRoutine.get(slot).get(1);
            if (sundayRoutine.get(slot).size() > 2)
                faculty = sundayRoutine.get(slot).get(2);
            Log.e(TAG, "#SUNDAY : slot = "+slot+" course = "+coursename+" section "+section);
            fillUpSundaySlot(slot, coursename, section, faculty, null);
            //it.remove(); // avoids a ConcurrentModificationException
        }
        //MONDAY
        Iterator monIt = mondayRoutine.entrySet().iterator();
        while (monIt.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)monIt.next();
            slot = pair.getKey().toString();
            coursename = mondayRoutine.get(slot).get(0);
            section = mondayRoutine.get(slot).get(1);
            if (mondayRoutine.get(slot).size() > 2)
                faculty = mondayRoutine.get(slot).get(2);
            Log.e(TAG, "#MONDAY : slot = "+slot+" course = "+coursename+" section "+section);
            fillUpMondaySlot(slot, coursename, section, faculty, null);
            //it.remove(); // avoids a ConcurrentModificationException
        }
        //TUESDAY
        Iterator tuesIt = mondayRoutine.entrySet().iterator();
        while (tuesIt.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)tuesIt.next();
            slot = pair.getKey().toString();
            coursename = mondayRoutine.get(slot).get(0);
            section = mondayRoutine.get(slot).get(1);
            if (mondayRoutine.get(slot).size() > 2)
                faculty = mondayRoutine.get(slot).get(2);
            Log.e(TAG, "#TUESDAY : slot = "+slot+" course = "+coursename+" section "+section);
            fillUpTuesdaySlot(slot, coursename, section, faculty, null);
            //it.remove(); // avoids a ConcurrentModificationException
        }
        //WEDNESDAY
        Iterator wedIt = mondayRoutine.entrySet().iterator();
        while (wedIt.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)wedIt.next();
            slot = pair.getKey().toString();
            coursename = mondayRoutine.get(slot).get(0);
            section = mondayRoutine.get(slot).get(1);
            if (mondayRoutine.get(slot).size() > 2)
                faculty = mondayRoutine.get(slot).get(2);
            Log.e(TAG, "#WEDNESDAY : slot = "+slot+" course = "+coursename+" section "+section);
            fillUpWednesdaySlot(slot, coursename, section, faculty, null);
            //it.remove(); // avoids a ConcurrentModificationException
        }
        //THURSDAY
        Iterator thursIt = mondayRoutine.entrySet().iterator();
        while (thursIt.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)thursIt.next();
            slot = pair.getKey().toString();
            coursename = mondayRoutine.get(slot).get(0);
            section = mondayRoutine.get(slot).get(1);
            if (mondayRoutine.get(slot).size() > 2)
                faculty = mondayRoutine.get(slot).get(2);
            Log.e(TAG, "#THURSDAY : slot = "+slot+" course = "+coursename+" section "+section);
            fillUpThursdaySlot(slot, coursename, section, faculty, null);
            //it.remove(); // avoids a ConcurrentModificationException
        }
    }

    void fillUpSundaySlot(String slot, String course, String section, String faculty, String room){
        TextView view = null;
        if (slot.startsWith("08"))
            view = (TextView) findViewById(R.id.sunday_8am_class);
        else if (slot.startsWith("09"))
            view = (TextView) findViewById(R.id.sunday_930am_class);
        else if (slot.startsWith("11"))
            view = (TextView) findViewById(R.id.sunday_11am_class);
        else if (slot.startsWith("12"))
            view = (TextView) findViewById(R.id.sunday_1230pm_class);
        else if (slot.startsWith("02"))
            view = (TextView) findViewById(R.id.sunday_2pm_class);
        else if (slot.startsWith("03"))
            view = (TextView) findViewById(R.id.sunday_330pm_class);
        else if (slot.startsWith("05"))
            view = (TextView) findViewById(R.id.sunday_5pm_class);

        //setting the right text info
        String text = course+" - "+section;
        if (faculty!=null)
            text = text+" - "+faculty;
        if (room!=null)
            text = text+" - "+room;
        view.setText(text);
    }

    void fillUpMondaySlot(String slot, String course, String section, String faculty, String room){
        TextView view = null;
        if (slot.startsWith("08"))
            view = (TextView) findViewById(R.id.monday_8am_class);
        else if (slot.startsWith("09"))
            view = (TextView) findViewById(R.id.monday_930am_class);
        else if (slot.startsWith("11"))
            view = (TextView) findViewById(R.id.monday_11am_class);
        else if (slot.startsWith("12"))
            view = (TextView) findViewById(R.id.monday_1230pm_class);
        else if (slot.startsWith("02"))
            view = (TextView) findViewById(R.id.monday_2pm_class);
        else if (slot.startsWith("03"))
            view = (TextView) findViewById(R.id.monday_330pm_class);
        else if (slot.startsWith("05"))
            view = (TextView) findViewById(R.id.monday_5pm_class);

        //setting the right text info
        String text = course+" - "+section;
        if (faculty!=null)
            text = text+" - "+faculty;
        if (room!=null)
            text = text+" - "+room;
        view.setText(text);
    }

    void fillUpTuesdaySlot(String slot, String course, String section, String faculty, String room){
        TextView view = null;
        if (slot.startsWith("08"))
            view = (TextView) findViewById(R.id.tuesday_8am_class);
        else if (slot.startsWith("09"))
            view = (TextView) findViewById(R.id.tuesday_930am_class);
        else if (slot.startsWith("11"))
            view = (TextView) findViewById(R.id.tuesday_11am_class);
        else if (slot.startsWith("12"))
            view = (TextView) findViewById(R.id.tuesday_1230pm_class);
        else if (slot.startsWith("02"))
            view = (TextView) findViewById(R.id.tuesday_2pm_class);
        else if (slot.startsWith("03"))
            view = (TextView) findViewById(R.id.tuesday_330pm_class);
        else if (slot.startsWith("05"))
            view = (TextView) findViewById(R.id.tuesday_5pm_class);

        //setting the right text info
        String text = course+" - "+section;
        if (faculty!=null)
            text = text+" - "+faculty;
        if (room!=null)
            text = text+" - "+room;
        view.setText(text);
    }

    void fillUpWednesdaySlot(String slot, String course, String section, String faculty, String room){
        TextView view = null;
        if (slot.startsWith("08"))
            view = (TextView) findViewById(R.id.wednesday_8am_class);
        else if (slot.startsWith("09"))
            view = (TextView) findViewById(R.id.wednesday_930am_class);
        else if (slot.startsWith("11"))
            view = (TextView) findViewById(R.id.wednesday_11am_class);
        else if (slot.startsWith("12"))
            view = (TextView) findViewById(R.id.wednesday_1230pm_class);
        else if (slot.startsWith("02"))
            view = (TextView) findViewById(R.id.wednesday_2pm_class);
        else if (slot.startsWith("03"))
            view = (TextView) findViewById(R.id.wednesday_330pm_class);
        else if (slot.startsWith("05"))
            view = (TextView) findViewById(R.id.wednesday_5pm_class);

        //setting the right text info
        String text = course+" - "+section;
        if (faculty!=null)
            text = text+" - "+faculty;
        if (room!=null)
            text = text+" - "+room;
        view.setText(text);
    }

    void fillUpThursdaySlot(String slot, String course, String section, String faculty, String room){
        TextView view = null;
        if (slot.startsWith("08"))
            view = (TextView) findViewById(R.id.thursday_8am_class);
        else if (slot.startsWith("09"))
            view = (TextView) findViewById(R.id.thursday_930am_class);
        else if (slot.startsWith("11"))
            view = (TextView) findViewById(R.id.thursday_11am_class);
        else if (slot.startsWith("12"))
            view = (TextView) findViewById(R.id.thursday_1230pm_class);
        else if (slot.startsWith("02"))
            view = (TextView) findViewById(R.id.thursday_2pm_class);
        else if (slot.startsWith("03"))
            view = (TextView) findViewById(R.id.thursday_330pm_class);
        else if (slot.startsWith("05"))
            view = (TextView) findViewById(R.id.thursday_5pm_class);

        //setting the right text info
        String text = course+" - "+section;
        if (faculty!=null)
            text = text+" - "+faculty;
        if (room!=null)
            text = text+" - "+room;
        view.setText(text);
    }

}
