package com.example.user.edeasy.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.edeasy.R;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;

/**
 * Created by ASUS on 15-Aug-17.
 */

public
class CalendarEventsAdapter extends BaseAdapter {

    private static final String TAG = "**Calendar Adapter**";

    int numberOfEvents;
    String[] eventNames;
    String[] eventDates;
    Context context;
    private static LayoutInflater inflater = null;
    MaterialCalendarView calendar;
    String currentMonth = convertToMonthName(Calendar.getInstance().get(Calendar.MONTH));
    //String todaysDate = currentMonth+" "+String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
    String selectedDate;

    public CalendarEventsAdapter(Context c, String[][] eventsInput, MaterialCalendarView cal,
                          int eventsCount){
        //Log.e(TAG, "today's date = "+todaysDate);
        Log.e(TAG, "month = "+currentMonth);
        Log.e(TAG, "events number = "+eventsCount);
        numberOfEvents = 0;
        eventNames = new String[eventsInput.length];
        eventDates = new String[eventsInput.length];
        context = c;
        for (int i = 0; i < eventsInput.length; i++) {
            eventNames[i] = eventsInput[i][0];
            eventDates[i] = eventsInput[i][1];
            if (eventsInput[i]!=null && eventDates[i]!=null) {
                numberOfEvents++;
                Log.e(TAG, "at i="+String.valueOf(i)+"event name = "+eventNames[i]+" on "+eventDates[i]);
            }
        }
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        calendar = cal;
        calendarListenerSetup();
    }

    private String dateToWords(String date){
        String dateInWords = "";
        //Log.e(TAG, "date="+date);
        if (date!=null){

            String[] sections = date.split("\\.");
            if (sections.length>0) {
                String day = sections[0];
                String month = convertToMonthName(sections[1]);
                String year = sections[2];
                dateInWords = month + " " + day + "," + year;
                //Log.e(TAG, "date in words = "+dateInWords);
            }else {
                Log.e(TAG, "splitted array length is zero ");
                dateInWords = date;
            }
        }else{
            Log.e(TAG, "date argument is null");
        }
        return dateInWords;
    }

    String convertToMonthName(String monthNumber){
        String month = "";
        switch (monthNumber) {
            case "01":
                month = "January";
                break;
            case "02":
                month = "February";
                break;
            case "03":
                month = "March";
                break;
            case "04":
                month = "April";
                break;
            case "05":
                month = "May";
                break;
            case "06":
                month = "June";
                break;
            case "07":
                month = "July";
                break;
            case "08":
                month = "August";
                break;
            case "09":
                month = "September";
                break;
            case "10":
                month = "October";
                break;
            case "11":
                month = "November";
                break;
            case "12":
                month = "December";
                break;
        }
        return  month;
    }

    String convertToMonthName(int monthNumber){
        String month = "";
        switch (monthNumber) {
            case 0:
                month = "January";
                break;
            case 1:
                month = "February";
                break;
            case 2:
                month = "March";
                break;
            case 3:
                month = "April";
                break;
            case 4:
                month = "May";
                break;
            case 5:
                month = "June";
                break;
            case 6:
                month = "July";
                break;
            case 7:
                month = "August";
                break;
            case 8:
                month = "September";
                break;
            case 9:
                month = "October";
                break;
            case 10:
                month = "November";
                break;
            case 11:
                month = "December";
                break;
        }
        return  month;
    }

    String formatDates(String previousDate){
        String formattedDate = "";
        String[] dateArray = previousDate.split("\\.");
        formattedDate = dateArray[2]+"-"+dateArray[1]+"-"+dateArray[0];
        //Log.e(TAG, "#305 : formatted date = "+formattedDate);
        return  formattedDate;
    }

    void calendarListenerSetup(){
//        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                String dateInt = String.valueOf(dayOfMonth)+"."+
//                        String.valueOf(month+1)+"."+
//                        String.valueOf(year);
//                selectedDate = dateToWords(dateInt);
//                currentMonth = convertToMonthName(month);
//                notifyDataSetChanged();
//                Log.e(TAG, "selected month = "+currentMonth);
//            }
//        });
    }

    @Override
    public int getCount() {
        return numberOfEvents;
    }

    @Override
    public Object getItem(int position) {
        return eventDates[position]+eventNames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (calendar!=null) {
            //Log.e(TAG, "calendar date = " + calendar.getDate());
        }else
            Log.e(TAG, "calendar is null");

        View view = convertView;
        if (view == null ) {
            view = inflater.inflate(R.layout.calendar_event_item, null);
        }
        TextView event_tv = (TextView) view.findViewById(R.id.event_textview);
        TextView date_tv = (TextView) view.findViewById(R.id.date_textview);

        if (eventNames[position]!=null && eventDates[position]!=null){
            String month = (dateToWords(eventDates[position]).split(" ")) [0];
            //Log.e(TAG, "month="+month+" current month="+currentMonth);
            event_tv.setText(eventNames[position]);
            date_tv.setText(dateToWords(eventDates[position]));
        }else{
            event_tv.setText("");
            date_tv.setText("");
        }
        return view;
    }
}