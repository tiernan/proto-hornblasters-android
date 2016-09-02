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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hornblasters.core.Cart;
import com.hornblasters.core.OrdersHelper;
import com.hornblasters.core.OrdersSchema;
import com.hornblasters.xml.Product;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductMainFragment extends HornBlastersFragment {
    @SuppressWarnings("unused")
    private static final String TAG = "OverviewFragment";
    private Product product;

    public static Fragment newInstance(Product product) {
        ProductMainFragment fragment = new ProductMainFragment();
        fragment.setProduct(product);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater li, ViewGroup vg, Bundle b) {

        if (product == null) {
            product = ((ProductActivity) getActivity()).retainFragment.dataProduct;
        }



        View v = li.inflate(R.layout.fragment_overview_main, vg, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.image);
        imageView.setTag(product.id);

        String mainImage = product.images.get(0);
        if (mainImage.length() > 0) {

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    nextActivity(ProductImagesActivity.class, HornBlastersActivity.INTENT_PRODUCT, (int) v.getTag());

                }
            });

            Picasso.with(getActivity().getApplicationContext()).load(product.images.get(0)).fit().centerInside().into(imageView);
        }
        TextView title = (TextView) v.findViewById(R.id.title);
        title.setText(String.format(getString(R.string.product_full_name), product.brand, product.title));

        TextView freeShipping = (TextView) v.findViewById(R.id.product_free_shipping);
        freeShipping.setText(getString(R.string.content_free_shipping));

        TextView price = (TextView) v.findViewById(R.id.product_price);
        price.setText(NumberFormat.getCurrencyInstance(Locale.US).format(product.price));

        Product.AvailabilityType availability = product.getAvailability();

        TextView stock = (TextView) v.findViewById(R.id.product_stock);
        switch(availability) {
            case STOCK:
                stock.setText(getString(R.string.content_in_stock));
                break;
            case SPECIAL:
                stock.setText(getString(R.string.content_special_order));
                break;
            case NONE:
                stock.setText(getString(R.string.content_stock_out));
        }

        TextView number = (TextView) v.findViewById(R.id.product_part_number);
        number.setText(String.format(getString(R.string.content_part_number), product.number));

        Button addToCart = (Button) v.findViewById(R.id.add_to_cart);
        if (HornBlastersActivity.FEATURE_CART) {
            if (product.stock || product.availability) {
                addToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setEnabled(false);
                        Toast.makeText(getActivity(), "Adding to cart", Toast.LENGTH_LONG).show();
                        new AddToCartTask().execute(product);
                    }
                });
            } else {
                addToCart.setVisibility(View.INVISIBLE);
            }
        } else {
            addToCart.setVisibility(View.INVISIBLE);
        }

        return v;
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
                db.close();
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