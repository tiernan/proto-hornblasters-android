package com.hornblasters.core;

import android.provider.BaseColumns;

import com.hornblasters.xml.Product;

import java.util.ArrayList;

public final class Cart implements BaseColumns {
    public static final String TABLE_NAME = "cart";
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_STOCK = "stock";
    public static final String COLUMN_NAME_AVAILABILITY = "availability";
    public static final String COLUMN_NAME_NUMBER = "number";
    public static final String COLUMN_NAME_BRAND = "brand";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_NAME_IMAGE = "image";
    public static final String COLUMN_NAME_PRICE = "price";
    public static final String COLUMN_NAME_FREE_SHIPPING = "free_shipping";
    public static final String COLUMN_NAME_QUANTITY = "quantity";

    public static final String[] ALL_FIELDS = {
            _ID,
            COLUMN_NAME_ID,
            COLUMN_NAME_STOCK,
            COLUMN_NAME_AVAILABILITY,
            COLUMN_NAME_NUMBER,
            COLUMN_NAME_BRAND,
            COLUMN_NAME_TITLE,
            COLUMN_NAME_IMAGE,
            COLUMN_NAME_PRICE,
            COLUMN_NAME_FREE_SHIPPING,
            COLUMN_NAME_QUANTITY,
    };

    public final ArrayList<Product> products;
    public final double subtotal;

    public Cart(ArrayList<Product> products) {
        this.products = products;
        double subtotal = 0;
        for (Product product : products) {
            subtotal += product.price;
        }
        this.subtotal = subtotal;
    }
}