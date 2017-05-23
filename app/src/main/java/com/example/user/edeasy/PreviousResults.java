package com.example.user.edeasy;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PreviousResults extends Activity {

    ExpandableListView previous_results_list ;
    List<String> semester_headers;
    HashMap<String, List<String>> semester_result_details;
    ExpandableListViewAdapterDemo adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_results);

        previous_results_list = (ExpandableListView) findViewById(R.id.previous_results_semesterwise);

        populateLists();

        adapter = new ExpandableListViewAdapterDemo(this, semester_headers, semester_result_details);
        previous_results_list.setAdapter(adapter);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void populateLists(){
        semester_headers = new ArrayList<String>();
        semester_result_details = new HashMap<String, List<String>>();

        semester_headers.add("Spring 2014");
        semester_headers.add("Summer 2014");
        semester_headers.add("Fall 2014");

        List<String> spring14 = new ArrayList<String>();
        spring14.add("CSE110");
        spring14.add("MAT110");
        spring14.add("PHY111");
        spring14.add("ENG101");

        List<String> summer14 = new ArrayList<String>();
        summer14.add("CSE111");
        summer14.add("MAT120");
        summer14.add("PHY112");
        summer14.add("ENG102");

        List<String> fall2014 = new ArrayList<String>();
        fall2014.add("DEV101");
        fall2014.add("HUM101");
        fall2014.add("ENG204");

        semester_result_details.put(semester_headers.get(0), spring14);
        semester_result_details.put(semester_headers.get(1), summer14);
        semester_result_details.put(semester_headers.get(2), fall2014);
    }

}

class ExpandableListViewAdapterDemo extends BaseExpandableListAdapter{

    Context context = null;
    private List<String> headersList;
    private HashMap<String, List<String>> tableList;

    ExpandableListViewAdapterDemo(Context context, List<String> list,
                                  HashMap<String, List<String>> hashMap){
        this.context = context;
        headersList = list;
        tableList = hashMap;
    }

    @Override
    public int getGroupCount() {
        return headersList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return this.tableList.size();
    }

    @Override
    public Object getGroup(int i) {
        return this.headersList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return this.tableList.get(this.headersList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String semesterTitle = (String) getGroup(i);
        if (view == null){
            LayoutInflater inf = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.previous_semesters_result_list_headers, null);
        }

        TextView semesterName = (TextView) view.findViewById(R.id.semester_name);
        semesterName.setText(semesterTitle);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String courseIdTitle = (String) getChild(i, i1);
        if (view == null){
            LayoutInflater inf = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.previous_semesters_results_list_child, null);
        }
        TextView courseIdValue = (TextView) view.findViewById(R.id.course_id_column_value);
        courseIdValue.setText(courseIdTitle);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}