package com.hornblasters.soundboard2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.hornblasters.core.Checkout;

public class CheckoutRetainFragment extends Fragment {
    private static final String TAG = "CheckoutRetainFragment";
    public Checkout checkout = null;

    public CheckoutRetainFragment() {}

    public static CheckoutRetainFragment findOrCreateRetainFragment(FragmentManager fm) {
        CheckoutRetainFragment fragment = (CheckoutRetainFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new CheckoutRetainFragment();
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