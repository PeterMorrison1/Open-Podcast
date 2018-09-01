package com.the_canuck.openpodcast.data.podcast;

import android.os.Handler;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import java.util.List;

import javax.inject.Inject;

public class PodcastServiceApiImpl implements PodcastServiceApi {

    MySQLiteHelper sqLiteHelper;

    @Inject
    public PodcastServiceApiImpl(MySQLiteHelper sqLiteHelper) {
        this.sqLiteHelper = sqLiteHelper;
    }

    @Override
    public void subscribe(final Podcast podcast, final int autoUpdate) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sqLiteHelper.subscribe(podcast, autoUpdate);
            }
        }).start();
    }

    @Override
    public void unsubscribe(final Podcast podcast) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sqLiteHelper.unsubscribe(podcast);
            }
        }).start();
    }

    @Override
    public void updatePodcast(final Podcast podcast, final int autoUpdate) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sqLiteHelper.updatePodcast(podcast, autoUpdate);
            }
        }).start();
    }

    @Override
    public void getSubscribedPodcasts(final PodcastServiceCallback<List<Podcast>> callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Podcast> podcastList = sqLiteHelper.getSubscribedPodcasts();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onLoaded(podcastList);
                    }
                });
            }
        }).start();
    }

    @Override
    public void doesPodcastExist(final Podcast podcast, final GetPodcastExistCallback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean exists = sqLiteHelper.doesPodcastExist(podcast);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onExistLoaded(exists);
                    }
                });
            }
        }).start();
    }

    @Override
    public void getPodcastArtwork600(final int collectionId, final GetArtworkCallback callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String artwork = sqLiteHelper.getPodcastArtwork600(collectionId);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onArtworkLoaded(artwork);
                    }
                });
            }
        }).start();
    }

    @Override
    public void getAutoUpdatePodcasts(final PodcastServiceCallback<List<Podcast>> callback) {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Podcast> podcastList = sqLiteHelper.getAutoUpdatePods();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onLoaded(podcastList);
                    }
                });
            }
        }).start();
    }
}
