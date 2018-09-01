package com.the_canuck.openpodcast.fragments.library;

import android.support.v4.app.Fragment;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import java.util.List;

import javax.inject.Inject;

public class LibraryPresenter implements LibraryContract.LibraryPresenter {

    private LibraryContract.LibraryView mLibraryView;

    public MySQLiteHelper sqLiteHelper;

    @Inject
    public LibraryPresenter(Fragment fragment, MySQLiteHelper sqLiteHelper) {
        mLibraryView = (LibraryFragment) fragment;
        this.sqLiteHelper = sqLiteHelper;
    }

    @Override
    public void updateSubscribedPodcasts() {
        mLibraryView.showLoadingIndicator(true);

        List<Podcast> subscribedPodcasts = sqLiteHelper.getSubscribedPodcasts();

        mLibraryView.showSubscribedPodcasts(subscribedPodcasts);
        mLibraryView.showLoadingIndicator(false);
    }
}
