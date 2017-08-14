package com.example.user.edeasy.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.user.edeasy.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ASUS on 15-Aug-17.
 */

public
class ExpandableListViewAdapterDemo extends BaseExpandableListAdapter {

    Context context = null;
    List<String> headersList;//semester's name and year
    HashMap<String, List<String>> tableList;//course names with its grades and gpa
    int[] numberOfCoursesDone;

    static final String TAG = "**Adapter Demo**";

    public ExpandableListViewAdapterDemo(Context context, List<String> list,
                                  HashMap<String, List<String>> hashMap, int[] coursesDoneCount){
        this.context = context;
        headersList = list;
        tableList = hashMap;
        numberOfCoursesDone = coursesDoneCount;
        if (numberOfCoursesDone==null)
            Log.e(TAG, "# of courses array null");
        for (int i = 0; i < coursesDoneCount.length; i++) {
            Log.e(TAG, "#288 : courses in semester "+i+" = "+numberOfCoursesDone[i]);
        }
        //notifyDataSetChanged();
        //Log.e(TAG, "hashmap list value = "+hashMap.get("Spring 2016"));
        //Log.e(TAG, "initial table list value = "+tableList.get("Spring 2016"));
        //printMap(tableList);
        printAll();
        Log.e(TAG, "groupCount = "+getGroupCount());
    }

    void printAll(){
        Log.e(TAG, "headers count = "+headersList.size());
        for (int i = 0; i < headersList.size() ; i++) {
            Log.e(TAG, "header at i="+i+" ,"+headersList.get(i));
        }
        //printMap(tableList);
    }

//    private static void printMap(HashMap mp) {
//        Iterator it = mp.entrySet().iterator();
//        while (it.hasNext()) {
//            HashMap.Entry pair = (HashMap.Entry)it.next();
//            Log.e(TAG, "#253 : "+pair.getKey() + " = " + pair.getValue());
//            it.remove(); // avoids a ConcurrentModificationException
//        }
//    }

    @Override
    public int getGroupCount() {
        //Log.e(TAG, "#299 : table list value = "+tableList.get("Spring 2016"));
        return headersList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        Log.e(TAG, "at i="+i);
        int returns = 0;
        //Log.e(TAG, "#307 : table list value = "+tableList.get("Spring 2016"));
        if (tableList.get(headersList.get(i)) != null)
            returns = tableList.get(headersList.get(i)).size();
        else
            Log.e(TAG, "tableList is null");
        //Log.e(TAG, "details size = "+returns);
        // Log.e(TAG, "group count = "+getGroupCount());
//        int tosubtract = 2 * getGroupCount();
//        if (returns>tosubtract)
//            returns = returns - tosubtract - 2;
        //returns = returns/3;
        if (numberOfCoursesDone!=null)
            returns = numberOfCoursesDone[i];
        Log.e(TAG, "child count returns = "+String.valueOf(returns) );//45
        return returns;
    }

    @Override
    public Object getGroup(int i) {
        //Log.e(TAG, "#323 : table list value = "+tableList.get("Spring 2016"));
        return headersList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        //Log.e(TAG, "#329 : getChild = "+tableList.get(headersList.get(i)).get(i1));
        return tableList.get(headersList.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        //Log.e(TAG, "#335 : table list value = "+tableList.get("Spring 2016"));
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        // Log.e(TAG, "#340 : table list value = "+tableList.get("Spring 2016"));
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        //Log.e(TAG, "#347 : table list value = "+tableList.get("Spring 2016"));
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String semesterTitle = (String) getGroup(i);
        //Log.e(TAG, "#353 : semester = "+semesterTitle);
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
        Log.e(TAG, "#367 : i = "+i+ " i1 = "+i1);
        if (view == null){
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.previous_semesters_results_list_child, null);
        }
        TextView courseIdValue = (TextView) view.findViewById(R.id.course_id_column_value);
        TextView gradeValue = (TextView) view.findViewById(R.id.grade_column_value);
        TextView gpaValue = (TextView) view.findViewById(R.id.gpa_column_value);

        //Log.e(TAG, "#397 : number of children = "+getChildrenCount(i));
        int courseNumber = getChildrenCount(i);
        //Log.e(TAG, "#399 : course numbers = "+numberOfCoursesDone[i]);
//            int index = 0;
//            for (int k = 0; k < courseNumber; k++) {
        int totalNumberOfCoursesDone = 0;
        if (i>0){
            for (int j = 0; j < i ; j++) {
                totalNumberOfCoursesDone = totalNumberOfCoursesDone + numberOfCoursesDone[j];
            }
            totalNumberOfCoursesDone = 3 * totalNumberOfCoursesDone;
        }
        Log.e(TAG, "total number of courses done = "+totalNumberOfCoursesDone);
        //i1 = totalNumberOfCoursesDone;

        if (i1==0) {
            i1=totalNumberOfCoursesDone;
            String course1IdTitle = (String) getChild(i, i1);
            Log.e(TAG, "course id = " + course1IdTitle + " at i1=" + i1);
            String gpa1 = (String) getChild(i, i1 + 1);//previously i1+4
            //Log.e(TAG, "gpa = " + gpa + " at k=" + String.valueOf(i1 + 1));
            String grade1 = (String) getChild(i, i1 + 2);
            // Log.e(TAG, "grade = " + grade + " at k=" + String.valueOf(i1 + 2));

            courseIdValue.setText(course1IdTitle);
            gradeValue.setText(grade1);
            gpaValue.setText(gpa1);
        }else if (i1==1) {
            i1=totalNumberOfCoursesDone;
            String course2IdTitle = (String) getChild(i, i1+3);
            Log.e(TAG, "course id = " + course2IdTitle + " at i1=" + i1);
            String gpa2 = (String) getChild(i, i1+4);//previously i1+4
            //Log.e(TAG, "gpa = " + gpa + " at k=" + String.valueOf(i1 + 1));
            String grade2 = (String) getChild(i, i1+5);
            // Log.e(TAG, "grade = " + grade + " at k=" + String.valueOf(i1 + 2));

            courseIdValue.setText(course2IdTitle);
            gradeValue.setText(grade2);
            gpaValue.setText(gpa2);
        }else if (i1==2) {
            i1=totalNumberOfCoursesDone;
            String course3IdTitle = (String) getChild(i, i1 + 6);
            Log.e(TAG, "course id = " + course3IdTitle + " at i1=" + i1);
            String gpa3 = (String) getChild(i, i1 + 7);//previously i1+4
            //Log.e(TAG, "gpa = " + gpa + " at k=" + String.valueOf(i1 + 1));
            String grade3 = (String) getChild(i, i1 + 8);
            // Log.e(TAG, "grade = " + grade + " at k=" + String.valueOf(i1 + 2));

            courseIdValue.setText(course3IdTitle);
            gradeValue.setText(grade3);
            gpaValue.setText(gpa3);
        }else if(i1==3){
            i1=totalNumberOfCoursesDone;
            String course4IdTitle = (String) getChild(i, i1 + 9);
            Log.e(TAG, "course id = " + course4IdTitle + " at i1=" + i1);
            String gpa4 = (String) getChild(i, i1 + 10);//previously i1+4
            //Log.e(TAG, "gpa = " + gpa + " at k=" + String.valueOf(i1 + 1));
            String grade4 = (String) getChild(i, i1 + 11);
            // Log.e(TAG, "grade = " + grade + " at k=" + String.valueOf(i1 + 2));

            courseIdValue.setText(course4IdTitle);
            gradeValue.setText(grade4);
            gpaValue.setText(gpa4);
        }
//        else if (i1==1 || i1==totalNumberOfCoursesDone+1){//|| (i1-1)%3==0
//            String courseIdTitle = (String) getChild(i, i1+2);
//            Log.e(TAG, "course id = " + courseIdTitle + " at k=" + String.valueOf(i1+2));
//            String gpa = (String) getChild(i, i1+3);//previously i1+4
//           // Log.e(TAG, "gpa = " + gpa + " at k=" + String.valueOf(i1+3));
//            String grade = (String) getChild(i, i1+4);
//           // Log.e(TAG, "grade = " + grade + " at k=" + String.valueOf(i1 + 4));
//
//            courseIdValue.setText(courseIdTitle);
//            gradeValue.setText(grade);
//            gpaValue.setText(gpa);
//        }else if (i1==2 || i1==totalNumberOfCoursesDone+2){//|| (i1-2)%3==0
//            String courseIdTitle = (String) getChild(i, i1+4);
//            Log.e(TAG, "course id = " + courseIdTitle + " at k=" + String.valueOf(i1+4));
//            String gpa = (String) getChild(i, i1+5);//previously i1+4
//           // Log.e(TAG, "gpa = " + gpa + " at k=" + String.valueOf(i1+5));
//            String grade = (String) getChild(i, i1+6);
        // Log.e(TAG, "grade = " + grade + " at k=" + String.valueOf(i1+6));
//
//            courseIdValue.setText(courseIdTitle);
//            gradeValue.setText(grade);
//            gpaValue.setText(gpa);
//            String grade = (String) getChild(i, i1);//previously i1+4
//            Log.e(TAG, "grade = " + grade + " at k=" + String.valueOf(i1));
//            gradeValue.setText(grade);
//        }else if (i1==3 || i1==totalNumberOfCoursesDone+3){//|| (i1-2)%3==0
//            String courseIdTitle = (String) getChild(i, i1+6);
//            Log.e(TAG, "course id = " + courseIdTitle + " at k=" + String.valueOf(i1+6));
//            String gpa = (String) getChild(i, i1+7);//previously i1+4
//           // Log.e(TAG, "gpa = " + gpa + " at k=" + String.valueOf(i1+7));
//            String grade = (String) getChild(i, i1+8);
//           // Log.e(TAG, "grade = " + grade + " at k=" + String.valueOf(i1+8));
//
//            courseIdValue.setText(courseIdTitle);
//            gradeValue.setText(grade);
//            gpaValue.setText(gpa);
//            String grade = (String) getChild(i, i1);//previously i1+4
//            Log.e(TAG, "grade = " + grade + " at k=" + String.valueOf(i1));
//            gradeValue.setText(grade);
        //}
//
//                index = index + 3;
//            }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        //Log.e(TAG, "#386 : table list value = "+tableList.get("Spring 2016"));
        return true;
    }
}