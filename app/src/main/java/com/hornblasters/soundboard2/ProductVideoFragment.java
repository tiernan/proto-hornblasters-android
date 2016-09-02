package com.hornblasters.soundboard2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hornblasters.xml.Product;

public class ProductVideoFragment extends HornBlastersFragment {
    private static final String TAG = "VideoFragment";
    private Product product;

    public static Fragment newInstance(Product product) {
        ProductVideoFragment fragment = new ProductVideoFragment();
        fragment.setProduct(product);
        return fragment;
    }

    private void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (product == null) {
            product = ((ProductActivity) getActivity()).retainFragment.dataProduct;
        }

        View v = inflater.inflate(R.layout.fragment_store_product_videos, container, false);

        ListView listView = (ListView) v.findViewById(R.id.videos_list);
        VideoFragmentAdapter listAdapter = new VideoFragmentAdapter(getActivity(), product.videos);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoFragmentAdapter.ViewHolder vh = (VideoFragmentAdapter.ViewHolder) view.getTag();
                startWebActivity("http://www.youtube.com/watch?v=" + vh.uri);
            }
        });




        return v;
    }
}