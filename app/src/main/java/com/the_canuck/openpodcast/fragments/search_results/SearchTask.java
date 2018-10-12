package com.the_canuck.openpodcast.fragments.search_results;

import android.os.AsyncTask;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.search.SearchHelper;
import com.the_canuck.openpodcast.search.SearchResultHelper;

import java.util.List;

/**
 * Runs the SearchHelper and returns the podcast list and sets the recyclerview adapter.
 */
public class SearchTask extends AsyncTask<String, Void, List<Podcast>> {

    private SearchContract.SearchView searchView;
    private boolean isGenre; // TODO: Delete after discovery rework

    public SearchTask(SearchContract.SearchView searchView, boolean isGenre) {
        this.searchView = searchView;
        this.isGenre = isGenre;
    }

    public boolean isRunning() {
        return getStatus() == Status.RUNNING;
    }

    // TODO: I want to rework the search stuff to have to access a repository to handle everything
    @Override
    protected List<Podcast> doInBackground(String... strings) {
        SearchHelper searchHelper;
        List<Podcast> podcastList;
        if (isGenre) {
            searchHelper = new SearchHelper(strings[0], isGenre);
        } else {
            searchHelper = new SearchHelper(strings[0]);
        }
        SearchResultHelper resultHelper = new SearchResultHelper();
        podcastList = resultHelper.populatePodcastList(searchHelper.runSearch());

        return podcastList;
    }

    @Override
    protected void onPostExecute(List<Podcast> podcasts) {
        searchView.showLoadingIndicator(false);
        searchView.setRecyclerAdapter(podcasts);
    }
}
