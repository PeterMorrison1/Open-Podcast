package com.the_canuck.openpodcast.fragments.library;

import android.content.Context;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.sqlite.MySQLiteHelper;

import java.util.List;

public class LibraryPresenter implements LibraryContract.LibraryPresenter {

    private LibraryContract.LibraryView mLibraryView;
    private MySQLiteHelper sqLiteHelper;

    public LibraryPresenter(LibraryContract.LibraryView libraryView, Context context) {
        mLibraryView = libraryView;
        sqLiteHelper = new MySQLiteHelper(context);
    }

    @Override
    public void updateSubscribedPodcasts() {
        mLibraryView.showLoadingIndicator(true);

        List<Podcast> subscribedPodcasts = sqLiteHelper.getSubscribedPodcasts();

        mLibraryView.showSubscribedPodcasts(subscribedPodcasts);
        mLibraryView.showLoadingIndicator(false);
    }
}
