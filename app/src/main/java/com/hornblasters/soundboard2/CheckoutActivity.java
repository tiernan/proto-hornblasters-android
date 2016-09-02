package com.hornblasters.soundboard2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.hornblasters.core.Address;
import com.hornblasters.core.Cart;
import com.hornblasters.core.Checkout;
import com.hornblasters.core.OrdersHelper;
import com.hornblasters.core.OrdersSchema;
import com.hornblasters.core.Payment;
import com.hornblasters.core.ToggledViewPager;
import com.hornblasters.xml.Product;

import java.util.ArrayList;


public class CheckoutActivity extends HornBlastersActivity {
    private static final String TAG = "CheckoutActivity";
    private Checkout checkout;
    private CheckoutRetainFragment retainFragment;

    public enum FragmentType {ADDRESS, SHIPPING, PAYMENT, PURCHASE}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        setDisplayHomeAsUpEnabled();

        retainFragment = CheckoutRetainFragment.findOrCreateRetainFragment(getSupportFragmentManager());
        if (retainFragment.checkout == null) {
            new ProcessDatabase(this).execute();
        } else {
            checkout = retainFragment.checkout;
        }

        trackHit(TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_checkout, menu);
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
                    Cart.TABLE_NAME,
                    Cart.ALL_FIELDS,
                    null,
                    null,
                    null,
                    null,
                    null
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
                    // This should kill the fragment pager.. but test first
                    setContentView(R.layout.fragment_error);
                    setTitle(getString(R.string.title_error));
                    TextView textView = (TextView) findViewById(R.id.error);
                    textView.setText(R.string.cart_db_error_text);
                } else {
                    finish();
                }
            } else {
                checkout = result;
                retainFragment.checkout = checkout;
                SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), result);
                final ToggledViewPager viewPager = (ToggledViewPager) findViewById(R.id.pager);
                viewPager.setPagingEnabled(false);
                viewPager.setAdapter(sectionsPagerAdapter);
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        }
    }

    double getSubtotal() {
        return checkout.cart.subtotal;
    }

    double getTotal() {
        return checkout.getTotal();
    }

    Checkout getCheckout() {
        return this.checkout;
    }

    void setPayment(String number, String month, String year, String check) {
        checkout.payment.cardNumber = number;
        checkout.payment.expiryMonth = month;
        checkout.payment.expiryYear = year;
        checkout.payment.cvv2 = check;
    }

    void setAddress(Address newAddress) {
        Address address = checkout.address;
        address.firstName = newAddress.firstName;
        address.lastName = newAddress.lastName;
        address.email = newAddress.email;
        address.phone = newAddress.phone;
        address.company = newAddress.company;
        address.street1 = newAddress.street1;
        address.street2 = newAddress.street2;
        address.city = newAddress.city;
        address.postal = newAddress.postal;
        address.setTerritory(newAddress.getTerritory(), newAddress.getCountry());
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final static String TAG = "SectionsPagerAdapter";
        public final Checkout checkout;
        private final int count = 3;
        private final ArrayList<String> titles;
        public final ArrayList<FragmentType> fragments;


        public SectionsPagerAdapter(FragmentManager fm, Checkout checkout) {
            super(fm);
            this.checkout = checkout;
            titles = new ArrayList<>();
            fragments = new ArrayList<>();
            titles.add(getString(R.string.title_fragment_address));
            titles.add(getString(R.string.title_fragment_shipping));
            titles.add(getString(R.string.title_fragment_payment));
            titles.add(getString(R.string.title_fragment_purchase));
            fragments.add(FragmentType.ADDRESS);
            fragments.add(FragmentType.SHIPPING);
            fragments.add(FragmentType.PAYMENT);
            fragments.add(FragmentType.PURCHASE);
        }

        @Override
        public Fragment getItem(int position) {
            FragmentType fragmentType = fragments.get(position);
            switch (fragmentType) {
                case ADDRESS:
                    return CheckoutAddressFragment.newInstance(checkout.address);
                case SHIPPING:
                    return CheckoutShippingFragment.newInstance(checkout.cart, checkout.address);
                case PAYMENT:
                    return CheckoutPaymentFragment.newInstance(checkout.payment);
                case PURCHASE:
                    return CheckoutPurchaseFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}