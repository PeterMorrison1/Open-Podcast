package com.the_canuck.openpodcast.media_player;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.media_store.MediaStoreHelper;

import java.io.IOException;

public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener {
    public static final String ACTION_PLAY = "com.the_canuck.openpodcast.media_player.PLAY";
    MediaPlayer mediaPlayer = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            if (action.equals(ACTION_PLAY)) {
                Episode mEpisode = (Episode) intent.getExtras().getSerializable(Episode.EPISODE);
                processPlayRequest(mEpisode);
            }
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        super.onDestroy();
    }

    public void processPlayRequest(Episode currentEpisode) {
        try {
            mediaPlayer = new MediaPlayer();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                        .build());
            } else {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            Uri mUri = MediaStoreHelper.getEpisodeUri(getApplicationContext(), currentEpisode);
            if (mUri != null) {
                mediaPlayer.setDataSource(getApplicationContext(), mUri);
            } else {
                Toast.makeText(getApplicationContext(), "Error loading episode",
                        Toast.LENGTH_SHORT).show();
            }
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync();
//            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
