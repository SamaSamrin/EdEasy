package com.example.user.edeasy.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.edeasy.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ASUS on 15-Aug-17.
 */

public class RoutineAdapter extends BaseAdapter {

    private static final String TAG ="**Routine Adapter**";

    Context context;
    //String[][] routine;//four values for each entry - slot, course, section, faculty
    //number of classes = routine's length
    LayoutInflater inflater;
    HashMap<String, List<String>> routine;
    String[] slotTitles;

    public RoutineAdapter(Context context, HashMap<String, List<String>> wholeRoutine){
        this.context = context;
        routine = wholeRoutine;
        if (routine==null)
            Log.e(TAG, "routine is null");
        else {
            Log.e(TAG, "routine size = " + routine.size());
            slotTitles = new String[routine.size()];
            Iterator it = routine.entrySet().iterator();
            int i=0;
            while (it.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry)it.next();
                slotTitles[i] = pair.getKey().toString();
                Log.e(TAG, "#252 : "+pair.getKey().toString() + " = " + pair.getValue());
                i++;
                //it.remove(); // avoids a ConcurrentModificationException
            }
            sort();
        }
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    void sort(){
        String[] tempArray = slotTitles;
        slotTitles = new String[tempArray.length];
        int emptyIndex = 0;
        //checking for AM-AM
        for (int i = 0; i < tempArray.length; i++) {
            String[] ampms = tempArray[i].split("-");
            if (ampms[0].endsWith("AM") && ampms[1].endsWith("AM")){
                Log.e(TAG, "index = "+emptyIndex);
                slotTitles[emptyIndex] = tempArray[i];
                emptyIndex++;
            }
        }
//        if (emptyIndex>0)
//            Arrays.sort(slotTitles);
        //checking for AM-PM
        for (int i = 0; i < tempArray.length; i++) {
            String[] ampms = tempArray[i].split("-");
            if (ampms[0].endsWith("AM") && ampms[1].endsWith("PM")){
                slotTitles[emptyIndex] = tempArray[i];
                Log.e(TAG, "index = "+emptyIndex);
                emptyIndex++;
            }
        }
        //checking for PM-PM
        String[] tempPMs = new String[4];
        int tempPMindex = 0;
        for (int i = 0; i < tempArray.length; i++) {
            String[] ampms = tempArray[i].split("-");
            if (ampms[0].endsWith("PM") && ampms[1].endsWith("PM")){
                tempPMs[tempPMindex] = tempArray[i];
                tempPMindex++;
            }
        }
        if (tempPMindex!=0) {
            //Arrays.sort(tempPMs);
            //copy the last checked and sorted array to main slot array
            for (int i = 0; i < tempPMindex; i++) {
                slotTitles[emptyIndex] = tempPMs[i];
                Log.e(TAG, "index = "+emptyIndex);
                emptyIndex++;
            }
        }
    }

    @Override
    public int getCount() {
        return slotTitles.length;
    }

    @Override
    public Object getItem(int position) {
        return slotTitles[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null ) {
            view = inflater.inflate(R.layout.routine_slot_item, null);
        }
        TextView slotText = (TextView) view.findViewById(R.id.slot);
        TextView courseText = (TextView) view.findViewById(R.id.course_name);
        TextView sectionText = (TextView) view.findViewById(R.id.section_number);
        TextView facultyInitialText = (TextView) view.findViewById(R.id.room_number);

        String slot = slotTitles[position];
        Log.e(TAG, "slot = "+slot);
        slotText.setText(slot);
        String course = routine.get(slot).get(0);
        Log.e(TAG, "course = "+course);
        courseText.setText(course);
        String section = routine.get(slot).get(1);
        Log.e(TAG, "section = "+section);
        section = " Section "+section;
        sectionText.setText(section);

        return view;
    }
}

