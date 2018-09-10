package com.the_canuck.openpodcast.fragments.discover;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.data.discover_list.DiscoverRepository;

import java.util.List;

public class DiscoverPresenter implements DiscoverContract.DiscoverPresenter {

    public DiscoverRepository discoverRepository;

    private DiscoverContract.DiscoverView discoverView;

    public DiscoverPresenter(DiscoverRepository discoverRepository, DiscoverContract.DiscoverView discoverView) {
        this.discoverRepository = discoverRepository;
        this.discoverView = discoverView;
    }

    @Override
    public void populatePodcastList() {
        discoverRepository.getAllPodcastLists(new DiscoverRepository.EveryListLoadedCallback() {
            @Override
            public void onListsLoaded(List<List<Podcast>> lists) {
                discoverView.setPodcastList(lists);
            }
        });
    }
}
