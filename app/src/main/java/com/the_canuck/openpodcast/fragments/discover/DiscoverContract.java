package com.the_canuck.openpodcast.fragments.discover;

import com.the_canuck.openpodcast.Podcast;

import java.util.List;

public interface DiscoverContract {

    interface DiscoverView {

        void setPodcastList(List<List<Podcast>> podcastLists);

    }

    interface DiscoverPresenter {

        void populatePodcastList();
    }
}
