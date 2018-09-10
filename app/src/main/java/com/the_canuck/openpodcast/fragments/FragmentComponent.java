package com.the_canuck.openpodcast.fragments;

import com.the_canuck.openpodcast.fragments.bottom_sheet.PodcastListDialogFragment;
import com.the_canuck.openpodcast.fragments.discover.DiscoverFragment;
import com.the_canuck.openpodcast.fragments.library.LibraryFragment;
import com.the_canuck.openpodcast.fragments.search_results.SearchFragment;

import dagger.Subcomponent;

@Subcomponent (modules = {FragmentModule.class})
@FragmentScope
public interface FragmentComponent {

    void inject(LibraryFragment fragment);

    void inject(SearchFragment fragment);

    void inject(PodcastListDialogFragment fragment);

    void inject(DiscoverFragment fragment);

}
