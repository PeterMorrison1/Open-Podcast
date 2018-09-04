package com.the_canuck.openpodcast.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;

import com.the_canuck.openpodcast.media_player.MediaControlApi;

public interface MainActivityContract {

    interface MainActivityView {

        void setArtwork600(String artwork);

        void setMediaState(int state);

        void setPosition(long position);
    }

    interface MainActivityPresenter {

        void getArtwork600(int collectionId);

        void setMediaController(MediaControlApi mediaController);

        void play();

        void playFromUri(Uri uri, Bundle bundle);

        void pause();

        void stop();

        void seekTo(int position);

        void registerCallback(MediaControllerCompat.Callback callback);

        void getState();

        void getPosition();
    }
}
