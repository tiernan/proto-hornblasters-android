package com.hornblasters.soundboard2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hornblasters.core.SquareImageView;

class SoundboardAdapter extends BaseAdapter {
    private final LayoutInflater li;
    private final int ic;

    public SoundboardAdapter(Context c) {
        this.ic = thumbs.length;
        li = LayoutInflater.from(c);
    }

    public int getAudio(int position) {
        return audios[position];
    }

    public int getString(int position) {
        return strings[position];
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
            v = li.inflate(R.layout.view_grid_soundboard, vg, false);
        }
        SquareImageView imageView = (SquareImageView) v.findViewById(R.id.image);
        imageView.setImageResource(thumbs[p]);
        return v;
    }

    private static final int[] thumbs = {
            R.drawable.sb_airchime_k5la, R.drawable.sb_airchime_k3la, R.drawable.sb_hornblasters_shocker,
            R.drawable.sb_airchime_p3, R.drawable.sb_hornblasters_outlaw, R.drawable.sb_hornblasters_klaxon,
            R.drawable.sb_hornblasters_caboose, R.drawable.sb_hornblasters_bandit, R.drawable.sb_hornblasters_psychoblasters,
            R.drawable.sb_hornblasters_psychoblasters_v2, R.drawable.sb_hornblasters_train_whistle, R.drawable.sb_hornblasters_motorcycle,
            R.drawable.sb_police_interceptor, R.drawable.sb_ambulance,
    };

    private static final int[] strings = {
            R.string.audio_k5, R.string.audio_k3, R.string.audio_shockers,
            R.string.audio_p3, R.string.audio_outlaw, R.string.audio_klaxon,
            R.string.audio_caboose, R.string.audio_bandit, R.string.audio_psycho,
            R.string.audio_psycho_v2, R.string.audio_train_whistle, R.string.audio_nautilus,
            R.string.audio_police, R.string.audio_ambulance,
    };

    private static final int[] audios = {
            R.raw.k5, R.raw.k3, R.raw.shockers,
            R.raw.p3, R.raw.outlaw, R.raw.ooga,
            R.raw.caboose, R.raw.bandit, R.raw.psycho,
            R.raw.psycho_v2, R.raw.train_whistle, R.raw.nautilus,
            R.raw.police, R.raw.ambulance,
    };
}