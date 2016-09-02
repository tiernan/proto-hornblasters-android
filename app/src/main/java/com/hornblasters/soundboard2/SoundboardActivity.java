package com.hornblasters.soundboard2;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SoundboardActivity extends HornBlastersActivity {
    private static final String TAG = "SoundboardActivity";
    private SoundboardAdapter adapter = null;
    public enum MediaType {MEDIA_ALARM, MEDIA_NOTIFICATION, MEDIA_RINGTONE}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundboard);

        GridView gridView = (GridView) findViewById(R.id.grid_view);
        adapter = new SoundboardAdapter(this);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                playAudio(position);
            }
        });
        registerForContextMenu(gridView);

        trackHit(TAG);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_soundboard, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_context_set_ringtone:
                setMedia(MediaType.MEDIA_RINGTONE, info.position);
                return true;
            case R.id.action_context_set_notification:
                setMedia(MediaType.MEDIA_NOTIFICATION, info.position);
                return true;
            case R.id.action_context_set_alarm:
                saveMedia(MediaType.MEDIA_ALARM, info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (BuildConfig.DEBUG) {
            getMenuInflater().inflate(R.menu.menu_debug, menu);
        }
        return true;
    }

    private Uri saveMedia(MediaType type, int position) {
        int resourceId = adapter.getAudio(position);
        String directory;
        boolean alarm = false;
        boolean notification = false;
        boolean ringtone = false;
        switch (type) {
            case MEDIA_ALARM:
                alarm = true;
                directory = Environment.DIRECTORY_ALARMS;
                break;
            case MEDIA_NOTIFICATION:
                notification = true;
                directory = Environment.DIRECTORY_NOTIFICATIONS;
                break;
            case MEDIA_RINGTONE:
            default:
                ringtone = true;
                directory = Environment.DIRECTORY_RINGTONES;
        }
        File newSoundFile = new File(Environment.getExternalStoragePublicDirectory(directory),
                "hornblasters-" + getResources().getResourceEntryName(resourceId)  + ".mp3");
        ContentResolver contentResolver = getContentResolver();
        if (!newSoundFile.exists()) {
            try {
                copyResources(resourceId, newSoundFile);
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "IO exception while copying resource");
                }
                return null;
            }

            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, newSoundFile.getAbsolutePath());
            values.put(MediaStore.MediaColumns.TITLE, "HornBlasters " + getString(adapter.getString(position)));
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
            values.put(MediaStore.MediaColumns.SIZE, newSoundFile.length());
            values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
            values.put(MediaStore.Audio.Media.IS_RINGTONE, ringtone);
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, notification);
            values.put(MediaStore.Audio.Media.IS_ALARM, alarm);
            values.put(MediaStore.Audio.Media.IS_MUSIC, false);

            Uri uri = MediaStore.Audio.Media.getContentUriForPath(newSoundFile.getAbsolutePath());

            return contentResolver.insert(uri, values);
        }
        String[] retCol = { MediaStore.Audio.Media._ID };
        Cursor cur = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                retCol,
                MediaStore.MediaColumns.DATA + "='" + newSoundFile.getAbsolutePath() + "'", null, null);
        try {
            assert cur != null;
            if (cur.getCount() == 0) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "File existed, but not in media store.");
                }
                return null;
            }
            cur.moveToFirst();
            int id = cur.getInt(cur.getColumnIndex(MediaStore.MediaColumns._ID));
            cur.close();
            return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        } catch (NullPointerException e) {
            return null;
        }
    }

    private void setMedia(MediaType type, int position) {
        Uri media = saveMedia(type, position);
        if (media != null) {
            int rmType;
            switch (type) {
                case MEDIA_NOTIFICATION:
                    rmType = RingtoneManager.TYPE_NOTIFICATION;
                    break;
                case MEDIA_RINGTONE:
                default:
                    rmType = RingtoneManager.TYPE_RINGTONE;
            }
            try {
                RingtoneManager.setActualDefaultRingtoneUri(this, rmType, media);
            } catch (Throwable t) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Could not set media");
                    t.printStackTrace();
                }
            }
        }
    }

    private void playAudio(int position) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, adapter.getAudio(position));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mp.release();
            }
        });
        mediaPlayer.start();

        Toast.makeText(SoundboardActivity.this, getString(adapter.getString(position)),
                Toast.LENGTH_SHORT).
                show();
    }

    private void copyResources(int resourceId, File out) throws IOException {
        Log.i("Test", "Setup::copyResources");
        InputStream inputStream = getResources().openRawResource(resourceId);
        FileOutputStream outputStream = new FileOutputStream(out);

        byte[] readData = new byte[1024];
        int i = inputStream.read(readData);

        while (i != -1) {
            outputStream.write(readData, 0, i);
            i = inputStream.read(readData);
        }
        outputStream.close();
        inputStream.close();
    }
}
