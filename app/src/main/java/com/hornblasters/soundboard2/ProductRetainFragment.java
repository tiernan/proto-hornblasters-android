package com.hornblasters.soundboard2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.hornblasters.xml.Product;

public class ProductRetainFragment extends Fragment {
    private static final String TAG = "ProductRetainFragment";
    public Product dataProduct = null;

    public ProductRetainFragment() {}

    public static ProductRetainFragment findOrCreateRetainFragment(FragmentManager fm) {
        ProductRetainFragment fragment = (ProductRetainFragment) fm.findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new ProductRetainFragment();
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