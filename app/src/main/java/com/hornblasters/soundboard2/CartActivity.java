package com.hornblasters.soundboard2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.hornblasters.core.Address;
import com.hornblasters.core.Cart;
import com.hornblasters.core.Checkout;
import com.hornblasters.core.OrdersHelper;
import com.hornblasters.core.OrdersSchema;
import com.hornblasters.core.Payment;
import com.hornblasters.xml.Product;

import java.util.ArrayList;


public class CartActivity extends HornBlastersActivity {
    @SuppressWarnings("unused")
    private static final String TAG = "CartActivity";
    private CartRetainFragment retainFragment;
    private Checkout checkout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
            getWindow().setExitTransition(new Explode());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_primary, CartLoadingFragment.newInstance()).commit();

        retainFragment = CartRetainFragment.findOrCreateRetainFragment(getSupportFragmentManager());

        if (retainFragment.checkout == null) {
            new ProcessDatabase(this).execute();
        } else {
            checkout = retainFragment.checkout;
        }
        // REMOVED FOR GITHUB
//        trackHit(TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }



    private class ProcessDatabase extends AsyncTask<Void, Void, Checkout> {
        private static final String TAG = "DownloadXmlTaskNative";
        private final SQLiteDatabase db;

        public ProcessDatabase(Context context) {
            db = new OrdersHelper(context).getReadableDatabase();
        }

        @Override
        protected Checkout doInBackground(Void... urls) {
            Checkout checkout;

            Cursor c = db.query(
                    Cart.TABLE_NAME,  // The table to query
                    Cart.ALL_FIELDS,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );

            if (c.getCount() == 0) {
                return null;
            } else {
                ArrayList<Product> products = new ArrayList<>();
                Address address;


                while (c.moveToNext()) {
                    int id = c.getInt(c.getColumnIndexOrThrow(Cart.COLUMN_NAME_ID));
                    int stockInt = c.getInt(c.getColumnIndexOrThrow(Cart.COLUMN_NAME_STOCK));
                    boolean stock = (stockInt != 0);
                    int availabilityInt = c.getInt(c.getColumnIndexOrThrow(Cart.COLUMN_NAME_AVAILABILITY));
                    boolean availability = (availabilityInt != 0);
                    String brand = c.getString(c.getColumnIndexOrThrow(Cart.COLUMN_NAME_BRAND));
                    String title = c.getString(c.getColumnIndexOrThrow(Cart.COLUMN_NAME_TITLE));
                    String number = c.getString(c.getColumnIndexOrThrow(Cart.COLUMN_NAME_NUMBER));
                    String image = c.getString(c.getColumnIndexOrThrow(Cart.COLUMN_NAME_IMAGE));
                    int freeShippingInt = c.getInt(c.getColumnIndexOrThrow(Cart.COLUMN_NAME_FREE_SHIPPING));
                    boolean freeShipping = (freeShippingInt != 0);
                    double price = c.getDouble(c.getColumnIndexOrThrow(OrdersSchema.Cart.COLUMN_NAME_PRICE));
                    int quantity = c.getInt(c.getColumnIndexOrThrow(OrdersSchema.Cart.COLUMN_NAME_QUANTITY));
                    ArrayList<String> images = new ArrayList<>();
                    images.add(image);
                    products.add(new Product(id, stock, availability, brand, title, number, images, null, null, null, price, freeShipping, null, null, quantity));
                }
                c.close();

                c = db.query(Address.TABLE_NAME, Address.ALL_FIELDS, null, null, null, null, null);

                address = new Address();
                if (c.getCount() > 0) {
                    c.moveToNext();
                    address.id = c.getInt(c.getColumnIndexOrThrow(Address.COLUMN_NAME_ID));
                    address.firstName = c.getString(c.getColumnIndexOrThrow(Address.COLUMN_NAME_FIRST));
                    address.lastName = c.getString(c.getColumnIndexOrThrow(Address.COLUMN_NAME_LAST));
                    address.email = c.getString(c.getColumnIndexOrThrow(Address.COLUMN_NAME_LAST));
                    address.phone = c.getString(c.getColumnIndexOrThrow(Address.COLUMN_NAME_LAST));
                    address.company = c.getString(c.getColumnIndexOrThrow(Address.COLUMN_NAME_LAST));
                    address.street1 = c.getString(c.getColumnIndexOrThrow(Address.COLUMN_NAME_STREET1));
                    address.street2 = c.getString(c.getColumnIndexOrThrow(Address.COLUMN_NAME_STREET2));
                    address.city = c.getString(c.getColumnIndexOrThrow(Address.COLUMN_NAME_CITY));
                    address.postal = c.getString(c.getColumnIndexOrThrow(Address.COLUMN_NAME_POSTAL));
                    address.setTerritory(
                            c.getString(c.getColumnIndexOrThrow(Address.COLUMN_NAME_TERRITORY)),
                            c.getString(c.getColumnIndexOrThrow(Address.COLUMN_NAME_COUNTRY))
                    );
                    c.close();
                }

                checkout = new Checkout(new Cart(products), address, new Payment());
                db.close();
                return checkout;
            }
        }

        @Override
        protected void onPostExecute(Checkout result) {

            if (result == null) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Failed to process database.");
                    setContentView(R.layout.fragment_error);
                    setTitle(getString(R.string.title_error));
                    TextView textView = (TextView) findViewById(R.id.error);
                    textView.setText(R.string.cart_empty);
                } else {
                    finish();
                }
            } else {
                checkout = result;
                retainFragment.checkout = checkout;

                if (checkout.cart.products.size() == 0) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_primary, CartEmptyFragment.newInstance()).commit();
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_primary, CartMainFragment.newInstance(checkout)).commit();



                    Button checkout = (Button) findViewById(R.id.next);
                    checkout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pageAddress();
                        }
                    });
                }


            }
        }
    }

    public void pageAddress() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_primary, CartAddressFragment.newInstance(checkout)).commit();
    }
}
