package com.hornblasters.core;

import android.provider.BaseColumns;

public final class OrdersSchema {

    private OrdersSchema() {}

    public static abstract class Cart implements BaseColumns {
        public static final String TABLE_NAME = "cart";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NUMBER = "number";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_FREE_SHIPPING = "free_shipping";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
    }

    public static abstract class Order implements BaseColumns {
        public static final String TABLE_NAME = "orders";
//        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NUMBER = "number";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TOTAL = "total";
        public static final String COLUMN_NAME_CARD = "card";
        public static final String COLUMN_NAME_STATUS = "status";
    }
}