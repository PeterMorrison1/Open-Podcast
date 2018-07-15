package com.the_canuck.openpodcast.media_player;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.media_store.MediaStoreHelper;

import java.io.IOException;

/**
 * <p>The service that runs the MediaPlayer, which is controlled with MediaController. </p>
 *
 * <p>To access the MediaPlayer object you must create a connection to the ServerInstance,
 * which is only to be accessed to get current position or update with seekbar. </p>
 *
 * All other MediaPlayer controls must be done through the MediaController (play, pause, resume,
 * stop).
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener {
    public static final String ACTION_PLAY = "com.the_canuck.openpodcast.media_player.PLAY";
    public static final String ACTION_PAUSE = "com.the_canuck.openpodcast.media_player.PAUSE";
    public static final String ACTION_RESUME = "com.the_canuck.openpodcast.media_player.RESUME";
    public static final String ACTION_STOP = "com.the_canuck.openpodcast.media_player.STOP";
    public static final String ACTION_SEEK_BUTTON =
            "com.the_canuck.openpodcast.media_player.SEEK_BUTTON";

    private MediaPlayer mediaPlayer = null;
    private Episode episode = null;

    IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public MediaPlayerService getServerInstance() {
            return MediaPlayerService.this;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

        if (episode.getBookmark() != null) {
            mp.seekTo(Integer.valueOf(episode.getBookmark()));
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            if (action.equals(ACTION_PLAY)) {
                Episode mEpisode = (Episode) intent.getExtras().getSerializable(Episode.EPISODE);
                episode = mEpisode;
                processPlayRequest(mEpisode);
            } else if (action.equals(ACTION_PAUSE)) {
                processPauseRequest();
            } else if (action.equals(ACTION_RESUME)) {
                processResumeRequest();
            } else if (action.equals(ACTION_SEEK_BUTTON)) {
                int mTime = intent.getIntExtra(MediaController.SEEK_TIME, 0);
                processSeekButtonRequest(mTime);
            }
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        super.onDestroy();
    }

    /**
     * Starts the mediaplayer with the passed in episode. Resets mediaplayer if new episode is
     * passed in.
     *
     * @param currentEpisode the episode to be played by the mediaplayer.
     */
    private void processPlayRequest(Episode currentEpisode) {
        try {
            // Resets mediaplayer if one already exists to prepare for new media
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            } else {
                mediaPlayer.reset();
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the mediaplayer completely.
     */
    private void processStopRequest() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     * Pauses the mediaplayer.
     */
    private void processPauseRequest() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    /**
     * Resumes the mediaplayer.
     */
    private void processResumeRequest() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    /**
     * Processes the seek button request by applying the time entered to the episode.
     *
     * @param time the time to add or subtract from the current time (in seconds, not ms)
     */
    private void processSeekButtonRequest(int time) {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int newPosition = currentPosition + time * 1000;
        mediaPlayer.seekTo(newPosition);
    }

    /**
     * Gets the mediaplayer object.
     *
     * @return the mediaplayer
     */
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
