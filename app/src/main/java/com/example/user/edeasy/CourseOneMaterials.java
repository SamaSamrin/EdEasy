package com.example.user.edeasy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

        Bundle args = getArguments();
        if (args!=null) {
            courseName = args.getString("course");
            Log.e(TAG, "#97 : course name = "+courseName);
            sectionNumber = args.getString("section");
            Log.e(TAG, "#99 : section number = "+sectionNumber);
            username = args.getString("username");
            Log.e(TAG, "#101 : username = "+username);
        }else
            Log.e(TAG, "#99 : received args is null");

        username = "COURSE ONE USERNAME";
        //Log.e(TAG, "#92: username is "+username);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
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
        View v = inflater.inflate(R.layout.fragment_course_one_materials, container, false);
        Button uploadOneButton = (Button)v.findViewById(R.id.course1_upload_button);
        //set onclick listener
        uploadOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return v;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity a    nd potentially other fragments contained in that
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
