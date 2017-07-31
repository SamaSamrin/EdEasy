package com.example.user.edeasy;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;


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
    DatabaseReference courseFileDownloadUrls;
    StorageReference storageReference;
    StorageReference courseOneFilesRef;
    FirebaseUser currentUser;

    String user_role;
    String user_department;
    String username;
    String user_email;
    String student_id;
    String credits_completed;
    int numberOfCourses;
    String[][] assignedCourses;
    String[] departments;
    DatabaseReference userRef;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_account_profile, container, false);

        //setting views

        //basic info
        TextView nameDisplay = (TextView) v.findViewById(R.id.account_name_display);
        TextView studentIdDisplay = (TextView) v.findViewById(R.id.studentID_account);
        TextView creditsDoneDisplay = (TextView) v.findViewById(R.id.completed_credits_account);
        TextView departmentDisplay = (TextView) v.findViewById(R.id.department_account);

        nameDisplay.setText(username.toUpperCase());
        studentIdDisplay.setText(student_id);
        departmentDisplay.setText(user_department);
        creditsDoneDisplay.setText(credits_completed);

        //setting photo according to user role
        ImageView propic = (ImageView) v.findViewById(R.id.propic_account);
        if (user_role.equals("student"))
            propic.setImageResource(R.drawable.student);
        else if (user_role.equals("teacher"))
            propic.setImageResource(R.drawable.teacher);

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

        return v;
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
