package com.example.user.edeasy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.user.edeasy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CourseOneMaterials.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CourseOneMaterials#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseOneMaterials extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private static final String TAG = "Course 1 Fragment";
    ListView courseMaterialsView;

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    DatabaseReference currentUserRef;
    StorageReference storageReference;
    StorageReference courseOneFilesRef;
    FirebaseUser currentUser;

    String roleOfUser;
    String courseName;
    String username;
    String sectionNumber;
    int numberOfCourses = 0;
    String[][] assignedCourses;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public CourseOneMaterials() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CourseOneMaterials.
     */
    // TODO: Rename and change types and number of parameters
    public static CourseOneMaterials newInstance(String param1, String param2) {
        Log.e(TAG, "newInstance");
        CourseOneMaterials fragment = new CourseOneMaterials();
        Bundle args = new Bundle();
//        args.putString(course1Name, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
////            mParam1 = getArguments().getString(course1Name);
////            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

        username = "COURSE ONE USERNAME";
        //Log.e(TAG, "#92: username is "+username);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //Log.e(TAG, "#107 : database ref = "+databaseReference.toString());
        String user_email;
        if (currentUser!=null) {
            user_email = currentUser.getEmail();
            Log.e(TAG, "#95: current user email = "+user_email);
            if (user_email!=null){
                handleCurrentUserInfo(user_email);
            }else{
                Log.e(TAG, "user email null");
            }
        }else
            Log.e(TAG, "#105: current user null");

        courseName = "CSE110";
        sectionNumber = "1";
        storageReference = FirebaseStorage.getInstance().getReference();
        courseOneFilesRef = storageReference.child("CSE").child(courseName).child(sectionNumber);
        Log.e(TAG, "#111: course reference = "+courseOneFilesRef.toString());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        // Inflate the layout for this fragment
        courseMaterialsView = (ListView) container.findViewById(R.id.course1_materials_listview);
        return inflater.inflate(R.layout.fragment_course_one_materials, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String username) {//argument was Uri originally
        Log.e(TAG, "onButtonPressed");
        if (mListener != null) {
            mListener.onFragmentInteraction(username);
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.e(TAG, "onAttach");
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            Log.e(TAG, "#135: fragment interaction listener not implemented");
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.e(TAG, "onDetach");
        super.onDetach();
        mListener = null;
    }

    void handleCurrentUserInfo(String user_email){//whole process of retrieving current user data

        if (roleOfUser!=null)
            Log.e(TAG, "#157: user role is - "+roleOfUser);
        else
            Log.e(TAG, "#159: user role is null");

        if(currentUser != null){
            Log.e(TAG, "#162: current user is not null");
            user_email = currentUser.getEmail();
            Log.e(TAG, "#164: current user email = " + user_email);
            if (user_email!=null) {
                int croppedEmailIdLimit = user_email.length() - 4;
                String emailID = user_email.substring(0, croppedEmailIdLimit);
                Log.e(TAG, "cropped email ID = "+emailID);
                currentUserRef = databaseReference.child("users").child("students").child(emailID);
                Log.e(TAG, " #170 : current user reference = "+currentUserRef.toString());
            }else {
                Log.e(TAG, "#172: user email is null");
            }

            if (currentUserRef != null){
                DatabaseReference nameRef = currentUserRef.child("name");
                nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        username = dataSnapshot.getValue(String.class);
                        Log.e(TAG, "#181: the current username from snapshot is = "+username);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "#186: database error = "+databaseError.toString());
                    }
                });

                DatabaseReference coursesRef = currentUserRef.child("courses_assigned");
                coursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long numberOfChildren = dataSnapshot.getChildrenCount();
                        numberOfCourses = (int) numberOfChildren;
                        Log.e(TAG, "#200 : number of courses = "+ String.valueOf(numberOfCourses));
                        assignedCourses = new String[numberOfCourses][2];
                        int i = 1;
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                            //Log.e(TAG, "#206: i="+String.valueOf(i)+" , "+postSnapshot.child(String.valueOf(i)).child("section").toString());
                            String key = postSnapshot.getKey();
                            String course = postSnapshot.child("course_code").getValue(String.class);
                            Log.e(TAG, "course = "+course);
                            Long section = postSnapshot.child("section").getValue(long.class);
                            Log.e(TAG, "section = "+String.valueOf(section));
                            i++;
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else{
                Log.e(TAG, "#190: current user reference is null");
            }
        }else{
            Log.e(TAG, "#193: current Firebase user is null");
        }
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
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String username);//argument was Uri originally
    }
}
