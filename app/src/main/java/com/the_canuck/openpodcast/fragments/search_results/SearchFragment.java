package com.the_canuck.openpodcast.fragments.search_results;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.the_canuck.openpodcast.Podcast;
import com.the_canuck.openpodcast.R;
import com.the_canuck.openpodcast.search.SearchHelper;
import com.the_canuck.openpodcast.search.SearchResultHelper;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class SearchFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private String query;
    private RecyclerView recyclerView = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static SearchFragment newInstance(int columnCount) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            query = bundle.getString("query", "test");
        }
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_list, container, false);

        // Set the adapter
        if (view.findViewById(R.id.recycler_view) instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = view.findViewById(R.id.recycler_view);
            recyclerView.requestFocus();
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
//            recyclerView.setAdapter(new MySearchRecyclerViewAdapter(searchPodcasts(query), mListener));
            recyclerView.addItemDecoration(new DividerItemDecoration
                    (view.getContext(), LinearLayoutManager.VERTICAL));
            new SearchTask().execute(query);
        }
        return view;
    }

//    /**
//     * Runs the SearchHelper and returns the podcast list.
//     *
//     * @param query term entered by user to search for
//     * @return the list of podcast objects
//     */
//    public List<Podcast> searchPodcasts(String query) {
//        SearchHelper searchHelper = new SearchHelper(query);
//        SearchResultHelper resultHelper = new SearchResultHelper();
//
//        searchHelper.runSearch();
////        return resultHelper.buildPodcastList(searchHelper.getHolder().getResults());
//        return resultHelper.populatePodcastList(searchHelper.runSearch());
//
//    }

    /**
     * Runs the SearchHelper and returns the podcast list and sets the recyclerview adapter.
     */
    private class SearchTask extends AsyncTask<String, Void, List<Podcast>> {
        @Override
        protected List<Podcast> doInBackground(String... strings) {
            SearchHelper searchHelper;
            List<Podcast> podcastList = null;
            searchHelper = new SearchHelper(strings[0]);
            SearchResultHelper resultHelper = new SearchResultHelper();
            podcastList = resultHelper.populatePodcastList(searchHelper.runSearch());
    //            podcastList = resultHelper.buildPodcastList(searchHelper.getHolder().getResults());

            return podcastList;
        }

        @Override
        protected void onPostExecute(List<Podcast> podcasts) {
            recyclerView.setAdapter(new MySearchRecyclerViewAdapter(podcasts, mListener));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Podcast item);
    }
}
