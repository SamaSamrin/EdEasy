package com.example.user.edeasy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.user.edeasy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

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
    final int RC_FILE_PICKER = 101;
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
//    int numberOfCourses = 0;
//    String[][] assignedCourses;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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
                //open the storage on phone
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Choose a file"), RC_FILE_PICKER);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==RC_FILE_PICKER){
            if (resultCode == RESULT_OK){
                Log.e(TAG, "file received - "+data.getData().getLastPathSegment());
                Uri uri = data.getData();
                //Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
                String lastpath = uri.getLastPathSegment();
               if (lastpath.contains("/")) {
                   int begin = lastpath.indexOf('/');
                   int length = lastpath.length();
                   Log.e(TAG, "begin = "+String.valueOf(begin)+" length="+String.valueOf(length));
                   lastpath = lastpath.substring(begin+1, length);
               }
               Log.e(TAG, "#151: last path segment = "+lastpath);
                StorageReference newDocRef = courseOneFilesRef.child(lastpath);
                UploadTask task = newDocRef.putFile(uri);
                task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri uri = taskSnapshot.getDownloadUrl();
                        if (uri!=null) {
                            Log.e(TAG, "#155 : download url of doc = " + uri.toString());
                            Log.e(TAG, "#156 : metadata of the doc = "+ taskSnapshot.getMetadata());
                        }
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "#167: upload failed due to: "+e.toString());
                    }
                });
            }else if (resultCode == RESULT_CANCELED){
                Log.e(TAG, "file not received");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    public static void verifyStoragePermissions(Activity activity) {
        Log.e(TAG, "verifyStorage: activity name = "+activity.getLocalClassName());
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
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
