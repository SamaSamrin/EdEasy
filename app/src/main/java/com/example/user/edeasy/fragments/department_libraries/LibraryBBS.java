package com.example.user.edeasy.fragments.department_libraries;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.user.edeasy.R;
import com.example.user.edeasy.adapters.LibraryAdapter;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LibraryBBS.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LibraryBBS#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryBBS extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    private static final String TAG = "**Library CSE**";
    DatabaseReference bbsBooksDatabaseRef = FirebaseDatabase.getInstance().getReference().child("departments/BBS/books");
    StorageReference bbsBooksStorageReference = FirebaseStorage.getInstance().getReference().child("BBS/Books");
    ListView booksListView;
    SearchView booksSearchView;
    ListAdapter adapter;
    String[] bookNames;

    public LibraryBBS() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LibraryBBS.
     */
    // TODO: Rename and change types and number of parameters
    public static LibraryBBS newInstance(String param1, String param2) {
        LibraryBBS fragment = new LibraryBBS();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_library_bbs, container, false);
        //setting the right title
        if (((AppCompatActivity)getActivity()).getSupportActionBar()!=null)
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("BBS Library");
        else
            Log.e(TAG, "action bar is null");
        booksSearchView = (SearchView) v.findViewById(R.id.library_bbs_searchView);
        booksListView = (ListView) v.findViewById(R.id.library_bbs_listView);
        bookNames = new String[]{"Dummy 1.pdf", "Dummy 2.docx", "Dummy 3.txt"};
        adapter = new LibraryAdapter(getContext(), bookNames);
        //adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, bookNames);
        booksListView.setAdapter(adapter);
        setListViewListener();
        return v;
    }

    void retrieveAllBooks(){
        bbsBooksDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long number = dataSnapshot.getChildrenCount();
                int numberOfBooks = (int) number;
                Log.e(TAG, "number = "+numberOfBooks);
                int i = 1;
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    String name = snap.child("name").getValue(String.class);
                    String type = snap.child("type").getValue(String.class);
                    bookNames[i-1] = name+"."+type;
                    Log.e(TAG, "at i = "+i+"name = "+bookNames[i-1]);
                    i++;
                }
                adapter = new LibraryAdapter(getContext(), bookNames);
                booksListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void setListViewListener(){
        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "#131 : "+bookNames[position]);
                StorageReference newRef = bbsBooksStorageReference.child(bookNames[position]);
                Task<Uri> downloadUrl = newRef.getDownloadUrl();
                Log.e(TAG, "#136 : "+downloadUrl.toString());
            }
        });
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
