package com.the_canuck.openpodcast.fragments.search_results;

public class SearchPresenter implements SearchContract.SearchPresenter {

    private SearchContract.SearchView searchView;
    private SearchTask mSearchTask;

    public SearchPresenter(SearchContract.SearchView searchView) {
        this.searchView = searchView;
    }

    @Override
    public void executeSearch(String searchTerm, boolean isGenre) {
        searchView.showLoadingIndicator(true);

        if (mSearchTask == null || !mSearchTask.isRunning()) {
            mSearchTask = new SearchTask(searchView, isGenre);
            mSearchTask.execute(searchTerm);
        }
    }

    @Override
    public void stop() {
        if (mSearchTask != null && mSearchTask.isRunning()) {
            mSearchTask.cancel(true);
        }
    }
}
