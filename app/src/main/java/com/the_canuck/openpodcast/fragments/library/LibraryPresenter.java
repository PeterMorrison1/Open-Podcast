package com.the_canuck.openpodcast.fragments.library;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.data.podcast.PodcastRepository;

import java.util.List;

import javax.inject.Inject;

public class LibraryPresenter implements LibraryContract.LibraryPresenter {

    private LibraryContract.LibraryView mLibraryView;

    private PodcastRepository podcastRepository;

    @Inject
    public LibraryPresenter(LibraryContract.LibraryView view, PodcastRepository podcastRepository) {
        mLibraryView = view;
        this.podcastRepository = podcastRepository;
    }

    @Override
    public void updateSubscribedPodcasts() {
        mLibraryView.showLoadingIndicator(true);

        podcastRepository.getSubscribedPodcasts(new PodcastRepository.LoadPodcastsCallback() {
            @Override
            public void onPodcastsLoaded(List<Podcast> podcasts) {
                mLibraryView.showSubscribedPodcasts(podcasts);
                mLibraryView.showLoadingIndicator(false);
                mLibraryView.populatePodcastViews();
            }
        });
    }
}
