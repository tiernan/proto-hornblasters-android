package com.hornblasters.soundboard2;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.HashMap;

public class HornBlastersApplication extends Application {
    private static OkHttpClient ok;
    private static final String CACHE_XML = "xml-cache";
    // NOTE: not actually implemented; this is just the current default value of Picasso
    public static final String CACHE_PICASSO = "picasso-cache";
    private static final int CACHE_XML_SIZE = 12582912;

    private static final String PROPERTY_ID = "UA-470222-9";
    public enum TrackerName {
        APP_TRACKER,
        GLOBAL_TRACKER
    }

    @Override public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }

    private final HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();

    synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
                    : analytics.newTracker(R.xml.ecommerce_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }

    OkHttpClient getOk() {
        if (ok == null) {
            ok = new OkHttpClient();
        }
        ok.setCache(getHttpCache());
        return ok;
    }

    Cache getHttpCache() {
        return getHttpCache(CACHE_XML, CACHE_XML_SIZE);
    }

    Cache getHttpCache(String dir, int size) {
        File cache = new File(this.getCacheDir(), dir);
        if (!cache.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cache.mkdirs();
        }
        return new Cache(cache, size);
    }
}
