package com.hornblasters.soundboard2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CartLoadingFragment extends Fragment {

    public static CartLoadingFragment newInstance() {
        return new CartLoadingFragment();
    }

    public CartLoadingFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_loading, container, false);
    }
}
