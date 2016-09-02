package com.hornblasters.soundboard2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageFragment extends Fragment {
    private static final String TAG = "ImageFragment";
    private static final String ARG_IMAGE = "image";

    public static ImageFragment newInstance(String imageString) {
        ImageFragment fragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_IMAGE, imageString);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        String imageString = args.getString(ARG_IMAGE);
        View v = inflater.inflate(R.layout.fragment_store_image, container, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.image);

        Picasso.with(getActivity().getApplicationContext()).load(imageString).fit().centerInside().into(imageView);
        return v;
    }
}
