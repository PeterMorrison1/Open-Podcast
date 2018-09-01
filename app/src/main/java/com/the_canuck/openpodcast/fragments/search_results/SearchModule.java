package com.the_canuck.openpodcast.fragments.search_results;

import com.the_canuck.openpodcast.fragments.FragmentScope;

import dagger.Module;
import dagger.Provides;

@Module
public class SearchModule {

    private SearchFragment searchFragment;

    public SearchModule(SearchFragment searchFragment) {
        this.searchFragment = searchFragment;
    }

    @Provides
    @FragmentScope
    public SearchFragment searchFragment() {
        return searchFragment;
    }

}
