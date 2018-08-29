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

    /* Below is commented out because not sure if it should be here or own file
    but figure it's best in its own file to more easily test future methods in presenter, by
    keeping the presenter 100% non-android code
     */

//    /**
//     * Runs the SearchHelper and returns the podcast list and sets the recyclerview adapter.
//     */
//    private static class SearchTask extends AsyncTask<String, Void, List<Podcast>> {
//
//        private SearchContract.SearchView searchView;
//        private boolean isGenre; // TODO: Delete after discovery rework
//
//        public SearchTask(SearchContract.SearchView searchView, boolean isGenre) {
//            this.searchView = searchView;
//            this.isGenre = isGenre;
//        }
//
//        @Override
//        protected List<Podcast> doInBackground(String... strings) {
//            SearchHelper searchHelper;
//            List<Podcast> podcastList;
//            if (isGenre) {
//                searchHelper = new SearchHelper(strings[0], isGenre);
//            } else {
//                searchHelper = new SearchHelper(strings[0]);
//            }
//            SearchResultHelper resultHelper = new SearchResultHelper();
//            podcastList = resultHelper.populatePodcastList(searchHelper.runSearch());
//
//            return podcastList;
//        }
//
//        @Override
//        protected void onPostExecute(List<Podcast> podcasts) {
//            searchView.showLoadingIndicator(false);
//            searchView.setRecyclerAdapter(podcasts);
//        }
//    }
}
