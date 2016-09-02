package com.hornblasters.soundboard2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hornblasters.xml.Category;

import java.util.List;

class CategoriesAdapter extends ArrayAdapter<Category> {
    private static final String TAG = "StoreCategoriesAdapter";
    private static final String TAG_PRELOAD = "preload";
    private static final boolean FEATURE_PRELOAD = false;
    private final LayoutInflater li;

    public CategoriesAdapter(Context context, List<Category> items) {
        super(context, R.layout.view_list_category, items);

        li = LayoutInflater.from(context);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView v = (TextView) convertView;

        if (v == null) {
            v = (TextView) li.inflate(R.layout.view_list_category, parent, false);
        }

        Category c = getItem(position);

        v.setTag(c.id);
        v.setText(c.title);
        return v;
    }
}