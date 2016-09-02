package com.hornblasters.soundboard2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ListView;

import java.util.ArrayList;

public class ProductSpecificationsFragment extends HornBlastersFragment {
    private static final String TAG = "SpecificationsFragment";
    private String specifications;

    public static ProductSpecificationsFragment newInstance(String specifications) {
        ProductSpecificationsFragment fragment = new ProductSpecificationsFragment();
        fragment.setSpecifications(specifications);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (specifications == null) {
            specifications = ((ProductActivity) getActivity()).retainFragment.dataProduct.specifications;
        }

        ArrayList<ProductSpecificationsParser.Specification> result;

        try {
            ProductSpecificationsParser parser = new ProductSpecificationsParser();
            result = parser.parse(specifications);
            View v = inflater.inflate(R.layout.fragment_store_product_specifications, container, false);
            ListView listView = (ListView) v.findViewById(R.id.specification_list);
            ProductSpecificationAdapter listAdapter = new ProductSpecificationAdapter(getActivity(), result);
            listView.setAdapter(listAdapter);
            return v;
        } catch(Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, e.getMessage());
            }
        }

        View v = inflater.inflate(R.layout.fragment_store_product_specifications_web, container, false);

        WebView webView = (WebView) v.findViewById(R.id.specs);
        loadCssWithWebView(webView, "style.css", specifications);
        return v;
    }

    private void setSpecifications(String specifications) {
        this.specifications = specifications;
    }
}