package com.hornblasters.soundboard2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.Log;

import com.hornblasters.core.OrdersHelper;
import com.squareup.okhttp.Cache;

import java.io.IOException;
import java.lang.ref.WeakReference;


public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Fade());
        }
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_splash);
        final Handler handler = new Handler();
        handler.postDelayed(new Setup(this), 1000);
    }

    private void setupCallback() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

    private static class Setup implements Runnable {
        private final WeakReference<SplashActivity> activity;

        public Setup(SplashActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        public void run() {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            new OrdersHelper(activity.get()).getReadableDatabase().close();

            if (BuildConfig.DEBUG) {
                HornBlastersActivity.FEATURE_CART = true;
            }

            SharedPreferences settings = activity.get().getSharedPreferences(HornBlastersActivity.PREFS_NAME, MODE_PRIVATE);
            int version = settings.getInt("version", 0);
            boolean update = true;
            if (version < 21) {
                Cache responseCache = ((HornBlastersApplication)activity.get().getApplication()).getHttpCache();
                try {
                    responseCache.delete();
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Deleting cache success.");
                    }
                } catch (IOException e) {
                    update = false;
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Deleting cache failed.");
                    }
                }
            } else {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Build up to date");
                }
            }

            if (update) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("version", BuildConfig.VERSION_CODE);
                editor.apply();

            }

            activity.get().setupCallback();
        }
    }
}
