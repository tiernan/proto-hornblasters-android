package com.hornblasters.soundboard2;

import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;

public class WebActivity extends HornBlastersActivity {
    private static final String TAG = "WebActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_web);

        String urlString = getIntent().getExtras().getString(HornBlastersActivity.INTENT_URL);
        if (urlString == null) {
            showErrorPage("URL not provided.");
            return;
        }
        switch(urlString) {
            case "https://www.hornblasters.com/contact":
                setTitle("Contact Us");
                break;
            case "https://www.hornblasters.com/support/manuals":
                setTitle("Support");
                break;
            default:
                setTitle("HornBlasters");
        }

        if (netConnected) {
            loadPage(urlString);
        } else {
            showErrorPage("No connection available");
        }
        trackHit(TAG);
    }

    private void loadPage(String urlString) {
        if (netConnected) {
            WebView webView = (WebView) findViewById(R.id.webView);
            webView.loadUrl(urlString);
        } else {
            showErrorPage("Unable to connect to network.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return true;
    }
}
