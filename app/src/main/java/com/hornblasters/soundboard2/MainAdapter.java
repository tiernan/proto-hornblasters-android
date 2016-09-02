package com.hornblasters.soundboard2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hornblasters.core.SquareImageView;

class MainAdapter extends BaseAdapter {
    private final LayoutInflater li;
    private final int ic;


    public MainAdapter(Context c) {
        li = LayoutInflater.from(c);
        ic = thumbs.length;
    }

    public int getCount() {
        return ic;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int p, View v, ViewGroup vg) {

        if (v == null) {
            v = li.inflate(R.layout.view_grid_social, vg, false);
        }

        SquareImageView imageView = (SquareImageView) v.findViewById(R.id.image);
        imageView.setImageResource(thumbs[p]);
        return v;
    }

    public String getUrl(int p) {
        return urls[p];
    }

    private static final int[] thumbs = {
            R.drawable.social_instagram, R.drawable.social_facebook,
            R.drawable.social_twitter, R.drawable.social_youtube,
    };

    private static final String[] urls = {
            "https://www.instagram.com/hornblasters",
            "https://www.facebook.com/hornblasters",
            "https://www.twitter.com/hornblasters",
            "https://www.youtube.com/user/hornblasters/"
    };
}