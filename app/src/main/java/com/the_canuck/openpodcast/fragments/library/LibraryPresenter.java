package com.the_canuck.openpodcast.fragments.library;

import android.support.v4.app.Fragment;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.data.podcast.PodcastRepository;
import com.the_canuck.openpodcast.data.podcast.PodcastRepositoryImpl;
import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import java.util.List;

import javax.inject.Inject;

public class LibraryPresenter implements LibraryContract.LibraryPresenter {

    private LibraryContract.LibraryView mLibraryView;

    private PodcastRepository podcastRepository;

    @Inject
    public LibraryPresenter(Fragment fragment, PodcastRepository podcastRepository) {
        mLibraryView = (LibraryFragment) fragment;
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
