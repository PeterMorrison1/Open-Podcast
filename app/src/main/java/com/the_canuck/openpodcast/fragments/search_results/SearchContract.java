package com.the_canuck.openpodcast.fragments.search_results;

import com.the_canuck.openpodcast.Podcast;

import java.util.List;

public interface SearchContract {

    interface SearchView {

        void showLoadingIndicator(boolean active);

        void setRecyclerAdapter(List<Podcast> podcastList);

    }

    interface SearchPresenter {

        void executeSearch(String searchTerm, boolean isGenre);

        void stop();
    }
}
