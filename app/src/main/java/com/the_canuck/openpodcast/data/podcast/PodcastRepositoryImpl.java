package com.the_canuck.openpodcast.data.podcast;

import com.the_canuck.openpodcast.Podcast;

import java.util.List;

import javax.inject.Inject;

public class PodcastRepositoryImpl implements PodcastRepository {

    public final PodcastServiceApi podcastServiceApi;

    private List<Podcast> cachedPodcastList;

    @Inject
    public PodcastRepositoryImpl(PodcastServiceApi podcastServiceApi) {
        this.podcastServiceApi = podcastServiceApi;
    }

    @Override
    public void subscribe(Podcast podcast, int autoUpdate) {
        podcastServiceApi.subscribe(podcast, autoUpdate);
    }

    @Override
    public void unsubscribe(Podcast podcast) {
        podcastServiceApi.unsubscribe(podcast);
    }

    @Override
    public void updatePodcast(Podcast podcast, int autoUpdate) {
        podcastServiceApi.updatePodcast(podcast, autoUpdate);
    }

    @Override
    public void getSubscribedPodcasts(final LoadPodcastsCallback callback) {
        if (cachedPodcastList == null) {
            podcastServiceApi.getSubscribedPodcasts(new PodcastServiceApi.PodcastServiceCallback<List<Podcast>>() {
                @Override
                public void onLoaded(List<Podcast> podcasts) {
                    cachedPodcastList = podcasts;
                    callback.onPodcastsLoaded(cachedPodcastList);
                }
            });
        } else {
            callback.onPodcastsLoaded(cachedPodcastList);
        }
    }

    @Override
    public void doesPodcastExist(Podcast podcast, final GetPodcastExistCallback callback) {
        podcastServiceApi.doesPodcastExist(podcast, new PodcastServiceApi.GetPodcastExistCallback() {
            @Override
            public void onExistLoaded(boolean exists) {
                callback.onExistLoaded(exists);
            }
        });
    }

    @Override
    public void getPodcastArtwork600(int collectionId, final GetArtworkCallback callback) {
        podcastServiceApi.getPodcastArtwork600(collectionId, new PodcastServiceApi.GetArtworkCallback() {
            @Override
            public void onArtworkLoaded(String artworkUrl) {
                callback.onArtworkLoaded(artworkUrl);
            }
        });
    }

    @Override
    public void getAutoUpdatePodcasts(final LoadPodcastsCallback callback) {
        podcastServiceApi.getAutoUpdatePodcasts(new PodcastServiceApi.PodcastServiceCallback<List<Podcast>>() {
            @Override
            public void onLoaded(List<Podcast> podcasts) {
                callback.onPodcastsLoaded(podcasts);
            }
        });
    }

    @Override
    public void refreshData() {
        cachedPodcastList = null;
    }
}
