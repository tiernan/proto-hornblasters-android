package com.hornblasters.soundboard2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hornblasters.xml.Product;

public class ProductOverviewFragment extends HornBlastersFragment {
    @SuppressWarnings("unused")
    private static final String TAG = "OverviewFragment";
    private Product product;

    public static Fragment newInstance(Product product) {
        ProductOverviewFragment fragment = new ProductOverviewFragment();
        fragment.setProduct(product);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {
        if (product == null) {
            product = ((ProductActivity) getActivity()).retainFragment.dataProduct;
        }

        boolean wide = ((ProductActivity) getActivity()).wide;

        View v = li.inflate(R.layout.fragment_store_product_overview, vg, false);
        View mainFragment = v.findViewById(R.id.fragment_main);
        View divider = v.findViewById(R.id.divider);
        Fragment productFragment = getChildFragmentManager().findFragmentByTag("main");

        if (!wide) {
            mainFragment.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            if (productFragment == null) {
                productFragment = ProductMainFragment.newInstance(product);
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.fragment_main, productFragment, "main").commit();
            }

        } else {
            mainFragment.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
        
        Fragment summaryFragment = ProductSummaryFragment.newInstance(product);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_summary, summaryFragment).commit();

        return v;
    }

    private void setProduct(Product product) {
        this.product = product;
    }

}