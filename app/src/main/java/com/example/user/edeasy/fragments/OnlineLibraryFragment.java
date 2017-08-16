package com.example.user.edeasy.fragments;

import android.app.ActionBar;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.example.user.edeasy.R;
import com.example.user.edeasy.fragments.department_libraries.LibraryBBS;
import com.example.user.edeasy.fragments.department_libraries.LibraryBIL;
import com.example.user.edeasy.fragments.department_libraries.LibraryCSE;
import com.example.user.edeasy.adapters.DepartmentsGridAdapter;
import com.example.user.edeasy.fragments.department_libraries.LibraryEEE;
import com.example.user.edeasy.fragments.department_libraries.LibraryESS;
import com.example.user.edeasy.fragments.department_libraries.LibraryMNS;
import com.example.user.edeasy.fragments.department_libraries.LibraryPHR;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnlineLibraryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OnlineLibraryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnlineLibraryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;


    private static final String TAG = "**Library**";

    String username;
    String email;

    private OnFragmentInteractionListener mListener;

//    ListView libraryListview ;
//    ListAdapter adapter;
    GridView departmentsGridView;

    public OnlineLibraryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OnlineLibraryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OnlineLibraryFragment newInstance(String param1, String param2) {
        OnlineLibraryFragment fragment = new OnlineLibraryFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();
            username = args.getString("username");
            email = args.getString("email");
            Log.e(TAG, "username = "+username+" email="+email);
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_online_library, container, false);

        //setting the right title
        if (((AppCompatActivity)getActivity()).getSupportActionBar()!=null)
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Online Library");
        else
            Log.e(TAG, "action bar is null");

        departmentsGridView = (GridView) v.findViewById(R.id.departments_gridview);
        ListAdapter adapter = new DepartmentsGridAdapter(getContext());
        departmentsGridView.setAdapter(adapter);

        departmentsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new LibraryCSE();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                //"BBS","BIL","CSE","EEE","MNS","PHR"
                switch (position){
                    case 0 :
                        fragment = new LibraryBBS();
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack("BBS").commit();
                        break;
                    case 1 :
                        fragment = new LibraryBIL();
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack("BIL").commit();
                        break;
                    case 2 :
                        fragment = new LibraryCSE();
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack("CSE").commit();
                        break;
                    case 3 :
                        fragment = new LibraryEEE();
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack("EEE").commit();
                        break;
                    case 4 : //MNS
                        fragment = new LibraryMNS();
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack("MNS").commit();
                        break;
                    case 5 : //ESS
                        fragment = new LibraryESS();
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack("ESS").commit();
                        break;
                    case 6 :
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack("PHR").commit();
                        break;
                }

            }
        });

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
        Log.e(TAG, "onAttach");
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
//        Fragment fragment = new Dashboard();
//        getFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, fragment).commit();
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
