package com.hornblasters.soundboard2;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;

import com.hornblasters.xml.Parser;
import com.hornblasters.xml.Product;

import java.util.ArrayList;

public class ProductActivity extends HornBlastersActivity {
    private static final String TAG = "StoreProductActivity";
    public ProductRetainFragment retainFragment;
    private Product product;
    public boolean wide = false;

    public enum FragmentType {OVERVIEW, SPECIFICATIONS, VIDEOS}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        drawerToggle.setDrawerIndicatorEnabled(false);

        int productId = getIntent().getExtras().getInt(INTENT_PRODUCT);

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Started with intent; ID: " + productId);
        }

        retainFragment = ProductRetainFragment.findOrCreateRetainFragment(getSupportFragmentManager());
        product = retainFragment.dataProduct;

        if (product == null) {
            new DownloadXmlTaskNative().execute(API_PRODUCT + productId);
        } else {
            render(product);
        }

        trackHit(TAG);
    }

    private void render(Product p) {

        setTitle(p.title);
        if (findViewById(R.id.fragment_main) != null) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Rendering wide");
            }
            wide = true;
            Fragment mainFragment = ProductMainFragment.newInstance(p);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_main, mainFragment).commit();
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Rendering narrow");
            }
        }

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getResources(), getSupportFragmentManager(), p);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store_product, menu);
        return true;
    }

    private class DownloadXmlTaskNative extends AsyncTask<String, Void, Product> {
        private static final String TAG = "DownloadXmlTaskNative";

        @Override
        protected Product doInBackground(String... urls) {
            try {
                return (Product) new Parser().parse(downloadCachedUrl(urls[0]));
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, e.getMessage());
                }
                return null;
            }
        }

        @Override
        protected void onPostExecute(Product result) {
            if (result == null) {
                showErrorPage("Parser failed to load XML.");
            } else {
                retainFragment.dataProduct = result;
                render(result);
            }
        }
    }

    public static class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final static String TAG = "SectionsPagerAdapter";
        public final Product product;
        private int count = 1;
        private final ArrayList<String> titles;
        public final ArrayList<FragmentType> fragments;


        public SectionsPagerAdapter(Resources r, FragmentManager fm, Product product) {
            super(fm);
            this.product = product;
            titles = new ArrayList<>();
            fragments = new ArrayList<>();
            titles.add(r.getString(R.string.title_fragment_overview));
            fragments.add(FragmentType.OVERVIEW);
            if (product.specifications != null && !product.specifications.equals("")) {
                count++;
                titles.add(r.getString(R.string.title_fragment_specifications));
                fragments.add(FragmentType.SPECIFICATIONS);
            }
            if (product.audios != null) {
                count++;
                titles.add(r.getString(R.string.title_fragment_audio));
                fragments.add(FragmentType.VIDEOS);
            }
            if (product.videos != null && product.videos.size() != 0) {
                count++;
                titles.add(r.getString(R.string.title_fragment_video));
                fragments.add(FragmentType.VIDEOS);
            }
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            FragmentType fragmentType = fragments.get(position);
            switch (fragmentType) {
                default:
                case OVERVIEW:
                    return ProductOverviewFragment.newInstance(product);
                case SPECIFICATIONS:
                    return ProductSpecificationsFragment.newInstance(product.specifications);
                case VIDEOS:
                    return ProductVideoFragment.newInstance(product);
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
