package com.the_canuck.openpodcast.data.discover_list;

import com.the_canuck.openpodcast.Podcast;

import java.util.List;

public interface DiscoverLocalApi {

    interface PodcastListLoadedCallback {

        void onPodcastsLoaded(List<Podcast> podcastList);
    }

    void parsePodcastList(int genre, PodcastListLoadedCallback callback);

}
