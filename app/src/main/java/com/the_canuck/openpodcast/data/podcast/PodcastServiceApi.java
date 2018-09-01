package com.the_canuck.openpodcast.data.podcast;

import com.the_canuck.openpodcast.Podcast;

import java.util.List;

public interface PodcastServiceApi {

    interface PodcastServiceCallback<T> {

        void onLoaded(T podcasts);
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

    void getSubscribedPodcasts(PodcastServiceCallback<List<Podcast>> callback);

    void doesPodcastExist(Podcast podcast, GetPodcastExistCallback callback);

    void getPodcastArtwork600(int collectionId, GetArtworkCallback callback);

    void getAutoUpdatePodcasts(PodcastServiceCallback<List<Podcast>> callback);

}
