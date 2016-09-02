package com.hornblasters.soundboard2;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hornblasters.xml.Product;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


class CategoryAdapter extends ArrayAdapter<Product> {
    private static final String TAG = "StoreCategoryAdapter";

    private final LayoutInflater vi;

    public CategoryAdapter(Context context, List<Product> items) {
        super(context, R.layout.view_list_product, items);

        vi = LayoutInflater.from(context);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).id;
    }

    @Override
    public View getView(int position, View v, ViewGroup vg) {
        ViewHolder holder;
        if (v == null) {
            v = vi.inflate(R.layout.view_list_product, vg, false);
            holder = new ViewHolder();
            holder.brand = (TextView) v.findViewById(R.id.product_brand);
            holder.title = (TextView) v.findViewById(R.id.product_title);
            holder.freeShipping = (TextView) v.findViewById(R.id.product_free_shipping);
            holder.price = (TextView) v.findViewById(R.id.product_price);
            holder.image = (ImageView) v.findViewById(R.id.product_image);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.position = position;
        Product p = getItem(position);

        if (p != null) {
            holder.id = p.id;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.brand.setAllCaps(true);
            }
            holder.brand.setText(p.brand);
            holder.title.setText(p.title);

            if (p.freeShipping) {
                holder.freeShipping.setText(getContext().getString(R.string.content_free_shipping));
            } else {
                holder.freeShipping.setText("");
            }

            holder.price.setText(NumberFormat.getCurrencyInstance(Locale.US).format(p.price));

            String imageString = p.images.get(0);
            if (imageString.length() > 0) {
                Picasso.with(vg.getContext().getApplicationContext()).load(imageString).fit().centerInside().into(holder.image);
            } else {
                holder.image.setImageResource(android.R.color.transparent);
            }
        } else if (BuildConfig.DEBUG) {
            Log.d(TAG, "Adapter was given a null data object.");
        }
        return v;
    }

    public static class ViewHolder {
        int id;
        TextView brand;
        TextView title;
        TextView freeShipping;
        TextView price;
        ImageView image;
        int position;
    }
}