package com.example.user.edeasy.fragments.course_materials;

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
import android.widget.Toast;

import com.example.user.edeasy.R;
import com.example.user.edeasy.fragments.Dashboard;
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
 * {@link CourseOneMaterials.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CourseOneMaterials#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseOneMaterials extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "Course 1 Fragment";
    final int RC_UPLOAD_FILES = 101;
    final int RC_DOWNLOAD_FILES = 102;

    FirebaseAuth auth;
    DatabaseReference databaseReference;
    DatabaseReference courseFileDownloadUrls;
    StorageReference storageReference;
    StorageReference courseOneFilesRef;
    FirebaseUser currentUser;

    String user_role;
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

        fileNames = new String[1];
        fileNames[0] = "Demo";
        fileDownloadUrls = new String[1];
        fileTypes = new String[5];
        fileTypeEmptyIndex = 0;

        Bundle args = getArguments();
        if (args!=null) {
            departmentName = args.getString("department");
            Log.e(TAG, "#137 : department = "+departmentName);
            courseName = args.getString("course");
            Log.e(TAG, "#139 : course name = "+courseName);
            sectionNumber = args.getString("section");
            Log.e(TAG, "#141 : section number = "+sectionNumber);
            username = args.getString("username");
            Log.e(TAG, "#143 : username = "+username);
            user_role = args.getString("role");
            Log.e(TAG, "#145 : role = "+user_role);
        }else
            Log.e(TAG, "#99 : received args is null");

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        courseFileDownloadUrls = databaseReference.child("departments").child(departmentName)
                .child("courses").child(courseName).child("sections").child(sectionNumber).child("file_urls");
        Log.e(TAG, "#127: file info reference = "+courseFileDownloadUrls.toString());
        storageReference = FirebaseStorage.getInstance().getReference();
        courseOneFilesRef = storageReference.child(departmentName).child(courseName).child(sectionNumber);
        Log.e(TAG, "#111: course reference = "+courseOneFilesRef.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_course_one_materials, container, false);
        Button uploadButton = (Button) v.findViewById(R.id.course1_upload_button);

        if (user_role.equals("student")){
            uploadButton.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "You are allowed to upload in this folder", Toast.LENGTH_LONG).show();
        }

        //displaying the files in ListView
        courseMaterialsView = (ListView) v.findViewById(R.id.course1_materials_listview);
        if (courseMaterialsView==null)
            Log.e(TAG, "course listview is null");
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, fileNames);
        courseMaterialsView.setAdapter(adapter);
        displayFiles();
        setOnItemClickListener();//downloading files on click

        //upload button
        uploadButton.setOnClickListener(new View.OnClickListener() {
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
                Uri uri = data.getData();
                lastpath = uri.getLastPathSegment();
               if (lastpath.contains("/")) {
                   int begin = lastpath.indexOf('/');
                   int length = lastpath.length();
                   lastpath = lastpath.substring(begin+1, length);
               }
                StorageReference newDocRef = courseOneFilesRef.child(lastpath);
                UploadTask task = newDocRef.putFile(uri);
                task.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri uri = taskSnapshot.getDownloadUrl();
                        if (uri!=null) {
                            Log.e(TAG, "#196");
                            String name = lastpath.substring(0, lastpath.indexOf('.'));
                            String type = lastpath.substring(lastpath.indexOf('.'), lastpath.length());
                            DatabaseReference newRef = courseFileDownloadUrls.push();
                            Log.e(TAG, "#208 : new ref in db = "+newRef.toString());
                            newRef.child("name").setValue(name);
                            newRef.child("type").setValue(type);
                            Log.e(TAG, "#211 : name & type of the doc = "+ name+type);
                            displayFiles();//to show the updated list
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
                    String name = snap.child("name").getValue(String.class);
                    String type = snap.child("type").getValue(String.class);
                    fileNames[i] = name+type;
                    if (!fileNames[i].contains("."))
                        fileNames[i] = name+"."+type;
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
        //to download on click
        courseMaterialsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.e(TAG, "#265 : clicked position = "+String.valueOf(position));
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Download?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StorageReference fileRef = courseOneFilesRef.child(fileNames[position]);
                        Log.e(TAG, "#275: file to download ref = "+fileRef.toString());
                        String prefix = fileNames[position].substring(0, fileNames[position].indexOf('.'));
                        String suffix = fileNames[position].substring(fileNames[position].indexOf('.'), fileNames[position].length());
                        try {
                            Log.e(TAG, "#286 : "+getContext().getExternalCacheDir().toString());
                            File rootPath = new File(getContext().getExternalCacheDir()+"/Firebase", fileNames[position]);
                            if(!rootPath.exists()) {
                                boolean dir = rootPath.mkdirs();
                                if (!dir)
                                    Log.e(TAG, "#289 : make directories failed");
                            }
                            Log.e(TAG, "#291 : directory = "+rootPath);
                            File file = new File(rootPath, fileNames[position]);
                            fileRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Log.e(TAG, "#296 : file download success");
                                    Toast.makeText(getContext(), "Download successful!", Toast.LENGTH_SHORT).show();
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
        Fragment fragment = new Dashboard();
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
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
