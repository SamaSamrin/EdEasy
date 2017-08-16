package com.example.user.edeasy.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.edeasy.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by ASUS on 15-Aug-17.
 */

public class LibraryAdapter extends BaseAdapter implements Filterable {

    private static final String TAG = "**Library Adapter**";

    Context context;
    String[] bookNames;
    String[][] details;
    int numberOfBooks;
    Filter filter;
    ViewGroup parent;
    String[] matchedNames;
    StorageReference cseBooksStorageReference = FirebaseStorage.getInstance().getReference().child("CSE/Books");

    public LibraryAdapter(Context c, String[] namesOfBooks){
        context = c;
        bookNames = namesOfBooks;
    }

    public LibraryAdapter(Context c,  String[] namesOfBooks, String[][] details){
        //Log.e(TAG, "constructor" );
        context = c;
        bookNames = namesOfBooks;
        numberOfBooks = namesOfBooks.length;
        this.details = new String[numberOfBooks][2];
        this.details = details;
        for (int i = 0; i < numberOfBooks ; i++) {
            //Log.e(TAG, "details = "+details[i][0]+", "+details[i][1]);
            details[i][1] = details[i][1]+" Edition";
        }
        //filter = super.getFilter();
    }

    @Override
    public int getCount() {
        return numberOfBooks;
    }

    @Override
    public Object getItem(int position) {
        return bookNames[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        this.parent = parent;
        View v = convertView;
        if (v == null) {
            v = ((Activity) context).getLayoutInflater().inflate(R.layout.item_library, parent, false);
        }
        TextView bookTitle = (TextView) v.findViewById(R.id.library_book_title);
        if (position<bookNames.length) {
            int indexOfDot = bookNames[position].indexOf(".");
            String name = bookNames[position].substring(0, indexOfDot);
            bookTitle.setText(name);
            bookTitle.setTypeface(Typeface.DEFAULT_BOLD);
            bookTitle.setAllCaps(true);
        }
        TextView author = (TextView) v.findViewById(R.id.library_authors_name);
        author.setTextColor(Color.BLACK);
        TextView edition = (TextView) v.findViewById(R.id.library_book_edition);
        edition.setTextColor(Color.BLACK);

        if (details!=null){
            author.setText(details[position][0]);
            edition.setText(details[position][1]);
        }else
            Log.e(TAG, "details is null");

        return v;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    // No filter implemented we return all the list
                    results.values = bookNames;
                    results.count = bookNames.length;
                }
                else {
                    // We perform filtering operation
                    matchedNames = new String[0];
                    int index = 0;
                    for (String p : bookNames) {
                        if (p.toUpperCase().contains(constraint.toString().toUpperCase())) {
                            //Log.e(TAG, "matched book= "+p);
                            matchedNames = resizeArray(matchedNames);
                            //Log.e(TAG, "resized length="+matchedNames.length);
                            matchedNames[index] = p;
                            //length = je koyta match hoise
                            index++;
                        }
                    }
                    results.values = matchedNames;
                    results.count = index;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                int matched = results.count;
                Log.e(TAG, "matches found = "+matched);
                // Now we have to inform the adapter about the new list filtered
                if (results.count == 0)
                    notifyDataSetInvalidated();
                else if (matched!=bookNames.length){
                   //bookNames = (String[]) results.values;
                    matchedNames = (String[]) results.values;
                    View v = null;
                    if (parent!=null)
                        v = ((Activity) context).getLayoutInflater().inflate(R.layout.item_library, parent, false);
                    else
                        Log.e(TAG, "#145: parent is null");

                    for (int i = 0; i < matched; i++) {
                        int index = findIndex(matchedNames[i]);
                        View gh = parent.getChildAt(index);
                        View tv = getView(0, gh, parent);
                        TextView name = (TextView) tv.findViewById(R.id.library_book_title);
                        name.setText(bookNames[index]);
                        name.setTextColor(Color.RED);
                    }
                    notifyDataSetChanged();
                }else {
                    View v = null;
                    if (parent!=null)
                        v = ((Activity) context).getLayoutInflater().inflate(R.layout.item_library, parent, false);
                    else
                        Log.e(TAG, "#145: parent is null");

                    for (int i = 0; i < matchedNames.length; i++) {
                        int index = findIndex(matchedNames[i]);
                        View gh = parent.getChildAt(index);
                        View tv = getView(0, gh, parent);
                        TextView name = (TextView) tv.findViewById(R.id.library_book_title);
                        name.setText(bookNames[index]);
                        name.setTextColor(Color.parseColor("#003c8f"));
                    }
                    notifyDataSetChanged();
                }
                //Log.e(TAG, matched+" matches found");
            }
        };
        return filter;
    }

    private String[] resizeArray(String[] array){
        String[] temp = array.clone();
        String[] resizedArray = new String[array.length+1];
        for (int i = 0; i < temp.length; i++) {
            resizedArray[i] = temp[i];
        }
        return resizedArray;
    }

    public void highlightItems(String[] name){
        View v = null;
        if (parent!=null)
            v = ((Activity) context).getLayoutInflater().inflate(R.layout.item_library, parent, false);
        else
            Log.e(TAG, "parent is null");
        int counter = 0;
        String[] matchedResults = new String[10];
        int emptyIndex = 0 ;
        for (String bookName : name) {
            int position = findIndex(bookName);

        }
        if (counter>=name.length) {
            Toast.makeText(context, "NO MATCH FOUND", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }

    private int findIndex(String name){
        int index = 0;
        for (int i = 0; i < bookNames.length; i++) {
            if (bookNames[i].equals(name)) {
                index = i;
                break;
            }
        }
        return index;
    }

}
