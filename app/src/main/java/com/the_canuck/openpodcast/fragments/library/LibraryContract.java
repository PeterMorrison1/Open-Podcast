package com.the_canuck.openpodcast.fragments.library;

import com.the_canuck.openpodcast.Podcast;

import java.util.List;

public interface LibraryContract {

    interface LibraryView {

        void showLoadingIndicator(boolean active);

        void showSubscribedPodcasts(List<Podcast> podcasts);

        void populatePodcastViews();

//        void setColumnCount(int columnCount);

    }

    interface LibraryPresenter {

        void updateSubscribedPodcasts();

    }
}
