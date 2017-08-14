package com.example.user.edeasy;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "**Library**";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        departmentsGridView = (GridView) v.findViewById(R.id.departments_gridview);
        ListAdapter adapter = new DepartmentsGridAdapter(getContext());
        departmentsGridView.setAdapter(adapter);
//        libraryListview = (ListView) v.findViewById(R.id.library_listview);
//        String[] items = {"Sama", "Samrin", "Orpa"};
//        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, items);
//        libraryListview.setAdapter(adapter);
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

class DepartmentsGridAdapter extends BaseAdapter{

    Context context;
    String[] departments = {"BBA","BIL","CSE","EEE","MNS","PHR"};
    String[] backgroundColors = {"#B3E5FC", "#EDE7F6",
            "#EDE7F6", "#B3E5FC", "#B3E5FC", "#EDE7F6"};

    DepartmentsGridAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return departments.length;
    }

    @Override
    public Object getItem(int i) {
        return departments[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView itemText = null;
        if(view==null){
            itemText = new TextView(context);
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int screenwidth = metrics.widthPixels;
            int orientation = context.getResources().getConfiguration().orientation;
            if(orientation== Configuration.ORIENTATION_PORTRAIT)
                itemText.setLayoutParams(new GridView.LayoutParams(screenwidth/2, screenwidth/2));
            else
                itemText.setLayoutParams(new GridView.LayoutParams(screenwidth/3, screenwidth/3));
            itemText.setPadding(10, 10, 10, 10);
            itemText.setGravity(Gravity.CENTER);
            itemText.setTextColor(Color.BLACK);
            itemText.setTypeface(itemText.getTypeface(), Typeface.BOLD);
        }else{
            itemText = (TextView) view;
        }
        itemText.setText(departments[i]);
        itemText.setBackgroundColor(Color.parseColor(backgroundColors[i]));
        return itemText;
    }
}