package com.the_canuck.openpodcast.media_player;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;

public interface MediaControlApi {

    interface StateCallback {

        void onLoaded(int state);
    }

    interface PositionCallback {

        void onLoaded(long position);
    }

    void play();

    void playFromUri(Uri uri, Bundle bundle);

    void pause();

    void stop();

    void seekTo(int position);

    void registerCallback(MediaControllerCompat.Callback callback);

    void getState(StateCallback callback);

    void getPosition(PositionCallback callback);

}
