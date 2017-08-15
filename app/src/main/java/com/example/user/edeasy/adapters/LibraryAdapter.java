package com.example.user.edeasy.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.user.edeasy.R;

/**
 * Created by ASUS on 15-Aug-17.
 */

public class LibraryAdapter extends BaseAdapter {

    private static final String TAG = "**Library Adapter**";

    Context context;
    String[] bookNames;
    String[][] details;
    int numberOfBooks;

    public LibraryAdapter(Context c, String[] namesOfBooks){
        context = c;
        bookNames = namesOfBooks;
    }

    public LibraryAdapter(Context c,  String[] namesOfBooks, String[][] details){
        Log.e(TAG, "constructor" );
        context = c;
        bookNames = namesOfBooks;
        numberOfBooks = namesOfBooks.length;
        this.details = new String[numberOfBooks][2];
        this.details = details;
        for (int i = 0; i < numberOfBooks ; i++) {
            //Log.e(TAG, "details = "+details[i][0]+", "+details[i][1]);
            details[i][1] = details[i][1]+" Edition";
        }
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
        View v = convertView;
        if (v == null) {
            v = ((Activity) context).getLayoutInflater().inflate(R.layout.item_library, parent, false);
        }
        TextView bookTitle = (TextView) v.findViewById(R.id.library_book_title);
        int indexOfDot = bookNames[position].indexOf(".");
        String name = bookNames[position].substring(0, indexOfDot);
        bookTitle.setText(name);
        bookTitle.setTypeface(Typeface.DEFAULT_BOLD);
        bookTitle.setAllCaps(true);

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
}
