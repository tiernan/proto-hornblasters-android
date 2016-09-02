package com.hornblasters.soundboard2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.hornblasters.xml.Store;
import com.squareup.okhttp.Cache;

import java.util.List;

public class StoreRetainFragment extends Fragment {
    private static final String TAG = "StoreRetainFragment";
    public Store dataStore = null;
    public Cache responseCache = null;
    public List<String> preloaded = null;
    public boolean[] preloadedPositions = null;

    public StoreRetainFragment() {}

    public static StoreRetainFragment findOrCreateRetainFragment(FragmentManager fm) {
        StoreRetainFragment fragment = (StoreRetainFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new StoreRetainFragment();
            fm.beginTransaction().add(fragment, TAG).commit();
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}