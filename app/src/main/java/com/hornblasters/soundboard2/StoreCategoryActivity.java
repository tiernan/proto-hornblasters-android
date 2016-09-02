package com.hornblasters.soundboard2;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hornblasters.xml.Category;
import com.hornblasters.xml.Parser;

public class StoreCategoryActivity extends HornBlastersActivity {
    private static final String TAG = "StoreCategoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_store_category);

        int categoryId = getIntent().getExtras().getInt(INTENT_CATEGORY);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Started with intent; ID: " + categoryId);
        }

        new DownloadXmlTaskNative().execute(API_CATEGORY + categoryId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return true;
    }

    private class DownloadXmlTaskNative extends AsyncTask<String, Void, Category> {
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
                ListView listView = (ListView) findViewById(R.id.product_list);
                CategoryAdapter listAdapter = new CategoryAdapter(getApplicationContext(), result.products);
                listView.setAdapter(listAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CategoryAdapter.ViewHolder vh = (CategoryAdapter.ViewHolder) view.getTag();
                            newActivity(ProductActivity.class, INTENT_PRODUCT, vh.id);
                    }
                });
            }
        }
    }
}
