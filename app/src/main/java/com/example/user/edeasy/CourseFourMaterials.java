package com.example.user.edeasy;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CourseFourMaterials.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CourseFourMaterials#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseFourMaterials extends Fragment {
    private static final String TAG = "Course 3 Fragment";
    final int RC_UPLOAD_FILES = 101;
    final int RC_DOWNLOAD_FILES = 102;

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    DatabaseReference courseFileDownloadUrls;
    StorageReference storageReference;
    StorageReference courseOneFilesRef;
    FirebaseUser currentUser;

    String roleOfUser;
    String departmentName;
    String courseName;
    String username;
    String sectionNumber;
    static int numberOfFiles;
    String[] fileNames;
    String[] fileTypes;
    int fileTypeEmptyIndex;
    String[] fileDownloadUrls;
    ListView courseMaterialsView;
    String lastpath; //the last part of the file you're going to upload
    ArrayAdapter<String> adapter;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private CourseFourMaterials.OnFragmentInteractionListener mListener;

    public CourseFourMaterials() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CourseFourMaterials.
     */
    // TODO: Rename and change types and number of parameters
    public static CourseFourMaterials newInstance(String param1, String param2) {
        Log.e(TAG, "newInstance");
        CourseFourMaterials fragment = new CourseFourMaterials();
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

        fileNames = new String[1];
        fileNames[0] = "Demo";
        fileDownloadUrls = new String[1];
        fileTypes = new String[5];
        fileTypeEmptyIndex = 0;

        Bundle args = getArguments();
        if (args!=null) {
            departmentName = args.getString("department");
            Log.e(TAG, "#136 : department = "+departmentName);
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
        courseFileDownloadUrls = databaseReference.child("departments/CSE/courses").child(courseName).child("sections").child(sectionNumber).child("file_urls");
        Log.e(TAG, "#127: file info reference = "+courseFileDownloadUrls.toString());
        storageReference = FirebaseStorage.getInstance().getReference();
        courseOneFilesRef = storageReference.child(departmentName).child(courseName).child(sectionNumber);
        Log.e(TAG, "#111: course reference = "+courseOneFilesRef.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_course_three_materials, container, false);

        //displaying the files in ListView
        courseMaterialsView = (ListView) v.findViewById(R.id.course3_materials_listview);
        if (courseMaterialsView==null)
            Log.e(TAG, "course listview is null");
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, fileNames);
        courseMaterialsView.setAdapter(adapter);
        displayFiles();
        setOnItemClickListener();//downloading files on click

        //upload button
        Button uploadOneButton = (Button)v.findViewById(R.id.course3_upload_button);
        uploadOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Choose a file"), RC_UPLOAD_FILES);
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult");
        //uploading files
        if (requestCode==RC_UPLOAD_FILES){
            if (resultCode == RESULT_OK){
                Log.e(TAG, "file received - "+data.getData().getLastPathSegment());
                Uri uri = data.getData();
                lastpath = uri.getLastPathSegment();
                if (lastpath.contains("/")) {
                    int begin = lastpath.indexOf('/');
                    int length = lastpath.length();
                    Log.e(TAG, "begin = "+String.valueOf(begin)+" length="+String.valueOf(length));
                    lastpath = lastpath.substring(begin+1, length);
                }
                Log.e(TAG, "#174: last path segment = "+lastpath);
                StorageReference newDocRef = courseOneFilesRef.child(lastpath);
                UploadTask task = newDocRef.putFile(uri);
                task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri uri = taskSnapshot.getDownloadUrl();
                        if (lastpath.contains(".")) {
                            String name = lastpath.substring(0, lastpath.indexOf('.'));
                            Log.e(TAG, "#198 : file name = "+name);
                            String type = lastpath.substring(lastpath.indexOf('.'), lastpath.length());
                            Log.e(TAG, "#200 : file type = " + type);
                            DatabaseReference newRef = courseFileDownloadUrls.push();
                            Log.e(TAG, "#208 : new ref in db = "+newRef.toString());
                            newRef.child("name").setValue(name);
                            newRef.child("type").setValue(type);
                            Log.e(TAG, "#211 : name & type of the doc = "+ name+type);
                            displayFiles();
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

    void displayFiles(){
        Log.e(TAG, "displayFiles");
        courseFileDownloadUrls.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "onDataChange");
                long filesNumber = dataSnapshot.getChildrenCount();
                numberOfFiles = (int) filesNumber;
                Log.e(TAG, "#208: files number = "+numberOfFiles);
                fileNames = new String[numberOfFiles];
                fileTypes = new String[numberOfFiles];
                int i = 0;
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    fileNames[i] = snap.child("name").getValue(String.class);
                    String type = snap.child("type").getValue(String.class);
                    fileNames[i] = fileNames[i]+"."+type;
                    Log.e(TAG, "#243 : file name and type = "+fileNames[i]);
                    i++;
                }
                updateListView(fileNames);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled");
            }
        });
    }

    void updateListView(String[] fileNames){
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, fileNames);
        courseMaterialsView.setAdapter(adapter);
    }

    void setOnItemClickListener(){
        Log.e(TAG, "setOnItemClickListener");
        courseMaterialsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.e(TAG, "#265 : clicked position = "+String.valueOf(position));
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("Download?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Uri uri = Uri.parse(fileDownloadUrls[position]);
//                        FileDownloadTask fileDownloadTask = courseOneFilesRef.getFile(uri);
                        StorageReference fileRef = courseOneFilesRef.child(fileNames[position]);
                        Log.e(TAG, "#275: file to download ref = "+fileRef.toString());
                        String prefix = fileNames[position].substring(0, fileNames[position].indexOf('.'));
                        String suffix = fileNames[position].substring(fileNames[position].indexOf('.'), fileNames[position].length());
                        try {
                            //getContext().getFilesDir()
                            Log.e(TAG, "#286 : "+getContext().getExternalCacheDir().toString());
                            File rootPath = new File(getContext().getExternalCacheDir()+"/Firebase", fileNames[position]);
                            if(!rootPath.exists()) {
                                boolean x = rootPath.mkdirs();
                                if (!x)
                                    Log.e(TAG, "#289 : make directories failed");
                            }
                            Log.e(TAG, "#291 : directory = "+rootPath);
                            File file = new File(rootPath, fileNames[position]);
                            fileRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Log.e(TAG, "#296 : file download success");
                                }
                            });
                        } catch (Exception e) {
                            Log.e(TAG, "#300 : file creating error = "+e.toString());
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing
                    }
                });
                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
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
        if (context instanceof CourseFourMaterials.OnFragmentInteractionListener) {
            mListener = (CourseFourMaterials.OnFragmentInteractionListener) context;
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
