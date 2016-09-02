package com.hornblasters.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OrdersHelper extends SQLiteOpenHelper {
    private static final String TAG = "OrdersHelper";
    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "hornblasters-orders.db";
    private static final String SQL_UPDATE_3_4 = "";
    private static final String SQL_DELETE_CART =
            "DROP TABLE IF EXISTS " + Cart.TABLE_NAME;
    private static final String SQL_DELETE_ORDERS =
            "DROP TABLE IF EXISTS " + OrdersSchema.Order.TABLE_NAME;
    private static final String SQL_DELETE_ADDRESSES =
            "DROP TABLE IF EXISTS " + Address.TABLE_NAME;

    private static final String TYPE_FLOAT = " REAL";
    private static final String TYPE_INT = " INTEGER";
    private static final String TYPE_TEXT = " TEXT";

    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_CART =
            "CREATE TABLE " + OrdersSchema.Cart.TABLE_NAME + " (" +
                    Cart._ID + " INTEGER PRIMARY KEY," +
                    Cart.COLUMN_NAME_ID + TYPE_INT + COMMA_SEP +
                    Cart.COLUMN_NAME_STOCK + TYPE_INT + COMMA_SEP +
                    Cart.COLUMN_NAME_AVAILABILITY + TYPE_INT + COMMA_SEP +
                    Cart.COLUMN_NAME_NUMBER + TYPE_TEXT + COMMA_SEP +
                    Cart.COLUMN_NAME_BRAND + TYPE_TEXT + COMMA_SEP +
                    Cart.COLUMN_NAME_TITLE + TYPE_TEXT + COMMA_SEP +
                    Cart.COLUMN_NAME_IMAGE + TYPE_TEXT + COMMA_SEP +
                    Cart.COLUMN_NAME_PRICE + TYPE_FLOAT + COMMA_SEP +
                    Cart.COLUMN_NAME_FREE_SHIPPING + TYPE_INT + COMMA_SEP +
                    Cart.COLUMN_NAME_QUANTITY + TYPE_INT + " )";

    private static final String SQL_CREATE_ORDERS =
            "CREATE TABLE " + OrdersSchema.Order.TABLE_NAME + " (" +
                    OrdersSchema.Order._ID + " INTEGER PRIMARY KEY," +
//                    OrdersSchema.Order.COLUMN_NAME_ID + TYPE_INT + COMMA_SEP +
                    OrdersSchema.Order.COLUMN_NAME_NUMBER + TYPE_TEXT + COMMA_SEP +
                    OrdersSchema.Order.COLUMN_NAME_DATE + TYPE_TEXT + COMMA_SEP +
                    OrdersSchema.Order.COLUMN_NAME_TOTAL + TYPE_FLOAT + COMMA_SEP +
                    OrdersSchema.Order.COLUMN_NAME_CARD + TYPE_TEXT + COMMA_SEP +
                    OrdersSchema.Order.COLUMN_NAME_STATUS + TYPE_INT + " )";

    private static final String SQL_CREATE_ADDRESSES =
            "CREATE TABLE " + Address.TABLE_NAME + " (" +
                    Address._ID + " INTEGER PRIMARY KEY," +
                    Address.COLUMN_NAME_ID + TYPE_INT + COMMA_SEP +
                    Address.COLUMN_NAME_FIRST + TYPE_TEXT + COMMA_SEP +
                    Address.COLUMN_NAME_LAST + TYPE_TEXT + COMMA_SEP +
                    Address.COLUMN_NAME_EMAIL + TYPE_TEXT + COMMA_SEP +
                    Address.COLUMN_NAME_PHONE + TYPE_TEXT + COMMA_SEP +
                    Address.COLUMN_NAME_COMPANY + TYPE_TEXT + COMMA_SEP +
                    Address.COLUMN_NAME_STREET1 + TYPE_TEXT + COMMA_SEP +
                    Address.COLUMN_NAME_STREET2 + TYPE_TEXT + COMMA_SEP +
                    Address.COLUMN_NAME_CITY + TYPE_TEXT + COMMA_SEP +
                    Address.COLUMN_NAME_POSTAL + TYPE_TEXT + COMMA_SEP +
                    Address.COLUMN_NAME_TERRITORY + TYPE_TEXT + COMMA_SEP +
                    Address.COLUMN_NAME_COUNTRY + TYPE_TEXT + " )";

    public OrdersHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "Database ready.");
        }
    }

    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Creating database.");
        }
        db.execSQL(SQL_CREATE_CART);
        db.execSQL(SQL_CREATE_ORDERS);
        db.execSQL(SQL_CREATE_ADDRESSES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Upgrading database.");
        }
        if (oldVersion <= 3 && newVersion == 4) {
            Log.d(TAG, "Supported upgrade, recreating Cart table.");
            dropCart(db);
        }
        if (oldVersion < 3 && newVersion == 3) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Supported upgrade, adding Addresses table.");
            }
            db.execSQL(SQL_DELETE_ADDRESSES);
            db.execSQL(SQL_CREATE_ADDRESSES);
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Unsupported upgrade, clearing database.");
            }
            db.execSQL(SQL_DELETE_CART);
            db.execSQL(SQL_DELETE_ORDERS);
            db.execSQL(SQL_DELETE_ADDRESSES);
            onCreate(db);
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Downgrading database.");
        }
        onUpgrade(db, oldVersion, newVersion);
    }

    public static void dropCart(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_CART);
        db.execSQL(SQL_CREATE_CART);
    }

    public static void dropAddresses(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ADDRESSES);
        db.execSQL(SQL_CREATE_ADDRESSES);
    }

    private static ContentValues getValues(Address address) {
        ContentValues values = new ContentValues();
        if (address._id > 0) {
            values.put(Address._ID, address._id);
        }
        values.put(Address.COLUMN_NAME_ID, address.id);
        values.put(Address.COLUMN_NAME_FIRST, address.firstName);
        values.put(Address.COLUMN_NAME_LAST, address.lastName);
        values.put(Address.COLUMN_NAME_EMAIL, address.email);
        values.put(Address.COLUMN_NAME_PHONE, address.phone);
        values.put(Address.COLUMN_NAME_COMPANY, address.company);
        values.put(Address.COLUMN_NAME_STREET1, address.street1);
        values.put(Address.COLUMN_NAME_STREET2, address.street2);
        values.put(Address.COLUMN_NAME_CITY, address.city);
        values.put(Address.COLUMN_NAME_POSTAL, address.postal);
        values.put(Address.COLUMN_NAME_TERRITORY, address.getTerritory());
        values.put(Address.COLUMN_NAME_COUNTRY, address.getCountry());
        return values;
    }

    public static void replace(SQLiteDatabase db, Address address) {
        ContentValues values = getValues(address);
        String[] projection = {Address._ID};
        Cursor c = db.query(Address.TABLE_NAME, projection, null, null, null, null, null);

        if (c.getCount() > 0) {
            db.update(Address.TABLE_NAME, values, "_id=1", null);
        } else {
            db.insert(Address.TABLE_NAME, "null", values);
        }
        c.close();
    }
}