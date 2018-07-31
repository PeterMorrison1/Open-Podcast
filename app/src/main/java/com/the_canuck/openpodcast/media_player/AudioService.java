package com.the_canuck.openpodcast.media_player;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.R;
import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import java.io.IOException;
import java.util.List;

public class AudioService extends MediaBrowserServiceCompat implements
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener{

    // Replace COMMAND_EXAMPLE with a real command if i use custom commands later
    public static final String COMMAND_EXAMPLE = "command_example";

    private MediaPlayer mediaPlayer;
    private MediaSessionCompat mediaSessionCompat;

    private Episode episode;

    private Handler handler = new Handler();

    private int currentState;
    private int currentSpeed;

    private boolean isNewEpisode;
    private boolean isRunning;

    private BroadcastReceiver mNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    };

    private MediaSessionCompat.Callback mMediaSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            super.onCommand(command, extras, cb);
        }

        @Override
        public void onPlay() {
            super.onPlay();
            if (!successfullyRetrievedAudioFocus()) {
                return;
            }
            isRunning = true;
            // FIXME: I think i accidentally put this here. Test if its not needed
//            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.reset();
//            }
            setPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            currentSpeed = 1;
            showPlayingNotification();
            mediaPlayer.start();
            mediaSessionCompat.setActive(true);


            if (episode != null && episode.getBookmark() != null && isNewEpisode) {
                mediaPlayer.seekTo(Integer.valueOf(episode.getBookmark()));
                isNewEpisode = false;
            }
        }

        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            // Extras contains serialized episode, not sure i need it yet. probably for notification
            super.onPlayFromUri(uri, extras);
            isRunning = true;

            episode = (Episode) extras.getSerializable(Episode.EPISODE);
            initMediaSessionMetadata();
//            createNotificationChannel();

            mediaPlayer.reset();

            isNewEpisode = true;

            if (uri != null) {
                try {
                    mediaPlayer.setDataSource(getApplicationContext(), uri);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    mediaPlayer.release();
                    initMediaPlayer();
                    try {
                        mediaPlayer.setDataSource(getApplicationContext(), uri);
                    } catch (IOException b) {
                        b.printStackTrace();
                    }
                }
            }
            mediaPlayer.prepareAsync();
        }

        @Override
        public void onPause() {
            super.onPause();

            if (mediaPlayer.isPlaying() && isRunning) {
                mediaPlayer.pause();
                currentSpeed = 0;
                setPlaybackState(PlaybackStateCompat.STATE_PAUSED);
                showPausedNotification();
            }
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            mediaPlayer.seekTo((int) pos);
        }

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            String action = mediaButtonEvent.getAction();
            if (Intent.ACTION_MEDIA_BUTTON.equals(action)) {
                KeyEvent event = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

                if (event != null) {
                    switch (event.getKeyCode()) {
                        case KeyEvent.KEYCODE_MEDIA_PAUSE: {
                            if (mediaPlayer.isPlaying()) {
                                onPause();
                                Log.d("eventv", "event pause: " + event);
                            }
                            break;
                        }
                        case KeyEvent.KEYCODE_MEDIA_PLAY: {
                            if (!mediaPlayer.isPlaying()) {
                                onPlay();
                                Log.d("eventv", "event play: " + event);
                            }
                            break;
                        }
                        /* Step forward keycode cant be used for some reason, so instead of fast
                        forwarding and rewinding, this will actually skip 30 seconds. For now.
                         */
                        case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD: {
                            onSeekTo(mediaPlayer.getCurrentPosition() + 30 * 1000);
                            break;
                        }
                        case KeyEvent.KEYCODE_MEDIA_REWIND: {
                            onSeekTo(mediaPlayer.getCurrentPosition() - 30 * 1000);
                        }
                    }

                }
            }

            return super.onMediaButtonEvent(mediaButtonEvent);

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSessionCompat, intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initMediaPlayer();
        initMediaSession();
        initNoisyReceiver();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                updatePosition();
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(this);
        try {
            unregisterReceiver(mNoisyReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        mediaSessionCompat.release();
        mediaPlayer.stop();
        mediaPlayer.release();
//        NotificationManagerCompat.from(AudioService.this).cancel(1);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    // Same as onDestroy but for swiping away when you haven't left the app yet
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        isRunning = false;
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(this);
        unregisterReceiver(mNoisyReceiver);
        mediaSessionCompat.release();
        mediaPlayer.stop();
        mediaPlayer.release();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        stopSelf();
    }



    //    private void createNotificationChannel() {
//        // Create the NotificationChannel, but only on API 26+ because
//        // the NotificationChannel class is new and not in the support library
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "Media_Channel";
//            String description = "Media Controller";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel("1", name, importance);
//            channel.setDescription(description);
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }

    private void showPlayingNotification() {
        mediaSessionCompat.setActive(true);
        NotificationCompat.Builder builder =
                NotificationHelper.from(AudioService.this, mediaSessionCompat);
        if (builder == null) {
            return;
        }

        // TODO: Write broadcast receiver to handle buttons in notification
        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_rew,
                "Rewind", MediaButtonReceiver.buildMediaButtonPendingIntent(
                this, PlaybackStateCompat.ACTION_REWIND)));

        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_pause,
                "Pause", MediaButtonReceiver.buildMediaButtonPendingIntent
                (this, PlaybackStateCompat.ACTION_PAUSE)));

        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_ff,
                "Forward", MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this, PlaybackStateCompat.ACTION_FAST_FORWARD)));

        builder.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this, PlaybackStateCompat.ACTION_STOP))
                .setShowActionsInCompactView(0, 1, 2)
                .setMediaSession(mediaSessionCompat.getSessionToken()));

        builder.setSmallIcon(R.mipmap.ic_launcher);
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Media_Channel";
            String description = "Media Controller";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            channel.enableVibration(false);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            manager.createNotificationChannel(channel);
        }

//        NotificationManagerCompat.from(AudioService.this).notify(1, builder.build());
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(1, builder.build());
        startForeground(1, builder.build());
    }

    private void showPausedNotification() {
        NotificationCompat.Builder builder =
                NotificationHelper.from(AudioService.this, mediaSessionCompat);
        if (builder == null) {
            return;
        }
        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_rew,
                "Rewind", MediaButtonReceiver.buildMediaButtonPendingIntent(
                this, PlaybackStateCompat.ACTION_REWIND)));

        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play,
                "Play", MediaButtonReceiver.buildMediaButtonPendingIntent
                (this, PlaybackStateCompat.ACTION_PLAY)));

        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_ff,
                "Forward", MediaButtonReceiver.buildMediaButtonPendingIntent(
                this, PlaybackStateCompat.ACTION_FAST_FORWARD)));

        builder.setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this, PlaybackStateCompat.ACTION_STOP))
                .setShowActionsInCompactView(0, 1, 2)
                .setMediaSession(mediaSessionCompat.getSessionToken()));

        builder.setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Media_Channel";
            String description = "Media Controller";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            channel.enableVibration(false);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            manager.createNotificationChannel(channel);
        }
//        NotificationManagerCompat.from(AudioService.this).notify(1, builder.build());
        startForeground(1, builder.build());
        stopForeground(false);
    }

    /**
     * Controls volume when headphones are plugged in/out.
     */
    private void initNoisyReceiver() {
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(mNoisyReceiver, filter);
    }

    /**
     * Initialize the media player and it's settings.
     */
    private void initMediaPlayer() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                        .build());
            } else {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            mediaPlayer.setVolume(1.0f, 1.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the media session and set the session token.
     */
    private void initMediaSession() {
        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(),
                MediaButtonReceiver.class);

        mediaSessionCompat = new MediaSessionCompat(getApplicationContext(), "Tag",
                mediaButtonReceiver, null);

        mediaSessionCompat.setCallback(mMediaSessionCallback);
        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                mediaButtonIntent, 0);

        mediaSessionCompat.setMediaButtonReceiver(pendingIntent);

        setSessionToken(mediaSessionCompat.getSessionToken());
    }

    private void updatePosition() {
        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();
        if (episode != null && mediaPlayer != null && isRunning) {
            builder.setState(currentState, mediaPlayer.getCurrentPosition(), currentSpeed);
            mediaSessionCompat.setPlaybackState(builder.build());
        }
    }

    private boolean successfullyRetrievedAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_GAIN;
    }

    /**
     * Sets the playback state for the media session.
     *
     * @param state the state of playback
     */
    private void setPlaybackState(int state) {
        PlaybackStateCompat.Builder builder = new PlaybackStateCompat.Builder();
        if (state == PlaybackStateCompat.STATE_PLAYING) {
            builder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE |
                    PlaybackStateCompat.ACTION_PAUSE);
        } else {
            builder.setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE |
                    PlaybackStateCompat.ACTION_PLAY);
        }
        currentState = state;

        if (episode == null || episode.getBookmark() == null) {
            builder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1);
        } else {
            builder.setState(state, Long.valueOf(episode.getBookmark()), 1);
        }

        mediaSessionCompat.setPlaybackState(builder.build());
    }

    private void initMediaSessionMetadata() {
        MySQLiteHelper sqLiteHelper = new MySQLiteHelper(this);

        Glide.with(getApplicationContext())
                .asBitmap()
                .load(sqLiteHelper.getPodcastArtwork600(episode.getCollectionId()))
                .apply(RequestOptions.circleCropTransform())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();

                        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, resource);
                        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, resource);
                        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART,
                                BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

                        builder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE,
                                episode.getTitle());
                        // TODO: Add artist to episode in sqlite
                        builder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,
                                episode.getArtist());
                        mediaSessionCompat.setMetadata(builder.build());
                    }
                });
    }


    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid,
                                 @Nullable Bundle rootHints) {
//        if (TextUtils.equals(clientPackageName, getPackageName())) {
//            return new BrowserRoot(getString(R.string.app_name), null);
//        }
        return new BrowserRoot(getString(R.string.app_name), null);
//        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId,
                               @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS: {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: {
                mediaPlayer.pause();
                break;
            }
            case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK: {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(0.3f, 0.3f);
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_GAIN: {
                if (mediaPlayer != null) {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                    mediaPlayer.setVolume(1.0f, 1.0f);
                }
                break;
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        }
    }
}
