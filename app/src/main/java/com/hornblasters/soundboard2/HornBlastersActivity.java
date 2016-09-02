package com.hornblasters.soundboard2;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

public abstract class HornBlastersActivity extends AppCompatActivity {
    private static final String TAG = "HornBlastersActivity";
    public static boolean FEATURE_CART = false;

    public enum CacheType {XML}

    private static final String CACHE_XML = "XML_Cache";
    private static final int CACHE_XML_SIZE = 12582912;
    private static final String CACHE_CONTROL = "Cache-Control";
    private static final String CACHE_CONTROL_ONLINE = "public, max-age=600";
    private static final String CACHE_CONTROL_OFFLINE = "public, only-if-cached, max-stale=2592000";
    private static final int HTTP_READ_TIMEOUT = 10;
    private static final int HTTP_CONNECT_TIMEOUT = 15;

    public enum ConnectionType {WIFI, MOBILE, UNKNOWN, NONE}

    // Defaults as connected; this won't matter anyway.
    static boolean netConnected = true;
    private static ConnectionType netType = ConnectionType.UNKNOWN;

    //    static final String WIFI = "Wi-Fi";
//    static final String ANY = "Any";
//    static boolean wifiConnected = false;
//    static boolean mobileConnected = false;
//    static boolean refreshDisplay = true;
    static String sPref = null;
    private NetworkReceiver receiver;

    static final String API_CATEGORIES = "https://us-east1.hornblasters.com/api/categories.php";
    static final String API_CATEGORY = "https://us-east1.hornblasters.com/api/category.php?v=19&id=";
    static final String API_PRODUCT = "https://us-east1.hornblasters.com/api/product.php?id=";
    static final String API_VIDEOS = "https://us-east1.hornblasters.com/api/videos.php";
    static final String INTENT_CATEGORY = "category_id";
    static final String INTENT_VIDEO = "video_id";
    static final String INTENT_PRODUCT = "product_id";
    static final String INTENT_URL = "url";
    static final String WEB_STORE = "https://us-east1.hornblasters.com/products/?wv=1";
    static final String WEB_SUPPORT = "https://www.hornblasters.com/support/manuals_and_schematics?content-only=yes";
    static final String WEB_CONTACT = "https://www.hornblasters.com/contact?content-only=yes";
    static final String PREFS_NAME = "hornblasters";

    private Cache responseCache = null;
    private OkHttpClient ok;

    private CharSequence title;
    private CharSequence drawerTitle;
    Toolbar toolbar;
    private DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//, Transition enterTransition, Transition exitTransition
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Overflow menu display failed.");
            }
        }
    }

    @Override
    public void setContentView(int resource) {
        super.setContentView(resource);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        // NOTE: This must be set before DrawerToggle is initialized.
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout != null) {

            ListView drawerList = (ListView) findViewById(R.id.left_drawer);
            String[] sectionTitles = getResources().getStringArray(R.array.drawer_titles);
            drawerList.setAdapter(new ArrayAdapter<>(this,
                    R.layout.drawer_list_item, sectionTitles));
            drawerList.setOnItemClickListener(new DrawerItemClickListener());

            title = getTitle();
            drawerTitle = getString(R.string.drawer_title);

            drawerToggle = new ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    R.string.drawer_open,
                    R.string.drawer_close
            ) {

            };

            drawerLayout.setDrawerListener(drawerToggle);
            drawerToggle.syncState();
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    switchActivity(MainActivity.class);
                    break;
                case 1:
                    switchActivity(StoreActivity.class);
                    break;
                case 2:
                    switchActivity(SoundboardActivity.class);
                    break;
                case 3:
                    switchActivity(WebActivity.class, INTENT_URL, WEB_SUPPORT);
                    break;
                case 4:
                    switchActivity(VideosActivity.class);
                    break;
                case 5:
                    switchActivity(WebActivity.class, INTENT_URL, WEB_CONTACT);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (drawerToggle != null) {
            drawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() == 1) {
                drawerToggle.setDrawerIndicatorEnabled(true);
            }
            super.onBackPressed();
        }
    }

    void trackHit(String screenName) {
        Tracker t = ((HornBlastersApplication) getApplication()).getTracker(
                HornBlastersApplication.TrackerName.APP_TRACKER);
        t.setScreenName(screenName);
        t.enableAdvertisingIdCollection(true);
        if (!BuildConfig.DEBUG) {
            t.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        this.title = title;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateConnectedFlags();
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem i) {
        if (drawerToggle != null && drawerToggle.onOptionsItemSelected(i)) {
            return true;
        }

        switch (i.getItemId()) {
            case R.id.home:
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_activity_videos:
                newActivity(VideosActivity.class);
                break;
            case R.id.action_activity_cart:
                cartActivity();
                return true;
        }

        return super.onOptionsItemSelected(i);
    }

    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            netConnected = true;
            switch (activeInfo.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    netType = ConnectionType.WIFI;
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    netType = ConnectionType.MOBILE;
                    break;
                default:
                    netType = ConnectionType.UNKNOWN;
            }
        } else {
            netConnected = false;
            netType = ConnectionType.NONE;
        }
    }

    Cache getHttpCache() {
        if (responseCache == null) {
            responseCache = ((HornBlastersApplication) getApplication()).getOk().getCache();
        }
        return responseCache;
    }

    String downloadCachedUrl(String urlString) throws IOException {
        if (ok == null) {
            ok = ((HornBlastersApplication) getApplication()).getOk();
        }

        ok.setReadTimeout(HTTP_READ_TIMEOUT, TimeUnit.SECONDS);
        ok.setConnectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS);
        Request.Builder builder =
         new Request.Builder()
                .url(urlString)
                .addHeader(CACHE_CONTROL,
                        (netConnected) ? CACHE_CONTROL_ONLINE : CACHE_CONTROL_OFFLINE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder.addHeader("Accept", "image/webp");
        }
        Request request = builder.build();

        Response response = ok.newCall(request).execute();
        if (response.code() == 504) {
            return null;
        } else {
            return response.body().string();
        }
    }

    void setExitTransition(Transition transition) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(transition);
        }
    }

    void showErrorPage(String message) {
        setContentView(R.layout.fragment_error);
        setDisplayHomeAsUpEnabled();
        setTitle(getString(R.string.title_error));
        TextView textView = (TextView) findViewById(R.id.error);
        textView.setText(message);
    }

    void setDisplayHomeAsUpEnabled() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    void newActivity(Class cl) {
        Intent intent = new Intent(this, cl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (cl != SoundboardActivity.class) {
            startActivity(intent);
        } else {
            super.startActivity(intent);
        }
    }

    void newActivity(Class cl, String key, int value) {
        Intent intent = new Intent(this, cl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    void newActivity(Class cl, String key, String value) {
        Intent intent = new Intent(this, cl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    private void switchActivity(Class cl) {
        Intent intent = new Intent(this, cl);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void switchActivity(Class cl, String key, String value) {
        Intent intent = new Intent(this, cl);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    private void cartActivity() {
        Intent intent = new Intent(this, CartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(intent);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void startActivity(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            super.startActivity(intent);
        }
    }

    void startWebActivity(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                netConnected = true;
                switch (networkInfo.getType()) {
                    case ConnectivityManager.TYPE_WIFI:
                        netType = ConnectionType.WIFI;
                        break;
                    case ConnectivityManager.TYPE_MOBILE:
                        netType = ConnectionType.MOBILE;
                        break;
                    default:
                        netType = ConnectionType.UNKNOWN;
                }
            } else {
                netConnected = false;
                netType = ConnectionType.NONE;
            }
        }
    }
}
