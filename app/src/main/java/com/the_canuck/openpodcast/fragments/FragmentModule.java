package com.the_canuck.openpodcast.fragments;

import android.content.Context;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;

import com.the_canuck.openpodcast.data.episode.EpisodeRepository;
import com.the_canuck.openpodcast.data.podcast.PodcastRepository;
import com.the_canuck.openpodcast.fragments.bottom_sheet.BottomSheetContract;
import com.the_canuck.openpodcast.fragments.bottom_sheet.BottomSheetPresenter;
import com.the_canuck.openpodcast.fragments.library.LibraryContract;
import com.the_canuck.openpodcast.fragments.library.LibraryPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class FragmentModule {

    private Fragment fragment;

    public FragmentModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    @FragmentScope
    public Fragment fragment() {
        return fragment;
    }
}
