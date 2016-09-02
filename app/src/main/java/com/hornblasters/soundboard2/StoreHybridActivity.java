package com.hornblasters.soundboard2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.hornblasters.soundboard2.StoreHybridParser.Product;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class StoreHybridActivity extends HornBlastersActivity {
    private static final String WIFI = "Wi-Fi";
    private static final String ANY = "Any";
    private static final String URL = "https://www.hornblasters.com/products.xml";

    private static boolean wifiConnected = false;
    private static boolean mobileConnected = false;
    private static boolean refreshDisplay = true;
    private static String sPref = null;
    private NetworkReceiver receiver = new NetworkReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_hybrid);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        setDisplayHomeAsUpEnabled();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sPref = sharedPrefs.getString("listPref", "Wi-Fi");

        updateConnectedFlags();

        if (refreshDisplay) {
            loadPage();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }

    private void loadPage() {
        if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
                || ((sPref.equals(WIFI)) && (wifiConnected))) {
            new DownloadXmlTask().execute(URL);
            WebView webView = (WebView) findViewById(R.id.webView);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();
                    return true;
                }
            });
        } else {
            showErrorPage();
        }
    }

    private void showErrorPage() {
        setContentView(R.layout.activity_store_hybrid);
        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadData(getResources().getString(R.string.error_connection), "text/html; charset=utf-8", "UTF-8");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return true;
    }

    private class DownloadXmlTask extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadXmlTask";

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, e.getMessage());
                }
                return getResources().getString(R.string.error_xml);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            WebView myWebView = (WebView) findViewById(R.id.webView);
            myWebView.loadData(result, "text/html", null);
        }
    }

    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        StoreHybridParser productXmlParser = new StoreHybridParser();
        List<Product> products = null;
        StringBuilder htmlString = new StringBuilder();
        htmlString.append("<h3>").append(getResources().getString(R.string.title_activity_store)).append("</h3>");

        try {
            stream = downloadUrlStream(urlString);
            products = productXmlParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        for (Product product : products) {
            htmlString.append("<p><a href='");
            htmlString.append(product.image);
            htmlString.append("'>").append(product.title).append("</a></p>");
        }
        return htmlString.toString();
    }

    private InputStream downloadUrlStream(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();

        return conn.getInputStream();
    }

    public class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            refreshDisplay = WIFI.equals(sPref) && networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI || ANY.equals(sPref) && networkInfo != null;
        }
    }
}
