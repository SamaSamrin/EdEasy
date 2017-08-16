package com.example.user.edeasy.fragments.department_libraries;

import android.app.SearchManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.user.edeasy.R;
import com.example.user.edeasy.adapters.LibraryAdapter;
import android.widget.Filterable;
import com.example.user.edeasy.adapters.SearchResultsAdapter;
import com.example.user.edeasy.fragments.OnlineLibraryFragment;
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
 * {@link LibraryCSE.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LibraryCSE#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LibraryCSE extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
    private OnFragmentInteractionListener mListener;

    private static final String TAG = "**Library CSE**";
    DatabaseReference cseBooksDatabaseRef = FirebaseDatabase.getInstance().getReference().child("departments/CSE/books");
    StorageReference cseBooksStorageReference = FirebaseStorage.getInstance().getReference().child("CSE/Books");
    ListView booksListView;
    SearchView booksSearchView;
    LibraryAdapter adapter;
    String[] bookNames;
    String[][] details;
    String queryText;
    ListView searchResultsListview;

    public LibraryCSE() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LibraryCSE.
     */
    // TODO: Rename and change types and number of parameters
    public static LibraryCSE newInstance(String param1, String param2) {
        LibraryCSE fragment = new LibraryCSE();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrieveAllBooks();
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "db ref = "+cseBooksDatabaseRef.toString());
        Log.e(TAG, "storage ref = "+cseBooksStorageReference.toString());
        // Inflate the layout for this fragment
        Log.e(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.fragment_library_cse, container, false);
        //setting the right title
        if (((AppCompatActivity)getActivity()).getSupportActionBar()!=null)
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("CSE Library");
        else
            Log.e(TAG, "action bar is null");
        //search view in actionbar
        setHasOptionsMenu(true);
        //views
        booksListView = (ListView) v.findViewById(R.id.library_cse_listView);
        bookNames = new String[]{"Dummy 1.pdf", "Dummy 2.docx", "Dummy 3.txt"};
        adapter = new LibraryAdapter(getContext(), bookNames);
        //adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, bookNames);
        booksListView.setAdapter(adapter);
        booksListView.setTextFilterEnabled(true);
        setListViewListener();
        return v;
    }

    void retrieveAllBooks(){
        cseBooksDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long number = dataSnapshot.getChildrenCount();
                int numberOfBooks = (int) number;
                bookNames = new String[numberOfBooks];
                details = new String[numberOfBooks][2];// authors, edition
                //Log.e(TAG, "number = "+numberOfBooks);
                int i = 1;
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    String name = snap.child("name").getValue(String.class);
                    String type = snap.child("type").getValue(String.class);
                    bookNames[i-1] = name+"."+type;
                    details[i-1][0] = snap.child("author").getValue(String.class);
                    details[i-1][1] = snap.child("edition").getValue(String.class);
                    //Log.e(TAG, "at i = "+i+" details = "+details[i-1][0]+", "+details[i-1][1]);
                    i++;
                }
                adapter = new LibraryAdapter(getContext(), bookNames, details);
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
                //Log.e(TAG, "#131 : "+bookNames[position]);
                StorageReference newRef = cseBooksStorageReference.child(bookNames[position]);
                Task<Uri> downloadUrl = newRef.getDownloadUrl();
                //Log.e(TAG, "#136 : "+downloadUrl.toString());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.e(TAG, "on create options menu");
        inflater.inflate(R.menu.options_menu, menu);
        //super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search){
            Log.e(TAG, "search clicked");
            SearchManager searchManager = (SearchManager) getActivity()
                    .getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) item.getActionView();

            searchView.setSearchableInfo(searchManager.getSearchableInfo(
                    getActivity().getComponentName()));
            searchView.setSubmitButtonEnabled(true);
            queryText = "";
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
//                    SearchResultsAdapter adapter = new SearchResultsAdapter(getContext(), bookNames);
//                    adapter.
                    adapter.getFilter().filter(query);
                    queryText = query;
                    Log.e(TAG, "query = "+queryText);
                    int counter = 0;
                    String[] matchedResults = new String[10];
                    int emptyIndex = 0 ;
                    for (String bookName : bookNames) {
                        if (bookName.contains(query)) {
                            Toast.makeText(getContext(), bookName, Toast.LENGTH_SHORT).show();
                            //need to display results in a list
                            bookName = bookName+".pdf";
                            StorageReference bookRef = cseBooksStorageReference.child(bookName);
                            //Log.e(TAG, "#194: book ref = "+bookRef);
                            matchedResults[emptyIndex] = bookName;
                            emptyIndex++;
                            break;
                        }else
                            counter++;
                    }
                    if (counter>=bookNames.length) {
                        Toast.makeText(getContext(), "NO MATCH FOUND", Toast.LENGTH_SHORT).show();
                    }else{
                        //send the matched book names to search results listview

                    }

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (adapter==null)
                        Log.e(TAG, "#226 : adapter is null");
                    adapter.getFilter().filter(newText);
                    return false;
                }
            });
        }
        return true;
    }

    void onQueryTextChange(String query) {
        //adapter.getFilter().filter(query);
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
        Log.e(TAG, "on Detach");
        Fragment fragment = new OnlineLibraryFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
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
