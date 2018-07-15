package com.the_canuck.openpodcast.media_player;

import android.content.Context;
import android.content.Intent;

import com.the_canuck.openpodcast.Episode;

/**
 * MediaController is used to control everything for mediaplayer except seek control due to
 * the seekbar requiring constant calls and ui updates. The other controls being in this controller
 * allows for the notification (not yet implemented) to pause and play. Probably, we'll see.
 */
public class MediaController {


    public static String SEEK_TIME = "SEEK_TIME";

    /**
     * Sends a start request with ACTION_PLAY to the MediaPlayerService to start an episode.
     *
     * @param context the application context
     * @param mEpisode the episode being requested to play
     */
    public static void startRequest(Context context, Episode mEpisode) {
        Intent mIntent = new Intent(context, MediaPlayerService.class);
        mIntent.setAction(MediaPlayerService.ACTION_PLAY);
        mIntent.putExtra(Episode.EPISODE, mEpisode);
        context.startService(mIntent);
    }

    /**
     * Sends a request based on the action passed in.
     * Must be MediaPlayerService.ACTION_PAUSE/PLAY/STOP.
     *
     * @param context the application context
     * @param action the action being sent, must be pause, play or stop
     */
    public static void stateRequest(Context context, String action) {
        Intent mIntent = new Intent(context, MediaPlayerService.class);
        mIntent.setAction(action);
        context.startService(mIntent);
    }

    /**
     * Sends a ACTION_SEEK_BUTTON request with the time to be added or subtracted from the current
     * progress time of an episode as an extra. Meant for seek forward/rewind buttons.
     *
     * @param context the application context
     * @param seekTime the time to be added or subtracted from the current episode progress
     */
    public static void seekButtonRequest(Context context, int seekTime) {
        Intent mIntent = new Intent(context, MediaPlayerService.class);
        mIntent.setAction(MediaPlayerService.ACTION_SEEK_BUTTON);
        mIntent.putExtra(SEEK_TIME, seekTime);
        context.startService(mIntent);
    }
}
