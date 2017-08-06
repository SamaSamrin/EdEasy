package com.example.user.edeasy;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormatSymbols;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AccountProfile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AccountProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Account";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseAuth auth;
    DatabaseReference databaseReference;

    SeekBar milestonerBar;
    int m_progress = 0;

    String beginDate;
    String endDate;
    int todaysDay;
    int todaysMonth;
    int beginDay;
    int beginMonth;
    int endDay;
    int endMonth;

    String user_role;
    String user_department;
    String username;
    String user_email;
    String student_id;
    String credits_completed;
    int numberOfCourses;
    String[][] assignedCourses;
    String[] departments;

    private OnFragmentInteractionListener mListener;

    public AccountProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountProfile newInstance(String param1, String param2) {
        AccountProfile fragment = new AccountProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //Intent intent = new Intent(getContext() , )

        Bundle args = getArguments();
        if (args!=null) {
            username = args.getString("username");
            //Log.e(TAG, "#92 : username = "+username);
            user_role = args.getString("role");
            Log.e(TAG, "#94 : role = "+user_role);
            user_email = args.getString("email");
            //Log.e(TAG, "email = "+user_email);
            user_department = args.getString("department");
            Log.e(TAG, "department = "+user_department);
            student_id = args.getString("student id");
            credits_completed = args.getString("credits");
            numberOfCourses = args.getInt("numberOfCourses");
            //Log.e(TAG, "number of courses = "+numberOfCourses);
            departments = new String[numberOfCourses];
            departments = args.getStringArray("departments");
            for (int i=0; i<numberOfCourses; i++)
                Log.e(TAG, "department "+String.valueOf(i+1)+" = "+departments[i]);
            assignedCourses = new String[numberOfCourses][2];
            for (int i=0; i<numberOfCourses; i++){
                assignedCourses[i] = args.getStringArray("course"+String.valueOf(i+1));
                //Log.e(TAG, "course "+String.valueOf(i+1)+" = "+assignedCourses[i][0]);
                //Log.e(TAG, "section = "+assignedCourses[i][1]);
            }
        }else
            Log.e(TAG, "#90 : received args is null");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        setBeginAndEndDates();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_account_profile, container, false);

        setBasicInfo(v);

        //setting photo according to user role
        ImageView propic = (ImageView) v.findViewById(R.id.propic_account);
        if (user_role.equals("student"))
            propic.setImageResource(R.drawable.student);
        else if (user_role.equals("teacher"))
            propic.setImageResource(R.drawable.teacher);


        //milestone bar
        milestonerBar = (SeekBar) v.findViewById(R.id.milestone_seekbar);
        Calendar calendar = Calendar.getInstance();
        todaysDay = calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);
        todaysMonth = calendar.get(Calendar.MONTH);
        String month = new DateFormatSymbols().getMonths()[todaysMonth];
        Log.e(TAG, "today = "+todaysDay+" month = "+month);
        milestonerBar.cancelLongPress();
        milestonerBar.setClickable(false);

        m_progress = 5;

        setProgressStuff(m_progress);
        milestonerBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setProgressStuff(m_progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                setProgressStuff(m_progress);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setProgressStuff(m_progress);
            }
        });

        //todaysName = new DateFormatSymbols().getWeekdays()[todayInt];//today's week day name


        return v;
    }

    void setProgressStuff(int progress){
        int drawableId = R.drawable.black_1_27;
        milestonerBar.setProgress(progress);
        switch (progress) {
            case 2 :
                drawableId = R.drawable.black_2_27;
                break;
            case 3 :
                drawableId = R.drawable.black_3_27;
                break;
            case 4 :
                drawableId = R.drawable.black_4_27;
                break;
            case 5 :
                drawableId = R.drawable.black_5_27;
                break;
            case 6 :
                drawableId = R.drawable.black_6_27;
                break;
            case 1 :
                drawableId = R.drawable.black_1_27;
                break;
        }
        if (Build.VERSION.SDK_INT>20) {
            Drawable drawable = getActivity().getDrawable(drawableId);
            milestonerBar.setThumb(drawable);
        }
    }

    void setBasicInfo(View v){
        //basic info
        TextView nameDisplay = (TextView) v.findViewById(R.id.account_name_display);
        TextView studentIdDisplay = (TextView) v.findViewById(R.id.studentID_account);
        TextView creditsDoneDisplay = (TextView) v.findViewById(R.id.completed_credits_account);
        TextView departmentDisplay = (TextView) v.findViewById(R.id.department_account);

        nameDisplay.setText(username.toUpperCase());
        studentIdDisplay.setText(student_id);
        departmentDisplay.setText(user_department);
        creditsDoneDisplay.setText(credits_completed);

        //setting assigned courses
        TextView course1name = (TextView) v.findViewById(R.id.first_course_name);
        TextView course2name = (TextView) v.findViewById(R.id.second_course_name);
        TextView course3name = (TextView) v.findViewById(R.id.third_course_name);
        TextView course4name = (TextView) v.findViewById(R.id.fourth_course_name);
        TextView course1section = (TextView) v.findViewById(R.id.first_course_section);
        TextView course2section = (TextView) v.findViewById(R.id.second_course_section);
        TextView course3section = (TextView) v.findViewById(R.id.third_course_section);
        TextView course4section = (TextView) v.findViewById(R.id.fourth_course_section);

        course1name.setText(assignedCourses[0][0]);
        course2name.setText(assignedCourses[1][0]);
        course3name.setText(assignedCourses[2][0]);
        course4name.setText(assignedCourses[3][0]);
        String section = "Section "+assignedCourses[0][1];
        course1section.setText(section);
        section = "Section "+assignedCourses[1][1];
        course2section.setText(section);
        section = "Section "+assignedCourses[2][1];
        course3section.setText(section);
        section = "Section "+assignedCourses[3][1];
        course4section.setText(section);

    }

    void setBeginAndEndDates(){
        DatabaseReference currentSemesterRef = databaseReference.child("current_semester");
        currentSemesterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                beginDate = dataSnapshot.child("begin date").getValue(String.class);
                endDate = dataSnapshot.child("end date").getValue(String.class);
                //calculation
                int totalSemesterDays = getTotalSemesterDays(beginDate, endDate) ;
                todaysMonth = todaysMonth+1;
                int numberOfDaysPassed = 0;
                if (todaysMonth==beginMonth && todaysDay>beginDay)
                    numberOfDaysPassed = todaysDay-beginDay;
                else if (todaysMonth==endMonth){
                    if (todaysDay<endDay)
                        numberOfDaysPassed = totalSemesterDays-(endDay-todaysDay);
                    else
                        numberOfDaysPassed = totalSemesterDays;
                }else if (todaysMonth>beginMonth && todaysMonth<endMonth){
                    numberOfDaysPassed = 30-beginDay;
                    int fullMonthsDifference = todaysMonth-beginMonth-1;
                    numberOfDaysPassed = numberOfDaysPassed + (fullMonthsDifference*30);
                    numberOfDaysPassed = numberOfDaysPassed + todaysDay;
                }
                int milestonesRatio = totalSemesterDays/5;
                m_progress = (numberOfDaysPassed/milestonesRatio) + 1;
                setProgressStuff(m_progress);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    int getTotalSemesterDays(String beginDate, String endDate){
        int numberOfDays = 0;
        String[] beginDateArray = beginDate.split("\\.");
        beginDay = Integer.parseInt(beginDateArray[0]);
        beginMonth = Integer.parseInt(beginDateArray[1]);
        String[] endDateArray = endDate.split("\\.");
        endDay = Integer.parseInt(endDateArray[0]);
        endMonth = Integer.parseInt(endDateArray[1]);
        int fullMonthsDifference = endMonth-beginMonth-1;
        /* counting the number of full months included in the semester. One month subtracted
        to ignore the partial months in the beginning and the end of a semester.
            For example, if begin month is 5 (May) and the end month is 8 (August), then to
        count June and July properly we need fullMonthsDifference = 2. And that can be achieved
        through this algorithm [8-5-1 = 3-1 = 2].
         */
        numberOfDays = (30-beginDay)+(fullMonthsDifference*30)+endDay;
        return  numberOfDays;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            Log.e(TAG, "onFragment Interaction Listener not implemented");
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
