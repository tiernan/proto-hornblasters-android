package com.hornblasters.soundboard2;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.transition.Explode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.hornblasters.core.OrdersHelper;
import com.squareup.okhttp.Cache;

import java.io.IOException;

/* Tested for leaks: none on 2015-05-07 */
public class MainActivity extends HornBlastersActivity {
    @SuppressWarnings("unused")
    private static final String TAG = "MainActivity";
    private ScrollView scrollView;
    private View imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setExitTransition(new Explode());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageView = findViewById(R.id.header);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            toolbar.getBackground().setAlpha(0);
            scrollView = (ScrollView) findViewById(R.id.scroll);
            ScrollListener scrollListener;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                scrollListener = new ScrollListenerLollipop(getWindow());
            } else {
                scrollListener = new ScrollListener(getWindow());
            }
            scrollView.getViewTreeObserver().addOnScrollChangedListener(scrollListener);
        }

        GridView gridview = (GridView) findViewById(R.id.grid_view);
        gridview.setAdapter(new MainAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                startWebActivity(((MainAdapter)parent.getAdapter()).getUrl(position));
            }
        });

        trackHit(TAG);
    }

    public void ctaListener(View v) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            preLollipopFix();
        }
        switch (v.getId()) {
            case R.id.cta_soundboard:
                newActivity(SoundboardActivity.class);
                break;
            case R.id.cta_horn_kits:
                newActivity(StoreCategoryActivity.class, INTENT_CATEGORY, 8);
                break;
            case R.id.cta_manuals:
                newActivity(WebActivity.class, INTENT_URL, WEB_SUPPORT);
                break;
            case R.id.cta_contact:
                newActivity(WebActivity.class, INTENT_URL, WEB_CONTACT);
                break;
            case R.id.cta_website:
                startWebActivity("https://www.hornblasters.com/");
                break;
            case R.id.cta_store:
                newActivity(StoreActivity.class);
                break;
            case R.id.cta_video:
                newActivity(VideosActivity.class);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        if (BuildConfig.DEBUG) {
            getMenuInflater().inflate(R.menu.menu_debug, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem i) {
        int id = i.getItemId();
        Class activity = null;
        switch (id) {
            case com.hornblasters.core.R.id.action_activity_store_native:
                activity = StoreActivity.class;
                break;
            case com.hornblasters.core.R.id.action_activity_store_web:
                activity = StoreWebActivity.class;
                break;
            case com.hornblasters.core.R.id.action_activity_store_hybrid:
                activity = StoreHybridActivity.class;
                break;
            case R.id.action_debug_clear_cart:
            case R.id.action_debug_clear_addresses:
                SQLiteDatabase db = new OrdersHelper(getApplicationContext()).getWritableDatabase();
                switch (id) {
                    case R.id.action_debug_clear_addresses:
                        Toast.makeText(this, "Deleting addresses", Toast.LENGTH_SHORT).show();
                        OrdersHelper.dropAddresses(db);
                        break;
                    case R.id.action_debug_clear_cart:
                        Toast.makeText(this, "Deleting cart", Toast.LENGTH_SHORT).show();
                        OrdersHelper.dropCart(db);
                }
                return true;
            case R.id.action_debug_clear_cache:
            case R.id.action_debug_clear_picasso_cache:
                Cache responseCache;
                switch (id) {
                    case R.id.action_debug_clear_cache:
                        responseCache = getHttpCache();
                        try {
                            responseCache.delete();
                            Toast.makeText(getApplicationContext(), "Cache cleared!", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), "Failed to clear cache", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    case R.id.action_debug_clear_picasso_cache:
                        responseCache = ((HornBlastersApplication)getApplication())
                                .getHttpCache(HornBlastersApplication.CACHE_PICASSO, 1);
                        try {
                            responseCache.delete();
                            Toast.makeText(getApplicationContext(), "Cache cleared!", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), "Failed to clear cache", Toast.LENGTH_LONG).show();
                        }
                        return true;
                }
        }
        if (activity != null) {
            newActivity(activity);
            return true;
        }

        return super.onOptionsItemSelected(i);
    }

    private class ScrollListener implements ViewTreeObserver.OnScrollChangedListener {
        final Window window;
        final int statusColor;
        int alpha;

        public ScrollListener(Window window) {
            this.window = window;
            this.statusColor = ContextCompat.getColor(getApplicationContext(), R.color.hornblasters_dark_red);
        }

        @SuppressLint("NewApi")
        @Override
        public void onScrollChanged() {
            int scrollHeight = scrollView.getScrollY();
            alpha = scrollHeight / 2;
            imageView.setTop(0 - alpha);
            if (alpha > 255) {
                alpha = 255;
            } else if (alpha < 5) {
                alpha = 0;
            }
            toolbar.getBackground().setAlpha(alpha);
        }
    }

    private class ScrollListenerLollipop extends ScrollListener {

        public ScrollListenerLollipop(Window window) {
            super(window);
        }

        @SuppressLint("NewApi")
        @Override
        public void onScrollChanged() {
            super.onScrollChanged();
            window.setStatusBarColor(Color.argb(alpha, Color.red(statusColor), Color.green(statusColor), Color.blue(statusColor)));
        }
    }

    private void preLollipopFix() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            toolbar.getBackground().setAlpha(255);
        }
    }
}
