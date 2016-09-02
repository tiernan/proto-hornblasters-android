package com.hornblasters.soundboard2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hornblasters.core.Checkout;
import com.hornblasters.xml.Product;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CartMainFragment extends CartFragment {

    private Checkout checkout;
    public static CartMainFragment newInstance(Checkout checkout) {
        CartMainFragment fragment = new CartMainFragment();
        fragment.checkout = checkout;
        return fragment;
    }

    public CartMainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_cart, container, false);
        LinearLayout cartList = (LinearLayout) layout.findViewById(R.id.product_list);

        ArrayList<Product> products = checkout.cart.products;
        int s = products.size();
        for (int i = 0; i < s; i++) {
            Product p = products.get(i);

            View v = inflater.inflate(R.layout.view_list_cart, cartList, false);
            TextView titleView = (TextView) v.findViewById(R.id.product_title);
            titleView.setText(p.title);
            TextView priceView = (TextView) v.findViewById(R.id.product_price);
            priceView.setText(NumberFormat.getCurrencyInstance(Locale.US).format(p.price));
            TextView freeShippingView = (TextView) v.findViewById(R.id.product_free_shipping);
            if (p.freeShipping) {
                freeShippingView.setText(getString(R.string.content_free_shipping));
            } else {
                freeShippingView.setText("");
            }
            TextView quantityView = (TextView) v.findViewById(R.id.product_quantity);
            quantityView.setText(String.format(Locale.US, "$1%d", p.quantity));
            ImageView imageView = (ImageView) v.findViewById(R.id.product_image);
            Picasso.with(getActivity().getApplicationContext()).load(p.images.get(0)).fit().centerInside().into(imageView);

            cartList.addView(v);
        }

        TextView subtotalView = (TextView) layout.findViewById(R.id.price_subtotal);

        subtotalView.setText(NumberFormat.getCurrencyInstance(Locale.US).format(checkout.cart.subtotal));

        TextView totalView = (TextView) layout.findViewById(R.id.price_total);

        totalView.setText(NumberFormat.getCurrencyInstance(Locale.US).format(checkout.cart.subtotal));

        return layout;
    }
}
