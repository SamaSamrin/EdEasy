package com.example.user.edeasy;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;



public class Dashboard extends Fragment {

    private static final String TAG = "**Dashboard**";
    private OnFragmentInteractionListener mListener;


    GridView dashboard_gv;
    String username;
    String email;

    public Dashboard() {
        // Required empty public constructor
    }

    @Override
     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_nav_drawer);

        Log.e(TAG, "Dashboard reached");
//
//        Intent intent = getIntent();
//        username = intent.getStringExtra("username");
//        email = intent.getStringExtra("email");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        Log.e(TAG, "Dashboard - onCreateView");

        View view = inflater.inflate(R.layout.activity_dashboard, container, false);

        Log.e(TAG, "view = "+view.getId());

//        Toolbar toolbar = (Toolbar) view.findViewById(R.id.dashboard_toolbar);
//
//        if(Build.VERSION.SDK_INT >= 21) {
//            if(toolbar != null)
//                getActivity().setActionBar(toolbar);
//        }
        //else
            //actio(toolbar);

        dashboard_gv = (GridView) view.findViewById(R.id.dashboard_gridview);

        Log.e(TAG, "gridview id = "+dashboard_gv.getId());

        Log.e(TAG, "current API="+Build.VERSION.SDK_INT);
        if(Build.VERSION.SDK_INT >= 23)
            dashboard_gv.setAdapter(new GridAdapter(getContext()));
        else
            dashboard_gv.setAdapter(new GridAdapter(getActivity().getApplicationContext()));

        dashboard_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(Dashboard.this, ""+String.valueOf(i+1), Toast.LENGTH_LONG).show();
                Intent intent = null;
                if(i==0){//done
                    intent = new Intent(getContext(), PreviousResults.class);
                    startActivity(intent);
                }else if(i==1){
                    intent = new Intent(getContext(), CurrentRoutine.class);
                    startActivity(intent);
                }else if(i==2){//done
                    intent = new Intent(getContext(), CurrentMarksheet.class);
                    startActivity(intent);
                }else if (i==3){
                    intent = new Intent(getContext(), Chatroom.class);
                    startActivity(intent);
                }else if (i==4){
                    intent = new Intent(getContext(), CalendarDisplay.class);
                    startActivity(intent);
                }else if (i==5){
                    intent = new Intent(getContext(), NotificationsDisplay.class);
                    startActivity(intent);
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            Log.e(TAG, "context is an instance of OnFragmentInteractionListener");
            mListener = (OnFragmentInteractionListener) context;

        } else {
            Log.e(TAG, "context is NOT an instance of OnFragmentInteractionListener");
            /*throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}

class GridAdapter extends BaseAdapter{

    Context context;
    String[] dashboard_items = {"PREVIOUS RESULTS", "CURRENT ROUTINE", "CURRENT MARKSHEET",
            "CHATROOM", "CALENDER", "NOTIFICATIONS"};
    String[] colorBacks = {"#FFEA00", "#76FF03", "#00B0FF", "#BA68C8", "#EC407A", "#FF3D00"};
//    int[] colors = {Color.CYAN, Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.RED, Color.BLACK};
//    int[] colors2 = {R.color.previousResultsBackg, R.color.currentRoutineBackg,
//                      R.color.currentMarksheetBackg, R.color.courseMaterialsBackg,
//                      R.color.calendarBackg, R.color.notificationsBackg};

    GridAdapter(Context context){
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        return dashboard_items.length;
    }

    @Override
    public Object getItem(int i) {
        return dashboard_items[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView itemText = null;
        if(view==null){
            itemText = new TextView(context);
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int screenwidth = metrics.widthPixels;
            int orientation = context.getResources().getConfiguration().orientation;
            if(orientation== Configuration.ORIENTATION_PORTRAIT)
                itemText.setLayoutParams(new GridView.LayoutParams(screenwidth/2, screenwidth/2));
            else
                itemText.setLayoutParams(new GridView.LayoutParams(screenwidth/3, screenwidth/3));
            itemText.setPadding(10, 10, 10, 10);
            itemText.setGravity(Gravity.CENTER);
            itemText.setTextColor(Color.WHITE);
            itemText.setTypeface(itemText.getTypeface(), Typeface.BOLD);
        }else{
            itemText = (TextView) view;
        }
        itemText.setText(dashboard_items[i]);
        itemText.setBackgroundColor(Color.parseColor(colorBacks[i]));
        return itemText;
    }
}
