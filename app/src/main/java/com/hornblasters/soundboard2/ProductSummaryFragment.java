package com.hornblasters.soundboard2;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.hornblasters.core.Cart;
import com.hornblasters.core.HornBlastersHtml;
import com.hornblasters.core.OrdersHelper;
import com.hornblasters.core.OrdersSchema;
import com.hornblasters.xml.Product;

public class ProductSummaryFragment extends HornBlastersFragment {
    @SuppressWarnings("unused")
    private static final String TAG = "OverviewFragment";
    private Product product;

    public static Fragment newInstance(Product product) {
        ProductSummaryFragment fragment = new ProductSummaryFragment();
        fragment.setProduct(product);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {

        if (product == null) {
            product = ((ProductActivity) getActivity()).retainFragment.dataProduct;
        }

        try {
            TextView v = (TextView) li.inflate(R.layout.fragment_overview_summary, vg, false);
            v.setText(HornBlastersHtml.fromHtml(product.summary));
            return v;
        } catch (Exception e) {
            // Fallback to shitty WebView
            WebView v = (WebView) li.inflate(R.layout.fragment_overview_summary, vg, false);
            loadCssWithWebView(v, "style.css", product.summary);
            return v;
        }
    }

    private void setProduct(Product product) {
        this.product = product;
    }

    private class AddToCartTask extends AsyncTask<Product, Void, Boolean> {
        private static final String TAG = "AddToCartTask";

        @Override
        protected Boolean doInBackground(Product... urls) {
            try {
                Product p = urls[0];
                SQLiteDatabase db = new OrdersHelper(getActivity()).getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(Cart.COLUMN_NAME_ID, p.id);
                values.put(Cart.COLUMN_NAME_STOCK, p.stock);
                values.put(Cart.COLUMN_NAME_AVAILABILITY, p.availability);
                values.put(Cart.COLUMN_NAME_NUMBER, p.number);
                values.put(Cart.COLUMN_NAME_BRAND, p.brand);
                values.put(Cart.COLUMN_NAME_TITLE, p.title);
                values.put(Cart.COLUMN_NAME_IMAGE, p.images.get(0));
                values.put(Cart.COLUMN_NAME_PRICE, p.price);
                values.put(Cart.COLUMN_NAME_FREE_SHIPPING, 1);
                values.put(Cart.COLUMN_NAME_QUANTITY, 1);

                db.insert(OrdersSchema.Cart.TABLE_NAME, "null", values);
                return true;
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, e.getMessage());
                }
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (BuildConfig.DEBUG) {
                if (success) {
                    Log.d(TAG, "Success");
                } else {
                    Log.d(TAG, "Failure");
                }
            }
        }
    }
}