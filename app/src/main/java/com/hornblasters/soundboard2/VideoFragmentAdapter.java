package com.hornblasters.soundboard2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hornblasters.xml.Video;
import com.squareup.picasso.Picasso;

import java.util.List;


class VideoFragmentAdapter extends ArrayAdapter<Video> {
    private static final String TAG = "VideoAdapter";
    private final LayoutInflater vi;

    public VideoFragmentAdapter(Context context, List<Video> items) {
        super(context, R.layout.view_list_video_fragment, items);
        vi = LayoutInflater.from(context);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View getView(int position, View v, ViewGroup vg) {
        ViewHolder holder;
        if (v == null) {

            v = vi.inflate(R.layout.view_list_video_fragment, vg, false);
            holder = new ViewHolder();
            holder.title = (TextView) v.findViewById(R.id.video_title);
            holder.image = (ImageView) v.findViewById(R.id.video_image);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.position = position;
        Video p = getItem(position);
        holder.uri = p.uri;
        holder.id = p.id;
        holder.title.setText(p.title);

        String imageString = "http://img.youtube.com/vi/" + p.uri + "/0.jpg";
        Picasso.with(vg.getContext().getApplicationContext()).load(imageString).fit().centerInside().into(holder.image);
        return v;
    }

    public static class ViewHolder {
        int id;
        TextView title;
        TextView summary;
        ImageView image;
        String uri;
        int position;
    }
}