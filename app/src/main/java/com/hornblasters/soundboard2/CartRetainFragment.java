package com.hornblasters.soundboard2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.hornblasters.core.Checkout;

public class CartRetainFragment extends Fragment {
    private static final String TAG = "CartRetainFragment";
    public Checkout checkout = null;

    public CartRetainFragment() {}

    public static CartRetainFragment findOrCreateRetainFragment(FragmentManager fm) {
        CartRetainFragment fragment = (CartRetainFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new CartRetainFragment();
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