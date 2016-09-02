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

import com.hornblasters.xml.Media;
import com.hornblasters.xml.Parser;

public class VideosActivity extends HornBlastersActivity {
    private static final String TAG = "VideosActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        new DownloadXmlTaskNative().execute(API_VIDEOS);
        trackHit(TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_store, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        VideosAdapter adapter = (VideosAdapter) ((ListView) findViewById(R.id.videos_list)).getAdapter();
        if (adapter != null) {
            adapter.cancelTasks();
        }
        super.onDestroy();
    }

    private void render(Media data) {
        setTitle("Videos");
        ListView listView = (ListView) findViewById(R.id.videos_list);
        VideosAdapter listAdapter = new VideosAdapter(getApplicationContext(), data.videos);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideosAdapter.ViewHolder vh = (VideosAdapter.ViewHolder) view.getTag();
                startWebActivity("http://www.youtube.com/watch?v=" + vh.uri);
            }
        });
    }

    private class DownloadXmlTaskNative extends AsyncTask<String, Void, Media> {
        private static final String TAG = "DownloadXmlTaskNative";

        @Override
        protected Media doInBackground(String... urls) {
            try {
                Log.d(TAG, "Parsing: " + urls[0]);
                return (Media) new Parser().parse(downloadCachedUrl(urls[0]));
            } catch (Exception e) {
                e.printStackTrace();
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, e.getMessage());
                }
                return null;
            }
        }

        @Override
        protected void onPostExecute(Media result) {
            if (result == null) {
                showErrorPage("Failed to load XML)");
            } else {
                if (!VideosActivity.this.isFinishing()) {
                    render(result);
                }
            }
        }
    }
}
