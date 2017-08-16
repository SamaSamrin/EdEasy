package com.example.user.edeasy.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

/**
 * Created by ASUS on 16-Aug-17.
 */

public class SearchResultsAdapter extends BaseAdapter implements Filterable {

    Context context;
    String[] bookNames;
    int numberOfMatchedResults = 0;

    public SearchResultsAdapter(Context c, String[] names){
        context = c;
        bookNames = names;
        if (names!=null)
            numberOfMatchedResults = bookNames.length;
    }

    @Override
    public int getCount() {
        return numberOfMatchedResults;
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
        return v;
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }
}
