package com.hornblasters.soundboard2;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public abstract class HornBlastersFragment extends Fragment {

    void nextActivity(Class cl, String key, int value) {
        Activity activity = getActivity();
        Intent intent = new Intent(activity, cl);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(key, value);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //noinspection unchecked
            activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
        } else {
            startActivity(intent);
        }
    }

    void startWebActivity(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    void loadCssWithWebView(WebView w, String cssFile, String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.loadDataWithBaseURL("file:///android_asset/",
                    "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + cssFile + "\" />" + html,
                    "text/html;charset=utf-8", "UTF-8", null);
        } else {
            StringBuilder buffer = new StringBuilder();
            String css;
            try {
                InputStream input = getActivity().getAssets().open(cssFile);
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
                    while ((css = reader.readLine()) != null) {
                        buffer.append(css);
                    }
                    css = buffer.toString();
                    reader.close();
                } catch (IOException e) {
                    css = ".app-android-hidden{display:none;}";
                }
                input.close();

            } catch (IOException e) {
                css = ".app-android-hidden{display:none;}";
            }

            try {
                // Note: this fixes Android 2.3 WebView to display UTF-8.
                w.getSettings().setDefaultTextEncodingName("utf-8");
                w.loadData(URLEncoder.encode("<style>" + css + "</style>" + html, "utf-8").replaceAll("\\+", " "),
                        "text/html;charset=utf-8", "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // no op
            }
        }
    }
}
