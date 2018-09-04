package com.the_canuck.openpodcast.media_player;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;

public class MediaControlApiImpl implements MediaControlApi {

    private MediaControllerCompat mediaControllerCompat;

    public MediaControlApiImpl(MediaControllerCompat mediaControllerCompat) {
        this.mediaControllerCompat = mediaControllerCompat;
    }

    @Override
    public void play() {
        mediaControllerCompat.getTransportControls().play();
    }

    @Override
    public void playFromUri(Uri uri, Bundle bundle) {
        mediaControllerCompat.getTransportControls().playFromUri(uri, bundle);
    }

    @Override
    public void pause() {
        mediaControllerCompat.getTransportControls().pause();
    }

    @Override
    public void stop() {
        mediaControllerCompat.getTransportControls().stop();
    }

    @Override
    public void seekTo(int position) {
        mediaControllerCompat.getTransportControls().seekTo(position);
    }

    @Override
    public void registerCallback(MediaControllerCompat.Callback callback) {
        mediaControllerCompat.registerCallback(callback);
    }

    @Override
    public void getState(StateCallback callback) {
        callback.onLoaded(mediaControllerCompat.getPlaybackState().getState());
    }

    @Override
    public void getPosition(PositionCallback callback) {
        callback.onLoaded(mediaControllerCompat.getPlaybackState().getPosition());
    }
}
