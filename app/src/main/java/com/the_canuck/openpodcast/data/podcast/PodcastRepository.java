package com.the_canuck.openpodcast.data.podcast;

import com.the_canuck.openpodcast.Podcast;

import java.util.List;

public interface PodcastRepository {

    interface LoadPodcastsCallback {

        void onPodcastsLoaded(List<Podcast> podcasts);

    }

    interface GetPodcastCallback {

        void onPodcastLoaded(Podcast podcast);

    }

    interface GetArtworkCallback {

        void onArtworkLoaded(String artworkUrl);
    }

    interface GetPodcastExistCallback {

        void onExistLoaded(boolean exists);
    }

    void subscribe(Podcast podcast, int autoUpdate);

    void unsubscribe(Podcast podcast);

    void updatePodcast(Podcast podcast, int autoUpdate);

    void getSubscribedPodcasts(LoadPodcastsCallback callback);

    void doesPodcastExist(Podcast podcast, GetPodcastExistCallback callback);

    void getPodcastArtwork600(int collectionId, GetArtworkCallback callback);

    void getAutoUpdatePodcasts(LoadPodcastsCallback callback);

    void refreshData();

}
