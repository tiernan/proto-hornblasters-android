package com.hornblasters.soundboard2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;

import com.hornblasters.xml.Category;
import com.hornblasters.xml.Parser;
import com.hornblasters.xml.Store;


/* Tested for leaks: none on 2015-05-07
* Adapter FEATURE_PRELOAD causes ~.005mb and 65 objects leaked.
* */
public class StoreActivity extends HornBlastersActivity implements OnFragmentInteractionListener {
    private static final String TAG = "StoreCategoriesActivity";

    private Store dataStore = null;
    private StoreRetainFragment retainFragment = null;
    private boolean loading = false;


    public void onFragmentInteraction(Action action, int id) {
        if (!loading) {
            switch (action) {
                case CATEGORY:
                    loading = true;
                    if (findViewById(R.id.fragment_secondary) != null) {
                        new DownloadCategory().execute(API_CATEGORY + id);
                    } else {
                        drawerToggle.setDrawerIndicatorEnabled(false);
                        new DownloadCategory().execute(API_CATEGORY + id);
                    }
                    break;
                case PRODUCT:
                    newActivity(ProductActivity.class, INTENT_PRODUCT, id);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
        super.onCreate(null);
        setContentView(R.layout.activity_store);

        retainFragment = StoreRetainFragment.findOrCreateRetainFragment(getSupportFragmentManager());
        dataStore = retainFragment.dataStore;

        loadNetwork();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Integer categoryId = getIntent().getExtras().getInt(INTENT_CATEGORY, 0);
            if (categoryId != 0) {
                new DownloadCategory().execute(API_CATEGORY + categoryId);
            }
        }
        trackHit(TAG);
    }

    private void loadNetwork() {
        if (dataStore == null) {
            new ParseXmlTask().execute(API_CATEGORIES);
        } else {
            loadData(dataStore);
        }
    }

    private void loadData(Store data) {
        if (data == null || data.categories == null) {
            showErrorPage("Parser failed to load XML.");
        } else {

            CategoriesFragment firstFragment = CategoriesFragment.newInstance(data.categories);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_primary, firstFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return true;
    }

    private void retainStore(Store store) {
        dataStore = store;
        retainFragment.dataStore = dataStore;
    }

    private class ParseXmlTask extends AsyncTask<String, Void, Store> {
        private static final String TAG = "ParseXmlTask";

        @Override
        protected Store doInBackground(String... urls) {
            if (BuildConfig.DEBUG) {
                Log.v(TAG, "Starting thread: " + Thread.currentThread().getId());
            }
            try {
                return (Store) new Parser().parse(downloadCachedUrl(urls[0]));
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Store result) {
            try {
                retainStore(result);
                loadData(result);
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Activity destroyed.");
                }
            }
        }
    }


    private class DownloadCategory extends AsyncTask<String, Void, Category> {
        private static final String TAG = "DownloadXmlTaskNative";

        @Override
        protected Category doInBackground(String... urls) {
            try {
                return (Category) new Parser().parse(downloadCachedUrl(urls[0]));
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, e.getMessage());
                }
                return null;
            }
        }

        @Override
        protected void onPostExecute(Category result) {
            if (result == null) {
                showErrorPage("Failed to load XML");
            } else {
                setTitle(result.title);
                CategoryFragment newFragment = CategoryFragment.newInstance(result.products);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                if (findViewById(R.id.fragment_secondary) != null) {
                    transaction.replace(R.id.fragment_secondary, newFragment).commit();
                } else {
                    transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                        .replace(R.id.fragment_primary, newFragment).addToBackStack(null).commit();
                }
                loading = false;
            }
        }
    }
}
