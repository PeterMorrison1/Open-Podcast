package com.the_canuck.openpodcast.data.discover_list;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.search.enums.GenreIds;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class DiscoverRepositoryImpl implements DiscoverRepository {

    private DiscoverLocalApi discoverLocalApi;

    private List<List<Podcast>> cachedLists;

    @Inject
    public DiscoverRepositoryImpl(DiscoverLocalApi discoverLocalApi) {
        this.discoverLocalApi = discoverLocalApi;
    }

    @Override
    public void getAllPodcastLists(final EveryListLoadedCallback callback) {
        if (cachedLists == null) {
            cachedLists = new ArrayList<>();
            GenreIds[] ids = GenreIds.values();

            for (GenreIds id : ids) {
                getPodcastList(id.getValue(), new ListLoadedCallback() {
                    @Override
                    public void onListLoaded(List<Podcast> podcastList) {
                        cachedLists.add(podcastList);
                    }
                });
            }
//            if (cachedLists.size() == GenreIds.getSize()) {
                callback.onListsLoaded(cachedLists);
//            }
        } else {
            callback.onListsLoaded(cachedLists);
        }
    }

    @Override
    public void getPodcastList(int genre, final ListLoadedCallback callback) {
        discoverLocalApi.parsePodcastList(genre, new DiscoverLocalApi.PodcastListLoadedCallback() {
            @Override
            public void onPodcastsLoaded(List<Podcast> podcastList) {
                callback.onListLoaded(podcastList);
            }
        });
    }

    @Override
    public void refreshCache() {
        cachedLists = null;
    }

    // Setter for tests

    protected void setCachedLists(List<List<Podcast>> cachedLists) {
        this.cachedLists = cachedLists;
    }
}
