package com.the_canuck.openpodcast.data.discover_list;

import com.the_canuck.openpodcast.Podcast;

import java.util.List;

public interface DiscoverRepository {

    interface ListLoadedCallback {

        void onListLoaded(List<Podcast> podcastList);
    }

    interface EveryListLoadedCallback {

        void onListsLoaded(List<List<Podcast>> lists);
    }

    void getAllPodcastLists(EveryListLoadedCallback callback);

    void getPodcastList(int genre, ListLoadedCallback callback);

    void refreshCache();

}
